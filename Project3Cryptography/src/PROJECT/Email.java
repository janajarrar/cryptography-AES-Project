package PROJECT;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.nio.file.Files;

public class Email {

    public static void send(String to, String subject, String content) {
        sendWithAttachment(to, subject, content, null);
    }

    public static void sendWithAttachment(String to, String subject, String content, String filePath) {

        final String from = "jarrarjana85@gmail.com";
        final String password = "rrmp xjfa xrfs xslc";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);

            if (filePath == null) {
                message.setText(content);
            } else {
              
                File f = new File(filePath);
                byte[] fileBytes = Files.readAllBytes(f.toPath());
                String encoded = java.util.Base64.getEncoder().encodeToString(fileBytes);
                String body = content + "\n\nAttached file (" + f.getName() + ") in Base64:\n" + encoded;
                message.setText(body);
            }

            Transport.send(message);
            System.out.println("Email sent successfully!");

        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }
}