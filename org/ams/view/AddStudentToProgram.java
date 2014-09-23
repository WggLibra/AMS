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
import org.ams.db.Program;
import org.ams.db.Student;
import org.ams.model.AdminModel;
import org.ams.model.ResultNotification;

public class AddStudentToProgram extends JPanel implements ActionListener,Observer{
	
	AdminModel am;
	DefaultListModel listModel_programs;
	DefaultListModel listModel_exist;
	DefaultListModel listModel_students;
	JList list_programs;
	JList list_exist;
	JList list_students;
	JLabel statusbar;
	List<Program> programs;
	List<Student> students;
	Student selectedStudent;
	Program selectedProgram;
	
	public AddStudentToProgram() {
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
		moduleManagePanel.setBorder(BorderFactory.createTitledBorder("Program management for students"));
		moduleManagePanel.setLayout(new GridLayout(1,3,5,10));
		listModel_programs = new DefaultListModel();
		listModel_exist = new DefaultListModel();
		listModel_students = new DefaultListModel();
		list_programs = new JList(listModel_programs);
		list_programs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!listModel_programs.isEmpty()){
					int selectedIndex = list_programs.getSelectedIndex();
					Program selectedProgram = (Program)listModel_programs.get(selectedIndex);
					Set<Student> studentsInProgram = selectedProgram.getStudents();
					if(studentsInProgram.isEmpty()){
						listModel_exist.clear();
					}
					else{
						listModel_exist.clear();
						for(Student s : studentsInProgram){
						listModel_exist.addElement(s);
						}
					}
				}
				updateUI();
			}
		});
		list_exist = new JList(listModel_exist);
		list_students = new JList(listModel_students);
		
		scrollpane = new JScrollPane(list_programs);
		listContainer = new JPanel();
		listContainer.setLayout(new BorderLayout());
		listContainer.setBorder(BorderFactory.createTitledBorder("All programs"));
		listContainer.add(scrollpane, BorderLayout.CENTER);
		JButton button_remove = new JButton("delete program");
		button_remove.addActionListener(this);
		buttonPane = new JPanel(new FlowLayout());
		buttonPane.add(button_remove);
		listContainer.add(buttonPane, BorderLayout.PAGE_END);
		moduleManagePanel.add(listContainer);
		
		scrollpane = new JScrollPane(list_exist);
		listContainer = new JPanel();
		listContainer.setLayout(new BorderLayout());
		listContainer.setBorder(BorderFactory.createTitledBorder("Students in the program"));
		listContainer.add(scrollpane, BorderLayout.CENTER);
		JButton deleteFromModule = new JButton("remove");
		deleteFromModule.addActionListener(this);
		buttonPane = new JPanel(new FlowLayout());
		buttonPane.add(deleteFromModule);
		listContainer.add(buttonPane, BorderLayout.PAGE_END);
		moduleManagePanel.add(listContainer);
		
		scrollpane = new JScrollPane(list_students);
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
				programs = am.getAllPrograms();
				students = am.getAllStudents();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						listModel_programs.clear();
						listModel_exist.clear();
						listModel_students.clear();
						try {
							for(Program p : programs){
								listModel_programs.addElement(p);
							}
							for(Student s: students){
								listModel_students.addElement(s);
							}
						} 
						catch (Exception e) {
							statusbar.setText("Error, please try again");
						}
						statusbar.setText("refresh finished");
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
							statusbar.setText("Please selset a existent student");
						}
					});
				}
				else{
					int programIndex = list_programs.getSelectedIndex();
					if(programIndex == -1){
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								statusbar.setText("Please selset a program");
							}
						});
					}
					else{
						List<Student> studentsToRemove = new ArrayList<Student>();
						Program p = (Program)listModel_programs.get(programIndex);
						for(int i=0;i<selectedIndices.length;i++){
							Student s = (Student)listModel_exist.get(selectedIndices[i]);
							studentsToRemove.add(s);
							statusbar.setText("Removing " + s + " from " + p + " ...");
							s.setProgram(null);
							am.saveEntity(s);
						}
						for(Student s : studentsToRemove){
							listModel_exist.removeElement(s);
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
				int[] studentIndices = list_students.getSelectedIndices();
				if(studentIndices.length == 0){
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							statusbar.setText("Please select a student");
						}
					});
				}
				else{
					int programIndex = list_programs.getSelectedIndex();
					if(programIndex == -1){
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								statusbar.setText("Please select a program");
							}
						});
					}
					else{
						for(int i=0;i<studentIndices.length;i++){
							Student s = (Student)listModel_students.get(studentIndices[i]);
							if(listModel_exist.contains(s)){
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										statusbar.setText("The student selected is exist");
									}
								});
							}
							else{
								Program p = (Program)listModel_programs.get(programIndex);
								statusbar.setText("Adding " + s + " to " + p + " ...");
								s.setProgram(p);
								p.getStudents().add(s);
								am.saveEntity(s);
								selectedStudent = s;
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										listModel_exist.addElement(selectedStudent);
									}
								});
							}
						}
					}
				}
				updateUI();
			}
			
		}.start();
	}
	
	private void removeProgram(){
		new Thread(){

			@Override
			public void run() {
				int[] selectedIndices = list_programs.getSelectedIndices();
				if(selectedIndices.length == 0){
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							statusbar.setText("Please select a program");
						}
					});
				}
				else{
					List<Program> programsToDelete = new ArrayList<Program>();
					for(int i=0;i<selectedIndices.length;i++){
						Program p = (Program)listModel_programs.get(selectedIndices[i]);
						statusbar.setText("Deleting " + p + "...");
						programsToDelete.add(p);
						am.deleteProgram(p);
					}
					for(Program p : programsToDelete){
						listModel_programs.removeElement(p);
					}
				}
			}
			
		}.start();
	}
	
	private void removeStudent(){
		new Thread(){

			@Override
			public void run() {
				int[] selectedIndices = list_students.getSelectedIndices();
				if(selectedIndices.length == 0){
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							statusbar.setText("Please select a student");
						}
					});
				}
				else{
					List<Student> studentsToDelete = new ArrayList<Student>();
					for(int i=0;i<selectedIndices.length;i++){
						Student s = (Student)listModel_students.get(selectedIndices[i]);
						statusbar.setText("Deleting " + s + "...");
						studentsToDelete.add(s);
						am.deleteStudent(s);
					}
					for(Student s : studentsToDelete){
						listModel_students.removeElement(s);
						for(int i=0;i<listModel_programs.size();i++){
							Program p = (Program)listModel_programs.get(i);
							if(p.getStudents().contains(s)){
								p.getStudents().remove(s);
							}
						}
					}
				}
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
			statusbar.setText("adding...");
			statusbar.updateUI();
			add();
		}
		else if(e.getActionCommand().equals("remove")){
			statusbar.setText("deleting...");
			statusbar.updateUI();
			delete();
		}
		else if(e.getActionCommand().equals("delete program")){
			statusbar.setText("deleting a program...");
			statusbar.updateUI();
			removeProgram();
		}
		else if(e.getActionCommand().equals("delete student")){
			statusbar.setText("deleting a student...");
			statusbar.updateUI();
			removeStudent();
		}
	}

	@Override
	public void update(Observable o, Object obj) {
		if(obj instanceof ResultNotification){
			ResultNotification rn = (ResultNotification)obj;
			if(rn.isSuccess){
				statusbar.setText("Operation successed");
			}
			else{
				statusbar.setText("Operation failed");
			}
		}
		updateUI();
	}
	
}
