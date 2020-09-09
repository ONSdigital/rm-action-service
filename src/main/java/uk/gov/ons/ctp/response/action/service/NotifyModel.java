package uk.gov.ons.ctp.response.action.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotifyModel {

  private Notify notify;

  @Getter
  @Builder
  @Setter
  public static class Notify {

    @JsonProperty("email_address")
    private String emailAddress;

    private Classifiers classifiers;

    private Personalisation personalisation;

    @Getter
    @Setter
    @Builder
    public static class Personalisation {

      @JsonProperty("reporting unit reference")
      private String reportingUnitReference;

      @JsonProperty("survey id")
      private String surveyId;

      @JsonProperty("survey name")
      private String surveyName;

      private String firstname;
      private String lastname;

      @JsonProperty("return by date")
      private String returnByDate;

      @JsonProperty("RU name")
      private String ruName;

      @JsonProperty("trading style")
      private String tradingSyle;

      @JsonProperty("respondent period")
      private String respondentPeriod;
    }

    @Getter
    @Setter
    @Builder
    public static class Classifiers {

      @JsonProperty("communication_type")
      private String acionType;

      @JsonProperty("survey")
      private String surveyRef;

      @JsonProperty("region")
      private String region;

      @JsonProperty("legal_basis")
      private String legalBasis;
    }
  }
}
