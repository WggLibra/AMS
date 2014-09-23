package org.ams.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.ams.db.Module;
import org.ams.db.Student;
import org.ams.db.User;
import org.ams.model.EmailModel;
import org.ams.model.ReportModel;
import org.ams.model.ReportModel.AData;
import org.ams.model.ReportNotification;

import com.toedter.calendar.JDateChooser;

public class Report extends JPanel implements Observer, ActionListener{
	
	private JLabel average;
	private double averageAttend = 0.0;
	private JTable table;
	private DefaultTableModel tableModel = null;
	private JDateChooser c1;
	private JDateChooser c2;
    private ReportModel rm;
    private EmailModel em;
    private Box vbox;
    private float[] dataset = null;
    private ArrayList<Student> absentees;
    private User user;
    private JLabel statues;
    private JComboBox moduleList;
    private DefaultComboBoxModel moduleListModel;
    private JCheckBox byProgram;
    private String prefix;
    
	String[] column = {"roll number","name","attendance count","percentage"};
	
	public Report (User user){
		this.user = user;
		rm = new ReportModel();
		try {
			em = new EmailModel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rm.addObserver(this);
		initReportUI();
	}
	
	private void initReportUI(){
		setLayout(new BorderLayout());
		vbox = Box.createVerticalBox();
		vbox.add(createAveragePanel());
		vbox.add(createReportPanel());
		JPanel bottom = new JPanel();
		JPanel dateChoose = new JPanel();
		dateChoose.setLayout(new FlowLayout());
		if(user.getProgram() != null){
			byProgram = new JCheckBox("Search by program");
			dateChoose.add(byProgram);
		}
		moduleListModel = new DefaultComboBoxModel();
		new Thread(){
			@Override
			public void run() {
				for(Module m : user.getModules()){
					moduleListModel.addElement(m);
				}
			}
		}.start();
		moduleList = new JComboBox(moduleListModel);
		dateChoose.add(moduleList);
		c1 = new JDateChooser();
		dateChoose.add(c1);
		c2 = new JDateChooser();
		dateChoose.add(c2);
		JButton submit = new JButton("search");
		submit.addActionListener(this);
		dateChoose.add(submit);
		statues = new JLabel("Ready");
		statues.setHorizontalAlignment(JLabel.CENTER);
		bottom.setLayout(new BorderLayout());
		bottom.add(dateChoose,BorderLayout.PAGE_START);
		bottom.add(statues,BorderLayout.PAGE_END);
		JPanel reportContainer = new JPanel(new BorderLayout());
		reportContainer.add(vbox,BorderLayout.CENTER);
		JScrollPane scroll = new JScrollPane(reportContainer);
		add(scroll,BorderLayout.CENTER);
		add(bottom,BorderLayout.PAGE_END);
	}
	
	private JPanel createAveragePanel(){
		JPanel averagePanel = new JPanel();
		average = new JLabel("Average Attendance: 0.0%");
		average.setHorizontalAlignment(JLabel.CENTER);
		averagePanel.add(average);
		return averagePanel;
	}
	
	private JPanel createReportPanel(){
		JPanel reportPanel = new JPanel();
		reportPanel.setLayout(new BorderLayout());
		tableModel = new DefaultTableModel();
		tableModel.setColumnIdentifiers(column);
		table = new JTable(tableModel);
		table.setPreferredScrollableViewportSize(new Dimension(550,300));
		JScrollPane panel = new JScrollPane(table);
		reportPanel.add(panel,BorderLayout.CENTER);
		reportPanel.setBorder(BorderFactory.createTitledBorder("Student Attendance Sheet"));
		return reportPanel;
	}
	
	private JPanel createChartPanel(){
		JPanel chartPanel = new JPanel();
		chartPanel.add(new AttendanceReportPane(dataset,prefix));
		chartPanel.setBorder(BorderFactory.createTitledBorder(prefix+"Average Attendance Bar Chart"));
		chartPanel.setMinimumSize(new Dimension(550,400));
		chartPanel.setBackground(Color.WHITE);
		return chartPanel;
	}
	
	private JPanel createAbsenteePanel(){
		JPanel absenteesPanel = new JPanel(new BorderLayout());
		DefaultTableModel model = new DefaultTableModel();
		String[] column = {"Rollnumber","Name","Program"};
		model.setColumnIdentifiers(column);
		Object[] row = new Object[3];
		for(Student s : absentees){
			System.out.println(s);
			em.sendEmail(s, prefix);
			row[0] = s.getRn();
			row[1] = s.getName();
			row[2] = s.getProgram();
			model.addRow(row);
		}
		JTable tableForAbsentees = new JTable(model);
		tableForAbsentees.setPreferredScrollableViewportSize(new Dimension(550,300));
		JScrollPane tableContainer = new JScrollPane(tableForAbsentees);
		absenteesPanel.add(tableContainer,BorderLayout.CENTER);
		absenteesPanel.setBorder(BorderFactory.createTitledBorder("Low Attendance Students"));
		return absenteesPanel;
	}
	
	private void populateTable(HashMap<Student, AData> items){
        clearAll();
		Object[] row = new Object[4];
		int attend = 0;
		int aa = 0;
		int attendanceCount = 0;
		int allCount = 0;
		DecimalFormat df = new DecimalFormat("#0.0");
		for(Student s : items.keySet()){
			attend = items.get(s).attendance;
			aa = items.get(s).contAll;
			attendanceCount += attend;
			allCount += aa;
			String percentage = df.format(((double)attend/(double)aa)*100) + "%";
			row[0] = s.getRn();
			row[1] = s.getName();
			row[2] = attend;
			row[3] = percentage;
			tableModel.addRow(row);
		}
		averageAttend = (double)attendanceCount/(double)allCount;
		//System.out.println(averageAttend);
		if(Double.isNaN(averageAttend))
		{
		//	System.out.println("23333");
			average.setText("Average Attendance: 0.0%");
		}
	    else
			average.setText("Average Attendance: " + df.format(averageAttend*100) + "%");
		average.updateUI();
	}
	
	private void clearAll(){
        table.removeAll();
		tableModel = new DefaultTableModel();
		tableModel.setColumnIdentifiers(column);
	    table.setModel(tableModel);
	}
	
	private void runSearchQuery(){
		new Thread(){
			@Override
			public void run() {
				Module m = (Module)moduleList.getSelectedItem();
				try{
					if(user.getProgram() != null && byProgram.isSelected()){
						rm.queryByProgram(c1.getDate(), c2.getDate(), user.getProgram());
						prefix = "Program - "+user.getProgram().getName()+": ";
					}
					else{
						rm.queryByModule(c1.getDate(), c2.getDate(), m);
						prefix = "Module - "+m+": ";
					}
				}
				catch (Exception e) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							statues.setText("Error");
							updateUI();
						}
					});
				}
				if(dataset != null){
					vbox.add(createChartPanel());
					if(!absentees.isEmpty()){
						vbox.add(createAbsenteePanel());
					}
					else{
						System.out.println("No absentees.");
					}
				}
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						statues.setText("Finish");
						updateUI();
					}
				});
			}
		}.start();
	}
	
	@Override
	public void update(Observable arg0, Object o) {
		if(o instanceof ReportNotification){
			HashMap<Student,AData> items = ((ReportNotification)o).getItems();
			populateTable(items);
			dataset = ((ReportNotification)o).getDataset();
			absentees = ((ReportNotification)o).getAbsenters();
		//	System.out.println("is empty" + absentees.isEmpty());
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("search")){
			statues.setText("Searching...");
			updateUI();
			runSearchQuery();
		}
	}
	
}
