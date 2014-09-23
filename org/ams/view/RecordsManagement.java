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
import java.util.Set;

import javax.swing.BorderFactory;
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

import org.ams.db.Module;
import org.ams.db.Record;
import org.ams.db.User;
import org.ams.model.AdminModel;
import org.ams.model.GetListNotification;
import org.ams.model.ResultNotification;

import com.toedter.calendar.JDateChooser;

public class RecordsManagement extends JPanel implements ActionListener,Observer{
	
	private JLabel status;
	private DefaultListModel listModel;
	private JList records;
	private DefaultComboBoxModel comboboxModel;
	private JComboBox modules;
	private JDateChooser date_since;
	private JDateChooser date_to;
	private User currentUser;
	private AdminModel am;
	
	public RecordsManagement(User u){
		this.currentUser = u;
		am = new AdminModel();
		am.addObserver(this);
		initGUIComponents();
	}
	
	private void initGUIComponents(){
		comboboxModel = new DefaultComboBoxModel();
		if(currentUser.getPerm() == User.LECTURER | currentUser.getPerm() == User.PROGRAM_LEADER){
			new SwingWorker<Void, Module>(){
				@Override
				protected Void doInBackground() throws Exception {
					Set<Module> modulesList = currentUser.getModules();
					for(Module m : modulesList){
						publish(m);
					}
					return null;
				}

				@Override
				protected void process(List<Module> modules) {
					for(Module m : modules){
						comboboxModel.addElement(m);
					}
				}
			}.execute();
		}
		setLayout(new BorderLayout());
		add(createChoosePanel(),BorderLayout.PAGE_START);
		add(createListPanel(),BorderLayout.CENTER);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(createOperationPanel(),BorderLayout.PAGE_START);
		panel.add(createStatusBar(),BorderLayout.PAGE_END);
		add(panel,BorderLayout.PAGE_END);
	}
	
	private JPanel createListPanel(){
		JPanel panel = new JPanel(new BorderLayout());
		listModel = new DefaultListModel();
		records = new JList(listModel);
		records.setCellRenderer(new ItemsInList());
		JScrollPane s = new JScrollPane(records);
		panel.add(s,BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createTitledBorder("Records"));
		return panel;
	}
	
	private JPanel createOperationPanel(){
		JPanel panel = new JPanel();
		JButton button;
		button = new JButton("delete");
		button.addActionListener(this);
		panel.add(button);
		return panel;
	}
	
	private JPanel createStatusBar(){
		JPanel panel = new JPanel();
		status = new JLabel("Reday");
		panel.add(status);
		return panel;
	}
	
	private JPanel createChoosePanel(){
		JPanel panel = new JPanel();
		JLabel label;
		if(currentUser.getPerm() == User.LECTURER || currentUser.getPerm() == User.PROGRAM_LEADER){
			modules = new JComboBox(comboboxModel);
			label = new JLabel("Modules");
			panel.add(label);
			panel.add(modules);
		}
		date_since = new JDateChooser();
		date_to = new JDateChooser();
		label = new JLabel("Since");
		panel.add(label);
		panel.add(date_since);
		label = new JLabel("to");
		panel.add(label);
		panel.add(date_to);
		JButton button = new JButton("search");
		button.addActionListener(this);
		panel.add(button);
		panel.setBorder(BorderFactory.createTitledBorder("Choose date and module"));
		return panel;
	}
	
	private void deleteRecords(){
		int[] indices = records.getSelectedIndices();
		if(indices.length == 0){
			status.setText("Please select some records to delete!");
		}
		else{
			List<Record> recordsToDelete = new ArrayList<Record>();
			for(int i=0;i<indices.length;i++){
				Record r = (Record)listModel.get(indices[i]);
				recordsToDelete.add(r);
			}
			new DeleteRecords(recordsToDelete).execute();
		}
	}
	
	private void updateRecords(List<Record> rec){
		listModel.clear();
		for(Record r : rec){
			listModel.addElement(r);
		}
		records.ensureIndexIsVisible(listModel.size());
		status.setText("Finish searching, find " + rec.size() + "results");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("search")){
			status.setText("Searching...");
			status.updateUI();
			new SwingWorker<Void, Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					if(currentUser.getPerm() == User.LECTURER || currentUser.getPerm() == User.PROGRAM_LEADER){
						Module m = (Module)modules.getSelectedItem();
						am.getRecordsFromModule(date_since.getDate(), date_to.getDate(), m);
						
					}
					else{
						am.getRecordsBetween(date_since.getDate(), date_to.getDate());
					}
					return null;
				}
			}.execute();
		}
		else if(e.getActionCommand().equals("delete")){
			deleteRecords();
		}
	}

	@Override
	public void update(Observable o, Object obj) {
		if(obj instanceof GetListNotification){
			GetListNotification gn = (GetListNotification)obj;
			List<Record> records = (List<Record>)gn.getEntities();
			updateRecords(records);
		}
		else if(obj instanceof ResultNotification){
			ResultNotification rn = (ResultNotification)obj;
			if(rn.isSuccess){
				status.setText("Operation successed");
			}
			else{
				status.setText("Operation failed");
			}
			status.updateUI();
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
			
			Record r = (Record)value;
			setText(r.toString());
			
			if(isSelected){
				if(r.isAttend()){
					setBackground(new Color(44,93,205));
					setForeground(Color.WHITE);
				}
				else{
					setBackground(new Color(44,93,205));
					setForeground(Color.PINK);
				}
			}
			else{
				if(r.isAttend()){
					setBackground(Color.WHITE);
				    setForeground(Color.BLACK);
				}
				else{
					setBackground(Color.WHITE);
				    setForeground(Color.RED);
				}
			}
			
			return this;
		}
		
	}
	
	private class DeleteRecords extends SwingWorker<Void, Record>{

		private List<Record> records;
		
		public DeleteRecords(List<Record> records){
			this.records = records;
		}
		
		@Override
		protected Void doInBackground() throws Exception {
			Iterator<Record> iter = records.iterator();
			while(iter.hasNext()){
				Record r = iter.next();
				am.deleteEntity(r);
				publish(r);
			}
			return null;
		}

		@Override
		protected void process(List<Record> records) {
			for(Record r : records){
				status.setText("Deleting " + r + "...");
				listModel.removeElement(r);
			}
		}
		
	}

}
