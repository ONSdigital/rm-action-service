package uk.gov.ons.ctp.response.action.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.ons.ctp.response.action.service.NotifyModel.Notify.Classifiers;

public class ClassifiersTest {

  @Test
  public void willCreateNudgeClassifiers() {
    Classifiers classifiers = Classifiers.builder().actionType("BSNUE").build();

    assertEquals(1, classifiers.getActionTypes().size());
    assertEquals("NUDGE", classifiers.getActionTypes().get(0));
  }

  @Test
  public void willCreateNotificationClassifiers() {
    Classifiers classifiers = Classifiers.builder().actionType("BSNE").build();

    assertEquals(1, classifiers.getActionTypes().size());
    assertEquals("NOTIFICATION", classifiers.getActionTypes().get(0));
  }

  @Test
  public void willAddCovidSurvey() {
    Classifiers classifiers = Classifiers.builder().actionType("BSNE").surveyRef("283").build();

    assertEquals("NOTIFICATION", classifiers.getActionTypes().get(0));
    assertEquals("283", classifiers.getSurveyRefs().get(0));
  }

  @Test
  public void willNotAddAnyOtherSurveySurvey() {
    Classifiers classifiers = Classifiers.builder().surveyRef("123").build();

    assertNull(classifiers.getSurveyRefs());
  }

  @Test
  public void willCreateReminderClassifiersAsExpectedJSON() throws JsonProcessingException {
    String ExpectedReminderClassifiers =
        "{\"communication_type\":[\"REMINDER\"],\"survey\":null,\"region\":[\"YY\"],\"legal_basis\":[\"Voluntary not stated\"]}";
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
}
