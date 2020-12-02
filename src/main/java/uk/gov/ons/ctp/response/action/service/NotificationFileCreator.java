package uk.gov.ons.ctp.response.action.service;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;

@Service
public class NotificationFileCreator {

  private static final Logger log = LoggerFactory.getLogger(NotificationFileCreator.class);

  private static final SimpleDateFormat FILENAME_DATE_FORMAT =
      new SimpleDateFormat("ddMMyyyy_HHmm");

  private final Clock clock;

  private ActionStateService actionStateService;

  private PrintFileService printFileService;

  private class ExportData {
    private List<ActionRequest> actionRequests;

    public ExportData(ActionRequest actionRequest) {
      this.actionRequests = new ArrayList<>(Arrays.asList(actionRequest));
    }

    public List<ActionRequest> getActionRequests() {
      return actionRequests;
    }

    public void addActionRequest(ActionRequest actionRequest) {
      if (actionRequests != null) {
        this.actionRequests.add(actionRequest);
      } else {
        this.actionRequests = Arrays.asList(actionRequest);
      }
    }
  }

  public NotificationFileCreator(
      Clock clock, PrintFileService printFileService, ActionStateService actionStateService) {
    this.clock = clock;
    this.printFileService = printFileService;
    this.actionStateService = actionStateService;
  }

  public void export(ActionDTO.ActionEvent actionEvent, List<ActionRequest> actionRequests) {
    Map<String, ExportData> filenamePrefixToDataMap = prepareData(actionRequests);

    createAndSendFiles(actionEvent, filenamePrefixToDataMap);
  }

  private Map<String, ExportData> prepareData(List<ActionRequest> actionRequests) {
    Map<String, ExportData> filenamePrefixToDataMap = new HashMap<>();

    actionRequests.forEach(
        ari -> {
          String filenamePrefix =
              FilenamePrefix.getPrefix(ari.getActionType())
                  + "_"
                  + ari.getSurveyRef()
                  + "_"
                  + getExerciseRefWithoutSurveyRef(ari.getExerciseRef());

          if (filenamePrefixToDataMap.containsKey(filenamePrefix)) {
            filenamePrefixToDataMap.get(filenamePrefix).addActionRequest(ari);
          } else {
            filenamePrefixToDataMap.put(filenamePrefix, new ExportData(ari));
          }
        });

    return filenamePrefixToDataMap;
  }

  private void createAndSendFiles(
      final ActionDTO.ActionEvent actionEvent, Map<String, ExportData> filenamePrefixToDataMap) {

    filenamePrefixToDataMap.forEach(
        (filenamePrefix, data) -> {
          List<ActionRequest> actionRequests = data.getActionRequests();
          uploadData(actionEvent, filenamePrefix, actionRequests);
        });
  }

  private String getExerciseRefWithoutSurveyRef(String exerciseRef) {
    String exerciseRefWithoutSurveyRef = StringUtils.substringAfter(exerciseRef, "_");
    return StringUtils.defaultIfEmpty(exerciseRefWithoutSurveyRef, exerciseRef);
  }

  public void uploadData(
      ActionDTO.ActionEvent actionEvent,
      String filenamePrefix,
      List<ActionRequest> actionRequests) {
    if (actionRequests.isEmpty()) {
      log.info("no action request instructions to export");
      return;
    }

    final String now = FILENAME_DATE_FORMAT.format(clock.millis());
    String filename = String.format("%s_%s.csv", filenamePrefix, now);

    log.info("filename: " + filename + ", uploading file");

    boolean success = printFileService.send(filename, actionRequests);
    if (success) {
      log.info("print file request successful, transitioning actions");
      for (ActionRequest actionRequest : actionRequests) {
        String actionId = actionRequest.getActionId();
        try {
          actionStateService.transitionAction(UUID.fromString(actionId), actionEvent);
        } catch (CTPException ctpExeption) {
          throw new IllegalStateException(ctpExeption);
        }
      }
    } else {
      log.warn("print file request not successful, not transitioning actions");
    }
  }
}
