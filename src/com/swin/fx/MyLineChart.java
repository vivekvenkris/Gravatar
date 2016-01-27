package com.swin.fx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.swin.bean.XYPair;
import com.swin.util.Constants;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class MyLineChart<X,Y> extends LineChart<X, Y> {
	List<Node> polyList = new ArrayList<Node>();
	List<Polygon> intersection = new ArrayList<Polygon>();
	Set<XYPair> circlePoints = new HashSet<XYPair>();
	Color intersectionColor = Color.DARKBLUE;
	int i=1;
	public MyLineChart(Axis<X> xAxis, Axis<Y> yAxis) {
		super(xAxis, yAxis);
	}
	@SuppressWarnings("unchecked")
	@Override
	protected void layoutPlotChildren() {
		try {
		getPlotChildren().removeAll(polyList);
		polyList.clear();
		ObservableList<Series<X, Y>> list=getData();
		Map<String, ArrayList<Series<X, Y>>> seriesMap = new HashMap<String, ArrayList<Series<X,Y>>>();
		for(int i=0;i<list.size();i++){
			Series<X, Y> series = list.get(i);
			ArrayList<Series<X, Y>> groupList = seriesMap.get(series.getName());
			if(groupList==null){
				groupList = new ArrayList<Series<X, Y>>();
			}
			groupList.add(series);
			seriesMap.put(series.getName(), groupList);
		}
		Iterator<Entry<String, ArrayList<Series<X, Y>>>> it = seriesMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ArrayList<Series<X, Y>>> pair = (Map.Entry<String, ArrayList<Series<X, Y>>>)it.next();
			ArrayList<Series<X, Y>> lst = pair.getValue();
			//System.err.println("lst size"+lst.size());
			if(lst.size()==2){
				Series<X, Y> a = lst.get(0);
				Series<X, Y> b = lst.get(1);
				//System.err.println("size:"+a.getData().size() + " "+b.getData().size());
				String str = a.getNode().getStyle().substring(a.getNode().getStyle().indexOf("-fx-stroke:"),a.getNode().getStyle().indexOf("-fx-stroke:")+20);
				str = str.split(":")[1].toLowerCase().substring(0,7);
			//	System.out.println("name:"+a.getName() +"color:"+str);
				ObservableList<Data<X, Y>> aDataList = a.getData();
				ObservableList<Data<X, Y>> bDataList = b.getData();
				if(aDataList.size()==bDataList.size())
				for( int i=0; i<aDataList.size()-1;i++){
					Data<X, Y> aData = aDataList.get(i);
					Data<X, Y> bData = bDataList.get(i);
					double x1 = getXAxis().getDisplayPosition(aData.getXValue());
					double y1 = getYAxis().getDisplayPosition(aData.getYValue());
					double x2 = getXAxis().getDisplayPosition(bData.getXValue());
					double y2 = getYAxis().getDisplayPosition(bData.getYValue());

					Data<X, Y> aData_2 = aDataList.get(i+1);
					Data<X, Y> bData_2 = bDataList.get(i+1);
					double x1_2 = getXAxis().getDisplayPosition(aData_2.getXValue());
					double y1_2 = getYAxis().getDisplayPosition(aData_2.getYValue());
					double x2_2 = getXAxis().getDisplayPosition(bData_2.getXValue());
					double y2_2 = getYAxis().getDisplayPosition(bData_2.getYValue());
					Polygon polygon = new Polygon();
					//						LinearGradient linearGrad = new LinearGradient( 0, 0, 0, 1,
					//								true, // proportional
					//								CycleMethod.NO_CYCLE, // cycle colors
					//								new Stop(0.1f, Color.rgb(255, 0, 0, .3)));
					//polygon.setStrokeWidth(2);
					//polygon.setStroke(Color.web(str));
					polygon.setFill(Color.web(str,0.2));
					polygon.setStroke(Color.web(str,0.6));
					polygon.getPoints().addAll(new Double[]{
							x1,y1,
							x1_2, y1_2,
							x2_2,y2_2,
							x2,y2
					});
					getPlotChildren().add(polygon);
					polyList.add(polygon);
					//polygon.toFront();
				}
				
			}
			else if(lst.size()==1){
				Series<X, Y> a = lst.get(0);
				if(a.getName()==Constants.massratio) continue;
				String str = a.getNode().getStyle().substring(a.getNode().getStyle().indexOf("-fx-stroke:"),a.getNode().getStyle().indexOf("-fx-stroke:")+20);
				str = str.split(":")[1].toLowerCase().substring(0,7);
				System.out.println("name:"+a.getName() +"color:"+str);
				ObservableList<Data<X, Y>> aDataList = a.getData();
				for( int i=0; i<aDataList.size()-1;i++){
					Data<X, Y> aData = aDataList.get(i);
					double x1 = getXAxis().getDisplayPosition(aData.getXValue());
					double y1 = getYAxis().getDisplayPosition(aData.getYValue());

					Data<X, Y> aData_2 = aDataList.get(i+1);
					double x1_2 = getXAxis().getDisplayPosition(aData_2.getXValue());
					double y1_2 = getYAxis().getDisplayPosition(aData_2.getYValue());

					@SuppressWarnings("unchecked")
					double y_0 = getYAxis().getDisplayPosition((Y)new Integer(0));

					Polygon polygon = new Polygon();
					//polygon.setStrokeWidth(0);
				//polygon.setStroke(Color.web(str,0.2));
					polygon.setFill(Color.web(str,0.2));
					polygon.getPoints().addAll(new Double[]{
							x1,y1,
							x1_2, y1_2,
							x1_2,y_0,
							x1,y_0

					});
					getPlotChildren().add(polygon);
					polygon.toFront();
					polyList.add(polygon);


				}

			}
		}
		for(int i=0;i<intersection.size()-1;i++){
			Polygon polygon = intersection.get(i);
			Polygon polygonDisplay = new Polygon();
			
			ObservableList<Double> ptList = polygon.getPoints();
			if(ptList.size()!=8){
				System.err.println("Problem mistyped polygon.");
			}
			polygonDisplay.getPoints().addAll(new Double[] {
					getXAxis().getDisplayPosition((X)ptList.get(0)),getYAxis().getDisplayPosition((Y)ptList.get(1)),
					getXAxis().getDisplayPosition((X)ptList.get(4)),getYAxis().getDisplayPosition((Y)ptList.get(5)),
					getXAxis().getDisplayPosition((X)ptList.get(6)),getYAxis().getDisplayPosition((Y)ptList.get(7)),
					getXAxis().getDisplayPosition((X)ptList.get(2)),getYAxis().getDisplayPosition((Y)ptList.get(3)),
			});
			
			polygonDisplay.setFill(Color.DARKCYAN);
			polygonDisplay.setStroke(Color.DARKCYAN);
			getPlotChildren().addAll(polygonDisplay);
			
			polygonDisplay.toBack();
			polyList.add(polygonDisplay);
		}
		for(XYPair pair: circlePoints){
			
			Circle circle = new Circle(getXAxis().getDisplayPosition((X)pair.getX()),getYAxis().getDisplayPosition((Y)pair.getY()),0.5);
			circle.setFill(intersectionColor);
			circle.setStroke(intersectionColor);
			getPlotChildren().add(circle);
			circle.toFront();
			polyList.add(circle);
		}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		super.layoutPlotChildren();

	}
	public boolean comparePolygons(Polygon p1, Polygon p2){
		Bounds b1 = p1.getBoundsInLocal();
		Bounds b2 = p2.getBoundsInLocal();
		return (b1.getMinX() == b2.getMinX()) && (b1.getMinY() == b2.getMinY()) && (b1.getMaxX() == b2.getMaxX()) && (b1.getMaxY() == b2.getMaxY());
	}
	
	public Color getRandomColor(){
		return new Color(Math.random(), Math.random(), Math.random(), 0.5);
		
	}
	
	
	public List<Polygon> getIntersection() {
		return intersection;
	}
	public void setIntersection(List<Polygon> intersection) {
		this.intersection = intersection;
	}
	public Set<XYPair> getCirclePoints() {
		return circlePoints;
	}
	public void setCirclePoints(Set<XYPair> circlePoints) {
		this.circlePoints = circlePoints;
	}
	public Color getIntersectionColor() {
		return intersectionColor;
	}
	public void setIntersectionColor(Color intersectionColor) {
		this.intersectionColor = intersectionColor;
	}

	
	
}