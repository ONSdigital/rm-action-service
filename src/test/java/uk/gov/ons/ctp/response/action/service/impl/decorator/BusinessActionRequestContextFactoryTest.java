package uk.gov.ons.ctp.response.action.service.impl.decorator;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.action.service.PartySvcClientService;
import uk.gov.ons.ctp.response.action.service.impl.decorator.context.BusinessActionRequestContextFactory;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO.SampleUnitType;

@RunWith(MockitoJUnitRunner.class)
public class BusinessActionRequestContextFactoryTest {

  private static final Integer B_PARTY = 4;
  private static final Integer SUSPENDED_BI = 6;
  private static final Integer CREATED_BI = 7;

  @Mock private PartySvcClientService partySvcClientService;

  @InjectMocks private BusinessActionRequestContextFactory factory;

  private List<PartyDTO> partyDTOs;

  @Before
  public void setUp() throws Exception {
    partyDTOs = FixtureHelper.loadClassFixtures(PartyDTO[].class);
  }

  @Test
  public void testGenerateChildPartyMap() {
    final PartyDTO respondentSuspendedBI = partyDTOs.get(SUSPENDED_BI);
    final PartyDTO respondentCreatedBI = partyDTOs.get(CREATED_BI);

    when(partySvcClientService.getParty(
            "BI", partyDTOs.get(B_PARTY).getAssociations().get(0).getPartyId()))
        .thenReturn(respondentSuspendedBI);
    when(partySvcClientService.getParty(
            "BI", partyDTOs.get(B_PARTY).getAssociations().get(1).getPartyId()))
        .thenReturn(respondentCreatedBI);

    final List<PartyDTO> actualChildPartyList =
        factory.getChildParties(partyDTOs.get(B_PARTY), SampleUnitType.B);

    assertTrue(
        "Party list should contain SUSPENDED BI",
        actualChildPartyList.contains(partyDTOs.get(SUSPENDED_BI)));
    assertTrue(
        "Party list should contain CREATED BI",
        actualChildPartyList.contains(partyDTOs.get(CREATED_BI)));
    assertEquals("Party list should contain 2 items", 2, actualChildPartyList.size());
  }
}
