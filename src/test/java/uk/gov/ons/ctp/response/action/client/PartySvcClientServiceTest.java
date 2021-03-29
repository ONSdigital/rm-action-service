package uk.gov.ons.ctp.response.action.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.config.PartySvc;
import uk.gov.ons.ctp.response.lib.common.FixtureHelper;
import uk.gov.ons.ctp.response.lib.common.rest.RestUtility;
import uk.gov.ons.ctp.response.lib.party.representation.PartyDTO;

@RunWith(MockitoJUnitRunner.class)
public class PartySvcClientServiceTest {

  @InjectMocks private PartySvcClientService client;

  @Mock private RestUtility restUtility;

  @Mock private RestTemplate restTemplate;

  @Mock private AppConfig appConfig;

  private PartyDTO businessParty;

  @Before
  public void setup() throws Exception {

    UUID partyId = UUID.randomUUID();

    // Load test data
    List<PartyDTO> partys = FixtureHelper.loadClassFixtures(PartyDTO[].class);
    businessParty = partys.get(0);
  }

  @Test
  public void testGetParty() {
    PartySvc partySvc = new PartySvc();
    partySvc.getPartyBySampleUnitTypeAndIdPath();

    given(appConfig.getPartySvc()).willReturn(partySvc);

    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .path(partySvc.getPartyBySampleUnitTypeAndIdPath())
            .queryParams(null)
            .build();

    HttpEntity httpEntity = new HttpEntity(null, null);
    given(restUtility.createUriComponents(any(String.class), any(), any(), any()))
        .willReturn(uriComponents);
    given(restUtility.createHttpEntity(isNull())).willReturn(httpEntity);

    ResponseEntity<PartyDTO> responseEntity = new ResponseEntity(businessParty, HttpStatus.OK);
    when(restTemplate.exchange(
            any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(PartyDTO.class)))
        .thenReturn(responseEntity);

    PartyDTO result = client.getParty("B", UUID.fromString(businessParty.getId()));

    assertThat(result.getSampleUnitType().equals('B'));
    assertThat(result.getId().equals(businessParty.getId()));
  }

  @Test
  public void testGetPartyWithAssociationsFilteredBySurvey() {
    PartySvc partySvc = new PartySvc();
    partySvc.setPartyBySampleUnitTypeAndIdPath("test:path");

    given(appConfig.getPartySvc()).willReturn(partySvc);

    List<String> enrolmentStatuses = new ArrayList<>();

    enrolmentStatuses.add("ENABLED");
    enrolmentStatuses.add("PENDING");

    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .path(partySvc.getPartyBySampleUnitTypeAndIdPath())
            .queryParam("survey_id", "123")
            .queryParam("enrolment_status", enrolmentStatuses)
            .build();

    HttpEntity httpEntity = new HttpEntity(null, null);
    given(restUtility.createUriComponents(any(String.class), any(), any(), any()))
        .willReturn(uriComponents);
    given(restUtility.createHttpEntity(isNull())).willReturn(httpEntity);

    ResponseEntity<PartyDTO> responseEntity = new ResponseEntity(businessParty, HttpStatus.OK);
    when(restTemplate.exchange(
            any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(PartyDTO.class)))
        .thenReturn(responseEntity);

    PartyDTO result =
        client.getPartyWithAssociationsFilteredBySurvey(
            "B", UUID.fromString(businessParty.getId()), "123", enrolmentStatuses);

    assertThat(result.getSampleUnitType().equals('B'));
    assertThat(result.getId().equals(businessParty.getId()));
  }
}
