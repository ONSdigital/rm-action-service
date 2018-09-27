package uk.gov.ons.ctp.response.action.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.domain.model.ActionRule;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRuleRepository;

/** Tests for ActionServiceImpl */
@RunWith(MockitoJUnitRunner.class)
public class ActionRuleServiceTest {

  private static final UUID ACTION_RULE_ID_1 =
      UUID.fromString("774afa97-8c87-4131-923b-b33ccbf72b3e");
  private static final UUID ACTION_RULE_ID_2 =
      UUID.fromString("774afa97-8c87-4131-923b-b33ccbf72bd9");

  @InjectMocks private ActionRuleService actionRuleService;

  @Mock private ActionRuleRepository actionRuleRepo;

  private ActionRule actionRule;

  @Before
  public void setUp() {
    actionRule =
        ActionRule.builder()
            .id(ACTION_RULE_ID_1)
            .priority(1)
            .description("test description")
            .triggerDateTime(OffsetDateTime.now())
            .name("BSNOT+0")
            .build();

    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testUpdateActionRuleCallsSave() {
    // Given
    when(actionRuleRepo.findById(ACTION_RULE_ID_1)).thenReturn(actionRule);
    when(actionRuleRepo.saveAndFlush(any())).then(returnsFirstArg());

    // When
    actionRuleService.updateActionRule(actionRule);

    // Then
    verify(actionRuleRepo, times(1)).saveAndFlush(actionRule);
  }

  @Test
  public void testUpdateActionRuleNoActionRuleFound() {
    // Given
    ActionRule actionRuleNotFound = ActionRule.builder().id(ACTION_RULE_ID_1).build();

    // When
    ActionRule existingAction = actionRuleService.updateActionRule(actionRuleNotFound);

    // Then
    verify(actionRuleRepo, times(0)).saveAndFlush(any());
    assertThat(existingAction, is(nullValue()));
  }

  @Test
  public void testUpdateActionNoUpdate() {
    final ActionRule actionRuleNoUpdate = ActionRule.builder().id(ACTION_RULE_ID_2).build();

    when(actionRuleRepo.findById(ACTION_RULE_ID_2)).thenReturn(actionRuleNoUpdate);
    final ActionRule existingAction = actionRuleService.updateActionRule(actionRuleNoUpdate);

    verify(actionRuleRepo, times(0)).saveAndFlush(any());
    assertThat(existingAction, is(actionRuleNoUpdate));
  }
}
