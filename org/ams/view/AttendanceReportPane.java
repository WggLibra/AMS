package org.ams.view;


import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class AttendanceReportPane extends JPanel{

	private float[] data;
	private String prefix;
	
	public AttendanceReportPane(float[] dataset,String prefix) {
		this.data = dataset;
		this.prefix = prefix;
        ChartPanel cp = new ChartPanel(createChart(createDataset()));
        setLayout(new BorderLayout());
        add(cp,BorderLayout.CENTER);
	}
	
	private CategoryDataset createDataset(){
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(int i=0;i<data.length;i++){
//			System.out.println(i + " : " + data[i]*100);
			dataset.setValue(data[i]*100, prefix+"Attendance per calendar week", ""+(i+1));
		}
		return dataset;
	}
	
	private JFreeChart createChart(CategoryDataset dataset){
		final JFreeChart chart = ChartFactory.createBarChart3D(
				prefix+"Average Attendance Bar Chart", 
				"Week", 
				"Attendance(%)", 
				dataset, 
				PlotOrientation.VERTICAL, 
				true, 
				true, 
				false);
		final CategoryPlot plot = chart.getCategoryPlot();
        final CategoryAxis axis = plot.getDomainAxis();
        axis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 8.0));
        final BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
        renderer.setDrawBarOutline(false);
            
        return chart;
	}
	
}
