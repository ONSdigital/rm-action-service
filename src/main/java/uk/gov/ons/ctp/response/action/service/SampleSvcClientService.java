package uk.gov.ons.ctp.response.action.service;

import uk.gov.ons.ctp.response.sample.representation.SampleAttributesDTO;

import java.util.UUID;

public interface SampleSvcClientService {
  public SampleAttributesDTO getSampleAttributes(UUID sampleUnitId);
}
