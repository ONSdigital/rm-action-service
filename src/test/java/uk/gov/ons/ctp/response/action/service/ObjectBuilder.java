package uk.gov.ons.ctp.response.action.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;
import uk.gov.ons.ctp.response.action.message.instruction.ActionContact;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;

/** Utility class to build objects required in tests */
public class ObjectBuilder {

  /**
   * Builds a list of Action Requests
   *
   * @return list of ActionRequests
   */
  public static List<ActionRequest> buildListOfActionRequests() {
    List<ActionRequest> result = new ArrayList<>();
    for (int i = 1; i < 51; i++) {
      result.add(buildActionRequest());
    }
    return result;
  }

  /**
   * Builds an Action Request
   *
   * @return ActionRequest object
   */
  private static ActionRequest buildActionRequest() {
    ActionRequest result = new ActionRequest();
    result.setActionId(UUID.randomUUID().toString());
    result.setActionType("testActionType");
    result.setIac("testIac");
    result.setAddress(buildActionAddress());
    result.setSurveyRef("testSurveyRef");
    result.setCaseGroupStatus("testCaseGroupStatus");
    result.setEnrolmentStatus("testEnrolmentStatus");
    result.setRespondentStatus("testRespondentStatus");
    result.setContact(buildContact());
    result.setRegion("testRegion");
    result.setSampleUnitRef("testSampleUnitRef");
    return result;
  }

  private static ActionContact buildContact() {
    ActionContact contact = new ActionContact();
    contact.setEmailAddress("testEmailAddress");
    contact.setForename("testForename");
    contact.setSurname("testSurname");
    return contact;
  }

  /**
   * Builds an Action Address
   *
   * @return ActionAddress object
   */
  private static ActionAddress buildActionAddress() {
    ActionAddress address = new ActionAddress();
    address.setLine1("1 High Street");
    address.setTownName("Southampton");
    address.setPostcode("SO16 0AS");
    return address;
  }
}
