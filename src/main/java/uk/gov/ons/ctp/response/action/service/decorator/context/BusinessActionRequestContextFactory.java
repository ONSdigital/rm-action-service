package uk.gov.ons.ctp.response.action.service.decorator.context;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.action.client.CollectionExerciseClientService;
import uk.gov.ons.ctp.response.action.client.PartySvcClientService;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionCase;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.party.representation.Association;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO.SampleUnitType;

@Component
@Qualifier("business")
public class BusinessActionRequestContextFactory implements ActionRequestContextFactory {
  private static final Logger log =
      LoggerFactory.getLogger(BusinessActionRequestContextFactory.class);

  private static final String ENABLED = "ENABLED";

  private final CollectionExerciseClientService collectionExerciseClientService;
  private final PartySvcClientService partySvcClientService;

  private final DefaultActionRequestContextFactory defaultFactory;

  public BusinessActionRequestContextFactory(
      CollectionExerciseClientService collectionExerciseClientService,
      PartySvcClientService partySvcClientService,
      DefaultActionRequestContextFactory defaultFactory) {
    this.collectionExerciseClientService = collectionExerciseClientService;
    this.partySvcClientService = partySvcClientService;
    this.defaultFactory = defaultFactory;
  }

  @Override
  public ActionRequestContext getActionRequestDecoratorContext(Action action) {
    ActionRequestContext context = this.defaultFactory.getActionRequestDecoratorContext(action);
    setParties(context);
    return context;
  }

  private void setParties(ActionRequestContext context) {
    PartyDTO businessParty =
        partySvcClientService.getPartyWithAssociationsFilteredBySurvey(
            SampleUnitType.B.name(),
            context.getCaseDetails().getPartyId(),
            context.getSurvey().getId());
    context.setParentParty(businessParty);

    List<PartyDTO> respondentParties = getRespondentParties(businessParty);
    context.setChildParties(respondentParties);
  }

  public List<PartyDTO> getRespondentParties(final PartyDTO businessParty) {
    final List<String> respondentPartyIds =
        businessParty
            .getAssociations()
            .stream()
            .map(Association::getPartyId)
            .collect(Collectors.toList());
    return respondentPartyIds
        .stream()
        .map(id -> partySvcClientService.getParty(SampleUnitType.BI.toString(), id))
        .collect(Collectors.toList());
  }

  private List<Association> respondentsEnrolledOnCase(ActionCase actionCase) {
    PartyDTO businessParty =
        partySvcClientService.getParty(actionCase.getSampleUnitType(), actionCase.getPartyId());
    CollectionExerciseDTO collectionExercise =
        collectionExerciseClientService.getCollectionExercise(actionCase.getCollectionExerciseId());
    return associationsEnrolledForSurvey(businessParty, collectionExercise.getSurveyId());
  }

  private List<Association> associationsEnrolledForSurvey(PartyDTO party, String surveyId) {
    return party
        .getAssociations()
        .stream()
        .filter(association -> isAssociationEnabledForSurvey(association, surveyId))
        .collect(Collectors.toList());
  }

  private boolean isAssociationEnabledForSurvey(Association association, String surveyId) {
    return association
        .getEnrolments()
        .stream()
        .anyMatch(
            enrolment ->
                enrolment.getSurveyId().equals(surveyId)
                    && enrolment.getEnrolmentStatus().equalsIgnoreCase(ENABLED));
  }
}
