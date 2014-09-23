package org.ams.model;

import java.util.List;
import java.util.Observable;

import org.ams.db.HibernateUtil;
import org.ams.db.User;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Transaction;

public class LoginModel extends Observable{
	 private static Logger log = Logger.getLogger(AdminModel.class.getName());
	

	public User verifyUser(String username,String password){
		
		UserVerifyNotification un = new UserVerifyNotification();
		String hql = "from User s WHERE s.name = :name";
		Transaction tx = HibernateUtil.getSession().beginTransaction();
		Query query = HibernateUtil.getSession().createQuery(hql);
		query.setParameter("name", username);

		List<User> us = query.list();
		// report.display(records ); do not do it this way
		tx.commit();
        User u = null;
		if (!us.isEmpty()){
			u = us.get(0);
			
			   un.setUser(u.getPwd().equals(password)?u:null);
		        if(un.getUser()==null){
		        	log.info("password wrong");
		        	un.setResult(UserVerifyNotification.PASSWORD_UNCORRECT);
		        }
		        else{
		        	log.info("log in success");
		        }
         }
		else{
			un.setResult(UserVerifyNotification.USER_NOT_EXIST);
			log.info("username dosen't exsist");
		}
		
    	this.setChanged();
		this.notifyObservers(un);
		return un.getUser();
      }

}
