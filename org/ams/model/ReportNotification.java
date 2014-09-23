package org.ams.model;


import java.util.ArrayList;
import java.util.HashMap;

import org.ams.db.Student;
import org.ams.model.ReportModel.AData;

public class ReportNotification {
	private HashMap<Student,AData> items;
	private ArrayList<Student> absenters;
	private float[] dataset;

	public HashMap<Student, AData> getItems() {
		return items;
	}

	public void setItems(HashMap<Student, AData> items) {
		this.items = items;
	}

	public ArrayList<Student> getAbsenters() {
		return absenters;
	}

	public void setAbsenters(ArrayList<Student> absenters) {
		this.absenters = absenters;
	}

	public float[] getDataset() {
		return dataset;
	}

	public void setDataset(float[] dataset) {
		this.dataset = dataset;
	}

   
   
	
}
