package uk.gov.ons.ctp.response.action.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.ctp.response.action.config.AppConfig;
import uk.gov.ons.ctp.response.action.config.CaseSvc;
import uk.gov.ons.ctp.response.lib.casesvc.representation.CaseIACDTO;
import uk.gov.ons.ctp.response.lib.common.rest.RestUtility;

@RunWith(MockitoJUnitRunner.class)
public class CaseSvcClientServiceTest {

  private UUID caseId;

  @InjectMocks private CaseSvcClientService client;

  @Mock private RestUtility restUtility;

  @Mock private RestTemplate restTemplate;

  @Mock private AppConfig appConfig;

  @Before
  public void setup() {
    caseId = UUID.randomUUID();
  }

  @Test
  public void ensureNewIacGenerated() {
    CaseSvc caseSvc = new CaseSvc();
    UriComponents uriComponents =
        UriComponentsBuilder.newInstance()
            .path(caseSvc.getGenerateNewIacForCasePostPath())
            .queryParams(null)
            .build();

    CaseIACDTO cazeiac = new CaseIACDTO("abc123");
    CaseSvc casesvc = mock(CaseSvc.class);

    when(appConfig.getCaseSvc()).thenReturn(casesvc);
    when(restUtility.createUriComponents(any(), any(), any())).thenReturn(uriComponents);
    when(restUtility.createHttpEntity(isNull())).thenReturn(mock(HttpEntity.class));

    ResponseEntity<CaseIACDTO> responseEntity = mock(ResponseEntity.class);
    when(responseEntity.getBody()).thenReturn(cazeiac);
    when(restTemplate.exchange(
            any(URI.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(CaseIACDTO.class)))
        .thenReturn(responseEntity);

    CaseIACDTO result = client.generateNewIacForCase(caseId);
    assertThat(result.getIac()).isEqualTo(cazeiac.getIac());
  }
}
