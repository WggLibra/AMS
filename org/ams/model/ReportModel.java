package org.ams.model;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import org.ams.db.HibernateUtil;
import org.ams.db.Module;
import org.ams.db.Program;
import org.ams.db.Record;
import org.ams.db.Student;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Transaction;





public class ReportModel extends Observable {

	private Date firstDayOfWeek;
	private AData[] weeklyData;

    private static Logger log = Logger.getLogger(ReportModel.class.getName());

	
	public ReportModel(){
		
	}
	
	public  List<Record> query(Date since,Date to){
		
        this.firstDayOfWeek = getFirstDay(since);
        
        long daterange = to.getTime() -  firstDayOfWeek.getTime();      
	    long weekTime = 1000*3600*24*7; 
	    weeklyData = new AData[(int) (daterange/weekTime)+1];
	    for(int i=0;i<weeklyData.length;i++)
	    	weeklyData[i] = new AData(0,0);
        
		String hql = "from Record r WHERE r.date >= :since AND r.date <= :to";
		Transaction tx = HibernateUtil.getSession().beginTransaction();

		Query query =HibernateUtil.getSession().createQuery(hql);

		query.setParameter("since", since);
		query.setParameter("to", to);
        List<Record> records = query.list();
       // report.display(records ); do not do it this way
        tx.commit();
        log.info("find records " + records.size());
        ReportNotification rn =  new ReportNotification();
        HashMap<Student,AData> items = calItems(records);
        rn.setItems(items);
        rn.setAbsenters(calAbsenters(items));
        rn.setDataset(createDatasetForChart());
		this.setChanged();
		this.notifyObservers(rn);
	    
        return records;
   }
   
	
	public  List<Record> queryByModule(Date since,Date to,Module m){
		
        this.firstDayOfWeek = getFirstDay(since);
        
        long daterange = to.getTime() -  firstDayOfWeek.getTime();      
	    long weekTime = 1000*3600*24*7; 
	    weeklyData = new AData[(int) (daterange/weekTime)+1];
	    for(int i=0;i<weeklyData.length;i++)
	    	weeklyData[i] = new AData(0,0);
        
		String hql = "from Record r WHERE r.date >= :since AND r.date <= :to AND r.module =:module";
		Transaction tx = HibernateUtil.getSession().beginTransaction();

		Query query =HibernateUtil.getSession().createQuery(hql);

		query.setParameter("since", since);
		query.setParameter("to", to);
		query.setParameter("module",m);
        List<Record> records = query.list();
       // report.display(records ); do not do it this way
        tx.commit();
        log.info("find records " + records.size());
        ReportNotification rn =  new ReportNotification();
        HashMap<Student,AData> items = calItems(records);
        rn.setItems(items);
        rn.setAbsenters(calAbsenters(items));
        rn.setDataset(createDatasetForChart());
		this.setChanged();
		this.notifyObservers(rn);
	    
        return records;
   }	
	
	
	public  List<Record> queryByProgram(Date since,Date to,Program p){
		
        this.firstDayOfWeek = getFirstDay(since);
        
        long daterange = to.getTime() -  firstDayOfWeek.getTime();      
	    long weekTime = 1000*3600*24*7; 
	    weeklyData = new AData[(int) (daterange/weekTime)+1];
	    for(int i=0;i<weeklyData.length;i++)
	    	weeklyData[i] = new AData(0,0);
        
		String hql = "from Record r WHERE r.date >= :since AND r.date <= :to AND r.student.program =:program";
		Transaction tx = HibernateUtil.getSession().beginTransaction();

		Query query =HibernateUtil.getSession().createQuery(hql);

		query.setParameter("since", since);
		query.setParameter("to", to);
		query.setParameter("program",p);
        List<Record> records = query.list();
       // report.display(records ); do not do it this way
        tx.commit();
        log.info("find records " + records.size());
        ReportNotification rn =  new ReportNotification();
        HashMap<Student,AData> items = calItems(records);
        rn.setItems(items);
        rn.setAbsenters(calAbsenters(items));
        rn.setDataset(createDatasetForChart());
		this.setChanged();
		this.notifyObservers(rn);
	    
        return records;
   }	
	
   private  HashMap<Student,AData> calItems(List<Record> records){
	   HashMap<Student,AData> items = new HashMap<Student,AData>();
		for(Record r : records){
			long daterange = r.getDate().getTime() -  firstDayOfWeek.getTime();
		    int week = (int) (daterange/(1000*3600*24*7));
		    weeklyData[week].contAll++;
		    if(r.isAttend())
		    	weeklyData[week].attendance++;
			if(items.containsKey(r.getStudent())){
				//System.out.println("contain: " + r.getStudent().getName());
				r.getDate();
				if(r.isAttend()){
					items.get(r.getStudent()).attendance++;
				}
				else{
					
				}
				items.get(r.getStudent()).contAll++;
			}
			else{
				
				if(r.isAttend()){
					items.put(r.getStudent(), new AData(1,1));
				}
				else{
					items.put(r.getStudent(), new AData(0,1));
				}
			}
		}
	  return items;
	}
   
   private ArrayList<Student> calAbsenters(HashMap<Student,AData> hm){
	   ArrayList<Student> as = new ArrayList<Student>();
	   Set<Student> keys = hm.keySet();
	   for(Student s:keys)
	   {
		//   System.out.println(s);
		   
		  AData a = hm.get(s);
		//  System.out.println(a.contAll);
		  if(a.attendance < (float)a.contAll/2.0)
			  as.add(s);
	   }	   
	   return as;
   }   

   public static int getDay(Date date) {
	   Calendar cal = Calendar.getInstance();
	   cal.setTime(date);
	   return cal.get(Calendar.DAY_OF_WEEK);
	  }
   
   
   public static Date getFirstDay(Date date) {
	   Calendar cal = Calendar.getInstance();
	   cal.setTime(date);
	   int weekday = cal.get(Calendar.DAY_OF_WEEK);
       cal.add(Calendar.DATE, -weekday + 1);
	  return cal.getTime();
	  }
   
   private float[] createDatasetForChart(){
	   float[] dataset = new float[weeklyData.length];
	   for(int i=0;i<weeklyData.length;i++){
		   if(weeklyData[i].contAll == 0){
			   dataset[i] = 0;
		   }else{
			   float percentage = (float)weeklyData[i].attendance/(float)weeklyData[i].contAll;
			   dataset[i] = percentage;
		   }
		   
	   }
	   return dataset;
   }
   
   public class AData {
		
		public AData(int a,int c){
			this.attendance = a;
			this.contAll = c;
		}
		
		public int attendance = 0;
		public int contAll = 0;
		
	}
}
