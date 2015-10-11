package com.psu.hpa;

import java.io.IOException;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class ErrorMailSender {
	
   public static void sendEmail(String recipientEmail, String[] attachFiles) {
      String from = "kumar.vijay281@gmail.com";
      final String username = "hls.analyzer@gmail.com";
      final String password = "HLSanalyzertest";
      String host = "smtp.gmail.com";
      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", host);
      props.put("mail.smtp.port", "587");

      Session session = Session.getInstance(props,
         new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(username, password);
            }
         });

      try {
         Message message = new MimeMessage(session);
         message.setFrom(new InternetAddress(from));
         message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
         message.setSubject("Media Stream Validation Results");
         BodyPart messageBodyPart = new MimeBodyPart();
         messageBodyPart.setContent(message, "text/html");
         messageBodyPart.setText("Please find attached the media stream validation results. \n\n\n\n\nPlease do NOT response to this email, it has been sent automatically from an unmonitored mailbox.");
         Multipart multipart = new MimeMultipart();
         multipart.addBodyPart(messageBodyPart);
         
         // adds attachments
         if (attachFiles != null && attachFiles.length > 0) {
             for (String filePath : attachFiles) {
                 MimeBodyPart attachPart = new MimeBodyPart();
  
                 try {
                     attachPart.attachFile(filePath);
                 } catch (IOException ex) {
                     ex.printStackTrace();
                 }
  
                 multipart.addBodyPart(attachPart);
             }
         }
         
         message.setContent(multipart);
         Transport.send(message);
      } catch (MessagingException e) {
         throw new RuntimeException(e);
      }
   }
}