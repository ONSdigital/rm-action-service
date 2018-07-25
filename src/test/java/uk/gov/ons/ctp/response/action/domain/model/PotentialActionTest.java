package uk.gov.ons.ctp.response.action.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

public class PotentialActionTest {

  private static final ActionType ACTION_TYPE = ActionType.builder().name("type 1").build();
  private static final ActionType ACTION_TYPE_2 = ActionType.builder().name("type 2").build();
  private static final Integer CASE_FK = 60;
  private static final Integer CASE_FK_2 = 61;
  private static final Integer ACTION_PLAN_FK = 50;
  private static final Integer ACTION_PLAN_FK_2 = 51;
  private static final Integer ACTION_RULE_FK = 40;
  private static final Integer ACTION_RULE_FK_2 = 41;
  private static final Integer PRIORITY = 3;
  private static final Integer PRIORITY_2 = 2;
  private static final UUID CASE_ID = UUID.fromString("590a4efc-322d-4c4a-8d84-d629bb1819db");
  private static final UUID CASE_ID_2 = UUID.fromString("590a4efc-322d-4c4a-8d84-d629bb1819de");
  public static final String TEST_STRING = "testing";

  private PotentialAction subject;

  @Before
  public void setUp() throws Exception {
    subject =
        new PotentialAction(
            CASE_ID, CASE_FK, ACTION_PLAN_FK, ACTION_RULE_FK, ACTION_TYPE, PRIORITY);
  }

  @Test
  public void testItHasACaseIdThatCanBeSet() {
    assertThat(subject.getCaseId(), is(CASE_ID));
  }

  @Test
  public void testItHasACaseFk() {
    assertThat(subject.getCaseFk(), is(CASE_FK));
  }

  @Test
  public void testItHasAnActionPlanFk() {
    assertThat(subject.getActionPlanFk(), is(ACTION_PLAN_FK));
  }

  @Test
  public void testItHasAnActionRuleFk() {
    assertThat(subject.getActionRuleFk(), is(ACTION_RULE_FK));
  }

  @Test
  public void testItHasAnActionType() {
    assertThat(subject.getActionType(), is(ACTION_TYPE));
  }

  @Test
  public void testEqualObjectsAreEqual() {
    final PotentialAction comparison =
        new PotentialAction(
            CASE_ID, CASE_FK, ACTION_PLAN_FK, ACTION_RULE_FK, ACTION_TYPE, PRIORITY);

    assertThat(subject, is(comparison));
  }

  @Test
  public void testInEqualObjectsAreNotEqual_CaseFk() {
    final PotentialAction comparison =
        new PotentialAction(
            CASE_ID, CASE_FK_2, ACTION_PLAN_FK, ACTION_RULE_FK, ACTION_TYPE, PRIORITY);

    assertThat(subject, is(not(comparison)));
  }

  @Test
  public void testInEqualObjectsAreNotEqual_ActionPlanFk() {
    final PotentialAction comparison =
        new PotentialAction(
            CASE_ID, CASE_FK, ACTION_PLAN_FK_2, ACTION_RULE_FK, ACTION_TYPE, PRIORITY);

    assertThat(subject, is(not(comparison)));
  }

  @Test
  public void testInEqualObjectsAreNotEqual_ActionRuleFk() {
    final PotentialAction comparison =
        new PotentialAction(
            CASE_ID, CASE_FK, ACTION_PLAN_FK, ACTION_RULE_FK_2, ACTION_TYPE, PRIORITY);

    assertThat(subject, is(not(comparison)));
  }

  @Test
  public void testInEqualObjectsAreNotEqual_ActionTypeFk() {
    final PotentialAction comparison =
        new PotentialAction(
            CASE_ID, CASE_FK, ACTION_PLAN_FK, ACTION_RULE_FK, ACTION_TYPE_2, PRIORITY);

    assertThat(subject, is(not(comparison)));
  }

  @Test
  public void testInEqualObjectsAreNotEqual_CaseId() {
    final PotentialAction comparison =
        new PotentialAction(
            CASE_ID_2, CASE_FK, ACTION_PLAN_FK, ACTION_RULE_FK, ACTION_TYPE, PRIORITY);

    assertThat(subject, is(not(comparison)));
  }

  @Test
  public void testInEqualObjectsAreNotEqual_DifferentTypes() {
    final String comparison = TEST_STRING;
    assertThat(subject, is(not(comparison)));
  }

  @Test
  public void testItHasAPriority() {
    assertThat(subject.getPriority(), is(PRIORITY));
  }

  @Test
  public void testInEqualObjectsAreNotEqual_Priority() {
    final PotentialAction comparison =
        new PotentialAction(
            CASE_ID, CASE_FK, ACTION_PLAN_FK, ACTION_RULE_FK, ACTION_TYPE, PRIORITY_2);

    assertThat(subject, is(not(comparison)));
  }
}
