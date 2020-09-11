package uk.gov.ons.ctp.response.action.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
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
    @AllArgsConstructor
    public static class Classifiers {

      @JsonProperty("communication_type")
      @JsonInclude(Include.NON_NULL)
      private List<String> actionTypes;

      @JsonProperty("survey")
      @JsonInclude(Include.NON_NULL)
      private List<String> surveyRefs;

      @JsonProperty("region")
      @JsonInclude(Include.NON_NULL)
      private List<String> regions;

      @JsonProperty("legal_basis")
      @JsonInclude(Include.NON_NULL)
      private List<String> legalBasisList;

      public static ClassifiersBuilder builder() {
        return new ClassifiersBuilder();
      }

      public static class ClassifiersBuilder {

        private static final String REMINDER_EMAIL = "BSRE";
        private static final String NUDGE_EMAIL = "BSNUE";
        private static final String NUDGE = "NUDGE";
        private static final String NOTIFICATION_EMAIL = "BSNE";
        private static final String REMINDER = "REMINDER";
        private static final String NOTIFICATION = "NOTIFICATION";
        private static final String COVID_SURVEY_ID = "283";

        private List<String> actionTypes;
        private String actionType;
        private List<String> legalBasisList;
        private String legalBasis;
        private List<String> regions;
        private String region;
        private List<String> surveyRefs;
        private String surveyRef;

        /* This is lifted directly from the old notify-gateway */
        public Classifiers build() {
          if (NUDGE_EMAIL.equals(actionType)) {
            actionTypes = new ArrayList<>();
            actionTypes.add(NUDGE);
          }

          if (legalBasis != null) {
            legalBasisList = new ArrayList<>();
            legalBasisList.add(legalBasis);
          }

          if (region != null) {
            regions = new ArrayList<>();
            regions.add(region);
          }

          if (surveyRef != null && surveyRef.equals(COVID_SURVEY_ID)) {
            surveyRefs = new ArrayList<>();
            surveyRefs.add(surveyRef);
          }

          if (NOTIFICATION_EMAIL.equalsIgnoreCase(actionType)) {
            actionTypes = new ArrayList<>();
            actionTypes.add(NOTIFICATION);
          } else if (REMINDER_EMAIL.equalsIgnoreCase(actionType)) {
            actionTypes = new ArrayList<>();
            actionTypes.add(REMINDER);
          }
          return new Classifiers(actionTypes, surveyRefs, regions, legalBasisList);
        }

        public ClassifiersBuilder actionType(String actionType) {
          this.actionType = actionType;
          return this;
        }

        public ClassifiersBuilder legalBasis(String legalBasis) {
          this.legalBasis = legalBasis;
          return this;
        }

        public ClassifiersBuilder surveyRef(String surveyRef) {
          this.surveyRef = surveyRef;
          return this;
        }

        public ClassifiersBuilder region(String region) {
          this.region = region;
          return this;
        }
      }
    }
  }
}
