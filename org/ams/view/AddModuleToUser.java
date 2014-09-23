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
import org.ams.db.User;
import org.ams.model.AdminModel;
import org.ams.model.ResultNotification;

public class AddModuleToUser extends JPanel implements ActionListener,Observer{

	AdminModel am;
	DefaultListModel listModel_modules;
	DefaultListModel listModel_exist;
	DefaultListModel listModel_users;
	JList list_modules;
	JList list_exist;
	JList list_users;
	JLabel statusbar;
	List<Module> modules;
	List<User> users;
	User selectedUser;
	Module selectedModule;
	
	public AddModuleToUser() {
		am = new AdminModel();
		am.addObserver(this);
		setLayout(new BorderLayout());
		add(createModuleManagePanel(),BorderLayout.CENTER);
	}
	
	private JPanel createModuleManagePanel(){
		JPanel wholePanel = new JPanel();
		wholePanel.setLayout(new BorderLayout());
		JPanel moduleManagePanel = new JPanel();
		JPanel listContainer;
		JPanel buttonPane;
		JScrollPane scrollpane;
		moduleManagePanel.setBorder(BorderFactory.createTitledBorder("Program management for modules"));
		moduleManagePanel.setLayout(new GridLayout(1,3,5,10));
		listModel_modules = new DefaultListModel();
		listModel_exist = new DefaultListModel();
		listModel_users = new DefaultListModel();
		list_modules = new JList(listModel_modules);
		list_modules.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!listModel_modules.isEmpty()){
					int selectedIndex = list_modules.getSelectedIndex();
					Module selectedModule = (Module)listModel_modules.get(selectedIndex);
					Set<User> usersInModule = selectedModule.getUsers();
					if(usersInModule.isEmpty()){
						listModel_exist.clear();
					}
					else{
						listModel_exist.clear();
						for(User u : usersInModule){
						listModel_exist.addElement(u);
						}
					}
				}
				updateUI();
			}
		});
		list_exist = new JList(listModel_exist);
		list_users = new JList(listModel_users);
		
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
		listContainer.setBorder(BorderFactory.createTitledBorder("Users in the module"));
		listContainer.add(scrollpane, BorderLayout.CENTER);
		JButton deleteFromModule = new JButton("remove");
		deleteFromModule.addActionListener(this);
		buttonPane = new JPanel(new FlowLayout());
		buttonPane.add(deleteFromModule);
		listContainer.add(buttonPane, BorderLayout.PAGE_END);
		moduleManagePanel.add(listContainer);
		
		scrollpane = new JScrollPane(list_users);
		listContainer = new JPanel();
		listContainer.setLayout(new BorderLayout());
		listContainer.setBorder(BorderFactory.createTitledBorder("All users"));
		listContainer.add(scrollpane, BorderLayout.CENTER);
		JButton button_addToModule = new JButton("add");
		button_addToModule.addActionListener(this);
		JButton button_removeStudent = new JButton("delete user");
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
				users = am.getAllUsers();
				for(User u : users){
					System.out.println(u);
				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						listModel_modules.clear();
						listModel_exist.clear();
						listModel_users.clear();
						try {
							for(Module m : modules){
								listModel_modules.addElement(m); 
							}
							for(User u : users){
								listModel_users.addElement(u);
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
							statusbar.setText("Please selset a existent user");
						}
					});
				}
				else{
					int moduleIndex = list_modules.getSelectedIndex();
					if(moduleIndex == -1){
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								statusbar.setText("Please selset a module");
							}
						});
					}
					else{
						List<User> usersToRemove = new ArrayList<User>();
						for(int i=0;i<selectedIndices.length;i++){
							User u = (User)listModel_exist.get(selectedIndices[i]);
							Module m = (Module)listModel_modules.get(moduleIndex);
							statusbar.setText("Removing " + u + " from " + m + " ...");
							usersToRemove.add(u);
							u.getModules().remove(m);
							m.getUsers().remove(u);
							am.saveEntity(m);
						}
						for(User ur : usersToRemove){
							listModel_exist.removeElement(ur);
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
				int[] userIndices = list_users.getSelectedIndices();
				if(userIndices.length == 0){
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							statusbar.setText("Please select a user");
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
						for(int i=0;i<userIndices.length;i++){
							User u = (User)listModel_users.get(userIndices[i]);
							if(listModel_exist.contains(u)){
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										statusbar.setText("The user selected is exist");
									}
								});
							}
							else{
								Module m = (Module)listModel_modules.get(moduleIndex);
								statusbar.setText("Adding " + u + " to " + m + " ...");
								u.getModules().add(m);
								m.getUsers().add(u);
								am.saveEntity(m);
								selectedUser = u;
								SwingUtilities.invokeLater(new Runnable() {
									@Override
									public void run() {
										listModel_exist.addElement(selectedUser);
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
	
	private void removeUser(){
		new Thread(){

			@Override
			public void run() {
				int[] selectedIndices = list_users.getSelectedIndices();
				if(selectedIndices.length == 0){
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							statusbar.setText("Please select a user");
						}
					});
				}
				else{
					List<User> usersToRemove = new ArrayList<User>();
					for(int i=0;i<selectedIndices.length;i++){
						User u = (User)listModel_users.get(selectedIndices[i]);
						statusbar.setText("Deleting " + u + "...");
						usersToRemove.add(u);
						am.deleteUser(u);
					}
					for(User u : usersToRemove){
						listModel_users.removeElement(u);
						for(int i=0;i<listModel_modules.size();i++){
							Module m = (Module)listModel_modules.get(i);
							if(m.getUsers().contains(u)){
								m.getUsers().remove(u);
							}
						}
					}
				}
				updateUI();
			}
			
		}.start();
	}
	
	private void removeModule(){
		new Thread(){

			@Override
			public void run() {
				int[] selectedIndices = list_modules.getSelectedIndices();
				if(selectedIndices.length == 0){
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							statusbar.setText("Please select a module");
						}
					});
				}
				else{
					List<Module> modulesToRemove = new ArrayList<Module>();
					for(int i=0;i<selectedIndices.length;i++){
						Module m = (Module)listModel_modules.get(selectedIndices[i]);
						statusbar.setText("Deleting " + m + "...");
						modulesToRemove.add(m);
						am.deleteModule(m);
					}
					for(Module m : modulesToRemove){
						listModel_modules.removeElement(m);
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
			statusbar.setText("adding...");
			statusbar.updateUI();
			add();
		}
		else if(e.getActionCommand().equals("remove")){
			statusbar.setText("deleting...");
			statusbar.updateUI();
			delete();
		}
		else if(e.getActionCommand().equals("delete module")){
			statusbar.setText("deleting a module...");
			statusbar.updateUI();
			removeModule();
		}
		else if(e.getActionCommand().equals("delete user")){
			statusbar.setText("deleting a user...");
			statusbar.updateUI();
			removeUser();
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
