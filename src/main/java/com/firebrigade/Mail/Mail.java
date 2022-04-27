package com.firebrigade.Mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;


public class Mail {
    final String fromEmail = "";
    final String password = "";

    public void sendTLSMail(String toEmail, String fromName, String body ) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.strato.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.strato.com");

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);
        session.setDebug(true);

        sendEmail(session, toEmail,"Offline due to Fire Service", fromName + body);
        }

        private void sendEmail(Session session, String toEmail, String subject, String body){
            try
            {
                MimeMessage msg = new MimeMessage(session);
                msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
                msg.addHeader("format", "flowed");
                msg.addHeader("Content-Transfer-Encoding", "8bit");
                msg.setFrom(new InternetAddress("thomas@heinrichs.services", "NoReply-FireBrigade-Notification"));
                msg.setReplyTo(InternetAddress.parse("no_reply@firebrigade-notifications.com", false));
                msg.setSubject(subject, "UTF-8");
                msg.setText(body, "UTF-8");
                msg.setSentDate(new Date());
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
                Transport.send(msg);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}



