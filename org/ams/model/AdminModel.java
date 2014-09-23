package org.ams.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import org.ams.db.BusinessEntity;
import org.ams.db.HibernateUtil;
import org.ams.db.Module;
import org.ams.db.Program;
import org.ams.db.Record;
import org.ams.db.Student;
import org.ams.db.User;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class AdminModel extends Observable {
	 private static Logger log = Logger.getLogger(AdminModel.class.getName());

	public void deleteEntity(BusinessEntity be) {
		ResultNotification dn = new ResultNotification();
		dn.be = be;
		try {
			Session s = HibernateUtil.getSession();
			Transaction tx = s.beginTransaction();

			s.delete(be);
			 
			tx.commit();
			dn.isSuccess = true;
			log.info("delete success");
		} catch (Exception e) {
			HibernateUtil.getSession().getTransaction().rollback();
			// TODO Auto-generated catch block
			dn.isSuccess = false;
			log.log(Level.WARN, "trouble DELETE",e);
		} finally {
			this.setChanged();
			this.notifyObservers(dn);

		}
	}

	public void saveEntity(BusinessEntity be) {
		ResultNotification rn = new ResultNotification();
		rn.be = be;
		try {
			Session s = HibernateUtil.getSession();
			Transaction tx = s.beginTransaction();

			s.saveOrUpdate(be);
		//	 System.out.println("save"+be);
			tx.commit();
			rn.isSuccess = true;
			log.info("save success");
		} catch (Exception e) {
		//	e.printStackTrace();
			HibernateUtil.getSession().getTransaction().rollback();
			// TODO Auto-generated catch block
			rn.isSuccess = false;
			
			log.log(Level.WARN, "trouble SAVE",e);
		} finally {
			this.setChanged();
			this.notifyObservers(rn);

		}
	}

	public void deleteUser(User u) {
		ResultNotification dn = new ResultNotification();
		dn.be = u;
		try {
			Session s = HibernateUtil.getSession();
			Transaction tx = s.beginTransaction();
			u.setProgram(null);
			s.saveOrUpdate(u);
	        for(Module m: u.getModules())
	        {
	        	m.getUsers().remove(u);
	        	s.saveOrUpdate(m);
	        }
			s.delete(u);
			// System.out.println("save"+user.getName());
			tx.commit();
			dn.isSuccess = true;
			log.info("delete user " + u.getName()  + " success");
		} catch (Exception e) {
			//e.printStackTrace();
			HibernateUtil.getSession().getTransaction().rollback();
			// TODO Auto-generated catch block
			dn.isSuccess = false;
			log.log(Level.WARN, "trouble DELETE user " + u.getName(),e);
		} finally {
			this.setChanged();
			this.notifyObservers(dn);

		}
	}

	public void deleteStudent(Student student) {
		ResultNotification dn = new ResultNotification();
		dn.be = student;
		try {
			Session s = HibernateUtil.getSession();
			Transaction tx = s.beginTransaction();
			student.setProgram(null);
			s.saveOrUpdate(student);
		     for(Module m: student.getModules())
		        {
		        	m.getStudents().remove(student);
		        	s.saveOrUpdate(m);
		        } 
		    
			s.delete(student);
			// System.out.println("save"+user.getName());
			tx.commit();
			dn.isSuccess = true;
			log.info("delete student " + student.getName()  + " success");
			
		} catch (Exception e) {
		//	e.printStackTrace();
			HibernateUtil.getSession().getTransaction().rollback();
			// TODO Auto-generated catch block
			dn.isSuccess = false;
			log.log(Level.WARN, "trouble DELETE student " + student.getName(),e);
		} finally {
			this.setChanged();
			this.notifyObservers(dn);

		}
	}
	
	public void deleteProgram(Program p) {
		
		ResultNotification dn = new ResultNotification();
		dn.be = p;
		try {

			
			Session s = HibernateUtil.getSession();
			Transaction tx = s.beginTransaction();
            for(Module m:  p.getModules()){
            	m.getPrograms().remove(p);
            	s.saveOrUpdate(m);	
			}
			
            for(Student student:p.getStudents()){
            	student.setProgram(null);
            	s.saveOrUpdate(student);
			}
            for(User u:p.getUsers()){
            	u.setProgram(null);
            	s.saveOrUpdate(u);
            }
            s.delete(p);
			// System.out.println("save"+user.getName());
			tx.commit();
			dn.isSuccess = true;
			log.info("delete Program " + p.getName()  + " success");
		} catch (Exception e) {
		//	e.printStackTrace();
			HibernateUtil.getSession().getTransaction().rollback();
			// TODO Auto-generated catch block
			dn.isSuccess = false;
			log.log(Level.WARN, "trouble DELETE program " + p.getName(),e);
		} finally {
			this.setChanged();
			this.notifyObservers(dn);

		}
	}

	public void deleteModule(Module m) {
	
		ResultNotification dn = new ResultNotification();
		dn.be = m;
		try {

		//	m.getUsers().clear();
		//	m.getStudents().clear();
		//	m.getPrograms().clear();
			Session s = HibernateUtil.getSession();
			Transaction tx = s.beginTransaction();
			
			s.delete(m);
			// System.out.println("save"+user.getName());
			tx.commit();
			dn.isSuccess = true;
			log.info("delete module " + m.getName()  + " success");
		} catch (Exception e) {
		//	e.printStackTrace();
			HibernateUtil.getSession().getTransaction().rollback();
			// TODO Auto-generated catch block
			dn.isSuccess = false;
			log.log(Level.WARN, "trouble DELETE module " + m.getName(),e);
		} finally {
			this.setChanged();
			this.notifyObservers(dn);

		}
	}

	public Student getStudentByRN(String rn) {
		GetSingleNotification gn = new GetSingleNotification();
		String hql = "from Student s WHERE s.rn = :rn";
		Transaction tx = HibernateUtil.getSession().beginTransaction();
		Query query = HibernateUtil.getSession().createQuery(hql);
		query.setParameter("rn", rn);

		List<Student> students = query.list();
		// report.display(records ); do not do it this way
		tx.commit();

		if (!students.isEmpty()){
			gn.setEntity(students.get(0));
			log.info("find student "+ rn+ " success");
		}
		else
			log.info("Didn't find student "+ rn);
		this.setChanged();
		this.notifyObservers(gn);
		return (Student) gn.getEntity();
	}

	public void upLaadRecords(List<Record> records) {

		ResultNotification rn = new ResultNotification();
		try {
			Session s = HibernateUtil.getSession();
			Transaction tx = s.beginTransaction();
			for (Record r : records) {
				s.saveOrUpdate(r);
				//System.out.println("save" + r.getRid());
			}
			tx.commit();

			rn.isSuccess = true;
			log.info("upload records: " + records.size()  + " success");
		} catch (Exception e) {
		//	e.printStackTrace();
			HibernateUtil.getSession().getTransaction().rollback();
			// TODO Auto-generated catch block
			rn.isSuccess = false;
			log.log(Level.WARN, "trouble UPLOAD: ",e);
		} finally {
			this.setChanged();
			this.notifyObservers(rn);

		}

	}

	public List<Student> getAllStudents() {

		GetListNotification gn = new GetListNotification();
		String hql = "from Student";
		Transaction tx = HibernateUtil.getSession().beginTransaction();
		Query query = HibernateUtil.getSession().createQuery(hql);
        List<Student> students = query.list();
        tx.commit();
		gn.setEntities(students);
		this.setChanged();
		this.notifyObservers(gn);
		log.info("find students "+ students.size());
		return students;
	}

	public List<User> getAllUsers() {
		GetListNotification gn = new GetListNotification();
		String hql = "from User";
		Transaction tx = HibernateUtil.getSession().beginTransaction();
		Query query = HibernateUtil.getSession().createQuery(hql);
        List<User> users = query.list();
        tx.commit();
		gn.setEntities(users);
		this.setChanged();
		this.notifyObservers(gn);
		log.info("find users "+ users.size());
		return users;
	}

	public List<Module> getAllModules() {
		GetListNotification gn = new GetListNotification();
		String hql = "from Module";
		Transaction tx = HibernateUtil.getSession().beginTransaction();
		Query query = HibernateUtil.getSession().createQuery(hql);
        List<Module> modules = query.list();
        tx.commit();
		gn.setEntities(modules);
		this.setChanged();
		this.notifyObservers(gn);
		log.info("find modules "+ modules.size());
        return modules;
	}

	public List<Program> getAllPrograms() {

		GetListNotification gn = new GetListNotification();
		String hql = "from Program";
		Transaction tx = HibernateUtil.getSession().beginTransaction();
		Query query = HibernateUtil.getSession().createQuery(hql);
        List<Program> programs = query.list();
        tx.commit();
		gn.setEntities(programs);
		this.setChanged();
		this.notifyObservers(gn);
		log.info("find programs "+ programs.size());
        return programs;
	}
	
	public List<Record> getAllRecords() {

		GetListNotification gn = new GetListNotification();
		String hql = "from Record";
		Transaction tx = HibernateUtil.getSession().beginTransaction();
		Query query = HibernateUtil.getSession().createQuery(hql);
        List<Record> rs = query.list();
        tx.commit();
		gn.setEntities(rs);
		this.setChanged();
		this.notifyObservers(gn);
		log.info("find records "+ rs.size());
        return rs;
	}
	
	public  List<Record> getRecordsBetween(Date since,Date to){
		
		GetListNotification gn = new GetListNotification();
        
		String hql = "from Record r WHERE r.date >= :since AND r.date <= :to";
		Transaction tx = HibernateUtil.getSession().beginTransaction();

		Query query =HibernateUtil.getSession().createQuery(hql);

		query.setParameter("since", since);
		query.setParameter("to", to);
        List<Record> records = query.list();
      
        tx.commit();
        gn.setEntities(records);
       
		this.setChanged();
		this.notifyObservers(gn);
		log.info("find records "+ records.size());
        return records;
   }
	
	public  List<Record> getRecordsFromModule(Date since,Date to ,Module module){
		
		GetListNotification gn = new GetListNotification();
        
		String hql = "from Record r WHERE r.date >= :since AND r.date <= :to AND r.module = :module";
		Transaction tx = HibernateUtil.getSession().beginTransaction();

		Query query =HibernateUtil.getSession().createQuery(hql);

		query.setParameter("since", since);
		query.setParameter("to", to);
		query.setParameter("module",module);
        List<Record> records = query.list();
      
        tx.commit();
        gn.setEntities(records);
       
		this.setChanged();
		this.notifyObservers(gn);
		log.info("find records "+ records.size());
        return records;
   }

}
