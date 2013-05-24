/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emailer;

import java.io.*;
import java.util.Properties;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/*
 * @author baojie
 */
public class Emailer {

    private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final int SMTP_HOST_PORT = 465;
    private static final String SMTP_AUTH_USER = "umntrafficgenerator@gmail.com";
    private static final String SMTP_AUTH_PWD = "thetrafficgenerator";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // TODO code application logic here
        String email = args[0];
        String id = args[1];

        try {
            sendEmail(email, id);
        } catch (MessagingException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Emailer.class.getName()).log(Level.SEVERE, null, ex);
        }

        //System.out.println(email);

    }

    public static void sendEmail(String email, String id)
            throws MessagingException, UnsupportedEncodingException {


        Properties props = new Properties();

        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", SMTP_HOST_NAME);
        props.put("mail.smtps.auth", "true");

        Session mailSession = Session.getDefaultInstance(props);
        mailSession.setDebug(false);
        Transport transport = mailSession.getTransport();



        MimeMessage message = new MimeMessage(mailSession);
        message.setSubject("Traffic Generation Request #" + id + " has received");

        message.setContent("Hi Sir/Madam, \n\n Your traffic generation request #" + id + " has been received by DMLab@UMN. \n You will be notified via email " + email + " , when we finish our processing.\n\n Please be patient. If you have any inquries, please send email to mntg@cs.umn.edu. \n\n Thanks, \n DMLab@UMN", "text/plain");


        InternetAddress[] mntgAddress = new InternetAddress[1];
        mntgAddress[0] = new InternetAddress("mntg@cs.umn.edu", "Minnesota Traffic Generator"); //here we set our email alias and the desired display name
        InternetAddress customer_email = new InternetAddress(email);//customer email

        message.addRecipient(Message.RecipientType.TO, customer_email);

        message.addRecipients(Message.RecipientType.CC, mntgAddress);
        message.addRecipients(Message.RecipientType.BCC, mntgAddress);



        message.setFrom(mntgAddress[0]);
        message.setReplyTo(mntgAddress);
        message.setSender(mntgAddress[0]);



        transport.connect(SMTP_HOST_NAME, SMTP_HOST_PORT, SMTP_AUTH_USER,
                SMTP_AUTH_PWD);


        Address[] recipientsTo = message.getRecipients(Message.RecipientType.TO);
        Address[] recipientsCC = message.getRecipients(Message.RecipientType.CC);
        Address[] recipientsBCC = message.getRecipients(Message.RecipientType.BCC);

        Address[] allRecipients = new Address[recipientsTo.length + recipientsCC.length + recipientsBCC.length];
        int allIndex = 0;
        for (int i = 0; i < recipientsTo.length; ++i, ++allIndex) {
            allRecipients[allIndex] = recipientsTo[i];
        }
        for (int i = 0; i < recipientsCC.length; ++i, ++allIndex) {
            allRecipients[allIndex] = recipientsCC[i];
        }
        for (int i = 0; i < recipientsBCC.length; ++i, ++allIndex) {
            allRecipients[allIndex] = recipientsBCC[i];
        }

        transport.sendMessage(message, allRecipients);


        transport.close();


        //  InternetAddress notifyAddress = new InternetAddress("mntg@cs.umn.edu", "Admin MailList"); //here we set our email alias and the desired display name
        //sendEmail(mntgAddress[0], notifyAddress,                "Traffic request #" + id + " has just been submitted by user " + email + ".", email);

        /*
         * InternetAddress notifyAddress3 = new
         * InternetAddress("mokbel@cs.umn.edu", "Mohamed Mokbel"); //here we set
         * our email alias and the desired display name
         * sendEmail(mntgAddress[0], notifyAddress3, "Traffic request #" + id +
         * " has just been submitted by user " + email + ".", email);
         *
         */



    }

    public static void sendEmail(InternetAddress from, InternetAddress to, String content, String requester)
            throws MessagingException, UnsupportedEncodingException {

        Properties props = new Properties();

        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtps.host", SMTP_HOST_NAME);
        props.put("mail.smtps.auth", "true");

        Session mailSession = Session.getDefaultInstance(props);
        mailSession.setDebug(false);
        Transport transport = mailSession.getTransport();

        MimeMessage message = new MimeMessage(mailSession);
        message.setSubject("MN Traffic Generation is used by " + requester);
        message.setContent(content, "text/plain");

        message.addRecipient(Message.RecipientType.TO, to);

        InternetAddress[] replyto = new InternetAddress[1];
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
