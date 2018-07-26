package uk.gov.ons.ctp.response.action.service.impl;

import static uk.gov.ons.ctp.common.time.DateTimeUtil.nowUTC;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.common.distributed.DistributedLockManager;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanJob;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanJobRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.representation.ActionPlanJobDTO;
import uk.gov.ons.ctp.response.action.service.ActionPlanJobService;
import uk.gov.ons.ctp.response.action.service.ActionService;

@Service
@Slf4j
public class ActionPlanJobServiceImpl implements ActionPlanJobService {

  public static final String CREATED_BY_SYSTEM = "SYSTEM";
  public static final String NO_ACTIONPLAN_MSG = "ActionPlan not found for id %s";

  @Autowired private DistributedLockManager actionPlanExecutionLockManager;

  @Autowired private AppConfig appConfig;

  @Autowired private ActionPlanRepository actionPlanRepo;

  @Autowired private ActionCaseRepository actionCaseRepo;

  @Autowired private ActionPlanJobRepository actionPlanJobRepo;

  @Autowired private ActionService actionSvc;

  @CoverageIgnore
  @Override
  public ActionPlanJob findActionPlanJob(final UUID actionPlanJobId) {
    log.debug("Entering findActionPlanJob with id {}", actionPlanJobId);
    return actionPlanJobRepo.findById(actionPlanJobId);
  }

  @CoverageIgnore
  @Override
  public List<ActionPlanJob> findActionPlanJobsForActionPlan(final UUID actionPlanId)
      throws CTPException {
    log.debug("Entering findActionPlanJobsForActionPlan with {}", actionPlanId);
    final ActionPlan actionPlan = actionPlanRepo.findById(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, NO_ACTIONPLAN_MSG, actionPlanId);
    }

    return actionPlanJobRepo.findByActionPlanFK(actionPlan.getActionPlanPK());
  }

  @Override
  public List<ActionPlanJob> createAndExecuteAllActionPlanJobs() {
    final List<ActionPlanJob> executedJobs = new ArrayList<>();
    actionPlanRepo
        .findAll()
        .forEach(
            actionPlan -> {
              final Date lastExecutionTime =
                  new Date(
                      nowUTC().getTime() - appConfig.getPlanExecution().getDelayMilliSeconds());
              if (actionPlan.getLastRunDateTime() == null
                  || actionPlan.getLastRunDateTime().before(lastExecutionTime)) {
                ActionPlanJob job =
                    ActionPlanJob.builder()
                        .actionPlanFK(actionPlan.getActionPlanPK())
                        .createdBy(CREATED_BY_SYSTEM)
                        .build();
                job = createAndExecuteActionPlanJob(job);
                if (job != null) {
                  executedJobs.add(job);
                }
              } else {
                log.debug(
                    "Job for plan {} has been run since last wake up - skipping",
                    actionPlan.getActionPlanPK());
              }
            });
    return executedJobs;
  }

  /**
   * Create a new ActionPlanJob record and execute createActions stored procedure.
   *
   * @param actionPlanJobTemplate template {@link ActionPlanJob} to create and execute.
   * @return ActionPlanJob that was created or null if it has not been created.
   */
  @Override
  public ActionPlanJob createAndExecuteActionPlanJob(final ActionPlanJob actionPlanJobTemplate) {
    final Integer actionPlanPK = actionPlanJobTemplate.getActionPlanFK();
    final ActionPlan actionPlan = actionPlanRepo.findOne(actionPlanPK);

    if (actionPlan == null) {
      log.debug("Action plan {} is null", actionPlanPK);
      return null;
    }

    if (actionCaseRepo.countByActionPlanFK(actionPlanPK) == 0) {
      log.debug("No open cases for action plan {}", actionPlanPK);

      return null;
    }

    if (!actionPlanExecutionLockManager.lock(actionPlan.getName())) {
      log.debug("Could not get lock on action plan {}", actionPlanPK);

      return null;
    }

    try {
      final ActionPlanJob job = createActionPlanJob(actionPlanJobTemplate);
      // createActions needs to be executed after actionPlanJob has been created and committed.
      // createActions invokes a database procedure which won't be able to see the actionPlanJob
      // if not committed.
      // This also means an actionPlanJob could be left with state SUBMITTED if createActions
      // failed.
      actionSvc.createScheduledActions(job.getActionPlanJobPK());
      return job;
    } finally {
      log.debug("Releasing lock on action plan {}", actionPlanPK);
      actionPlanExecutionLockManager.unlock(actionPlan.getName());
    }
  }

  private ActionPlanJob createActionPlanJob(final ActionPlanJob actionPlanJobTemplate) {
    final Timestamp now = nowUTC();
    actionPlanJobTemplate.setState(ActionPlanJobDTO.ActionPlanJobState.SUBMITTED);
    actionPlanJobTemplate.setCreatedDateTime(now);
    actionPlanJobTemplate.setUpdatedDateTime(now);
    actionPlanJobTemplate.setId(UUID.randomUUID());
    final ActionPlanJob createdJob = actionPlanJobRepo.save(actionPlanJobTemplate);
    log.info(
        "Running actionplanjobid {} actionplanid {}",
        createdJob.getActionPlanJobPK(),
        createdJob.getActionPlanFK());
    return createdJob;
  }
}
