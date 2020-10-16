package uk.gov.ons.ctp.response.action.config;

import lombok.Data;

@Data
public class GCP {
  String project;
  String printFileTopic;
  String notifyTopic;
}
