package org.ams.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingWorker;

import org.ams.db.BusinessEntity;
import org.ams.db.Program;
import org.ams.db.User;
import org.ams.model.AdminModel;
import org.ams.model.ResultNotification;

public class UserToProgram extends JPanel implements ActionListener, Observer{

	private AdminModel am;
	private JLabel statues;
	private DefaultComboBoxModel model_programs;
	private DefaultComboBoxModel model_users;
	private JComboBox usersList;
	private JComboBox programsList;
	private DefaultListModel listModel;
	private JList records;
	private int sizeOfRecords;
	
	public UserToProgram(){
		am = new AdminModel();
		am.addObserver(this);
		Box vbox = Box.createVerticalBox();
		vbox.add(createUsersAndPrograms());
		vbox.add(createInfoPanel());
		setLayout(new BorderLayout());
		add(vbox, BorderLayout.CENTER);
		add(createOptionsAndStatues(), BorderLayout.PAGE_END);
		refreshAll();
	}
	
	private JPanel createUsersAndPrograms(){
		JPanel panel = new JPanel();
		model_programs = new DefaultComboBoxModel();
		model_users = new DefaultComboBoxModel();
		model_programs.addElement("Empty");
		model_users.addElement("Empty");
		JLabel label = new JLabel("Please select a user with a specified program");
		panel.add(label);
		usersList = new JComboBox(model_users);
		panel.add(usersList);
		programsList = new JComboBox(model_programs);
		panel.add(programsList);
		JButton confirm = new JButton("confirm");
		confirm.addActionListener(this);
		panel.add(confirm);
		panel.setBorder(BorderFactory.createTitledBorder("Users and Programs"));
		return panel;
	}
	
	private JPanel createInfoPanel(){
		JPanel panel = new JPanel(new BorderLayout());
		listModel = new DefaultListModel();
		records = new JList(listModel);
		records.setCellRenderer(new ItemsInList());
		JScrollPane s = new JScrollPane(records);
		panel.add(s, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createTitledBorder("Details"));
		return panel;
	}
	
	private JPanel createOptionsAndStatues(){
		JPanel panel = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel();
		JButton button;
		button = new JButton("remove");
		button.addActionListener(this);
		buttons.add(button);
		button = new JButton("refresh all");
		button.addActionListener(this);
		buttons.add(button);
		button = new JButton("remove all");
		button.addActionListener(this);
		buttons.add(button);
		statues = new JLabel("ready");
		statues.setHorizontalAlignment(JLabel.CENTER);
		panel.add(buttons, BorderLayout.PAGE_START);
		panel.add(statues, BorderLayout.PAGE_END);
		
		return panel;
	}
	
	private void confirm(){
		User u = (User)model_users.getSelectedItem();
		Program p = (Program)model_programs.getSelectedItem();
		new MapPtoM(u, p).execute();
	}
	
	private void remove(){
		int[] indices = records.getSelectedIndices();
		if(indices.length == 0){
			statues.setText("Please select a record");
		}
		else{
			List<User> users = new ArrayList<User>();
			for(int i=0;i<indices.length;i++){
				User u = (User)listModel.get(indices[i]);
				users.add(u);
			}
			new RemoveAll(users).execute();
		}
	}
	
	private void addItem(User u){
		if(listModel.contains(u)){
			listModel.removeElement(u);
		}
		listModel.addElement(u);
	}
	
	private void removeItem(User u){
		listModel.removeElement(u);
	}
	
	private void removeAllItems(){
		if(listModel.isEmpty()){
			statues.setText("No records!");
		}
		else{
			ArrayList<User> users= new ArrayList<User>();
			for(int i=0;i<listModel.size();i++){
				users.add((User)listModel.get(i));
			}
			new RemoveAll(users).execute();
		}
	}
	
	private void refreshAll(){
		listModel.clear();
		new SwingWorker<List<User>, Void>(){
			@Override
			protected List<User> doInBackground() throws Exception {
				List<User> users = am.getAllUsers();
				return users;
			}

			@Override
			protected void done() {
				try {
					model_users.removeAllElements();
					for(User u : get()){
						model_users.addElement(u);
						if(u.getProgram() != null){
							listModel.addElement(u);
							sizeOfRecords++;
						}
					}
					records.ensureIndexIsVisible(sizeOfRecords);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.execute();
		
		new SwingWorker<List<Program>, Void>(){
			@Override
			protected List<Program> doInBackground() throws Exception {
				List<Program> programs = am.getAllPrograms();
				return programs;
			}

			@Override
			protected void done() {
				try {
					model_programs.removeAllElements();
					for(Program p : get()){
						model_programs.addElement(p);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.execute();
	}
	
	@Override
	public void update(Observable o, Object obj) {
		if(obj instanceof ResultNotification){
			ResultNotification rn = (ResultNotification)obj;
			if(rn.isSuccess){
				statues.setText("Operation successed");
				BusinessEntity bn = rn.be;
				if(bn instanceof User){
					User u = (User)bn;
					if(u.getProgram() == null){
						removeItem(u);
					}
					else{
						addItem(u);
					}
				}
			}
			else{
				statues.setText("Operation failed");
			}
		}
		updateUI();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("confirm")){
			statues.setText("Confirming..");
			confirm();
		}
		else if(e.getActionCommand().equals("remove")){
			statues.setText("Removing..");
			remove();
		}
		else if(e.getActionCommand().equals("remove all")){
			statues.setText("Removing..");
			removeAllItems();
		}
		else if(e.getActionCommand().equals("refresh all")){
			refreshAll();
		}
	}
	
	private class ItemsInList extends JLabel implements ListCellRenderer{

		public ItemsInList(){
			setOpaque(true);
		}
		
		@Override
		public Component getListCellRendererComponent(JList list, 
				Object value,
				int index, 
				boolean isSelected, 
				boolean cellHasFocus) {
			
			User u = (User)value;
		/*	String an = u.getAname();
			String pn = u.getProgram()==null?"":u.getProgram().toString();
			System.out.println(an);
			System.out.println(pn);
			System.out.println(an+" - "+pn);*/
	
			//setText(an+pn);
			/*if(u.getProgram()==null)
				setText(u.getAname());
			else
				setText(u.getAname() + " - " + u.getProgram().toString());*/
			setText(u.getAname() + " - " + (u.getProgram()==null?"":u.getProgram().toString()));
			if(isSelected){
				setBackground(new Color(44,93,205));
			    setForeground(Color.WHITE);
			}
			else{
				setBackground(Color.WHITE);
			    setForeground(Color.BLACK);
			}
			
			return this;
		}
		
	}
	
	private class RemoveAll extends SwingWorker<Void, Void>{

		private List<User> users;
		
		public RemoveAll(List<User> users){
			this.users = users;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			Iterator<User> iter = users.iterator();
			while(iter.hasNext()){
				User u = iter.next();
				u.setProgram(null);
				am.saveEntity(u);
			}
			return null;
		}
	}
	
	private class MapPtoM extends SwingWorker<Void, Void>{

		private Program p;
		private User u;
		
		public MapPtoM(User u,Program p){
			this.p = p;
			this.u = u;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			u.setProgram(p);
			p.getUsers().add(u);
			am.saveEntity(u);
			return null;
		}
		
	}

}
