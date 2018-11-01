package uk.gov.ons.ctp.response.action.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.client.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionCaseRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.message.ActionInstructionPublisher;
import uk.gov.ons.ctp.response.action.message.instruction.ActionCancel;
import uk.gov.ons.ctp.response.action.service.decorator.ActionAndActionPlan;
import uk.gov.ons.ctp.response.action.service.decorator.ActionRequestDecorator;
import uk.gov.ons.ctp.response.action.service.decorator.CaseAndCaseEvent;
import uk.gov.ons.ctp.response.action.service.decorator.CollectionExerciseAndSurvey;
import uk.gov.ons.ctp.response.action.service.decorator.SampleAttributes;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContextFactory;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;

@Service
@Qualifier("social")
public class SocialActionProcessingService extends ActionProcessingService {

  private static final Logger log = LoggerFactory.getLogger(SocialActionProcessingService.class);

  public static final String CANCELLATION_REASON = "Case closed";
  public static final String SOCIAL_ICF = "SOCIALICF";

  private ActionRepository actionRepository;
  private ActionTypeRepository actionTypeRepository;

  @Autowired private ActionInstructionPublisher actionInstructionPublisher;

  @Autowired private CaseSvcClientService caseSvcClientService;

  @Autowired
  @Qualifier("social")
  private ActionRequestContextFactory decoratorContextFactory;

  private static final ActionRequestDecorator[] SOCIAL_DECORATORS = {
    new ActionAndActionPlan(),
    new CaseAndCaseEvent(),
    new CollectionExerciseAndSurvey(),
    new SampleAttributes()
  };

  public SocialActionProcessingService(
      ActionCaseRepository actionCaseRepo,
      ActionRepository actionRepository,
      ActionTypeRepository actionTypeRespository) {
    super(SOCIAL_DECORATORS);
    this.actionRepository = actionRepository;
    this.actionTypeRepository = actionTypeRespository;
  }

  public void cancelFieldWorkReminder(UUID caseId) {
    log.with("case ID", caseId).info("Cancelling social field work reminder.");
    List<Action> actions = actionRepository.findByCaseId(caseId);
    ActionCancel actionCancel = null;
    for (Action action : actions) {
      ActionType at = action.getActionType();
      if (at.getName().equals(SOCIAL_ICF)) {
        actionCancel = prepareActionCancel(action);
        log.with("actionCancel", actionCancel).info("Cancellation message.");
      }
    }
    ActionType actionType = actionTypeRepository.findByName(SOCIAL_ICF);
    actionInstructionPublisher.sendActionInstruction(actionType.getHandler(), actionCancel);
  }

  private ActionCancel prepareActionCancel(Action action) {
    CaseDetailsDTO caseDetails = caseSvcClientService.getCase(action.getCaseId());
    final ActionCancel actionCancel = new ActionCancel();
    actionCancel.setCaseId(action.getCaseId().toString());
    actionCancel.setCaseRef(caseDetails.getCaseRef());
    actionCancel.setActionId(action.getId().toString());
    actionCancel.setResponseRequired(true);
    actionCancel.setReason(CANCELLATION_REASON);
    return actionCancel;
  }

  @Override
  public ActionRequestContextFactory getActionRequestDecoratorContextFactory() {
    return decoratorContextFactory;
  }
}
