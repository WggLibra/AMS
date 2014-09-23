package org.ams.model;

import static org.junit.Assert.assertEquals;

import org.ams.db.HibernateUtil;
import org.ams.db.Student;
import org.ams.db.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLoginModel {
	
	 AdminModel am = new AdminModel();
	 LoginModel lm = new LoginModel();
	 User u ;
	 
	 @Before          
	 public void setUp() throws Exception {
		 HibernateUtil.initHibernate(); 
	   u = new User("tj","sga706","123456",1);
		 am.saveEntity(u); 
	 }
	
	 @After
	 public void tearDown() throws Exception {
		 am.deleteUser(u);
	 }
	 
	 @Test
	 public void testSaveEntity() throws Exception{
		

		 
		 assertEquals(lm.verifyUser("sga706", "123456"),u);
		 
		 
	 }

}
