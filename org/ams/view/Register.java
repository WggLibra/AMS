package org.ams.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.ams.db.BusinessEntity;
import org.ams.db.User;
import org.ams.model.AdminModel;
import org.ams.model.ResultNotification;

public class Register extends JPanel implements ActionListener,Observer{
	
	private AdminModel am;
	private JComboBox usersList;
	private JTextField name;
	private JTextField username;
	private JPasswordField password;
	private JPasswordField re_password;
	private static final String[] users= {"Administrator", "Program Leader", "Lecturer"};
	
	public Register(){
		am = new AdminModel();
		am.addObserver(this);
		//add(createBackground());
		add(createRegisterPanel());
	}
	
	
	
	
	private JPanel createRegisterPanel(){
		JPanel registerPanel = new JPanel();
		registerPanel.setOpaque(false);
		//registerPanel.setBackground(Color.BLUE);
		//registerPanel.add(createBackground());
		JLabel label;
		registerPanel.setLayout(new GridLayout(5,2));
		usersList = new JComboBox(users);
		label = new JLabel("name");
		name = new JTextField();
		registerPanel.add(label);
		registerPanel.add(name);
		label = new JLabel("username");
		username = new JTextField();
		registerPanel.add(label);
		registerPanel.add(username);
		label = new JLabel("password");
		password = new JPasswordField();
		registerPanel.add(label);
		registerPanel.add(password);
		label = new JLabel("confirm password");
		re_password = new JPasswordField();
		
		registerPanel.add(label);
		registerPanel.add(re_password);
		registerPanel.add(usersList);
	
		
		JButton register = new JButton("register");
		register.addActionListener(this);
		registerPanel.add(register);
		registerPanel.setMinimumSize(new Dimension(200, 150));
		registerPanel.setBorder(BorderFactory.createTitledBorder("Register a new user"));
		//registerPanel.setResizeble(false);
		return registerPanel;
	}
	
	

	private void submit(){
		String aname = name.getText();
		String uname = username.getText();
		String pwd = new String(password.getPassword());
		String repwd = new String(re_password.getPassword());
		int perm = usersList.getSelectedIndex();
		if(aname.equals("")){
			name.setBackground(Color.PINK);
		}
		else if(uname.equals("")){
			username.setBackground(Color.PINK);
		}
		else{
			if(pwd.equals("")){
				password.setBackground(Color.PINK);
			}
			else{
				if(!pwd.equals(repwd)){
					password.setBackground(Color.PINK);
					re_password.setBackground(Color.PINK);
				}
				else{
					User newUser = new User(aname, uname, pwd, perm);
					System.out.println("TEST");
					am.saveEntity(newUser);
				}
			}
		}
		updateUI();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("register")){
			submit();
		}
	}

	@Override
	public void update(Observable o, Object obj) {
		if(obj instanceof ResultNotification){
			ResultNotification rn = (ResultNotification)obj;
			if(rn.isSuccess){
				JOptionPane.showMessageDialog(null, "Register successed");
				name.setText("");
				username.setText("");
				password.setText("");
				re_password.setText("");
				name.setBackground(Color.WHITE);
				username.setBackground(Color.WHITE);
				password.setBackground(Color.WHITE);
				re_password.setBackground(Color.WHITE);
			}
			else{
				JOptionPane.showMessageDialog(null, "Register failed, please try again");
				name.setBackground(Color.PINK);
				username.setBackground(Color.PINK);
				password.setBackground(Color.PINK);
				re_password.setBackground(Color.PINK);
			}
		}
	}
	
}
