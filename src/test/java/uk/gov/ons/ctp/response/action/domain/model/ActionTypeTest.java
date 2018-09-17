package uk.gov.ons.ctp.response.action.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import uk.gov.ons.ctp.response.action.representation.ActionTypes;

public class ActionTypeTest {

  @Test
  public void setsActionTypeNameWithActionTypeEnum() {
    ActionType actionType = new ActionType();

    actionType.setNameFromActionTypeEnum(ActionTypes.BSNE);

    assertThat(actionType.getName(), is(ActionTypes.BSNE.toString()));
  }

  @Test
  public void getActionTypeEnum() {
    ActionType actionType = new ActionType();

    actionType.setName("BSNE");

    assertThat(actionType.getActionTypeNameEnum(), is(ActionTypes.BSNE));
  }
}
