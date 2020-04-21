package ca.bc.gov.open.pssg.rsbc.dps.dpsemailpoller.email.api;

import ca.bc.gov.open.pssg.rsbc.models.DpsMetadata;
import ca.bc.gov.open.pssg.rsbc.dps.dpsemailpoller.email.DpsEmailException;
import ca.bc.gov.open.pssg.rsbc.dps.dpsemailpoller.email.models.DpsEmailResponse;
import ca.bc.gov.open.pssg.rsbc.dps.dpsemailpoller.email.services.EmailService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DpsEmailControllerTest {

    public static final String CASE_1 = "case1";
    public static final String CASE_2 = "case2";
    public static final String EMAIL_EXCEPTION = "email exception";
    private DpsEmailController sut;

    @Mock
    private EmailService emailServiceMock;

    @Mock
    private EmailMessage emailMessageMock;

    @BeforeAll
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        Mockito.when(emailServiceMock.moveToProcessedFolder(Mockito.eq(CASE_1))).thenReturn(emailMessageMock);
        Mockito.when(emailServiceMock.moveToProcessedFolder(Mockito.eq(CASE_2))).thenThrow(new DpsEmailException(
                EMAIL_EXCEPTION));

        Mockito.when(emailServiceMock.moveToErrorFolder(Mockito.eq(CASE_1))).thenReturn(emailMessageMock);
        Mockito.when(emailServiceMock.moveToErrorFolder(Mockito.eq(CASE_2))).thenThrow(new DpsEmailException(
                EMAIL_EXCEPTION));

        sut = new DpsEmailController(emailServiceMock);
    }

    @DisplayName("success - with failed email moved should return acknowledge")
    @Test
    public void withFailedEmailMovedShouldReturnSuccess() {

        DpsEmailProcessedRequest dpsEmailProcessedRequest = new DpsEmailProcessedRequest();
        dpsEmailProcessedRequest.setCorrelationId("test");

        ResponseEntity<DpsEmailResponse> response = sut.ProcessFailed(new DpsMetadata.Builder().withEmailId(CASE_1).build().getBase64EmailId(),
                dpsEmailProcessedRequest);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.getBody().isAcknowledge());

    }

    @DisplayName("success - with failed email not moved should return error message")
    @Test
    public void withFailedEmailNotMovedShouldReturnError() {

        DpsEmailProcessedRequest dpsEmailProcessedRequest = new DpsEmailProcessedRequest();
        dpsEmailProcessedRequest.setCorrelationId("test");

        ResponseEntity<DpsEmailResponse> response = sut.ProcessFailed(new DpsMetadata.Builder().withEmailId(CASE_2).build().getBase64EmailId(), dpsEmailProcessedRequest);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertFalse(response.getBody().isAcknowledge());
        Assertions.assertEquals(EMAIL_EXCEPTION, response.getBody().getMessage());

    }

    @DisplayName("success - with processed email moved should return acknowledge")
    @Test
    public void withProcessedEmailMovedShouldReturnSuccess() {

        DpsEmailProcessedRequest dpsEmailProcessedRequest = new DpsEmailProcessedRequest();
        dpsEmailProcessedRequest.setCorrelationId("test");

        ResponseEntity<DpsEmailResponse> response = sut.Processed(new DpsMetadata.Builder().withEmailId(CASE_1).build().getBase64EmailId(),
                dpsEmailProcessedRequest);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertTrue(response.getBody().isAcknowledge());

    }

    @DisplayName("success - with processed email not moved should return error message")
    @Test
    public void withProcessedEmailNotMovedShouldReturnError() {

        DpsEmailProcessedRequest dpsEmailProcessedRequest = new DpsEmailProcessedRequest();
        dpsEmailProcessedRequest.setCorrelationId("test");

        ResponseEntity<DpsEmailResponse> response = sut.Processed(new DpsMetadata.Builder().withEmailId(CASE_2).build().getBase64EmailId(), dpsEmailProcessedRequest);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertFalse(response.getBody().isAcknowledge());
        Assertions.assertEquals(EMAIL_EXCEPTION, response.getBody().getMessage());

    }






}
