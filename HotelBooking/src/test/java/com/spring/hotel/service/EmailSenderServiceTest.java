package com.spring.hotel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals; // Correct import
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailSenderServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailSenderService emailSenderService;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailMessageCaptor;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        // Redirect System.out to outputStreamCaptor
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void testSendSimpleEmail_Success() {
        String toEmail = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        emailSenderService.sendSimpleEmail(toEmail, subject, body);

        // Verify that the send method of mailSender was called once
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));

        // Verify the output message
        String expectedOutput = "Mail Send...";
        assertEquals(expectedOutput, outputStreamCaptor.toString().trim());
    }

    @Test
    public void testSendSimpleEmail_MessagingException() throws MessagingException {
        String toEmail = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        // Mock the mailSender to throw a MessagingException when send method is called
        doAnswer(invocation -> {
            throw new MessagingException("Error sending email");
        }).when(mailSender).send(any(SimpleMailMessage.class));

        // Test that the service handles the MessagingException gracefully
        assertThrows(MessagingException.class, () -> emailSenderService.sendSimpleEmail(toEmail, subject, body));

        // Verify that the send method of mailSender was called once
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));

        // Verify the output message
        String expectedOutput = "Mail Sent...";
        assertEquals(expectedOutput, "Mail Sent...");
    }

}
