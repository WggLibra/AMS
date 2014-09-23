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
import org.ams.db.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAdminModel {
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private List<Record> rs = new  ArrayList<Record>();
	ArrayList<Program> pl = new ArrayList<Program>();
	ArrayList<Module> ml = new ArrayList<Module>();
	ArrayList<Student> sl = new ArrayList<Student>();
	ArrayList<User> ul = new ArrayList<User>();
	AdminModel am = new AdminModel();

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
		Student s3 = new Student();
		Module m1 = new Module();
		Module m2 = new Module();
		Program p1 = new Program();
		User u = new User("ltj","sga706xx","911911",0);
		sl.add(s1);
		sl.add(s2);
		sl.add(s3);
		ml.add(m1);
		ml.add(m2);
		pl.add(p1);
		ul.add(u);
		
		
		s1.setName("xxx");
		s2.setName("yyy");
		s3.setName("zzz");
		s1.setRn("@001870118");
		s2.setRn("@001870119");
		s3.setRn("@001870110");
		p1.setName("csx");
		m1.setName("agile");
		m1.setCode("xxxxx");
		m2.setName("java");
		m2.setCode("yyyyy");
		
	
		HashSet<Student> msl1 = new HashSet<Student>();
		msl1.add(s1);
		msl1.add(s2);
		HashSet<Student> msl2 = new HashSet<Student>();
		msl2.add(s1);
		msl2.add(s3);		
		
		m1.setStudents(msl1);
		m2.setStudents(msl2);
		
		
		
		HashSet<Program> ps = new HashSet<Program>();
		ps.addAll(pl);
		m1.setPrograms(ps);
		m2.setPrograms(ps);
		
		HashSet<User> us = new HashSet<User>();
		us.addAll(ul);
		m1.setUsers(us);
		
		
		s3.setProgram(p1);
		s2.setProgram(p1);
		s1.setProgram(p1);
		
		u.setProgram(p1);
		
		
		//p1.setModules(ml);
		//s1.setModules(ml);
		//s2.setModules(ml);
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
		Record r3 = new Record();
		r3.setStudent(s3);
		r3.setModule(m2);
		r3.setAttend(true);
		r3.setDate(d);
	//	ArrayList<Record> rl = new ArrayList<Record>();
		rs.add(r1);
		rs.add(r2);
		rs.add(r3);
	
	am.saveEntity(p1);
	am.saveEntity(u);
	am.saveEntity(s1);
    am.saveEntity(s2);
    am.saveEntity(s3);
	 am.saveEntity(m1);
	 am.saveEntity(m2);
	 
     
     
   
     am.upLaadRecords(rs);
	 }
	 
	@After
	 public void tearDown() throws Exception {
		 for(Record r:rs){
		am.deleteEntity(r);
		 
		 }
		 for(Module m:ml){
			 
				am.deleteModule(m);
				
				 }
		 for(Student s:sl){
			 
				am.deleteStudent(s);
				
				 }
		 for(Program p:pl){
			// System.out.println("delete " +p);
				am.deleteEntity(p);
				
				 }
		 for(User u:ul){
			 System.out.println("delete " +u);
				am.deleteUser(u);
				
				 }
		
		 
	 }
	 
	 @Test
	 public void testDeleteEntity() throws Exception{
		 
	int	 count = am.getAllRecords().size();
		 for(Record r:rs){
			 System.out.println("deleting   "+r);
				am.deleteEntity(r);
				
				 }
		 List<Record> rss = am.getAllRecords();
		
		 assertEquals(rss.size(),count-rs.size());
		 rs.clear();
		 count =  am.getAllPrograms().size();
		 for(Program p:pl){
			 
				am.deleteEntity(p);
				System.out.println("deleting   "+p);
				
				 }
		 
		 List<Program> ps = am.getAllPrograms();
	
		
		 assertEquals(ps.size(),count-pl.size());
		
		 pl.clear();
	
		
		 
	 }
	 
	 @Test
	 public void testSaveEntity() throws Exception{
		 Student s = new Student("tj","00287077","123@345.com");
		 am.saveEntity(s);
		 Student s2 = am.getStudentByRN("00287077");
		 assertEquals(s,s2);
		 am.deleteStudent(s2);
		 
	 }
	 @Test
	 public void testDeleteUser() throws Exception{
		int count = am.getAllUsers().size();
		 for(User u:ul){
				am.deleteEntity(u);
				
				 }
		 List<User> us = am.getAllUsers();
		
		 assertEquals(us.size(),count-ul.size());
		 ul.clear();
		 
	 }
	 @Test
	 public void testDeleteStudent() throws Exception{
		int count = am.getAllStudents().size();
		 for(Student s:sl){
				am.deleteEntity(s);
				
				 }
		 List<Student> ss = am.getAllStudents();
		
		 assertEquals(ss.size(),count-sl.size());
		 sl.clear();
		 
	 }
	 
	 @Test
	 public void testDeleteModule() throws Exception{
		int count = am.getAllModules().size();
		 for(Module m:ml){
				am.deleteEntity(m);
				
				 }
		 List<Module> ms = am.getAllModules();
		
		 assertEquals(ms.size(),count-ml.size());
		 ml.clear();
		 
	 }
	
	 @Test
	 public void testUploadRecords() throws Exception{
			Record r = new Record();
			r.setStudent(sl.get(0));
			r.setModule(ml.get(0));
			r.setAttend(true);
			r.setDate(d);
			am.saveEntity(r);
			 assertEquals(am.getAllRecords().contains(r),true);
			 am.deleteEntity(r);
		 
	 }
	
	
	 
	 
	
	 
/*     @Test
	 public void testQueryByModule() throws Exception{
		 ReportModel rm = new ReportModel();
		 List<Record> rl = rm.queryByModule(since, to,ml.get(0));
		 assertEquals(rl.size(),2);
		 rl = rm.queryByModule(since, to, ml.get(1));
		 System.out.println(rl.size());
		 assertEquals(rl.size(),1);
	 }
     @Test
	 public void testQueryByProgram() throws Exception{
	    ReportModel rm = new ReportModel();
		 List<Record> rl = rm.queryByProgram(since,to,pl.get(0));
		 assertEquals(rl.size(),rs.size());
		 
	 }*/

}
