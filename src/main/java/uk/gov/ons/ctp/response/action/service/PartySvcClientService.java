package uk.gov.ons.ctp.response.action.service;

import java.util.UUID;
import uk.gov.ons.ctp.response.party.representation.PartyDTO;

/** A Service which utilises the CaseSvc via RESTful client calls */
public interface PartySvcClientService {

  /**
   * Call PartySvc using REST to get the Party MAY throw a RuntimeException if the call fails
   *
   * @param partyId the PartySvc UUID
   * @param sampleUnitType type of sample unit
   * @return the Party we fetched!
   */
  PartyDTO getParty(String sampleUnitType, UUID partyId);

  /**
   * Call PartySvc using REST to get the Party MAY throw a RuntimeException if the call fails
   *
   * @param partyId the PartySvc UUID as string
   * @param sampleUnitType type of sample unit
   * @return the Party we fetched!
   */
  PartyDTO getParty(String sampleUnitType, String partyId);

  /** Get a party with the associations filtered by surveyId */
  PartyDTO getPartyWithAssociationsFilteredBySurvey(
      String sampleUnitType, UUID partyid, String surveyId);
}
