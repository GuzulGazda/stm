package dn.stm.shop.utils;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(MailSender.class.getName());
    
    // TODO move to properties
    private final String EMAIL_TO = "kalinchukihor@gmail.com";      // Recipient's email 
    private final String HOST = "localhost";                        // sending email from localhost

    private final String userEmail;
    private final String subj;
    private final String messageBody;

    public MailSender(String userEmail, String subj, String messageBody) {
        this.userEmail = userEmail;
        this.subj = subj;
        this.messageBody = messageBody;
    }

    private void sendMail() {
        // Get system properties
        Properties properties = System.getProperties();
        // Setup mail server
        properties.setProperty("mail.smtp.host", HOST);
        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);
        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);
            // Set From: header field of the header.
            message.setFrom(new InternetAddress(userEmail));
            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(EMAIL_TO));
            // Set Subject: header field
            message.setSubject(subj, "UTF-8");
            // Set the actual message
            message.setText(messageBody, "UTF-8", "html");
            // Send message
            Transport.send(message);
            LOGGER.info("Sent message successfully....");
        } catch (MessagingException mex) {
            LOGGER.log(Level.SEVERE, "Error class: {0}, errorMessage: {1}", new Object[]{mex.getClass().getName(), mex.getMessage()});
        }
    }

    @Override
    public void run() {
        sendMail();
    }
}
