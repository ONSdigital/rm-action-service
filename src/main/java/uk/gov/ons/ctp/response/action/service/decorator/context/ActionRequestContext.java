package uk.gov.ons.ctp.response.action.service.decorator.context;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleAttributesDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO.SampleUnitType;
import uk.gov.ons.response.survey.representation.SurveyDTO;

@Data
@NoArgsConstructor
public class ActionRequestContext {
  private Action action;
  private ActionPlan actionPlan;
  private CollectionExerciseDTO collectionExercise;
  private SurveyDTO survey;
  private CaseDetailsDTO caseDetails;
  private SampleUnitType sampleUnitType;
  private PartyDTO parentParty;
  private List<PartyDTO> childParties;
  private SampleAttributesDTO sampleAttributes;
}
