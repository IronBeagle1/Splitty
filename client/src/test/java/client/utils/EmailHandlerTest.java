package client.utils;

import commons.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailHandlerTest {
    private Properties properties;
    private EmailHandler emailHandler;
    private JavaMailSender javaMailSender;
    private ConfigUtils configUtils;
    @BeforeEach
    void setUp() {
        properties = mock(Properties.class);
        javaMailSender = mock(JavaMailSender.class);
        configUtils = mock(ConfigUtils.class);
        emailHandler = new EmailHandler(javaMailSender);
        when(properties.getProperty("spring.mail.host")).thenReturn("smtp.gmail.com");
        when(properties.getProperty("spring.mail.port")).thenReturn("587");
        when(properties.getProperty("spring.mail.username")).thenReturn("username");
        when(properties.getProperty("spring.mail.password")).thenReturn("password");
        when(properties.getProperty("spring.mail.properties.mail.smtp.auth")).thenReturn("true");
        when(properties.getProperty("spring.mail.properties.mail.smtp.starttls.enable")).thenReturn("true");
    }

    @Test
    void readPropertiesValid() {
        emailHandler.readProperties(properties);
        assertTrue(emailHandler.isConfigured());
    }

    @Test
    void readPropertiesNull(){
        when(properties.getProperty("spring.mail.host")).thenReturn(null);
        when(properties.getProperty("spring.mail.port")).thenReturn(null);
        when(properties.getProperty("spring.mail.username")).thenReturn(null);
        when(properties.getProperty("spring.mail.password")).thenReturn(null);
        when(properties.getProperty("spring.mail.properties.mail.smtp.auth")).thenReturn(null);
        when(properties.getProperty("spring.mail.properties.mail.smtp.starttls.enable")).thenReturn(null);
        emailHandler.readProperties(properties);
        assertFalse(emailHandler.isConfigured());
    }

    @Test
    void readPortNotANumber(){
        when(properties.getProperty("spring.mail.port")).thenReturn("not a number");
        emailHandler.readProperties(properties);
        assertFalse(emailHandler.isConfigured());
    }

    @Test
    void readAuthNotBoolean(){
        when(properties.getProperty("spring.mail.properties.mail.smtp.auth")).thenReturn("not a boolean");
        emailHandler.readProperties(properties);
        assertTrue(emailHandler.isConfigured());
    }

    @Test
    void readStartTTLSNotBoolean(){
        when(properties.getProperty("spring.mail.properties.mail.smtp.starttls.enable")).thenReturn("not a boolean");
        emailHandler.readProperties(properties);
        assertTrue(emailHandler.isConfigured());
    }

    @Test
    void sendEmailFail(){
        emailHandler.readProperties(properties);
        doThrow(new MailSendException("")).when(javaMailSender).send(Mockito.any(SimpleMailMessage.class));
        assertFalse(emailHandler.sendEmail("", "", ""));
    }

    @Test
    void sendEmailSuccess(){
        emailHandler.readProperties(properties);
        assertTrue(emailHandler.sendEmail("", "", ""));
    }

    @Test
    void sendTestEmailFail(){
        emailHandler.readProperties(properties);
        doThrow(new MailSendException("")).when(javaMailSender).send(Mockito.any(SimpleMailMessage.class));
        assertFalse(emailHandler.sendTestEmail());
    }

    @Test
    void sendTestEmailSuccess(){
        emailHandler.readProperties(properties);
        assertTrue(emailHandler.sendTestEmail());
    }

    @Test
    void getInviteText() {
        Event testEvent = new Event("Test Event", null);
        Properties properties = mock(Properties.class);
        emailHandler.setConfigUtils(configUtils);
        when(this.configUtils.easyLoadProperties()).thenReturn(properties);
        when(properties.getProperty("connection.URL")).thenReturn("localhost:8080");
        String returned = emailHandler.getInviteText(testEvent);
        String expected = String.format("You have been invited to event %s" +
                " with the invitation code of %s!" +
                " The event is hosted on the server with address: localhost:8080", testEvent.getTitle(), testEvent.getId());
        assertEquals(expected, returned);
    }
}