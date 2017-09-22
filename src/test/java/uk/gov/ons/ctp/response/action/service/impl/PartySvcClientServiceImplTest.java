package uk.gov.ons.ctp.response.action.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.apache.commons.collections.map.MultiValueMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.rest.RestUtility;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.config.PartySvc;

/**
 * test for PartySvcClient
 */
@RunWith(MockitoJUnitRunner.class)
public class PartySvcClientServiceImplTest {

  private static final String PATH = "/sampleUnit";
  private static final String HTTP = "http";
  private static final String LOCALHOST = "localhost";

  @InjectMocks
  private PartySvcClientServiceImpl partySvcClientService;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private RestUtility restUtility;

  @Mock
  private AppConfig appConfig;

  /**
   * Initialises Mockito
   * 
   * @throws Exception exception thrown
   */
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Verifies that Party Endpoint is invoked correctly
   * 
   * @throws CTPException
   */
  @Test
  public void verifyGetPartyCallInvokesRESTEndpoint() throws CTPException {
    PartySvc partySvc = new PartySvc();
    partySvc.setPartyBySampleUnitTypeAndIdPath("/sampleUnit");

    UriComponents uriComponents = UriComponentsBuilder.newInstance()
        .scheme(HTTP)
        .host(LOCALHOST)
        .port(80)
        .path(PATH)
        .build();

    // cast to MultiValueMap was required to get mock to work.
    when(restUtility.createUriComponents(any(String.class),
        (org.springframework.util.MultiValueMap<String, String>) isNull(MultiValueMap.class), any(String.class),any(UUID.class)))
            .thenReturn(uriComponents);
    when(appConfig.getPartySvc()).thenReturn(partySvc);

    partySvcClientService.getParty("B", UUID.fromString("d06c440e-4fad-4ea6-952a-72d9db144f05"));

    verify(restUtility, times(1)).createUriComponents(PATH, null, "B",
        UUID.fromString("d06c440e-4fad-4ea6-952a-72d9db144f05"));
    verify(restUtility, times(1)).createHttpEntity(null);
  }
}