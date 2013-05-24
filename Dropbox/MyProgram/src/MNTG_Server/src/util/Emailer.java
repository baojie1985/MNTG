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

    
    public static String downloadRoad(TrafficRequest tr){
        String result=null;
        if(tr.getMaprequestStatus()==true){
            result = "Your requested road network file can be downloaded via "
                    + "http://"+ ConnectionProvider.HOST_NAME+ "/downloads/"
                    + tr.getRequestId() + "-Nodes.txt.\n"
                    +"and "
                    +"http://"+ ConnectionProvider.HOST_NAME+ "/downloads/"
                    + tr.getRequestId() + "-Edges.txt.\n";
        }
        return result;
    }
    public static void sendEmail(TrafficRequest trafficRequest, int result)
            throws MessagingException, UnsupportedEncodingException {

        BufferedWriter br = null;
        try {
            br = new BufferedWriter(new FileWriter(new File("/home/yackel/TrafficGenerator/my_mntg_log" + trafficRequest.getRequestId() + ".txt")));
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
         File file = new File("/home/yackel/public_html/app/webroot/downloads/"+trafficRequest.getRequestId()+".txt");
         long filesize = file.length();
        MimeMessage message = new MimeMessage(mailSession);
        message.setSubject("Traffic Generation Request #" + trafficRequest.getRequestId() + " has finished being processed.");
     /*
        String roadnetworks=null;
        if(trafficRequest.getName().contains("Brinkhoff")){
            roadnetworks = "Edge File: http://"
                    + ConnectionProvider.HOST_NAME
                    + "/downloads/"
                    + trafficRequest.getRequestId() + ".edge. \n"
                    + "Nodes File: http://"
                    + ConnectionProvider.HOST_NAME
                    + "/downloads/"
                    + trafficRequest.getRequestId() + ".node. \n"
                    + "Format Descriptions: http://iapg.jade-hs.de/personen/brinkhoff/generator/FormatNetworkFiles.pdf";
        } else if (trafficRequest.getName().equals("BerlinMod")) {

        }*/
        
        if (result == 1) {
            message.setContent(
                    "Hi, Sir/Madam\n\n"
                    + "Your traffic generation, "
                    + trafficRequest.getName()
                    + "with the following parameters:\n"
                    + trafficRequest.toString()
                    + " \n has finished being generated.\n\n  You can download the benchmark at http://"
                    + ConnectionProvider.HOST_NAME
                    + "/downloads/"
                    + trafficRequest.getRequestId() + ".txt."
                    +" The estimated size of the file is "
                    +((int)filesize/1024)+"KB. The link may be expired in one week.\n"                    
                    +downloadRoad(trafficRequest)
                    //+roadnetworks
                    + ".\n\n You can also visualize the benchmark at http://"
                    + ConnectionProvider.HOST_NAME
                    + "/traffic_results/view_results/"
                    + trafficRequest.getRequestId() + ". Please be adviced, the visualization maybe very slow or fail, when the data file is large\r\n\n"
                    + "If you have any comments, please leave your feedback on http://mntg.cs.umn.edu or email us at mntg@cs.umn.edu.\r\n\r\n"
                    + "Thanks for using our traffic generator,\r\n"
                    + "UMN DMLab Team", "text/plain");
        } else {
            message.setContent( 
                    "Hi, Sir/Madam\n\n"
                    +"I'm sorry to inform you that your traffic generation, "
                    + trafficRequest.getName()
                    +"with the following parameters:\n"
                    + trafficRequest.toString()
                    + "is failed. \n"
                    +"Most likely, you may specifiy a very large parameter in your request.\n"
                    +"We have to be limited by the the underlying generator. Here are few points that you can make use of: \n 1) Brinkhoff generator is mostly limited by the simulation time. You can try increasing the number of objects to simulate, and reduce the simulation time significantly. This will give you much more stable result.\n"
                    +"2) If you want to select X moving objects, and X is larger than what Brinkhoff can support, you may want to generate two request, each of size X/2. For the second request, you may want to scan the generated file and change the IDs. Then, you can combine the two files.\n"
                    + "3) If you want to generate Y timesteps, and Y is larger than what Brinkhoff can support, you may want to generate only Y/2 timesteps. Then, scan the output file, and write a mall script that will add a new point between each two, using a simple interpolation function. In this case, you end up having the Y steps you need.\n"
                    +"These are functionality that we aim to support later in our MNTG. For now, MNTG is basically a wrapper around Brinkhoff, making it much easier to use, but we are still limited by its capabilities..\r\n\r\n"
                    +"If you still get problems in generating the traffic, please contact mntg@cs.umn.edu.\n"
                    + "Thanks for using our traffic generator,\r\n"
                    + "UMN DMLab Team",
                    "text/plain");
        }

        try {
            br.write("adding receiptant\n");
            br.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InternetAddress[] mntgAddress = new InternetAddress[1];
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
       // sendEmail(mntgAddress[0], notifyAddress,
          //      "Minnesota Traffic Generator has been used today and the results for request #" + trafficRequest.getRequestId() + " has been submitted to the user on " + trafficRequest.getEmail() + ".", trafficRequest.getEmail());
        //InternetAddress notifyAddress2 = new InternetAddress("amr@cs.umn.edu", "Amr Magdy"); //here we set our email alias and the desired display name
        //sendEmail(mntgAddress[0],notifyAddress2,
        //		"Minnesota Traffic Generator has been used today and the results for request #"+trafficRequest.getRequestId()+" has been submitted to the user.");
        
        /*
        InternetAddress notifyAddress3 = new InternetAddress("mokbel@cs.umn.edu", "Mohamed Mokbel"); //here we set our email alias and the desired display name
        sendEmail(mntgAddress[0], notifyAddress3,
                "Minnesota Traffic Generator has been used today and the results for request #" + trafficRequest.getRequestId() + " has been submitted to the user on " + trafficRequest.getEmail() + "." , trafficRequest.getEmail());
                * 
                */

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
        message.setSubject("MN Traffic Generation is completed for " + requester);
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
