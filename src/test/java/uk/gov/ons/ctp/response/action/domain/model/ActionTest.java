package uk.gov.ons.ctp.response.action.domain.model;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import uk.gov.ons.ctp.response.action.representation.ActionDTO.ActionState;

public class ActionTest {
  private static final int ACTION_TYPE_PK = 123;
  private static final Integer ACTION_PLAN_FK = 50;
  private static final Integer ACTION_RULE_FK = 40;
  private static final Integer CASE_FK = 60;
  private static final UUID CASE_ID = UUID.fromString("590a4efc-322d-4c4a-8d84-d629bb1819db");
  private static final String CREATED_BY_SYSTEM = "SYSTEM";
  private static final Integer DEFAULT_PRIORITY = 3;
  private static final ActionType ACTION_TYPE =
      ActionType.builder().actionTypePK(ACTION_TYPE_PK).build();
  private static final Timestamp CURRENT_TIME = new Timestamp(new Date().getTime());
  private static final Integer PRIORITY = 4;
  private Action subject;
  private PotentialAction potentialAction;

  @Before
  public void setUp() throws Exception {

    potentialAction =
        new PotentialAction(
            CASE_ID, CASE_FK, ACTION_PLAN_FK, ACTION_RULE_FK, ACTION_TYPE, PRIORITY);
    subject = Action.fromPotentialAction(potentialAction, CURRENT_TIME);
  }

  @Test
  public void testFromPotentialAction_IdIsUUID() {
    assertThat(subject.getId(), instanceOf(UUID.class));
  }

  @Test
  public void testFromPotentialAction_IdIsUnique() {
    final Action comparison = Action.fromPotentialAction(potentialAction, CURRENT_TIME);
    assertThat(subject.getId(), is(not(comparison.getId())));
  }

  @Test
  public void testFromPotentialAction_CaseIdSet() {
    assertThat(subject.getCaseId(), is(CASE_ID));
  }

  @Test
  public void testFromPotentialAction_CaseFKSet() {
    assertThat(subject.getCaseFK(), is(CASE_FK));
  }

  @Test
  public void testFromPotentialAction_ActionPlanRuleFkSet() {
    assertThat(subject.getActionRuleFK(), is(ACTION_RULE_FK));
  }

  @Test
  public void testFromPotentialAction_ActionPlanRuleTypeSet() {
    assertThat(subject.getActionType(), is(ACTION_TYPE));
  }

  @Test
  public void testFromPotentialAction_CreatedBySet() {
    assertThat(subject.getCreatedBy(), is(CREATED_BY_SYSTEM));
  }

  @Test
  public void testFromPotentialAction_ManuallyCreatedSet() {
    assertThat(subject.getManuallyCreated(), is(false));
  }

  @Test
  public void testFromPotentialAction_SituationNull() {
    assertThat(subject.getSituation(), is(nullValue()));
  }

  @Test
  public void testFromPotentialAction_StateSet() {
    assertThat(subject.getState(), is(ActionState.SUBMITTED));
  }

  @Test
  public void testFromPotentialAction_CreatedAtSet() {
    assertThat(subject.getCreatedDateTime(), is(CURRENT_TIME));
  }

  @Test
  public void testFromPotentialAction_UpdatedAtSet() {
    assertThat(subject.getUpdatedDateTime(), is(CURRENT_TIME));
  }

  @Test
  public void testFromPotentialAction_PriorityIsSetToGiveValue() {
    assertThat(subject.getPriority(), is(PRIORITY));
  }

  @Test
  public void testFromPotentialAction_PriorityDefaultOnNull() {
    potentialAction =
        new PotentialAction(CASE_ID, CASE_FK, ACTION_PLAN_FK, ACTION_RULE_FK, ACTION_TYPE, null);
    subject = Action.fromPotentialAction(potentialAction, CURRENT_TIME);

    assertThat(subject.getPriority(), is(DEFAULT_PRIORITY));
  }
}
