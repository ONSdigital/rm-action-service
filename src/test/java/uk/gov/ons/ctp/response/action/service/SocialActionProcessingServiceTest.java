package uk.gov.ons.ctp.response.action.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.client.CaseSvcClientService;
import uk.gov.ons.ctp.response.action.domain.model.Action;
import uk.gov.ons.ctp.response.action.domain.model.ActionType;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTypeRepository;
import uk.gov.ons.ctp.response.action.message.ActionInstructionPublisher;
import uk.gov.ons.ctp.response.action.service.decorator.context.ActionRequestContextFactory;
import uk.gov.ons.ctp.response.lib.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.lib.common.FixtureHelper;

@RunWith(MockitoJUnitRunner.class)
public class SocialActionProcessingServiceTest {

  @Mock ActionRepository actionRepository;

  @Mock ActionTypeRepository actionTypeRepository;

  @Mock ActionInstructionPublisher actionInstructionPublisher;

  @Mock CaseSvcClientService caseSvcClientService;

  @Mock ActionRequestContextFactory decoratorContextFactory;

  @InjectMocks private SocialActionProcessingService socialActionProcessingService;

  private static final UUID ACTION_CASEID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3991");

  private UUID testUUID;

  private List<ActionType> actionTypes;
  private ActionType actionType;

  private static final String FIELD = "Field";
  private static final String SOCIAL_ICF = "SOCIALICF";

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    testUUID = UUID.randomUUID();
    actionTypes = FixtureHelper.loadClassFixtures(ActionType[].class);
    actionType = actionTypes.get(0);
  }

  @Test
  public void testCancelFieldWorkerReminder() throws Exception {
    final List<Action> actions = FixtureHelper.loadClassFixtures(Action[].class);
    final List<CaseDetailsDTO> caseDetails =
        FixtureHelper.loadClassFixtures(CaseDetailsDTO[].class);
    CaseDetailsDTO caseDetail = caseDetails.get(0);
    when(actionRepository.findByCaseId(ACTION_CASEID)).thenReturn(actions);
    when(actionTypeRepository.findByName(SOCIAL_ICF)).thenReturn(actionType);
    when(caseSvcClientService.getCase(ACTION_CASEID)).thenReturn(caseDetail);
    socialActionProcessingService.cancelFieldWorkReminder(caseDetail.getId());
    verify(actionInstructionPublisher, times(1))
        .sendActionInstruction(
            eq(FIELD), any(uk.gov.ons.ctp.response.action.message.instruction.Action.class));
  }
}
