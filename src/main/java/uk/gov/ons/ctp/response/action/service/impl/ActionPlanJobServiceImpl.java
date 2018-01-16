package uk.gov.ons.ctp.response.action.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.cobertura.CoverageIgnore;
import uk.gov.ons.ctp.common.distributed.DistributedLockManager;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlanJob;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanJobRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.representation.ActionPlanJobDTO;
import uk.gov.ons.ctp.response.action.service.ActionPlanJobService;

/**
 * Implementation
 */
@Service
@Slf4j
public class ActionPlanJobServiceImpl implements ActionPlanJobService {

  public static final String CREATED_BY_SYSTEM = "SYSTEM";
  public static final String NO_ACTIONPLAN_MSG = "ActionPlan not found for id %s";

  @Autowired
  private DistributedLockManager actionPlanExecutionLockManager;

  @Autowired
  private AppConfig appConfig;

  @Autowired
  private ActionPlanRepository actionPlanRepo;

  @Autowired
  private ActionRepository actionRepo;

  @Autowired
  private ActionCaseRepository actionCaseRepo;

  @Autowired
  private ActionPlanJobRepository actionPlanJobRepo;

  @CoverageIgnore
  @Override
  public ActionPlanJob findActionPlanJob(final UUID actionPlanJobId) {
    log.debug("Entering findActionPlanJob with id {}", actionPlanJobId);
    return actionPlanJobRepo.findById(actionPlanJobId);
  }

  @CoverageIgnore
  @Override
  public List<ActionPlanJob> findActionPlanJobsForActionPlan(final UUID actionPlanId) throws CTPException {
    log.debug("Entering findActionPlanJobsForActionPlan with {}", actionPlanId);
    ActionPlan actionPlan = actionPlanRepo.findById(actionPlanId);
    if (actionPlan == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, NO_ACTIONPLAN_MSG, actionPlanId);
    }

    return actionPlanJobRepo.findByActionPlanFK(actionPlan.getActionPlanPK());
  }

  @Override
  public List<ActionPlanJob> createAndExecuteAllActionPlanJobs() {
    List<ActionPlanJob> executedJobs = new ArrayList<>();
    actionPlanRepo.findAll().forEach(actionPlan -> {
      ActionPlanJob job = new ActionPlanJob();
      job.setActionPlanFK(actionPlan.getActionPlanPK());
      job.setCreatedBy(CREATED_BY_SYSTEM);
      job = createAndExecuteActionPlanJob(job, false);
      if (job != null) {
        executedJobs.add(job);
      }
    });
    return executedJobs;
  }

  @Override
  public ActionPlanJob createAndExecuteActionPlanJob(final ActionPlanJob actionPlanJob) {
    return createAndExecuteActionPlanJob(actionPlanJob, true);
  }

  /**
   * The root method for executing an action plan - called indirectly by the
   * restful endpoint when executing a single plan manually and by the scheduled
   * execution of all plans in sequence. See the other createAndExecute plan
   * methods in this class.
   *
   * @param actionPlanJob the plan to execute.
   * @param forcedExecution true when called indirectly for manual execution -
   *          the plan lock is still used (we don't want more than one
   *          concurrent plan execution), but we skip the last run time check.
   * @return the plan job if it was run or null if not.
   */
  private ActionPlanJob createAndExecuteActionPlanJob(final ActionPlanJob actionPlanJob, boolean forcedExecution) {
    ActionPlanJob createdJob = null;

    Integer actionPlanPK = actionPlanJob.getActionPlanFK();
    ActionPlan actionPlan = actionPlanRepo.findOne(actionPlanPK);
    if (actionPlan != null) {
      if (actionPlanExecutionLockManager.lock(actionPlan.getName())) {
        try {
          Timestamp now = DateTimeUtil.nowUTC();
          if (!forcedExecution) {
            Date lastExecutionTime = new Date(now.getTime() - appConfig.getPlanExecution().getDelayMilliSeconds());
            if (actionPlan.getLastRunDateTime() != null && actionPlan.getLastRunDateTime().after(lastExecutionTime)) {
              log.debug("Job for plan {} has been run since last wake up - skipping", actionPlanPK);
              return createdJob;
            }
          }

          // If no cases for actionplan why bother?
          if (actionCaseRepo.countByActionPlanFK(actionPlanPK) == 0) {
            log.debug("No open cases for action plan {} - skipping", actionPlanPK);
            return createdJob;
          }
                              
          final Long totalInCompleteActions = actionRepo.countByActionPlanFKAndStateNot(actionPlanPK,
        		  ActionDTO.ActionState.COMPLETED);
          log.info("Total number of not completed actions for a given action plan{} - {}", actionPlanPK,
        		  totalInCompleteActions);
        		
          // If no actions for a given action plan are pending then skip ActionPlanJob
          if (totalInCompleteActions == 0) {
        	  	log.debug("No actions are pending for a given action plan{} - skipping", actionPlanPK);
        	  	return createdJob;
          }
                    
          // Enrich and save the job.
          actionPlanJob.setState(ActionPlanJobDTO.ActionPlanJobState.SUBMITTED);
          actionPlanJob.setCreatedDateTime(now);
          actionPlanJob.setUpdatedDateTime(now);
          actionPlanJob.setId(UUID.randomUUID());
          // Please note it is not possible to place the saving of the
          // actionPlanJob and call to createActions SQL function in the same
          // transaction. The createActions function uses the actionPlanJob row
          // which will not be visible to it until the transaction is committed.
          // The function creates all the actions in one transaction block
          // however, and needs the actionPlanJob row to be there to work. A
          // actionPlanJob could be left with state SUBMITTED if the function
          // failed.
          createdJob = actionPlanJobRepo.save(actionPlanJob);
          log.info("Running actionplanjobid {} actionplanid {}", createdJob.getActionPlanJobPK(),
              createdJob.getActionPlanFK());
          // Get the repo to call SQL function to create actions.
          actionCaseRepo.createActions(createdJob.getActionPlanJobPK());
        } finally {
          log.debug("Releasing lock on action plan {}", actionPlanPK);
          actionPlanExecutionLockManager.unlock(actionPlan.getName());
        }
      } else {
        log.debug("Could not get lock on action plan {}", actionPlanPK);
      }
    }

    return createdJob;
  }
}
