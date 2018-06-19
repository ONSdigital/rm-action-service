package uk.gov.ons.ctp.response.action.service.impl;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRuleRepository;

/** Tests for ActionServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class ActionRuleServiceImplTest {

  private static final UUID ACTION_RULE_ID_1 =
      UUID.fromString("774afa97-8c87-4131-923b-b33ccbf72b3e");
  private static final UUID ACTION_RULE_ID_2 =
      UUID.fromString("774afa97-8c87-4131-923b-b33ccbf72bd9");

  @InjectMocks private ActionRuleServiceImpl actionRuleServiceImpl;

  @Mock private ActionRuleRepository actionRuleRepo;

  private List<ActionRule> actionrules;

  /**
   * Initialises Mockito and loads Class Fixtures
   *
   * @throws Exception exception thrown
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    actionrules = FixtureHelper.loadClassFixtures(ActionRule[].class);
  }

  @Test
  public void testUpdateActionRuleCallsSave() throws Exception {
    final ActionRule actionRule = actionrules.get(0);
    when(actionRuleRepo.findById(ACTION_RULE_ID_1)).thenReturn(actionRule);
    when(actionRuleRepo.saveAndFlush(any())).then(returnsFirstArg());

    actionRuleServiceImpl.updateActionRule(actionRule);

    verify(actionRuleRepo, times(1)).saveAndFlush(any());
  }

  @Test
  public void testUpdateActionRuleNoActionRuleFound() throws Exception {
    final ActionRule existingAction = actionRuleServiceImpl.updateActionRule(actionrules.get(0));

    verify(actionRuleRepo, times(0)).saveAndFlush(any());
    assertThat(existingAction, is(nullValue()));
  }

  @Test
  public void testUpdateActionNoUpdate() throws Exception {
    when(actionRuleRepo.findById(ACTION_RULE_ID_2)).thenReturn(actionrules.get(1));
    final ActionRule existingAction = actionRuleServiceImpl.updateActionRule(actionrules.get(1));

    verify(actionRuleRepo, times(0)).saveAndFlush(any());
    assertThat(existingAction, is(actionrules.get(1)));
  }
}
