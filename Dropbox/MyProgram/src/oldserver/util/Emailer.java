package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import traffic.TrafficRequest;
import connection.ConnectionProvider;

public class Emailer {

	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	private static final int SMTP_HOST_PORT = 465;
	private static final String SMTP_AUTH_USER = "umntrafficgenerator@gmail.com";
	private static final String SMTP_AUTH_PWD = "thetrafficgenerator";

	public static void sendEmail(TrafficRequest trafficRequest, int result)
			throws MessagingException, UnsupportedEncodingException {

		BufferedWriter br = null;
		try {
			br = new BufferedWriter(new FileWriter(new File("/home/yackel/TrafficGenerator/my_mntg_log.txt")));
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		try {
			br.write("is going to get email parameters\n");
			br.flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		if (trafficRequest.getEmail() == null
				|| trafficRequest.getEmail().trim().isEmpty()) {
			return;
		}

		try {
			br.write("setting email properties\n");
			br.flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		Properties props = new Properties();

		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtps.host", SMTP_HOST_NAME);
		props.put("mail.smtps.auth", "true");

		try {
			br.write("creating mail session\n");
			br.flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		Session mailSession = Session.getDefaultInstance(props);
		mailSession.setDebug(false);
		Transport transport = mailSession.getTransport();

		try {
			br.write("setting message content\n");
			br.flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		MimeMessage message = new MimeMessage(mailSession);
		message.setSubject("Traffic Generation Request #"+trafficRequest.getRequestId()+" has finished being processed");
		if (result == 1) {
			message.setContent(
					"Your traffic generation, "
							+ trafficRequest.getName()
							+ " has finished being generated.  You can download the benchmark at http://"
							+ ConnectionProvider.HOST_NAME
							+ "/traffic_results/download/"
							+ trafficRequest.getRequestId() + ". You can also visualize the benchmark at http://"
							+ ConnectionProvider.HOST_NAME
							+ "/traffic_results/view_results/"
							+ trafficRequest.getRequestId()+".\r\n"
							+"If you any comments, please leave your feedback on http://mntg.cs.umn.edu or email us at mntg@cs.umn.edu.\r\n\r\n"
							+"Thanks for using our traffic generator,\r\n"
							+"UMN DMLab Team", "text/plain");
		} else {
			message.setContent(
					"Your traffic generation, "
							+ trafficRequest.getName()
							+ " has failed to be generated.  We are sorry for any inconvenience. If you would like, you may try your request again.\r\n\r\n"
							+"Thanks for using our traffic generator,\r\n"
							+"UMN DMLab Team",
					"text/plain");
		}

		try {
			br.write("adding receiptant\n");
			br.flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		InternetAddress [] mntgAddress = new InternetAddress[1];
		mntgAddress[0] = new InternetAddress("mntg@cs.umn.edu", "Minnesota Traffic Generator"); //here we set our email alias and the desired display name
		InternetAddress customer_email = new InternetAddress(trafficRequest.getEmail());//customer email

		message.addRecipient(Message.RecipientType.TO, customer_email);
		message.addRecipients(Message.RecipientType.CC, mntgAddress);
		message.addRecipients(Message.RecipientType.BCC, mntgAddress);

		try {
			br.write("adding reply to headers\n");
			br.flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		message.setFrom(mntgAddress[0]);
		message.setReplyTo(mntgAddress);
		message.setSender(mntgAddress[0]);
		
		try {
			br.write("connecting to mail server\n");
			br.flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER,
				SMTP_AUTH_PWD);

		try {
			br.write("seding the message\n");
			br.flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		Address [] recipientsTo = message.getRecipients(Message.RecipientType.TO);
		Address [] recipientsCC = message.getRecipients(Message.RecipientType.CC);
		Address [] recipientsBCC = message.getRecipients(Message.RecipientType.BCC);
		
		Address [] allRecipients = new Address [recipientsTo.length+recipientsCC.length+recipientsBCC.length];
		int allIndex = 0;
		for(int i = 0; i < recipientsTo.length; ++i,++allIndex)
			allRecipients[allIndex] = recipientsTo[i];
		for(int i = 0; i < recipientsCC.length; ++i,++allIndex)
			allRecipients[allIndex] = recipientsCC[i];
		for(int i = 0; i < recipientsBCC.length; ++i,++allIndex)
			allRecipients[allIndex] = recipientsBCC[i];
		
		transport.sendMessage(message, allRecipients);
		
		try {
			br.write("closing the connection\n");
			br.flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		transport.close();
		
		try {
			br.write("Done! message sent\n");
			br.flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		InternetAddress notifyAddress = new InternetAddress("mntg@cs.umn.edu", "Traffic Generator Admin"); //here we set our email alias and the desired display name
		sendEmail(mntgAddress[0],notifyAddress,
				"Minnesota Traffic Generator has been used today and the results for request #"+trafficRequest.getRequestId()+" has been submitted to the user on "+trafficRequest.getEmail()+".");
		//InternetAddress notifyAddress2 = new InternetAddress("amr@cs.umn.edu", "Amr Magdy"); //here we set our email alias and the desired display name
		//sendEmail(mntgAddress[0],notifyAddress2,
		//		"Minnesota Traffic Generator has been used today and the results for request #"+trafficRequest.getRequestId()+" has been submitted to the user.");
		InternetAddress notifyAddress3 = new InternetAddress("mokbel@cs.umn.edu", "Mohamed Mokbel"); //here we set our email alias and the desired display name
		sendEmail(mntgAddress[0],notifyAddress3,
				"Minnesota Traffic Generator has been used today and the results for request #"+trafficRequest.getRequestId()+" has been submitted to the user on "+trafficRequest.getEmail()+".");
		
		try {
			br.write("Done sending notification messages\n");
			br.flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		try {
			br.flush();
			br.close();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	public static void sendEmail(InternetAddress from, InternetAddress to, String content)
			throws MessagingException, UnsupportedEncodingException {
		
		Properties props = new Properties();

		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtps.host", SMTP_HOST_NAME);
		props.put("mail.smtps.auth", "true");
		
		Session mailSession = Session.getDefaultInstance(props);
		mailSession.setDebug(false);
		Transport transport = mailSession.getTransport();
		
		MimeMessage message = new MimeMessage(mailSession);
		message.setSubject("MN Traffic Generation has been used today");
		message.setContent(content, "text/plain");
		
		message.addRecipient(Message.RecipientType.TO, to);
		
		InternetAddress [] replyto = new InternetAddress [1];
		replyto[0] = from;
		
		message.setFrom(from);
		message.setReplyTo(replyto);
		message.setSender(from);
		
		transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER,
				SMTP_AUTH_PWD);
		
		transport.sendMessage(message,
				message.getRecipients(Message.RecipientType.TO));
		
		transport.close();

	}
}
