package uk.gov.ons.ctp.response.action.service.impl;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.OutcomeCategory;
import uk.gov.ons.ctp.response.action.domain.model.OutcomeHandlerId;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.OutcomeCategoryRepository;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;
import uk.gov.ons.ctp.response.action.service.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.service.FeedbackService;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

/** Accept feedback from handlers */
@Slf4j
@Service
public class FeedbackServiceImpl implements FeedbackService {

  private static final int TRANSACTION_TIMEOUT = 30;

  @Autowired private CaseSvcClientService caseSvcClientService;

  @Autowired private ActionRepository actionRepo;

  @Autowired private OutcomeCategoryRepository outcomeCategoryRepository;

  @Autowired
  private StateTransitionManager<ActionState, ActionDTO.ActionEvent>
      actionSvcStateTransitionManager;

  @Transactional(
      propagation = Propagation.REQUIRED,
      readOnly = false,
      timeout = TRANSACTION_TIMEOUT)
  @Override
  public void acceptFeedback(final ActionFeedback feedback) throws CTPException {
    final UUID actionId = UUID.fromString(feedback.getActionId());

    final Action action = actionRepo.findById(actionId);

    if (action != null) {
      final ActionDTO.ActionEvent outcomeEvent =
          ActionDTO.ActionEvent.valueOf(feedback.getOutcome().name());

      if (outcomeEvent != null) {
        final String situation = feedback.getSituation();
        final ActionDTO.ActionState nextState =
            actionSvcStateTransitionManager.transition(action.getState(), outcomeEvent);
        updateAction(action, nextState, situation);

        final String handler = action.getActionType().getHandler();
        final OutcomeHandlerId outcomeHandlerId =
            OutcomeHandlerId.builder().handler(handler).actionOutcome(outcomeEvent).build();
        final OutcomeCategory outcomeCategory = outcomeCategoryRepository.findOne(outcomeHandlerId);
        if (outcomeCategory != null) {
          final CategoryDTO.CategoryName category =
              CategoryDTO.CategoryName.valueOf(outcomeCategory.getEventCategory());
          caseSvcClientService.createNewCaseEvent(action, category);
        }
      } else {
        log.error(
            "Feedback Service unable to decipher the outcome {}"
                + " from feedback - ignoring this feedback",
            feedback.getOutcome());
        throw new CTPException(
            CTPException.Fault.SYSTEM_ERROR,
            String.format("Outcome % unknown", feedback.getOutcome()));
      }
    } else {
      log.error(
          "Feedback Service unable to find action id {} from feedback - ignoring this feedback",
          feedback.getActionId());
      throw new CTPException(
          CTPException.Fault.SYSTEM_ERROR,
          String.format("ActionID %s unknown", feedback.getActionId()));
    }
  }

  /**
   * Update the action
   *
   * @param action the action to update
   * @param nextState the state to transition to
   * @param situation the situation provided by the feedback
   */
  private void updateAction(
      final Action action, final ActionDTO.ActionState nextState, final String situation) {
    action.setSituation(situation);
    action.setState(nextState);
    action.setUpdatedDateTime(DateTimeUtil.nowUTC());
    actionRepo.saveAndFlush(action);
  }
}
