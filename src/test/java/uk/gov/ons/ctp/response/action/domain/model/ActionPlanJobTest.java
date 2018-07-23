package uk.gov.ons.ctp.response.action.domain.model;

import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import uk.gov.ons.ctp.response.action.representation.ActionPlanJobDTO;

public class ActionPlanJobTest {

  private ActionPlanJob actionPlanJob;
  private Timestamp currentTime;

  @Before
  public void setUp() throws Exception {
    actionPlanJob = new ActionPlanJob();
    currentTime = new Timestamp(new Date().getTime());
  }

  @Test
  public void testCompleteSetsStateToCompleted() {
    actionPlanJob.complete(currentTime);

    assertEquals(ActionPlanJobDTO.ActionPlanJobState.COMPLETED, actionPlanJob.getState());
  }

  @Test
  public void testCompleteSetsUpdatedDateTime() {
    actionPlanJob.complete(currentTime);
    assertEquals(currentTime, actionPlanJob.getUpdatedDateTime());
  }
}
