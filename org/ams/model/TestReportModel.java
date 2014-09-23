package org.ams.model;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.ams.db.HibernateUtil;
import org.ams.db.Module;
import org.ams.db.Program;
import org.ams.db.Record;
import org.ams.db.Student;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestReportModel {
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private List<Record> rs = new  ArrayList<Record>();
	private Date since ;
	private Date to ;
	private Date d ;
	@Before          
	 public void setUp() throws Exception {
     HibernateUtil.initHibernate(); 
     since = df.parse("1090-01-01");
	 to = df.parse("1090-10-01");
	 d = df.parse("1090-03-01");
     Student s1 = new Student();
		Student s2 = new Student();
		s1.setName("xxx");
		s2.setName("yyy");
		s1.setRn("@00287084");
		s2.setRn("@00287083");
		Module m1 = new Module();
		Program p1 = new Program();
		p1.setName("cs");
		m1.setName("agile");
		m1.setCode("abcd");
		HashSet<Program> pl = new HashSet<Program>();
		HashSet<Module> ml = new HashSet<Module>();
		pl.add(p1);
		m1.setPrograms(pl);
		ml.add(m1);
		p1.setModules(ml);
		s1.setModules(ml);
		s2.setModules(ml);
		Record r1 = new Record();
		r1.setStudent(s1);
		r1.setModule(m1);
		r1.setAttend(false);
		r1.setDate(d);
		Record r2 = new Record();
		r2.setStudent(s2);
		r2.setModule(m1);
		r2.setAttend(true);
		r2.setDate(d);
	//	ArrayList<Record> rl = new ArrayList<Record>();
		rs.add(r1);
		rs.add(r2);
	AdminModel am = new AdminModel();
     am.saveEntity(s1);
     am.saveEntity(s2);
     
   
     am.upLaadRecords(rs);
	 }
	 @After
	 public void tearDown() throws Exception {
		 for(Record r:rs){
		Session s =  HibernateUtil.getSession();
		Transaction tx =   s.beginTransaction();
		//.beginTransaction();
		 s.delete(r);
		 tx.commit();
		 }
		 HibernateUtil.shutDown();
	 }
	 
	 @Test
	 public void testQuery() throws Exception{
		 ReportModel rm = new ReportModel();
		 List<Record> rl = rm.query(since, to);
		 assertEquals(rl.size(),rs.size() );
	 }

}

