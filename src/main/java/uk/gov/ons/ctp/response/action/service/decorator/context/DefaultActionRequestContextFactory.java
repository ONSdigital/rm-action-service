package uk.gov.ons.ctp.response.action.service.decorator.context;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.client.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.client.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.client.SurveySvcClientService;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO.SampleUnitType;
import uk.gov.ons.response.survey.representation.SurveyDTO;

@Component
@Slf4j
public class DefaultActionRequestContextFactory implements ActionRequestContextFactory {

  private ActionPlanRepository actionPlanRepo;

  private CaseSvcClientService caseSvcClientService;
  private CollectionExerciseClientService collectionExerciseClientService;
  private SurveySvcClientService surveySvcClientService;

  public DefaultActionRequestContextFactory(
      ActionPlanRepository actionPlanRepo,
      CaseSvcClientService caseSvcClientService,
      CollectionExerciseClientService collectionExerciseClientService,
      SurveySvcClientService surveySvcClientService) {
    this.actionPlanRepo = actionPlanRepo;
    this.caseSvcClientService = caseSvcClientService;
    this.collectionExerciseClientService = collectionExerciseClientService;
    this.surveySvcClientService = surveySvcClientService;
  }

  @Override
  public ActionRequestContext getActionRequestDecoratorContext(Action action) {
    ActionRequestContext context = new ActionRequestContext();

    context.setAction(action);
    context.setActionPlan(getActionPlan(action));

    CaseDetailsDTO caseDetails = getCase(action);
    context.setCaseDetails(caseDetails);
    context.setSampleUnitType(SampleUnitType.valueOf(caseDetails.getSampleUnitType()));

    CollectionExerciseDTO collectionExercise = getCollectionExercise(caseDetails);
    context.setCollectionExercise(collectionExercise);
    context.setSurvey(getSurvey(collectionExercise));

    return context;
  }

  private ActionPlan getActionPlan(Action action) {
    ActionPlan actionPlan =
        (action.getActionPlanFK() == null)
            ? null
            : actionPlanRepo.findOne(action.getActionPlanFK());
    log.debug("actionPlan {}", actionPlan);

    return actionPlan;
  }

  private CaseDetailsDTO getCase(Action action) {
    return caseSvcClientService.getCaseWithIACandCaseEvents(action.getCaseId());
  }

  private CollectionExerciseDTO getCollectionExercise(CaseDetailsDTO caseDetails) {
    final CaseGroupDTO caseGroupDTO = caseDetails.getCaseGroup();

    final UUID collectionExerciseId = caseGroupDTO.getCollectionExerciseId();

    return collectionExerciseClientService.getCollectionExercise(collectionExerciseId);
  }

  private SurveyDTO getSurvey(CollectionExerciseDTO collectionExercise) {
    return surveySvcClientService.requestDetailsForSurvey(collectionExercise.getSurveyId());
  }
}
