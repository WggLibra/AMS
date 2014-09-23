package org.ams.view;

import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.ams.db.Program;
import org.ams.model.AdminModel;
import org.ams.model.ResultNotification;

public class ProgramManager extends JPanel implements Observer,ActionListener{

	TextField program_title;
	AdminModel am;
	
	public ProgramManager(){
		add(createStudentPanel());
		am = new AdminModel();
		am.addObserver(this);
	}
	
	private JPanel createStudentPanel(){
		JPanel panel = new JPanel();
		program_title = new TextField();
		JLabel title = new JLabel("Programme Title");
		JButton submit = new JButton("submit");
		submit.addActionListener(this);
		panel.setLayout(new GridLayout(2,1,5,10));
		panel.add(title);
		panel.add(program_title);;
		panel.add(submit);
		panel.setBorder(BorderFactory.createTitledBorder("Add new programme"));
		return panel;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("submit")){
			if(program_title.getText().equals("")){
				JOptionPane.showMessageDialog(this,"The programme title should not be empty.");
			}
			else{
				String name = program_title.getText();
				Program program = new Program(name);
				am.saveEntity(program);
				program_title.setText("");
			}
		}
	}

	@Override
	public void update(Observable arg0, Object o) {
		if(o instanceof ResultNotification){
			
		   if(((ResultNotification) o).isSuccess){
			   JOptionPane.showMessageDialog(this,"save success");
		   }
		   else{
			   JOptionPane.showMessageDialog(this,"save failed");
		   }
		}
		
	}
	
}
