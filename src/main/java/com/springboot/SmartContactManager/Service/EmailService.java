package com.springboot.SmartContactManager.Service;

import java.io.File;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class EmailService {
	public boolean sendEmail(String subject, String message, String to) {
		boolean flag = false;

		// Variable for gmail host
		String host = "smtp.gmail.com";
		// Get the system properties
		Properties properties = System.getProperties();
		// Setting important information to properties object
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		// Step1: Get the session Object
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("debfaltu420@gmail.com", "jmyfdslclrawgati");
			}
		});
		session.setDebug(true);
		// Setp2: Compose the message
		MimeMessage mimeMessage = new MimeMessage(session);
		try {
			// From email
			// mimeMessage.setFrom(from);
			// Add Recipient
			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			// Adding Subject to message
			mimeMessage.setSubject(subject);

			// Adding Attachment and text to message
			/*
			 * String path = "C:\\Users\\debra\\OneDrive\\Desktop\\contact.png";
			 * MimeMultipart mimeMultipart = new MimeMultipart();
			 * 
			 * // Text MimeBodyPart mimeBodyPartText = new MimeBodyPart(); // File
			 * MimeBodyPart mimeBodyPartFile = new MimeBodyPart();
			 * 
			 * try { mimeBodyPartText.setText(message); File file = new File(path);
			 * mimeBodyPartFile.attachFile(file);
			 * 
			 * mimeMultipart.addBodyPart(mimeBodyPartFile);
			 * mimeMultipart.addBodyPart(mimeBodyPartText); } catch (Exception e) {
			 * e.printStackTrace(); }
			 */

			mimeMessage.setContent(message, "text/html");

			// Step3: Send the message using transport class
			Transport.send(mimeMessage);
			System.out.println("Send success");
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
}
