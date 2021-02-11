package uk.gov.ons.ctp.response.action.service;

import static junit.framework.TestCase.*;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.domain.model.ActionTemplate;
import uk.gov.ons.ctp.response.action.domain.repository.ActionTemplateRepository;
import uk.gov.ons.ctp.response.action.representation.ActionTemplateDTO;

@RunWith(MockitoJUnitRunner.class)
public class ActionTemplateServiceTest {
  @Mock private ActionTemplateRepository actionTemplateRepository;
  @InjectMocks private ActionTemplateService actionTemplateService;

  private ActionTemplate mockMpsActionTemplate = new ActionTemplate();
  private ActionTemplate mockReminderActionTemplate = new ActionTemplate();
  private ActionTemplate mockGoLiveActionTemplate = new ActionTemplate();

  @Before
  public void setUp() throws Exception {
    mockMpsActionTemplate.setType("BSNL");
    mockMpsActionTemplate.setDescription("Business Survey Notification Letter");
    mockMpsActionTemplate.setPrefix("BSNOT");
    mockMpsActionTemplate.setTag("mps");
    mockMpsActionTemplate.setHandler(ActionTemplateDTO.Handler.LETTER);
    Mockito.when(
            actionTemplateRepository.findByTagAndHandler("mps", ActionTemplateDTO.Handler.LETTER))
        .thenReturn(mockMpsActionTemplate);

    mockReminderActionTemplate.setType("BSRE");
    mockReminderActionTemplate.setDescription("Business Survey Reminder Email");
    mockReminderActionTemplate.setPrefix(null);
    mockReminderActionTemplate.setTag("reminder");
    mockReminderActionTemplate.setHandler(ActionTemplateDTO.Handler.EMAIL);
    Mockito.when(
            actionTemplateRepository.findByTagAndHandler(
                "reminder", ActionTemplateDTO.Handler.EMAIL))
        .thenReturn(mockReminderActionTemplate);

    mockGoLiveActionTemplate.setType("BSNE");
    mockGoLiveActionTemplate.setDescription("Business Survey Notification Email");
    mockGoLiveActionTemplate.setPrefix(null);
    mockGoLiveActionTemplate.setTag("go_live");
    mockGoLiveActionTemplate.setHandler(ActionTemplateDTO.Handler.LETTER);
    Mockito.when(
            actionTemplateRepository.findByTagAndHandler(
                "go_live", ActionTemplateDTO.Handler.LETTER))
        .thenReturn(mockGoLiveActionTemplate);
  }

  @Test
  public void testMapEventTagToTemplateReturnsCorrectTemplate() {
    assertEquals(mockMpsActionTemplate, actionTemplateService.mapEventTagToTemplate("mps", false));
  }

  @Test
  public void testMapEventTagToTemplateReturnsDifferentResult() {
    ActionTemplate actualActionTemplate =
        actionTemplateService.mapEventTagToTemplate("nudge_email_0", true);
    assertNotEquals(mockMpsActionTemplate, actualActionTemplate);
    assertNull(actualActionTemplate);
  }

  @Test
  public void testMapEventTagToTemplateReturnsAssociatedTemplates() {
    ActionTemplate actualReminderActionTemplate =
        actionTemplateService.mapEventTagToTemplate("reminder2", true);
    assertEquals(mockReminderActionTemplate, actualReminderActionTemplate);
    assertNotNull(actualReminderActionTemplate);
    ActionTemplate actualGoLiveActionTemplate =
        actionTemplateService.mapEventTagToTemplate("go_live", false);
    assertEquals(mockGoLiveActionTemplate, actualGoLiveActionTemplate);
    assertNotNull(actualGoLiveActionTemplate);
    ActionTemplate actualMPSActionTemplate =
        actionTemplateService.mapEventTagToTemplate("mps", false);
    assertEquals(mockMpsActionTemplate, actualMPSActionTemplate);
    assertNotNull(actualMPSActionTemplate);
  }
}
