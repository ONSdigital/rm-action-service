package uk.gov.ons.ctp.response.action.service;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;

@Service
public class NotificationFileCreator {

  private static final Logger log = LoggerFactory.getLogger(NotificationFileCreator.class);

  private static final SimpleDateFormat FILENAME_DATE_FORMAT =
      new SimpleDateFormat("ddMMyyyy_HHmm");

  private final Clock clock;

  private PrintFileService printFileService;

  private class ExportData {
    private List<ActionRequest> ariList;

    public ExportData(ActionRequest ari) {
      this.ariList = new ArrayList<>(Arrays.asList(ari));
    }

    public List<ActionRequest> getActionRequests() {
      return ariList;
    }

    public void addActionRequest(ActionRequest ari) {
      if (ariList != null) {
        this.ariList.add(ari);
      } else {
        this.ariList = Arrays.asList(ari);
      }
    }
  }

  public NotificationFileCreator(Clock clock, PrintFileService printFileService) {
    this.clock = clock;
    this.printFileService = printFileService;
  }

  public void export(List<ActionRequest> actionRequests) {
    Map<String, ExportData> filenamePrefixToDataMap = prepareData(actionRequests);

    createAndSendFiles(filenamePrefixToDataMap);
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

  private void createAndSendFiles(Map<String, ExportData> filenamePrefixToDataMap) {

    filenamePrefixToDataMap.forEach(
        (filenamePrefix, data) -> {
          List<ActionRequest> actionRequests = data.getActionRequests();
          uploadData(filenamePrefix, actionRequests);
        });
  }

  private String getExerciseRefWithoutSurveyRef(String exerciseRef) {
    String exerciseRefWithoutSurveyRef = StringUtils.substringAfter(exerciseRef, "_");
    return StringUtils.defaultIfEmpty(exerciseRefWithoutSurveyRef, exerciseRef);
  }

  public void uploadData(String filenamePrefix, List<ActionRequest> actionRequests) {
    if (actionRequests.isEmpty()) {
      log.info("no action request instructions to export");
      return;
    }

    final String now = FILENAME_DATE_FORMAT.format(clock.millis());
    String filename = String.format("%s_%s.csv", filenamePrefix, now);

    log.info("filename: " + filename + ", uploading file");

    // temporarily hook in here as at this point we know the name of the file
    // and all the action request instructions
    boolean success = printFileService.send(filename, actionRequests);
    // TODO what happens if this fails

  }
}
