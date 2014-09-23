import javax.swing.SwingUtilities;

import org.ams.db.HibernateUtil;
import org.ams.view.Login;
import org.apache.log4j.BasicConfigurator;

public class Launcher {

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		HibernateUtil.initHibernate();
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new Login();
			}
		});	
	}
}	
		
		


