package uk.gov.ons.ctp.response.action.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class ActionTypeTest {

  @Test
  public void setsActionTypeNameWithActionTypeEnum() {
    ActionType actionType = new ActionType();

    actionType.setNameFromActionTypeEnum(
        uk.gov.ons.ctp.response.action.representation.ActionType.BSNE);

    assertThat(
        actionType.getName(),
        is(uk.gov.ons.ctp.response.action.representation.ActionType.BSNE.toString()));
  }

  @Test
  public void getActionTypeEnum() {
    ActionType actionType = new ActionType();

    actionType.setName("BSNE");

    assertThat(
        actionType.getActionTypeNameEnum(),
        is(uk.gov.ons.ctp.response.action.representation.ActionType.BSNE));
  }
}
