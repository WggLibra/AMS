package org.ams.view;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import org.ams.db.HibernateUtil;
import org.ams.db.User;

public class MainUI extends JFrame implements ActionListener{

	private User user;
	
	public MainUI(User u){
		this.user = u;
		setJMenuBar(createMenuBar());
		launchUI();
	}
	
	private JMenuBar createMenuBar(){
		JMenuBar menubar = new JMenuBar();
		JMenu menu;
		menu = new JMenu("User");
		JMenuItem logout = new JMenuItem("Logout");
		logout.addActionListener(this);
		menu.add(logout);
		menubar.add(menu);
		return menubar;
	}
	
	private void launchUI(){
		if(user.getPerm() == User.ADMINISTRATOR){
			launchAdminFrame();
		}
		else if(user.getPerm() == User.PROGRAM_LEADER | user.getPerm() == User.LECTURER){
			launchFrame();
		}
	}
	
	private void launchAdminFrame(){
		setTitle("Attendance Monitoring System " + "Wellcome -- "+user.getAname());
		
		final JTabbedPane tab;
		Dimension dem=Toolkit.getDefaultToolkit().getScreenSize();
		  int sHeight=dem.height;
		  int sWidth=dem.width;
		  int fHeight=this.getHeight();
		  int fWidth=this.getWidth();
		  this.setLocation((sWidth-fWidth)/4, (sHeight-fHeight)/6);
		  
		tab = new JTabbedPane(JTabbedPane.TOP);
		tab.addTab("Register", new Register());
		tab.addTab("Report",new Report(user));
		tab.addTab("Students ",new StudentManger());
		tab.addTab("Modules", new  ModuleManager());
		tab.addTab("Programmes", new ProgramManager());
		tab.addTab("Module Manager", new AddStudentToModule());
		tab.addTab("Programme Manager", new AddModuleToProgram());
		tab.addTab("Student Manager", new AddStudentToProgram());
		tab.addTab("User Manager", new AddModuleToUser());
		tab.addTab("Users Manager", new UserToProgram());
		tab.addTab("Records", new RecordsManagement(user));
		tab.addTab("Upload Att.Sheet", new UploadManager(user));
		add(tab);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1300, 700));
		pack();
		setVisible(true);
	}
	
	private void launchFrame(){
		setTitle("Attendance Monitoring System -- " + user.getAname());
	//	setResizable(false);
		final JTabbedPane tab;
		Dimension dem=Toolkit.getDefaultToolkit().getScreenSize();
		  int sHeight=dem.height;
		  int sWidth=dem.width;
		  int fHeight=this.getHeight();
		  int fWidth=this.getWidth();
		  this.setLocation((sWidth-fWidth)/4, (sHeight-fHeight)/6);

		tab = new JTabbedPane(JTabbedPane.TOP);
		tab.addTab("Attendance report",new Report(user));
		tab.addTab("Remove records", new RecordsManagement(user));
		tab.addTab("Upload Att.Sheet", new UploadManager(user));
		add(tab);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1200, 700));
		pack();
		setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Logout")){
			HibernateUtil.shutDown();
			HibernateUtil.initHibernate();
			new Login();
			this.dispose();
		}
	}
	
}
