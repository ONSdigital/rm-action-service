package uk.gov.ons.ctp.response.action.service.impl.decorator;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.action.domain.repository.ActionPlanRepository;
import uk.gov.ons.ctp.response.action.service.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.service.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.service.SurveySvcClientService;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO.SampleUnitType;
import uk.gov.ons.response.survey.representation.SurveyDTO;

@Component
@Slf4j
public class ActionRequestDecoratorContextFactoryImpl
    implements ActionRequestDecoratorContextFactory {

  @Autowired private ActionPlanRepository actionPlanRepo;

  @Autowired private CaseSvcClientService caseSvcClientService;

  @Autowired private CollectionExerciseClientService collectionExerciseClientService;

  @Autowired private SurveySvcClientService surveySvcClientService;

  @Override
  public ActionRequestDecoratorContext getActionRequestDecoratorContext(Action action) {
    ActionRequestDecoratorContext context = new ActionRequestDecoratorContext();

    context.setAction(action);

    ActionPlan actionPlan =
        (action.getActionPlanFK() == null)
            ? null
            : actionPlanRepo.findOne(action.getActionPlanFK());
    context.setActionPlan(actionPlan);
    log.debug("actionPlan {}", actionPlan);

    CaseDetailsDTO caseDetails =
        caseSvcClientService.getCaseWithIACandCaseEvents(action.getCaseId());
    context.setCaseDetails(caseDetails);

    // Throws IllegalArgumentException
    context.setSampleUnitType(SampleUnitType.valueOf(caseDetails.getSampleUnitType()));

    final CaseGroupDTO caseGroupDTO = caseDetails.getCaseGroup();

    final UUID collectionExerciseId = caseGroupDTO.getCollectionExerciseId();

    CollectionExerciseDTO collectionExercise =
        collectionExerciseClientService.getCollectionExercise(collectionExerciseId);

    context.setCollectionExercise(collectionExercise);

    SurveyDTO survey =
        surveySvcClientService.requestDetailsForSurvey(collectionExercise.getSurveyId());

    context.setSurvey(survey);

    return context;
  }
}
