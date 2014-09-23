package org.ams.view;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import org.ams.db.Module;
import org.ams.db.Record;
import org.ams.db.User;
import org.ams.model.ResultNotification;
import org.ams.model.UploadModel;

import com.toedter.calendar.JDateChooser;

public class UploadManager extends JPanel implements Observer,ActionListener{

	JButton select;
	JButton upload;
	TextField file_loc;
	JComboBox modulesBox;
	JDateChooser attDate;
	UploadModel um;
	User u;
	Set<Module> modules;
	 InputStream fis ;
	
	//JList list_modulesList;
	//List<Module> modules2;
	//DefaultListModel listModel_modules;
	
	public UploadManager(User u){
		um = new UploadModel();
		this.u = u;
		add(createAllUploadPanel());
		
		um.addObserver(this);
	}
	
	private JPanel createAllUploadPanel(){
		JPanel panel = new JPanel();
		
		file_loc = new TextField();
		
		select = new JButton("Select");
		JButton clear = new JButton("Modules");
		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayout(0,2,10,10));
		
		select.addActionListener(this);
		clear.addActionListener(this);
		
		JLabel date_label = new JLabel("Select date:");
		attDate = new JDateChooser();
		
		JLabel modules_label = new JLabel("Select module");		
		modulesBox = new JComboBox();
		
		upload = new JButton("Upload");
		upload.addActionListener(this);
		panel.setLayout(new GridLayout(4,2,55,10));
		
		panel.add(file_loc);
		
		panel1.add(select);
		panel1.add(clear);
		panel.add(panel1);
		panel.add(date_label);
		panel.add(attDate);
		panel.add(modules_label);
		panel.add(modulesBox);
		panel.add(upload);
		panel.setBorder(BorderFactory.createTitledBorder("Upload attendance spreadsheet"));
		//panel1.setBorder(BorderFactory.createTitledBorder("Select file"));
		panel.setPreferredSize(new Dimension(400, 200));
		panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		return panel;
	}
	
	public void getModules(){
		//list all modules as String objects and add them to the JComboBox
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Select")){
			
			JFileChooser fileChooser = new JFileChooser(".");
			    fileChooser.setFileFilter(new ExtensionFileFilter());
			    int status = fileChooser.showOpenDialog(null);
			    if (status == JFileChooser.APPROVE_OPTION) {
			      File selectedFile = fileChooser.getSelectedFile();
			      file_loc.setText(selectedFile.getAbsolutePath());
			    } else if (status == JFileChooser.CANCEL_OPTION) {
			      System.out.println(JFileChooser.CANCEL_OPTION);
			    }
			
		}
		else if(e.getActionCommand().equals("Upload")){
			if(file_loc.getText() == ""){
				JOptionPane.showMessageDialog(this,"Please select a file from Your computer");
			}
			else{
				init(file_loc.getText());
			}
		}
		
		else if(e.getActionCommand().equals("Modules")){
			modules = u.getModules();
			ArrayList<Module> ml = new ArrayList<Module>();
			ml.addAll(modules);
			modulesBox.removeAllItems();
			for(int i=0; i<modules.size(); i++){
				modulesBox.addItem(ml.get(i));
			}
		}

			
		
			
	}
	
	public void init(String filePath) {
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(new File(filePath));
			contentReading(fs);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void contentReading(InputStream fileInputStream) {
		this.fis= fileInputStream;
		new Thread(){
			
			@Override
			public void run() {
				
				WorkbookSettings ws = null;
				Workbook workbook = null;
				Sheet s = null;
				try {
					ws = new WorkbookSettings();
					ws.setLocale(new Locale("en", "EN"));
					workbook = Workbook.getWorkbook(fis, ws);			
					s = workbook.getSheet(0);
					um.uploadXLS((Module)modulesBox.getSelectedItem(), s, attDate.getDate());
		            
			
				} catch (IOException e) {
					e.printStackTrace();
				} catch (BiffException e) {
					e.printStackTrace();
				}
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}		
		}.start();
	
	}
	

	
	

	@Override
	public void update(Observable arg0, Object o) {
		if(o instanceof ResultNotification){
			
		   if(((ResultNotification) o).isSuccess){
			   JOptionPane.showMessageDialog(this,"upload success");
		   }
		   else{
			   JOptionPane.showMessageDialog(this,"upload failed");
		   }
		   
		}
		
		
	}
	
}

class ExtensionFileFilter extends FileFilter {
	public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".xls");
    }
    
    public String getDescription() {
        return "XLS files";
    }
	 }

