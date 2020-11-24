package uk.gov.ons.ctp.response.action.scheduled.export;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.action.domain.repository.ActionRequestRepository;
import uk.gov.ons.ctp.response.action.domain.repository.ExportJobRepository;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.service.FilenamePrefix;
import uk.gov.ons.ctp.response.action.service.NotificationFileCreator;

@Component
public class ExportProcessor {
  private static final Logger log = LoggerFactory.getLogger(ExportProcessor.class);

  private static final SimpleDateFormat FILENAME_DATE_FORMAT =
      new SimpleDateFormat("ddMMyyyy_HHmm");

  private final NotificationFileCreator notificationFileCreator;

  private final ActionRequestRepository actionRequestRepository;

  private ExportJobRepository exportJobRepository;

  public ExportProcessor(
      NotificationFileCreator notificationFileCreator,
      ActionRequestRepository actionRequestRepository,
      ExportJobRepository exportJobRepository) {
    this.notificationFileCreator = notificationFileCreator;
    this.actionRequestRepository = actionRequestRepository;
    this.exportJobRepository = exportJobRepository;
  }

  private class ExportData {
    private List<ActionRequest> ariList;

    public ExportData(ActionRequest ari) {
      this.ariList = new ArrayList<ActionRequest>(Arrays.asList(ari));
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

  @Transactional
  public void processExport() {
    log.info("export process started");
    throw new UnsupportedOperationException();
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
          notificationFileCreator.uploadData(filenamePrefix, actionRequests);
        });
  }

  private String getExerciseRefWithoutSurveyRef(String exerciseRef) {
    String exerciseRefWithoutSurveyRef = StringUtils.substringAfter(exerciseRef, "_");
    return StringUtils.defaultIfEmpty(exerciseRefWithoutSurveyRef, exerciseRef);
  }
}
