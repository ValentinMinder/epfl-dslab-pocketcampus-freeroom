package org.pocketcampus.platform.server;

import java.io.IOException;
import java.util.List;
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

import org.pocketcampus.platform.server.launcher.PocketCampusServer;

public class EmailSender {


	public static class SendEmailInfo {
		private String emailAddress;
		private String userToken;
		private String addressingName;

		public SendEmailInfo(String ea, String ut, String an) {
			emailAddress = ea;
			userToken = ut;
			addressingName = an;
		}

		public String getEmailAddress() {
			return emailAddress;
		}

		public String getUserToken() {
			return userToken;
		}

		public String getAddressingName() {
			return addressingName;
		}
	}

	public static class EmailTemplateInfo {
		private long participantsPool;
		private String emailTitle;
		private String emailBody;
		private List<String> sendOnlyTo;

		public EmailTemplateInfo(long pp, String et, String eb, List<String> sot) {
			participantsPool = pp;
			emailTitle = et;
			emailBody = eb;
			sendOnlyTo = sot;
		}

		public long getParticipantsPool() {
			return participantsPool;
		}

		public String getEmailTitle() {
			return emailTitle;
		}

		public String getEmailBody() {
			return emailBody;
		}

		public List<String> getSendOnlyTo() {
			return sendOnlyTo;
		}
	}

	

	static Session session;

	public static boolean openSession() {
		final String username = PocketCampusServer.CONFIG.getString("BOT_EMAIL_ACCOUNT_USERNAME");
		final String password = PocketCampusServer.CONFIG.getString("BOT_EMAIL_ACCOUNT_PASSWORD");
		if (username == null || password == null)
			return false;

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username,
								password);
					}
				});

		try {
			session.getTransport("smtp").connect();
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean closeSession() {
		try {
			session.getTransport("smtp").close();
			session = null;
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void sendEmailP(String to, String subject, String htmlBody) {
		long delay = 30000;
		while (!sendEmail(to, subject, htmlBody)) {
			delay <<= 1;
			System.out.println("sending failed, waiting " + delay / 1000 + " seconds");
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
			}
		}
	}

	public static boolean sendEmail(String to, String subject, String htmlBody) {
		if (session == null)
			return false;
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("noreply@pocketcampus.org", "PocketCampus"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			// message.setText("Dear Mail Crawler," + "\n\n No spam to my email, please!");

			// Create a multi-part to combine the parts
			Multipart multipart = new MimeMultipart("alternative");
			// Create your text message part
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("Your browser does not support the format of this email. Please open it in a browser that supports HTML.");
			// Add the text part to the multipart
			multipart.addBodyPart(messageBodyPart);
			// Create the html part
			messageBodyPart = new MimeBodyPart();
			String htmlMessage = htmlBody;
			messageBodyPart.setContent(htmlMessage, "text/html");
			// Add html part to multi part
			multipart.addBodyPart(messageBodyPart);
			// Associate multi-part with message
			message.setContent(multipart);

			Transport.send(message);

			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


}
