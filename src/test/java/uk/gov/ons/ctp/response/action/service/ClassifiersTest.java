package uk.gov.ons.ctp.response.action.service;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.ons.ctp.response.action.service.NotifyModel.Notify.Classifiers;

public class ClassifiersTest {

  @Test
  public void willCreateNudgeClassifiers() {
    Classifiers classifiers = Classifiers.builder().actionType("BSNUE").build();

    assertEquals("NUDGE", classifiers.getActionType());
  }

  @Test
  public void willCreateNotificationClassifiers() {
    Classifiers classifiers = Classifiers.builder().actionType("BSNE").build();

    assertEquals("NOTIFICATION", classifiers.getActionType());
  }

  @Test
  public void willAddCovidSurvey() {
    Classifiers classifiers = Classifiers.builder().actionType("BSNE").surveyRef("283").build();

    assertEquals("NOTIFICATION", classifiers.getActionType());
    assertEquals("283", classifiers.getSurveyRef());
  }

  @Test
  public void willNotAddAnyOtherSurveySurvey() {
    Classifiers classifiers = Classifiers.builder().surveyRef("123").build();

    assertEquals("", classifiers.getSurveyRef());
  }

  @Test
  public void willCreateReminderClassifiersAsExpectedJSON() throws JsonProcessingException {
    String ExpectedReminderClassifiers =
        "{\"communication_type\":\"REMINDER\",\"survey\":\"\",\"region\":\"YY\",\"legal_basis\":\"Voluntary not stated\"}";
    Classifiers classifiers =
        Classifiers.builder()
            .actionType("BSRE")
            .region("YY")
            .legalBasis("Voluntary not stated")
            .build();

    ObjectMapper mapper = new ObjectMapper();
    String data = mapper.writeValueAsString(classifiers);
    assertEquals(ExpectedReminderClassifiers, data);
  }

  @Test
  public void willCreateNudgeClassifiersAsExpectedJSON() throws JsonProcessingException {
    String ExpectedReminderClassifiers =
        "{\"communication_type\":\"NUDGE\",\"survey\":\"\",\"region\":\"\",\"legal_basis\":\"\"}";
    Classifiers classifiers =
        Classifiers.builder()
            .actionType("BSNUE")
            .region("YY")
            .legalBasis("Voluntary not stated")
            .build();

    ObjectMapper mapper = new ObjectMapper();
    String data = mapper.writeValueAsString(classifiers);
    assertEquals(ExpectedReminderClassifiers, data);
  }
}
