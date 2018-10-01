package uk.gov.ons.ctp.response.action.service.decorator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContext;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;

@RunWith(MockitoJUnitRunner.class)
public class PartyAndContactTest {

  private static final String ACTIVE = "ACTIVE";
  private static final String CREATED = "CREATED";
  private static final String ENABLED = "ENABLED";
  private static final String INVALID_TYPE = "INVALID_TYPE";
  private static final String NOTIFY = "Notify";
  private static final String PENDING = "PENDING";
  private static final String PRINTER = "Printer";

  @InjectMocks private PartyAndContact partyAndContactDecorator;

  private PartyDTO businessParty;
  private List<PartyDTO> createdRespondentPartyList;
  private List<PartyDTO> multipleRespondentParties;
  private PartyDTO respondentParty;
  private List<PartyDTO> respondentParties;
  private PartyDTO pendingBusinessParty;
  private ActionRequest actionRequest;
  private ActionRequestContext context;

  @Before
  public void setUp() throws Exception {
    List<PartyDTO> partyDTOs = FixtureHelper.loadClassFixtures(PartyDTO[].class);
    businessParty = partyDTOs.get(0);
    pendingBusinessParty = partyDTOs.get(1);
    respondentParty = partyDTOs.get(2);
    createdRespondentPartyList = Collections.singletonList(partyDTOs.get(3));
    respondentParties = Collections.singletonList(respondentParty);
    multipleRespondentParties = partyDTOs.subList(2, 4);
  }

  private ActionRequestContext createContext(
      String handler, PartyDTO businessParty, List<PartyDTO> respondentParties) {
    context = new ActionRequestContext();
    context.setParentParty(businessParty);
    context.setChildParties(respondentParties);
    context.setSampleUnitType(SampleUnitDTO.SampleUnitType.B);
    context.setAction(createContextAction(handler));
    return context;
  }

  private Action createContextAction(String handler) {
    Action contextAction = new Action();
    contextAction.setActionType(ActionType.builder().actionTypePK(1).handler(handler).build());
    return contextAction;
  }

  @Test
  public void testDecorateActionRequestNotifyType() {
    // Given
    actionRequest = new ActionRequest();
    context = createContext(NOTIFY, businessParty, respondentParties);

    // When
    partyAndContactDecorator.decorateActionRequest(actionRequest, context);

    // Then
    assertEquals(actionRequest.getRegion(), businessParty.getAttributes().getRegion());
    assertEquals(actionRequest.getEnrolmentStatus(), ENABLED);
    assertEquals(actionRequest.getRespondentStatus(), respondentParty.getStatus());
    assertEquals(actionRequest.getContact().getRuName(), businessParty.getAttributes().getName());
    assertEquals(
        actionRequest.getContact().getForename(), respondentParty.getAttributes().getFirstName());
    assertEquals(
        actionRequest.getContact().getSurname(), respondentParty.getAttributes().getLastName());
    assertEquals(
        actionRequest.getContact().getEmailAddress(),
        respondentParty.getAttributes().getEmailAddress());
  }

  @Test
  public void testDecorateActionRequestNotifyTypePendingEnrolment() {
    // Given
    actionRequest = new ActionRequest();
    context = createContext(NOTIFY, pendingBusinessParty, respondentParties);

    // When
    partyAndContactDecorator.decorateActionRequest(actionRequest, context);

    // Then
    assertEquals(actionRequest.getRegion(), businessParty.getAttributes().getRegion());
    assertEquals(actionRequest.getEnrolmentStatus(), PENDING);
    assertEquals(actionRequest.getRespondentStatus(), respondentParty.getStatus());
    assertEquals(actionRequest.getContact().getRuName(), businessParty.getAttributes().getName());
    assertEquals(
        actionRequest.getContact().getForename(), respondentParty.getAttributes().getFirstName());
    assertEquals(
        actionRequest.getContact().getSurname(), respondentParty.getAttributes().getLastName());
    assertEquals(
        actionRequest.getContact().getEmailAddress(),
        respondentParty.getAttributes().getEmailAddress());
  }

  @Test(expected = IllegalStateException.class)
  public void testDecorateActionRequestNotifyTypeMultipleRespondentsException() {
    // Given
    actionRequest = new ActionRequest();
    context = createContext(NOTIFY, pendingBusinessParty, multipleRespondentParties);

    // When
    partyAndContactDecorator.decorateActionRequest(actionRequest, context);

    // Then IllegalStateException is throws
  }

  @Test
  public void testDecorateActionRequestPrinterType() {
    // Given
    actionRequest = new ActionRequest();
    context = createContext(PRINTER, businessParty, respondentParties);

    // When
    partyAndContactDecorator.decorateActionRequest(actionRequest, context);

    // Then
    assertEquals(actionRequest.getRegion(), businessParty.getAttributes().getRegion());
    assertEquals(actionRequest.getEnrolmentStatus(), ENABLED);
    assertEquals(actionRequest.getRespondentStatus(), ACTIVE);
    assertEquals(actionRequest.getContact().getRuName(), businessParty.getAttributes().getName());
    // No respondent so empty contact details
    assertNull(actionRequest.getContact().getForename());
    assertNull(actionRequest.getContact().getSurname());
    assertNull(actionRequest.getContact().getEmailAddress());
  }

  @Test
  public void testDecorateActionRequestPrinterTypeCreatedRespondent() {
    // Given
    actionRequest = new ActionRequest();
    context = createContext(PRINTER, businessParty, createdRespondentPartyList);

    // When
    partyAndContactDecorator.decorateActionRequest(actionRequest, context);

    // Then
    assertEquals(actionRequest.getRegion(), businessParty.getAttributes().getRegion());
    assertEquals(actionRequest.getEnrolmentStatus(), ENABLED);
    assertEquals(actionRequest.getRespondentStatus(), CREATED);
    assertEquals(actionRequest.getContact().getRuName(), businessParty.getAttributes().getName());
    // No respondent so empty contact details
    assertEquals(
        actionRequest.getContact().getForename(),
        createdRespondentPartyList.get(0).getAttributes().getFirstName());
    assertEquals(
        actionRequest.getContact().getSurname(),
        createdRespondentPartyList.get(0).getAttributes().getLastName());
    assertEquals(
        actionRequest.getContact().getEmailAddress(),
        createdRespondentPartyList.get(0).getAttributes().getEmailAddress());
  }

  @Test(expected = IllegalStateException.class)
  public void testDecorateActionRequestInvalidType() {
    // Given
    actionRequest = new ActionRequest();
    context = createContext(INVALID_TYPE, businessParty, respondentParties);

    // When
    partyAndContactDecorator.decorateActionRequest(actionRequest, context);

    // Then throws IllegalStateException
  }
}
