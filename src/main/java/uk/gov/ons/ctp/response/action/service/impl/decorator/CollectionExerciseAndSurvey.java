package uk.gov.ons.ctp.response.action.service.impl.decorator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.service.impl.ActionProcessingServiceImpl;
import uk.gov.ons.ctp.response.action.service.impl.decorator.context.ActionRequestContext;

public class CollectionExerciseAndSurvey implements ActionRequestDecorator {

  @Override
  public void decorateActionRequest(ActionRequest actionRequest, ActionRequestContext context) {

    actionRequest.setExerciseRef(context.getCollectionExercise().getExerciseRef());
    actionRequest.setUserDescription(context.getCollectionExercise().getUserDescription());

    actionRequest.setSurveyName(context.getSurvey().getLongName());
    actionRequest.setSurveyRef(context.getSurvey().getSurveyRef());
    actionRequest.setLegalBasis(context.getSurvey().getLegalBasis());

    final Date scheduledReturnDateTime =
        context.getCollectionExercise().getScheduledReturnDateTime();
    if (scheduledReturnDateTime != null) {
      final DateFormat df =
          new SimpleDateFormat(ActionProcessingServiceImpl.DATE_FORMAT_IN_REMINDER_EMAIL);
      actionRequest.setReturnByDate(df.format(scheduledReturnDateTime));
    }
  }
}
