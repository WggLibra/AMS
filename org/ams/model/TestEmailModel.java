package org.ams.model;


import org.ams.db.Student;
import org.junit.Test;

public class TestEmailModel {
	 @Test
	 public void testSendMail() throws Exception{
		 EmailModel em = new EmailModel();
		 Student s = new Student("tj","00287077","mayokaze@me.com");
		 em.sendEmail(s, "Module Agile: ");
		 
	 }
}
