package com.swin.bean;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

public class MMLine {
	private ObservableList<XYChart.Series<Number,Number>> seriesList;
	private String name;
	private Color color;
	private boolean colorChanged;
	private boolean isActive;
	private double sigma=1;


	public MMLine() {
		seriesList =FXCollections.observableArrayList();
		isActive=true;
	} 
	public MMLine(String name){
		this();
		this.name = name;

	}

	@Override
	public boolean equals(Object obj) {
		return this.name.equals(((MMLine)obj).getName());
	}
	

	public double getSigma() {
		return sigma;
	}
	public void setSigma(double sigma) {
		this.sigma = sigma;
	}
	public ObservableList<XYChart.Series<Number, Number>> getSeriesList() {
		return seriesList;
	}



	public boolean isColorChanged() {
		return colorChanged;
	}

	public void setColorChanged(boolean colorChanged) {
		this.colorChanged = colorChanged;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setSeriesList(
			ObservableList<XYChart.Series<Number, Number>> seriesList) {
		this.seriesList = seriesList;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}


}
