package uk.gov.ons.ctp.response.action.service;

import java.util.UUID;
import uk.gov.ons.ctp.response.sample.representation.SampleAttributesDTO;

public interface SampleSvcClientService {
  public SampleAttributesDTO getSampleAttributes(UUID sampleUnitId);
}
