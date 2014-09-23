package org.ams.model;

import java.io.FileInputStream;
import java.util.Observable;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.ams.db.Student;
import org.jfree.util.Log;

public class EmailModel extends Observable{
	
	private String username;
	private String host;
	private String port;
	private String password;
	private String subject;
	private String contenet;
	
	
	
	public EmailModel() throws Exception{
		  Properties prop = new Properties();  
	      FileInputStream fis =   
	        new FileInputStream("src/mail.properties");  
	      prop.load(fis);  
	      this.username = prop.getProperty("username");
	      this.host = prop.getProperty("host");
	      this.port = prop.getProperty("port");
	      this.password = prop.getProperty("password");
	      this.subject = prop.getProperty("subject");
	      this.contenet= prop.getProperty("content");
	}
	
	public boolean sendEmail(Student s,String prefix){
		ResultNotification sn = new ResultNotification();
		sn.be = s;
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", this.host);
		props.put("mail.smtp.port", this.port);
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(this.username));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(s.getEmail()));
			message.setSubject(this.subject);
			message.setText(prefix+this.contenet);
 
			Transport.send(message);
 
			Log.info("mail sent to: "+s.getEmail());
			sn.isSuccess = true;
 
		} catch (MessagingException e) {
			sn.isSuccess = false;
		}
		finally{
			this.setChanged();
			this.notifyObservers(sn);
		}
		return sn.isSuccess;
	}

}
