package org.ams.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.ams.db.Student;
import org.ams.model.AdminModel;
import org.ams.model.ResultNotification;

public class AddSingleStudentPanel extends JPanel implements Observer{
	
	private JTextField rollNumber;
	private JTextField studentName;
	private JTextField emailAddress;
	private AdminModel am;
	private int index;
	
	
	public AddSingleStudentPanel(int studentIndex){
		this.index = studentIndex;
		launchAddOneStudentPanel();
		am = new AdminModel();
		am.addObserver(this);
	}
	
	private void launchAddOneStudentPanel(){
		setLayout(new GridLayout(3, 1));
		JPanel panel;
		JLabel label;
		
		label = new JLabel("Rollnumber");
		rollNumber = new JTextField();
		panel = new JPanel(new GridLayout(1, 2));
		panel.add(label);
		panel.add(rollNumber);
		add(panel);
		
		label = new JLabel("Name");
		studentName = new JTextField();
		panel = new JPanel(new GridLayout(1, 2));
		panel.add(label);
		panel.add(studentName);
		add(panel);
		
		label = new JLabel("Email Address");
		emailAddress = new JTextField();
		panel = new JPanel(new GridLayout(1, 2));
		panel.add(label);
		panel.add(emailAddress);
		add(panel);
		setBorder(BorderFactory.createTitledBorder("Student " + index +":"));
	}
	
	public void submit(){
		if(rollNumber.getText().equals("") || studentName.getText().equals("") || emailAddress.getText().equals("")){
			System.out.println("Student " + index + " is empty.");
		}
		else{
			if(!emailAddress.getText().matches("[a-z|A-Z|\\d|_].+@[a-z|A-Z|\\d|_].+\\.[a-z|A-Z].+")){
				emailAddress.setBackground(Color.CYAN);
				emailAddress.updateUI();
			}
			else{
				new SwingWorker<Void, Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						String rollnumber = rollNumber.getText();
						String name = studentName.getText();
						String email = emailAddress.getText();
						Student newStudent = new Student(name, rollnumber,email);
						am.saveEntity(newStudent);
						return null;
					}
					
				}.execute();
			}
		}
	}
	
	

	@Override
	public void update(Observable arg0, Object o) {
		if(o instanceof ResultNotification){
		   if(((ResultNotification) o).isSuccess){
			   rollNumber.setText("");
			   studentName.setText("");
			   emailAddress.setText("");
			   rollNumber.setBackground(Color.WHITE);
			   studentName.setBackground(Color.WHITE);
			   emailAddress.setBackground(Color.WHITE);
		   }
		   else{
			   rollNumber.setBackground(Color.PINK);
			   studentName.setBackground(Color.PINK);
			   emailAddress.setBackground(Color.PINK);
		   }
		}
	}
	
}
