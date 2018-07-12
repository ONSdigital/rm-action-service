package uk.gov.ons.ctp.response.action.domain.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.model.*;
import uk.gov.ons.ctp.response.action.representation.ActionPlanJobDTO;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActionCaseRepositoryTest {
    @Autowired
    private ActionCaseRepository actionCaseRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private ActionPlanJobRepository actionPlanJobRepository;

    @Autowired
    private ActionPlanRepository actionPlanRepository;

    @Autowired
    private ActionRuleRepository actionRuleRepository;

    @Autowired
    private ActionTypeRepository actionTypeRepository;

    @Before
    public void setUp() throws Exception {
        actionCaseRepository.deleteAll();
        actionRepository.deleteAll();
        actionPlanJobRepository.deleteAll();
        actionRuleRepository.deleteAll();
        actionPlanRepository.deleteAll();
    }

    @Test
    public void testWithoutAnActionPlanJobNothingHappens() {
        assertTrue(actionCaseRepository.createActions(1));

        assertEquals(0, actionRepository.count());
    }

    @Test
    public void testWithAnActionPlanJobButActionPlansNotStartedDoNothing() {
        //// Given
        // Create Action Plan
        ActionPlan actionPlan = new ActionPlan();
        actionPlan.setId(UUID.randomUUID());
        actionPlan.setName("notification2");
        actionPlan.setDescription("bres enrolment notification");
        actionPlan.setCreatedBy("SYSTEM");
        actionPlanRepository.saveAndFlush(actionPlan);

        //Use static Action type
        ActionType actionType = actionTypeRepository.findByName("BSNL");

        // Create Action Rule for Action Plan
        ActionRule actionRule = new ActionRule();
        actionRule.setId(UUID.randomUUID());
        actionRule.setDaysOffset(1);
        actionRule.setActionPlanFK(actionPlan.getActionPlanPK());
        actionRule.setDescription("Notification file");
        actionRule.setName("Notifaction");
        actionRule.setActionTypeFK(actionType.getActionTypePK());
        actionRuleRepository.saveAndFlush(actionRule);

        // Create Action Case for action plan that is not started
        ActionCase actionCase = new ActionCase();
        actionCase.setId(UUID.randomUUID());
        actionCase.setActionPlanFK(actionPlan.getActionPlanPK());
        actionCase.setActionPlanId(actionPlan.getId());
        actionCase.setPartyId(UUID.randomUUID());

        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.HOUR, 24);
        Date datePlusOne = calendar1.getTime();
        Timestamp timestampPlusOne = new Timestamp(datePlusOne.getTime());
        actionCase.setActionPlanStartDate(timestampPlusOne);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.HOUR, 72);
        Date datePlusThree = calendar2.getTime();
        Timestamp timestampPlusThree = new Timestamp(datePlusThree.getTime());
        actionCase.setActionPlanEndDate(timestampPlusThree);
        actionCaseRepository.saveAndFlush(actionCase);

        // Create Action Plan Job for Action Plan
        ActionPlanJob actionPlanJob = new ActionPlanJob();
        actionPlanJob.setId(UUID.randomUUID());
        actionPlanJob.setActionPlanFK(actionPlan.getActionPlanPK());
        actionPlanJob.setState(ActionPlanJobDTO.ActionPlanJobState.SUBMITTED);
        actionPlanJob.setCreatedBy("SYSTEM");
        actionPlanJob.setCreatedDateTime(new Timestamp(new Date().getTime()));
        actionPlanJobRepository.saveAndFlush(actionPlanJob);

        //// When
        boolean actual = actionCaseRepository.createActions(actionPlanJob.getActionPlanJobPK());

        //// Then
        assertTrue(actual);
        assertEquals(0, actionRepository.count());

        //action plan has been updated
        ActionPlan updatedActionPlan = actionPlanRepository.findById(actionPlan.getId());
        assertNotNull(updatedActionPlan.getLastRunDateTime());

        //action plan job has been updated
        ActionPlanJob updatedActionPlanJob = actionPlanJobRepository.findById(actionPlanJob.getId());
        assertNotNull(updatedActionPlanJob.getUpdatedDateTime());
        assertEquals(ActionPlanJobDTO.ActionPlanJobState.COMPLETED, updatedActionPlanJob.getState());
    }

    @Test
    public void testWithAnActionPlanJobButActionPlansEndedDoNothing() {
        //// Given
        // Create Action Plan
        ActionPlan actionPlan = new ActionPlan();
        actionPlan.setId(UUID.randomUUID());
        actionPlan.setName("notification2");
        actionPlan.setDescription("bres enrolment notification");
        actionPlan.setCreatedBy("SYSTEM");
        actionPlanRepository.saveAndFlush(actionPlan);

        //Use static Action type
        ActionType actionType = actionTypeRepository.findByName("BSNL");

        // Create Action Rule for Action Plan
        ActionRule actionRule = new ActionRule();
        actionRule.setId(UUID.randomUUID());
        actionRule.setDaysOffset(1);
        actionRule.setActionPlanFK(actionPlan.getActionPlanPK());
        actionRule.setDescription("Notification file");
        actionRule.setName("Notifaction");
        actionRule.setActionTypeFK(actionType.getActionTypePK());
        actionRuleRepository.saveAndFlush(actionRule);

        // Create Action Case for action plan that has ended
        ActionCase actionCase = new ActionCase();
        actionCase.setId(UUID.randomUUID());
        actionCase.setActionPlanFK(actionPlan.getActionPlanPK());
        actionCase.setActionPlanId(actionPlan.getId());
        actionCase.setPartyId(UUID.randomUUID());

        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.HOUR, -72);
        Date dateMinusThree = calendar1.getTime();
        Timestamp timestampMinusThree = new Timestamp(dateMinusThree.getTime());
        actionCase.setActionPlanStartDate(timestampMinusThree);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.HOUR, -24);
        Date dateMinusOne = calendar2.getTime();
        Timestamp timestampMinusOne = new Timestamp(dateMinusOne.getTime());
        actionCase.setActionPlanEndDate(timestampMinusOne);
        actionCaseRepository.saveAndFlush(actionCase);

        // Create Action Plan Job for Action Plan
        ActionPlanJob actionPlanJob = new ActionPlanJob();
        actionPlanJob.setId(UUID.randomUUID());
        actionPlanJob.setActionPlanFK(actionPlan.getActionPlanPK());
        actionPlanJob.setState(ActionPlanJobDTO.ActionPlanJobState.SUBMITTED);
        actionPlanJob.setCreatedBy("SYSTEM");
        actionPlanJob.setCreatedDateTime(new Timestamp(new Date().getTime()));
        actionPlanJobRepository.saveAndFlush(actionPlanJob);

        //// When
        boolean actual = actionCaseRepository.createActions(actionPlanJob.getActionPlanJobPK());

        //// Then
        assertTrue(actual);
        assertEquals(0, actionRepository.count());

        //action plan has been updated
        ActionPlan updatedActionPlan = actionPlanRepository.findById(actionPlan.getId());
        assertNotNull(updatedActionPlan.getLastRunDateTime());

        //action plan job has been updated
        ActionPlanJob updatedActionPlanJob = actionPlanJobRepository.findById(actionPlanJob.getId());
        assertNotNull(updatedActionPlanJob.getUpdatedDateTime());
        assertEquals(ActionPlanJobDTO.ActionPlanJobState.COMPLETED, updatedActionPlanJob.getState());
    }

    @Test
    public void testActiveActionPlanJobAndActionPlanCreatesAction() {
        //// Given
        // Create Action Plan
        ActionPlan actionPlan = new ActionPlan();
        actionPlan.setId(UUID.randomUUID());
        actionPlan.setName("notification2");
        actionPlan.setDescription("bres enrolment notification");
        actionPlan.setCreatedBy("SYSTEM");
        actionPlanRepository.saveAndFlush(actionPlan);

        //Use static Action type
        ActionType actionType = actionTypeRepository.findByName("BSNL");

        // Create Action Rule for Action Plan
        ActionRule actionRule = new ActionRule();
        actionRule.setId(UUID.randomUUID());
        actionRule.setDaysOffset(1);
        actionRule.setActionPlanFK(actionPlan.getActionPlanPK());
        actionRule.setDescription("Notification file");
        actionRule.setName("Notifaction");
        actionRule.setActionTypeFK(actionType.getActionTypePK());
        actionRuleRepository.saveAndFlush(actionRule);

        // Create Action Case for action plan that has ended
        ActionCase actionCase = new ActionCase();
        actionCase.setId(UUID.randomUUID());
        actionCase.setActionPlanFK(actionPlan.getActionPlanPK());
        actionCase.setActionPlanId(actionPlan.getId());
        actionCase.setPartyId(UUID.randomUUID());

        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.HOUR, -72);
        Date dateMinusThree = calendar1.getTime();
        Timestamp timestampMinusThree = new Timestamp(dateMinusThree.getTime());
        actionCase.setActionPlanStartDate(timestampMinusThree);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.HOUR, 48);
        Date datePlusTwo = calendar2.getTime();
        Timestamp timestampPlusTwo = new Timestamp(datePlusTwo.getTime());
        actionCase.setActionPlanEndDate(timestampPlusTwo);
        actionCaseRepository.saveAndFlush(actionCase);

        // Create Action Plan Job for Action Plan
        ActionPlanJob actionPlanJob = new ActionPlanJob();
        actionPlanJob.setId(UUID.randomUUID());
        actionPlanJob.setActionPlanFK(actionPlan.getActionPlanPK());
        actionPlanJob.setState(ActionPlanJobDTO.ActionPlanJobState.SUBMITTED);
        actionPlanJob.setCreatedBy("SYSTEM");
        actionPlanJob.setCreatedDateTime(new Timestamp(new Date().getTime()));
        actionPlanJobRepository.saveAndFlush(actionPlanJob);

        //// When
        boolean actual = actionCaseRepository.createActions(actionPlanJob.getActionPlanJobPK());

        //// Then
        assertTrue(actual);

        //Action created with correct relations
        List<Action> createdActions = actionRepository.findAllByOrderByCreatedDateTimeDesc();
        assertEquals(1, createdActions.size());
        assertEquals(actionCase.getCasePK(),createdActions.get(0).getCaseFK());
        assertEquals(actionPlan.getActionPlanPK(),createdActions.get(0).getActionPlanFK());
        assertEquals(actionRule.getActionRulePK(),createdActions.get(0).getActionRuleFK());
        assertEquals(actionType.getActionTypePK(),createdActions.get(0).getActionType().getActionTypePK());

        //action plan has been updated
        ActionPlan updatedActionPlan = actionPlanRepository.findById(actionPlan.getId());
        assertNotNull(updatedActionPlan.getLastRunDateTime());

        //action plan job has been updated
        ActionPlanJob updatedActionPlanJob = actionPlanJobRepository.findById(actionPlanJob.getId());
        assertNotNull(updatedActionPlanJob.getUpdatedDateTime());
        assertEquals(ActionPlanJobDTO.ActionPlanJobState.COMPLETED, updatedActionPlanJob.getState());
    }
}