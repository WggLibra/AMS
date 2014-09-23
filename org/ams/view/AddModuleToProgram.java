package org.ams.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
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
import javax.swing.SwingWorker;

import org.ams.db.Module;
import org.ams.db.Program;
import org.ams.model.AdminModel;
import org.ams.model.ResultNotification;

public class AddModuleToProgram extends JPanel implements ActionListener, Observer{

	private AdminModel am;
	private DefaultListModel listModel_programs;
	private DefaultListModel listModel_exist;
	private DefaultListModel listModel_modules;
	private JList list_programs;
	private JList list_exist;
	private JList list_modules;
	private JLabel statusbar;
	private List<Module> modules;
	private List<Program> programs;
	private Set<Module> modulesInProgram;
	
	public AddModuleToProgram() {
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
		moduleManagePanel.setBorder(BorderFactory.createTitledBorder("Program management for modules"));
		moduleManagePanel.setLayout(new GridLayout(1,3,5,10));
		listModel_programs = new DefaultListModel();
		listModel_exist = new DefaultListModel();
		listModel_modules = new DefaultListModel();
		list_programs = new JList(listModel_programs);
		list_programs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!listModel_programs.isEmpty()){
					int selectedIndex = list_programs.getSelectedIndex();
					Program selectedProgram = (Program)listModel_programs.get(selectedIndex);
					modulesInProgram = selectedProgram.getModules();
					if(modulesInProgram.isEmpty()){
						listModel_exist.clear();
					}
					else{
						listModel_exist.clear();
						new SwingWorker<Void, Module>() {
							@Override
							protected Void doInBackground() throws Exception {
								for(Module m : modulesInProgram){
									publish(m);
								}
								return null;
							}
							protected void process(List<Module> modules) {
								for(Module m : modules){
									listModel_exist.addElement(m);
								}
							}
						}.execute();
					}
				}
			}
		});
		list_exist = new JList(listModel_exist);
		list_modules = new JList(listModel_modules);
		
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
		listContainer.setBorder(BorderFactory.createTitledBorder("Modules in the program"));
		listContainer.add(scrollpane, BorderLayout.CENTER);
		JButton deleteFromModule = new JButton("remove");
		deleteFromModule.addActionListener(this);
		buttonPane = new JPanel(new FlowLayout());
		buttonPane.add(deleteFromModule);
		listContainer.add(buttonPane, BorderLayout.PAGE_END);
		moduleManagePanel.add(listContainer);
		
		scrollpane = new JScrollPane(list_modules);
		listContainer = new JPanel();
		listContainer.setLayout(new BorderLayout());
		listContainer.setBorder(BorderFactory.createTitledBorder("All modules"));
		listContainer.add(scrollpane, BorderLayout.CENTER);
		JButton button_addToModule = new JButton("add");
		button_addToModule.addActionListener(this);
		JButton button_removeStudent = new JButton("delete module");
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
		listModel_exist.clear();
		new SwingWorker<List<Module>, Void>() {

			@Override
			protected List<Module> doInBackground() throws Exception {
				List<Module> modules = am.getAllModules();
				return modules;
			}
			
			@Override
			protected void done() {
				try {
					listModel_modules.clear();
					for(Module m : get()){
						listModel_modules.addElement(m);
					}
					statusbar.setText("Finish refreshing");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.execute();
		
		new SwingWorker<List<Program>, Void>() {

			@Override
			protected List<Program> doInBackground() throws Exception {
				List<Program> programs = am.getAllPrograms();
				return programs;
			}
			
			@Override
			protected void done() {
				try {
					listModel_programs.clear();
					for(Program p : get()){
						listModel_programs.addElement(p);
					}
					statusbar.setText("Finish refreshing");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.execute();
	}
	
	private void delete(){
		int[] selectedIndices = list_exist.getSelectedIndices();
		if(selectedIndices.length == 0){
			statusbar.setText("Please selset a existent module");
		}
		else{
			int programIndex = list_programs.getSelectedIndex();
			if(programIndex == -1){
				statusbar.setText("Please selset a program");
			}
			else{
				Program p = (Program)listModel_programs.get(programIndex);
				for(int i=0;i<selectedIndices.length;i++){
					Module m = (Module)listModel_exist.get(selectedIndices[i]);
					new removeMfromP(m, p).execute();
				}
			}
		}
	}
	
	private void add(){
		int[] moduleIndices = list_modules.getSelectedIndices();
		if(moduleIndices.length == 0){
			statusbar.setText("Please select a module");
		}
		else{
			int programIndex = list_programs.getSelectedIndex();
			if(programIndex == -1){
				statusbar.setText("Please select a program");
			}
			else{
				for(int i=0;i<moduleIndices.length;i++){
					Module m = (Module)listModel_modules.get(moduleIndices[i]);
					if(listModel_exist.contains(m)){
						statusbar.setText("The module selected is exist");
					}
					else{
						Program p = (Program)listModel_programs.get(programIndex);
						statusbar.setText("Adding " + m + " to " + p + " ...");
						new addMtoP(m, p).execute();
					}
				}
			}
		}
	}
	
	private void removeProgram(){
		int[] selectedIndices = list_programs.getSelectedIndices();
		if(selectedIndices.length == 0){
			statusbar.setText("Please select a program");
		}
		else{
			programs = new ArrayList<Program>();
			for(int i=0;i<selectedIndices.length;i++){
				Program p = (Program)listModel_programs.get(selectedIndices[i]);
				programs.add(p);
			}
			new SwingWorker<Void, Program>() {
				@Override
				protected Void doInBackground() throws Exception {
					Iterator<Program> iter = programs.iterator();
					while(iter.hasNext()){
						Program p = iter.next();
						am.deleteProgram(p);
						publish(p);
					}
					return null;
				}
				
				@Override
				protected void process(List<Program> programs) {
					for(Program p : programs){
						statusbar.setText("Deleting " + p + "...");
						listModel_programs.removeElement(p);
					}
				}
			}.execute();
		}
	}
	
	private void removeModule(){
		int[] selectedIndices = list_modules.getSelectedIndices();
		if(selectedIndices.length == 0){
			statusbar.setText("Please select a module");
		}
		else{
			modules = new ArrayList<Module>();
			for(int i=0;i<selectedIndices.length;i++){
				Module m = (Module)listModel_modules.get(selectedIndices[i]);
				modules.add(m);
			}
			new SwingWorker<Void, Module>(){
				@Override
				protected Void doInBackground() throws Exception {
					Iterator<Module> iter = modules.iterator();
					while(iter.hasNext()){
						Module m = iter.next();
						am.deleteModule(m);
						publish(m);
					}
					return null;
				}

				@Override
				protected void process(List<Module> modules) {
					for(Module m : modules){
						statusbar.setText("Deleting " + m + "...");
						listModel_modules.removeElement(m);
					}
				}
			}.execute();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Refresh All")){
			statusbar.setText("refreshing...");
			refreshAll();
		}
		else if(e.getActionCommand().equals("add")){
			statusbar.setText("adding...");
			add();
		}
		else if(e.getActionCommand().equals("remove")){
			statusbar.setText("removing...");
			statusbar.updateUI();
			delete();
		}
		else if(e.getActionCommand().equals("delete program")){
			statusbar.setText("deleting a program...");
			statusbar.updateUI();
			removeProgram();
		}
		else if(e.getActionCommand().equals("delete module")){
			statusbar.setText("deleting a module...");
			statusbar.updateUI();
			removeModule();
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
	
	private class addMtoP extends SwingWorker<Module, Void>{

		private Module module;
		private Program pgogram;
		
		public addMtoP(Module m, Program p){
			this.module = m;
			this.pgogram = p;
		}
		
		@Override
		protected Module doInBackground() throws Exception {
			pgogram.getModules().add(module);
			module.getPrograms().add(pgogram);
			am.saveEntity(module);
			return module;
		}
		
		@Override
		protected void done() {
			try {
				listModel_exist.addElement(get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private class removeMfromP extends SwingWorker<Module, Void>{

		private Module module;
		private Program pgogram;
		
		public removeMfromP(Module m, Program p){
			this.module = m;
			this.pgogram = p;
		}
		
		@Override
		protected Module doInBackground() throws Exception {
			module.getPrograms().remove(pgogram);
			pgogram.getModules().remove(module);
			am.saveEntity(module);
			return module;
		}
		
		@Override
		protected void done() {
			try {
				statusbar.setText("Removing " + module + " from " + pgogram + " ...");
				listModel_exist.removeElement(get());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
