package com.trash;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;


public class Trash2 extends Application{
	
	/*w  w w .j a  v  a  2 s .  c  o  m*/

	  @Override
	  public void start(Stage stage) {
	    final NumberAxis xAxis = new NumberAxis();
	    final LogarithmicAxis yAxis = new LogarithmicAxis();
	    xAxis.setLabel("Number of Month");
	    final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(
	        xAxis, yAxis);
	    yAxis.setUpperBound(0.1);
	    lineChart.setTitle("Line Chart");
	    XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
	    series.setName("My Data");
	    // populating the series with data
	    series.getData().add(new XYChart.Data<Number, Number>(1, 0.05));
	    series.getData().add(new XYChart.Data<Number, Number>(2, 0.1));
	    series.getData().add(new XYChart.Data<Number, Number>(3, 0.02));
	    series.getData().add(new XYChart.Data<Number, Number>(4, 0.1));

	    Scene scene = new Scene(lineChart, 800, 600);
	    lineChart.getData().add(series);

	    stage.setScene(scene);
	    stage.show();
	  }

	  public static void main(String[] args) {
	    launch(args);
	  }
	}