package org.ams.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.ams.db.Module;
import org.ams.db.Student;
import org.ams.model.AdminModel;
import org.ams.model.ResultNotification;

public class AddStudentToModule extends JPanel implements ActionListener, Observer{

	AdminModel am;
	DefaultListModel listModel_modules;
	DefaultListModel listModel_exist;
	DefaultListModel listModel_all;
	JList list_modules;
	JList list_exist;
	JList list_all;
	JLabel statusbar;
	List<Module> modules;
	List<Student> students;
	Student selectedStudent;
	Module selectedModule;
	
	public AddStudentToModule() {
		am = new AdminModel();
		am.addObserver(this);
		setLayout(new BorderLayout());
		add(createModuleManagePanel(),BorderLayout.CENTER);
	}

	//manage all the students' module(s)
	private JPanel createModuleManagePanel(){
		JPanel wholePanel = new JPanel();
		wholePanel.setLayout(new BorderLayout());
		JPanel moduleManagePanel = new JPanel();
		JPanel listContainer;
		JPanel buttonPane;
		JScrollPane scrollpane;
		moduleManagePanel.setBorder(BorderFactory.createTitledBorder("Module management for students"));
		moduleManagePanel.setLayout(new GridLayout(1,3,5,10));
		listModel_modules = new DefaultListModel();
		listModel_exist = new DefaultListModel();
		listModel_all = new DefaultListModel();
		list_modules = new JList(listModel_modules);
		list_exist = new JList(listModel_exist);
		list_all = new JList(listModel_all);
		list_modules.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!listModel_modules.isEmpty()){
					int selectedIndex = list_modules.getSelectedIndex();
					Module selectedModule = (Module)listModel_modules.get(selectedIndex);
					Set<Student> studentsInModule = selectedModule.getStudents();
					if(studentsInModule.isEmpty()){
						System.out.println("empty");
						listModel_exist.clear();
						list_exist.updateUI();
					}
					else{
						listModel_exist.clear();
						//System.out.println("cleared, now contains: ");
						for(Student s : studentsInModule){
						listModel_exist.addElement(s);
						//System.out.println(s);
						}
						//System.out.println("=============");
						list_exist.updateUI();
					}
				}
			}
		});
		scrollpane = new JScrollPane(list_modules);
		listContainer = new JPanel();
		listContainer.setLayout(new BorderLayout());
		listContainer.setBorder(BorderFactory.createTitledBorder("All modules"));
		listContainer.add(scrollpane, BorderLayout.CENTER);
		JButton button_remove = new JButton("delete module");
		button_remove.addActionListener(this);
		buttonPane = new JPanel(new FlowLayout());
		buttonPane.add(button_remove);
		listContainer.add(buttonPane, BorderLayout.PAGE_END);
		moduleManagePanel.add(listContainer);
		
		scrollpane = new JScrollPane(list_exist);
		listContainer = new JPanel();
		listContainer.setLayout(new BorderLayout());
		listContainer.setBorder(BorderFactory.createTitledBorder("Students in the module"));
		listContainer.add(scrollpane, BorderLayout.CENTER);
		JButton deleteFromModule = new JButton("remove");
		deleteFromModule.addActionListener(this);
		buttonPane = new JPanel(new FlowLayout());
		buttonPane.add(deleteFromModule);
		listContainer.add(buttonPane, BorderLayout.PAGE_END);
		moduleManagePanel.add(listContainer);
		
		scrollpane = new JScrollPane(list_all);
		listContainer = new JPanel();
		listContainer.setLayout(new BorderLayout());
		listContainer.setBorder(BorderFactory.createTitledBorder("All students"));
		listContainer.add(scrollpane, BorderLayout.CENTER);
		JButton button_addToModule = new JButton("add");
		button_addToModule.addActionListener(this);
		JButton button_removeStudent = new JButton("delete student");
		button_removeStudent.addActionListener(this);
		buttonPane = new JPanel(new FlowLayout());
		buttonPane.add(button_addToModule);
		buttonPane.add(button_removeStudent);
		listContainer.add(buttonPane, BorderLayout.PAGE_END);
		moduleManagePanel.add(listContainer);
		
		JPanel operationPane = new JPanel(new FlowLayout());
		JButton refresh = new JButton("Refresh All");
		refresh.addActionListener(this);
		operationPane.add(refresh);
		JPanel statuePane = new JPanel(new FlowLayout());
		statusbar = new JLabel("ready");
		statusbar.setHorizontalAlignment(JLabel.CENTER);
		statuePane.add(statusbar);
		
		wholePanel.add(moduleManagePanel,BorderLayout.CENTER);
		wholePanel.add(operationPane,BorderLayout.PAGE_START);
		wholePanel.add(statusbar,BorderLayout.PAGE_END);
		return wholePanel;
	}
	
	private void refreshAll(){
		new Thread(){

			@Override
			public void run() {
				
				modules = am.getAllModules();
				students = am.getAllStudents();
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						listModel_modules.clear();
						listModel_exist.clear();
						listModel_all.clear();
						try{
							for(Module m : modules){
								listModel_modules.addElement(m);
							}
							for(Student s : students){
								listModel_all.addElement(s);
							}
						}
						catch (Exception e) {
							statusbar.setText("Error, please try again");
						}
						statusbar.setText("Finish refreshing");
						updateUI();
					}
					
				});
				
			}
			
		}.start();
	}
	
	private void delete(){
		new Thread(){

			@Override
			public void run() {
				int[] selectedIndices = list_exist.getSelectedIndices();
				if(selectedIndices.length == 0){
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							statusbar.setText("Please select a student");
						}
					});
				}
				else{
					int moduleIndex = list_modules.getSelectedIndex();
					if(moduleIndex == -1){
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								statusbar.setText("Please select a module");
							}
						});
					}
					else{
						List<Student> studentsToRemove = new ArrayList<Student>();
						for(int i=0;i<selectedIndices.length;i++){
							Student s = (Student)listModel_exist.get(selectedIndices[i]);
							Module m = (Module)listModel_modules.get(moduleIndex);
							studentsToRemove.add(s);
							statusbar.setText("Removing " + s + " from " + m + " ...");
							s.getModules().remove(m);
							m.getStudents().remove(s);
							am.saveEntity(s);
						}
						for(Student rs: studentsToRemove){
							listModel_exist.removeElement(rs);
						}
					}
				}
			}
		}.start();
		
	}
	
	private void add(){
		new Thread(){

			@Override
			public void run() {
				int[] selectedIndices = list_all.getSelectedIndices();
				if(selectedIndices.length == 0){
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							statusbar.setText("Please select a student");
						}
					});
				}
				else{
					int moduleIndex = list_modules.getSelectedIndex();
					if(moduleIndex == -1){
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								statusbar.setText("Please select a module");
							}
						});
					}
					else{
						for(int i=0;i<selectedIndices.length;i++){
							Student s = (Student)listModel_all.get(selectedIndices[i]);
							if(listModel_exist.contains(s)){
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										statusbar.setText("The student selected is exist");
									}
								});
							}
							else{
								Module m = (Module)listModel_modules.get(moduleIndex);
								s.getModules().add(m);
								m.getStudents().add(s);
								statusbar.setText("Adding " + s + " to " + m + "...");
								am.saveEntity(m);
								selectedStudent = s;
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										listModel_exist.addElement(selectedStudent);
										list_exist.updateUI();
									}
								});
							}
						}
					}
				}
			}
			
		}.start();
		
	}
	
	private void removeModule(){
		new Thread(){
			
			@Override
			public void run() {
				int[] indices = list_modules.getSelectedIndices();
				if(indices.length == 0){
					statusbar.setText("Please select a module");
				}
				else{
					List<Module> moduleToDelete = new ArrayList<Module>();
					for(int i=0;i<indices.length;i++){
						Module m = (Module)listModel_modules.get(indices[i]);
						statusbar.setText("Deleting " + m + "...");
						moduleToDelete.add(m);
						am.deleteModule(m);
					}
					for(Module m : moduleToDelete){
						listModel_modules.removeElement(m);
					}
				}
				updateUI();
			}
			
		}.start();
	}
	
	private void removeStudent(){
		new Thread(){

			@Override
			public void run() {
				int[] indices = list_all.getSelectedIndices();
				if(indices.length == 0){
					statusbar.setText("Please select a student");
				}
				else{
					List<Student> studentsToDelete = new ArrayList<Student>();
					for(int i=0;i<indices.length;i++){
						Student s = (Student)listModel_all.get(indices[i]);
						statusbar.setText("Deleting " + s + "...");
						studentsToDelete.add(s);
						am.deleteStudent(s);
					}
					for(Student s : studentsToDelete){
						listModel_all.removeElement(s);
						for(int i=0;i<listModel_modules.size();i++){
							Module m = (Module)listModel_modules.get(i);
							if(m.getStudents().contains(s)){
								m.getStudents().remove(s);
							}
						}
					}
				}
				updateUI();
			}
			
		}.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Refresh All")){
			statusbar.setText("refreshing...");
			statusbar.updateUI();
			refreshAll();
		}
		else if(e.getActionCommand().equals("add")){
			statusbar.setText("adding..");
			statusbar.updateUI();
			add();
		}
		else if(e.getActionCommand().equals("remove")){
			statusbar.setText("removing..");
			statusbar.updateUI();
			delete();
		}
		else if(e.getActionCommand().equals("delete module")){
			statusbar.setText("deleting a module..");
			statusbar.updateUI();
			removeModule();
		}
		else if(e.getActionCommand().equals("delete student")){
			statusbar.setText("deleting a student..");
			statusbar.updateUI();
			removeStudent();
		}
	}


	@Override
	public void update(Observable arg0, Object o) {
		if(o instanceof ResultNotification){
		   if(((ResultNotification) o).isSuccess){
			   statusbar.setText("Operation successed");
		   }
		   else{
			   statusbar.setText("Operation failed");
		   }
		}
		updateUI();
	}
}
