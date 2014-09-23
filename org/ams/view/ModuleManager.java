 package org.ams.view;

import java.awt.Color;
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

import org.ams.db.Module;

import org.ams.model.AdminModel;
import org.ams.model.ResultNotification;
import org.hibernate.metamodel.source.binder.JpaCallbackClass;


public class ModuleManager extends JPanel implements Observer,ActionListener{

	TextField module_title;
	TextField module_code;
	TextField module_level;
	AdminModel am;
	
	public ModuleManager(){
		add(createModulePanel());
		am = new AdminModel();
		am.addObserver(this);
	}
	
	private JPanel createModulePanel(){
		JPanel panel = new JPanel();
		module_title = new TextField();
		module_code = new TextField();
		module_level = new TextField();
		JLabel lTitle = new JLabel("Module Title");
		JLabel lCode = new JLabel("Module Code");
		JLabel lLevel = new JLabel("Module Level");
		JButton submit = new JButton("Submit");
		submit.addActionListener(this);
		panel.setLayout(new GridLayout(5,1,5,10));
		panel.add(lTitle);
		panel.add(module_title);
		panel.add(lCode);
		panel.add(module_code);
		panel.add(lLevel);
		panel.add(module_level);
		panel.add(submit);
		panel.setBorder(BorderFactory.createTitledBorder("Add new module"));
		return panel;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Submit")){
			if(module_title.getText().equals("") || module_code.getText().equals("") || module_level.getText().equals("")){
				JOptionPane.showMessageDialog(this,"None of the fields should be empty.");
			}
			else{
				String title = module_title.getText();
				String code = module_code.getText();
				try {
					int level = Integer.parseInt(module_level.getText());
					Module module = new Module(title,code,level);
					am.saveEntity(module);
					module_title.setText("");
					module_code.setText("");
					module_level.setText("");
					module_level.setBackground(Color.WHITE);
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(this,"Invalid value!");
					module_level.setBackground(Color.PINK);
				}
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

