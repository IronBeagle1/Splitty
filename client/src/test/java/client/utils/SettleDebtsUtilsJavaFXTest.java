package client.utils;

import commons.Participant;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
public class SettleDebtsUtilsJavaFXTest {
    @InjectMocks
    SettleDebtsUtils sut;
    @Mock
    ServerUtils server;
    @Mock
    Translation translation;
    @Mock
    TransferMoneyUtils transferUtils;
    @Mock
    EmailHandler emailHandler;

    @BeforeAll
    static void testFXSetup(){
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("prism.order", "sw");
    }

    @Test
    void generateEmailBodyNoBankCredentials(){
        Participant sender = new Participant("Sender");
        Participant receiver = new Participant("Receiver");
        Transfer t = new Transfer(sender, 10,receiver);
        String emailBody = sut.generateEmailBody(t);

        assertEquals(emailBody, "Please transfer the amount of " + FormattingUtils.getFormattedPrice(10) + " to Receiver\n\n" +"Thank you!");
    }

    @Test
    void generateEmailBodyWithBankCredentials(){
        Participant sender = new Participant("Sender");
        Participant receiver = new Participant("Receiver");
        receiver.setBic("BIC");
        receiver.setIban("IBAN");
        receiver.setLegalName("Legal Name");
        Transfer t = new Transfer(sender, 10,receiver);
        String emailBody = sut.generateEmailBody(t);
        assertEquals(emailBody, "Please transfer the amount of " + FormattingUtils.getFormattedPrice(10) + " to Receiver to the following bank account:\n" +
                "\n" +
                "Name: Legal Name\n" +
                "IBAN: IBAN\n" +
                "BIC: BIC\n" +
                "\n" +
                "Thank you!");
    }

    @Test
    void sendEmailTransferTestSuccess(){
        when(emailHandler.sendEmail(any(), any(), any())).thenReturn(true);
        Participant sender = new Participant("Sender");
        Participant receiver = new Participant("Receiver");
        Transfer t = new Transfer(sender, 10,receiver);
        sut.sendEmailTransferEmail(t);
        waitForJavaFX();
        verify(emailHandler, times(1)).showSuccessPrompt();
    }

    @Test
    void sendEmailTransferTestFail(){
        when(emailHandler.sendEmail(any(), any(), any())).thenReturn(false);
        Participant sender = new Participant("Sender");
        Participant receiver = new Participant("Receiver");
        Transfer t = new Transfer(sender, 10,receiver);
        sut.sendEmailTransferEmail(t);
        waitForJavaFX();
        verify(emailHandler, times(1)).showFailPrompt();
    }

    private void waitForJavaFX() {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
