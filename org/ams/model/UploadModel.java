package org.ams.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import jxl.Sheet;

import org.ams.db.HibernateUtil;
import org.ams.db.Module;
import org.ams.db.Record;
import org.ams.db.Student;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;


public class UploadModel extends Observable {
	
  
   private static Logger log = Logger.getLogger(UploadModel.class.getName());
 
   
   public void uploadXLS(Module m,Sheet s,Date date){
	  ArrayList<Record> rs = new ArrayList<Record>();
	  System.out.println("size    "+s.getRows());
	  for(int i=1;i<s.getRows();i++){
		  System.out.println(s.getRow(i)[0].getContents());
		  Student student = this.getStudentByRN(s.getRow(i)[0].getContents());
		  if(student!=null){
			  Record record =  new Record();
			  record.setDate(date);
			  record.setStudent(student);
			  record.setModule(m);
			  String att = s.getRow(i)[2].getContents();
			  if(att.equals("0"))
				  record.setAttend(false);
			  else if(att.equals("1"))
			       record.setAttend(true);
			  else
			       record.setAttend(Boolean.parseBoolean(s.getRow(i)[2].getContents()));
			  rs.add(record);
		  }
		  
	  }
	  this.upLaadRecords(rs);
   }
    
	private void upLaadRecords(List<Record> records) {

		ResultNotification rn = new ResultNotification();
	   
		try {
			Session s = HibernateUtil.getSession();
			
			Transaction tx = s.beginTransaction();
		//	s.clear();
			for (Record r : records) {
				r.getStudent().getRecords().add(r);
				r.getModule().getRecords().add(r);
		        
				s.saveOrUpdate(r.getStudent());
				
				s.saveOrUpdate(r.getModule());
				s.saveOrUpdate(r);
				System.out.println("save" + r.getRid());
			}
			tx.commit();
			

			rn.isSuccess = true;
			log.info("upload records: " + records.size()  + " success");
		} catch (Exception e) {
		//	e.printStackTrace();
			log.log(Level.WARN, "trouble UPLOAD: ",e);
			HibernateUtil.getSession().getTransaction().rollback();
			// TODO Auto-generated catch block
			rn.isSuccess = false;
			
		} finally {
			this.setChanged();
			this.notifyObservers(rn);

		}

	}
	
	private Student getStudentByRN(String rn) {
		
		String hql = "from Student s WHERE s.rn = :rn";
		Transaction tx = HibernateUtil.getSession().beginTransaction();
		Query query = HibernateUtil.getSession().createQuery(hql);
		query.setParameter("rn", rn);

		List<Student> students = query.list();
		// report.display(records ); do not do it this way
		tx.commit();

		if (!students.isEmpty()){
			log.info("find student "+ rn+ " success");
			return students.get(0);
		}
		else{
			log.info("Didn't find student "+ rn);
			return null;
		}
	}

 
      
}	


