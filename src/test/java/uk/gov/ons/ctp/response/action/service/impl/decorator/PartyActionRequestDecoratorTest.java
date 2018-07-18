package uk.gov.ons.ctp.response.action.service.impl.decorator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.action.service.ActionProcessingService;
import uk.gov.ons.ctp.response.party.representation.Attributes;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;

@RunWith(MockitoJUnitRunner.class)
public class PartyActionRequestDecoratorTest {

  private static final Integer ACTIVE_BI = 5;
  private static final Integer SUSPENDED_BI = 6;
  private static final Integer CREATED_BI = 7;

  @InjectMocks private PartyAndContact partyActionRequestDecorator;

  private List<PartyDTO> partyDTOs;

  @Before
  public void setUp() throws Exception {
    partyDTOs = FixtureHelper.loadClassFixtures(PartyDTO[].class);
  }

  @Test
  public void testParseRespondentStatusCreated() {
    final List<PartyDTO> partyList =
        Arrays.asList(new PartyDTO[] {partyDTOs.get(CREATED_BI), partyDTOs.get(SUSPENDED_BI)});

    final String respondentStatus =
        this.partyActionRequestDecorator.parseRespondentStatuses(partyList);

    assertEquals(ActionProcessingService.CREATED, respondentStatus);
  }

  @Test
  public void testParseRespondentStatusActive() {
    final List<PartyDTO> partyList =
        Arrays.asList(new PartyDTO[] {partyDTOs.get(CREATED_BI), partyDTOs.get(ACTIVE_BI)});

    final String respondentStatus =
        this.partyActionRequestDecorator.parseRespondentStatuses(partyList);

    assertEquals(ActionProcessingService.ACTIVE, respondentStatus);
  }

  @Test
  public void testParseRespondentStatusesEmpty() {
    final List<PartyDTO> partyList = new ArrayList<>();
    final String respondentStatus =
        this.partyActionRequestDecorator.parseRespondentStatuses(partyList);

    assertEquals(null, respondentStatus);
  }

  @Test
  public void testGetEnrolmentStatusEnabled() {
    final PartyDTO partyDTO = partyDTOs.get(0);
    assertEquals(
        ActionProcessingService.ENABLED,
        this.partyActionRequestDecorator.getEnrolmentStatus(partyDTO));
  }

  @Test
  public void testGetEnrolmentStatusPending() {
    final PartyDTO partyDTO = partyDTOs.get(1);
    assertEquals(
        ActionProcessingService.PENDING,
        this.partyActionRequestDecorator.getEnrolmentStatus(partyDTO));
  }

  @Test
  public void testGetEnrolmentStatusDefault() {
    final PartyDTO partyDTO = partyDTOs.get(2);
    assertEquals(null, this.partyActionRequestDecorator.getEnrolmentStatus(partyDTO));
  }

  @Test
  public void testGetEnrolmentStatusNoEnrolments() {
    final PartyDTO partyDTO = partyDTOs.get(2);
    partyDTO.setAssociations(null);
    assertEquals(null, this.partyActionRequestDecorator.getEnrolmentStatus(partyDTO));
  }

  @Test
  public void testGenerateTradingStyle() {
    final Attributes businessAttributes = new Attributes();
    businessAttributes.setTradstyle1("TRADSTYLE1");
    businessAttributes.setTradstyle2("TRADSTYLE2");
    businessAttributes.setTradstyle3("TRADSTYLE3");

    final String generatedTradingStyle =
        this.partyActionRequestDecorator.generateTradingStyle(businessAttributes);
    final String expectedTradingStyle = "TRADSTYLE1 TRADSTYLE2 TRADSTYLE3";

    assertEquals(expectedTradingStyle, generatedTradingStyle);
  }

  @Test
  public void testGenerateTradingStyleWithEmptyValues() {
    final Attributes businessAttributes = new Attributes();

    final String generatedTradingStyle =
        this.partyActionRequestDecorator.generateTradingStyle(businessAttributes);
    final String expectedTradingStyle = "";

    assertEquals(expectedTradingStyle, generatedTradingStyle);
  }

  @Test
  public void testGenerateTradingStyleWithSubsetOfTradingStyles() {
    final Attributes businessAttributes = new Attributes();
    businessAttributes.setTradstyle1("TRADSTYLE1");
    businessAttributes.setTradstyle3("TRADSTYLE3");

    final String generatedTradingStyle =
        this.partyActionRequestDecorator.generateTradingStyle(businessAttributes);
    final String expectedTradingStyle = "TRADSTYLE1 TRADSTYLE3";

    assertEquals(expectedTradingStyle, generatedTradingStyle);
  }
}
