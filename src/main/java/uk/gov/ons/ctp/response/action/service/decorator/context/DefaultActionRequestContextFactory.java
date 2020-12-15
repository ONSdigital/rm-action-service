package uk.gov.ons.ctp.response.action.service.decorator.context;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.UUID;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.client.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.client.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.client.SurveySvcClientService;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.lib.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.lib.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.lib.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.sample.representation.SampleUnitDTO.SampleUnitType;
import uk.gov.ons.ctp.response.lib.survey.representation.SurveyDTO;

@Component
public class DefaultActionRequestContextFactory implements ActionRequestContextFactory {
  private static final Logger log =
      LoggerFactory.getLogger(DefaultActionRequestContextFactory.class);

  private final ActionPlanRepository actionPlanRepo;

  private final CaseSvcClientService caseSvcClientService;
  private final CollectionExerciseClientService collectionExerciseClientService;
  private final SurveySvcClientService surveySvcClientService;

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
