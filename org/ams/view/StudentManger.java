package org.ams.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

public class StudentManger extends JPanel implements ActionListener{

	private JButton submit;
	private JButton add;
	private JButton delete;
	private JPanel allStudentsPanel;
	private int initStudents;
	private final int MAX_STUDENT_NUMBER = 8;
	
	private ArrayList<AddSingleStudentPanel> allStudents;
	
	public StudentManger(){
		initStudents = 5;
		setLayout(new BorderLayout());
		createAllStudentsPanel();
		add(allStudentsPanel,BorderLayout.CENTER);
		add(createOperationsPanel(),BorderLayout.PAGE_END);
	}
	
	private void createAllStudentsPanel(){
		allStudents = new ArrayList<AddSingleStudentPanel>();
		allStudentsPanel = new JPanel();
		allStudentsPanel.setLayout(new GridLayout(4,2));
		for(int i=1;i<initStudents+1;i++){
			AddSingleStudentPanel currentPanel = new AddSingleStudentPanel(i);
			allStudents.add(currentPanel);
			allStudentsPanel.add(allStudents.get(i-1));
		}
	}
	
	private JPanel createOperationsPanel(){
		JPanel operations = new JPanel(new FlowLayout());
		add = new JButton("+");
		submit = new JButton("submit");
		delete = new JButton("-");
		add.addActionListener(this);
		submit.addActionListener(this);
		delete.addActionListener(this);
		operations.add(add);
		operations.add(submit);
		operations.add(delete);
		return operations;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("+")){
			if(allStudents.size() >= MAX_STUDENT_NUMBER){
				System.out.println("you can't add student any more.");
			}
			else{
				AddSingleStudentPanel newStudent = new AddSingleStudentPanel(allStudents.size()+1);
				allStudents.add(newStudent);
				allStudentsPanel.add(allStudents.get(allStudents.size()-1));
				allStudentsPanel.updateUI();
			}
		}
		else if(e.getActionCommand().equals("submit")){
			for(AddSingleStudentPanel a : allStudents){
				a.submit();
			}
		}
		else if(e.getActionCommand().equals("-")){
			JPanel lastStudent = allStudents.get(allStudents.size()-1);
			allStudentsPanel.remove(lastStudent);
			allStudents.remove(allStudents.size()-1);
			allStudentsPanel.updateUI();
		}
	}
	
}
