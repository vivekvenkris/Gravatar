package com.swin.bean;

import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import com.swin.manager.Calculator;
import com.swin.manager.ScalarTensorCalc;
import com.swin.util.Constants;

public class MMBean {
	PlotInputs plotInputs;
	private ObservableList<MMLine> lineList;
	private List<Boolean> activeList;
	private List<List<List<XYPair>>> pbDotList;
	private List<List<List<XYPair>>> omDotList;
	private List<List<List<XYPair>>> gammaList;
	private List<List<List<XYPair>>> massFuncList;
	private List<List<List<XYPair>>> massRatioList;
	private List<Polygon> intersection;
	int sigmaPbDot=1;
	int sigmaOmDot=1;
	int sigmaGamma=1;
	int sigmaMassRatio = 1;
	public MMBean(PlotInputs plotInputs){
		lineList= FXCollections.observableArrayList();
		this.plotInputs = plotInputs;
		this.initMMBean();
	}

	void initMMBean(){
		Calculator calculations =  new ScalarTensorCalc();
		pbDotList = calculations.getPbDot(plotInputs);
		omDotList = calculations.getOmDot(plotInputs);
		gammaList = calculations.getGamma(plotInputs);
		massFuncList= calculations.getMassFunc(plotInputs);
		massRatioList = calculations.getMassRatio(plotInputs);
		activeList = new ArrayList<Boolean>();
		lineList= FXCollections.observableArrayList();
		populateData();
	}
	public void recompute(){
		this.initMMBean();
	}

	public void  populateData(){
		Color c = null;
		MMLine oldLine = null;
		MMLine pbDotLine = new MMLine();
		pbDotLine.setName(Constants.pbdot);
		if(lineList.contains(pbDotLine)){
			if((oldLine = lineList.get(lineList.indexOf(pbDotLine))).isColorChanged()){
				System.err.println("old color "+ oldLine.getColor());
				c = oldLine.getColor();
				pbDotLine.setColorChanged(true);
			}
			else{
			//	System.err.println("default color 1");
				c = Color.BURLYWOOD;	
			}
		}
		else{
			//System.err.println("default color 2");
			c = Color.BURLYWOOD;
		}
		pbDotLine.setColor(c);
		addDataToLine(pbDotLine, pbDotList.get(sigmaPbDot),Constants.pbdot); 
		oldLine = null;

		MMLine omDotLine = new MMLine();
		omDotLine.setName(Constants.omdot);
		if(lineList.contains(omDotLine)){
			if((oldLine = lineList.get(lineList.indexOf(omDotLine))).isColorChanged()){
				c = oldLine.getColor();
				omDotLine.setColorChanged(true);
			}
			else
				c = Color.CORNFLOWERBLUE;	
		}
		else
			c = Color.CORNFLOWERBLUE;	
		omDotLine.setColor(c);
		addDataToLine(omDotLine, omDotList.get(sigmaOmDot),Constants.omdot); 
		oldLine = null;

		MMLine gammaLine = new MMLine();
		gammaLine.setName(Constants.gamma);
		if(lineList.contains(gammaLine)){
			if((oldLine = lineList.get(lineList.indexOf(gammaLine))).isColorChanged()){
				c = oldLine.getColor();
				gammaLine.setColorChanged(true);
			}
			else
				c = Color.DARKSEAGREEN;	
		}  
		else
			c = Color.DARKSEAGREEN;	
		gammaLine.setColor(c);
		addDataToLine(gammaLine, gammaList.get(sigmaGamma),Constants.gamma); 
		oldLine = null;


		MMLine massFuncLine = new MMLine();
		massFuncLine.setName(Constants.massfunc);
		if(lineList.contains(massFuncLine)){
			if((oldLine = lineList.get(lineList.indexOf(massFuncLine))).isColorChanged()){
				c = oldLine.getColor();
				massFuncLine.setColorChanged(true);
			}
			else
				c = Color.DARKKHAKI;	
		}     	
		else
			c = Color.DARKKHAKI;
		massFuncLine.setColor(c);
		addDataToLine(massFuncLine, massFuncList.get(0),Constants.massfunc); 
		oldLine = null;

		MMLine massRatioLine = new MMLine();
		massRatioLine.setName(Constants.massratio);
		if(lineList.contains(massRatioLine)){
			if((oldLine = lineList.get(lineList.indexOf(massRatioLine))).isColorChanged()){
				c = oldLine.getColor();
				massRatioLine.setColorChanged(true);
			}
			else
				c = Color.DARKVIOLET;	
		}     	
		else
			c = Color.DARKVIOLET;
		massRatioLine.setColor(c);
		addDataToLine(massRatioLine, massRatioList.get(sigmaMassRatio),Constants.massratio); 
		oldLine = null;

		lineList= FXCollections.observableArrayList();

		lineList.add(pbDotLine);
		activeList.add(true);

		lineList.add(omDotLine);
		activeList.add(true);

		lineList.add(gammaLine);
		activeList.add(true);

		lineList.add(massFuncLine);
		activeList.add(true);

		//lineList.add(massRatioLine);
		//activeList.add(true);

		List<List<List<XYPair>>> dataList = new ArrayList<List<List<XYPair>>>();
		dataList.add(gammaList.get(sigmaGamma));
		dataList.add(pbDotList.get(sigmaPbDot));
		dataList.add(omDotList.get(sigmaOmDot));
		//dataList.add(massRatioList.get(sigmaMassRatio));
		this.intersection =  findIntersection(dataList);


	}

	/* List <XY pair> tuples for a particular sigma
	 * List<List<XY pair>> list of tuple for the same sigma on either side
	 * List<List<List<XY pair>>>> sever lines of list of same sigma tuples */

	List<Polygon> findIntersection(List<List<List<XYPair>>> dataList){
		List<List<Polygon>> linePolygons = new ArrayList<List<Polygon>>();
		List<Polygon> intersectPolygons  = new ArrayList<Polygon>();

		for(List<List<XYPair>> lines:dataList){
			if(lines.size()!=2){
				System.err.println("bad lines");
				break;
			}
			List<XYPair> line1 = lines.get(0);
			List<XYPair> line2 = lines.get(1);
			if(line1.size()!=line2.size()){
				System.err.println("different lines");
				break;
			}
			List<Polygon> polygons = new ArrayList<Polygon>();
			for(int i=0;i<line1.size()-1;i++){

				XYPair pt1 = line1.get(i);
				XYPair pt2 = line2.get(i);

				XYPair pt3 = line1.get(i+1);
				XYPair pt4 = line2.get(i+1);
				
				Polygon polygon = new Polygon();
				polygon.getPoints().addAll(new Double[] {

						pt1.getX(),pt1.getY(),
						pt3.getX(),pt3.getY(),
						pt2.getX(),pt2.getY(),
						pt4.getX(),pt4.getY(),

				});
				polygons.add(polygon);
				polygon.setStroke(Color.BLACK);
				polygon.setFill(Color.BLACK);
			}
			linePolygons.add(polygons);
		}
	//	System.err.println("linePolygons.size() + intersectPolygons.size() "+linePolygons.size()+" "+intersectPolygons.size());
		LinkedList<Polygon> intersected = new LinkedList<Polygon>();
		List<Polygon> saved = new ArrayList<Polygon>();
		//findIntersections(intersected, linePolygons, saved);
		//System.err.println(saved.size());
//		Polygon minAreaPolygon = findMinAreaOfIntersection(saved);
//		List<Polygon> ret  =  new ArrayList<Polygon>();
//		ret.add(minAreaPolygon);
		return saved;
	}
		
//		if(linePolygons.size()>1 ){
//			List<Polygon> firstLine = linePolygons.get(0);
//			List<Polygon> secondLine = linePolygons.get(1);
//			List<Polygon> intersected = new ArrayList<Polygon>();
//			List<Polygon> saved = new ArrayList<Polygon>();
//			for(Polygon poly1:firstLine){
//				for(Polygon poly2:secondLine){
//					int intersect  = 0;
//					intersected.clear();
//					if(poly1.intersects(poly2.getBoundsInLocal())){
//						intersected.add(poly1);
//						intersected.add(poly2);
//						saved.add(poly1);
//						saved.add(poly2);
//						System.err.println(linePolygons.subList(2, linePolygons.size()).size());
//						findIntersections(intersected, linePolygons.subList(2, linePolygons.size()),saved);
//						if(saved.size()>linePolygons.size())
//							intersectPolygons.addAll(saved );
//						else
//							System.err.println(intersected.size()+";"+linePolygons.size());
//						//	System.err.println("2 lines intersect");
//						intersections.add(poly1);
//						intersections.add(poly2);
//						intersect++;
//						List<Polygon> iIntersectPoly = new ArrayList<Polygon>();
//						if(linePolygons.size()==2){
//							//System.err.println(" 2 lines Intersects!!!!!!!!!!");
//							iIntersectPoly.add(poly1);
//							iIntersectPoly.add(poly2);
//							intersectPolygons.addAll(iIntersectPoly);
//						}
//						for(int i=2;i<linePolygons.size();i++){
//							List<Polygon> iLine = linePolygons.get(i);
//							boolean intersected = false;
//							for(Polygon iPoly: iLine){
//								if(poly1.intersects(iPoly.getBoundsInLocal()) && poly2.intersects(iPoly.getBoundsInLocal())){
//									System.err.println("here----");
//									//printPolygon(poly1,iPoly);
//									iIntersectPoly.add(iPoly);
//									intersected = true;
//								}
//							}
//							if(intersected){
//								intersect++;
//							}
//						}
//
//						if(intersect== linePolygons.size()-1){
//							System.err.println("It Intersects!!!!!!!!!!");
//							System.err.println("Poly1");
//							printPolygon(poly1);
//							System.err.println("Poly2");
//							printPolygon(poly2);
//							System.err.println("Polylist");
//							for(Polygon polygon:iIntersectPoly)
//								printPolygon(polygon);
//							
//							iIntersectPoly.add(poly1);
//							iIntersectPoly.add(poly2);
//							intersectPolygons.addAll((iIntersectPoly));
//						}
//						else{
//							System.err.println("does not intersect -- "+ intersect + " "+linePolygons.size());
//						}
//					}
//				}
//			//}
//		}
		
	
	void findIntersections(LinkedList<Polygon> intersected, List<List<Polygon>> linePolygons,List<Polygon> saved){
		if(linePolygons.size()>0){
			List<Polygon> iLine = linePolygons.get(0);
			boolean intersects = false;
			int r = 0;
			for(Polygon iPoly: iLine){
				intersects = false;
				if(intersected.isEmpty()){
					intersected.add(iPoly);
					//System.err.println("++r:"+ ++r);
				//	System.err.println("was empty now:"+intersected.size() + " "+linePolygons.size());
					findIntersections(intersected, linePolygons.subList(1, linePolygons.size()),saved);
				//	System.err.println("--r:"+ --r);
					intersected.clear();
				//	System.err.println("came out");
				}
				else if(intersects(intersected, iPoly)){
					intersects = true;
					intersected.add(iPoly);
				//	System.err.println("adding - "+intersected.size());
					findIntersections(intersected, linePolygons.subList(1, linePolygons.size()),saved);

				}
				else{
					//System.err.println("removing - "+intersected.size());
					
				}
			}

		}
		else{
		//	System.err.println("saving "+ intersected.size());
			for(Polygon polygon: intersected){
				if(!saved.contains(polygon)){
					saved.add(polygon);
				}
			}
			intersected.removeLast();
		}
	}
	
	boolean intersects2(List<Polygon> intersected,Polygon polygon){
		int intersects = 0;
		List<Double> polygonPts = polygon.getPoints();
		//Line2D line1 = new Line2D.Float(polygonPts.get(0).floatValue(),polygonPts.get(1).floatValue(),polygonPts.get(2).floatValue(),polygonPts.get(3).floatValue());
		//Line2D line2 = new Line2D.Float(polygonPts.get(4).floatValue(),polygonPts.get(5).floatValue(),polygonPts.get(6).floatValue(),polygonPts.get(7).floatValue());
		for(Polygon iPoly: intersected) {
			List<Double> iPolyPts = iPoly.getPoints();
			//Line2D line3 = new Line2D.Float(iPolyPts.get(0).floatValue(),iPolyPts.get(1).floatValue(),iPolyPts.get(2).floatValue(),iPolyPts.get(3).floatValue());
			//Line2D line4 = new Line2D.Float(iPolyPts.get(4).floatValue(),iPolyPts.get(5).floatValue(),iPolyPts.get(6).floatValue(),iPolyPts.get(7).floatValue());
			if(Line2D.linesIntersect(polygonPts.get(0),polygonPts.get(1),polygonPts.get(2),polygonPts.get(3),
					iPolyPts.get(0),iPolyPts.get(1),iPolyPts.get(2),iPolyPts.get(3)));
			else if(Line2D.linesIntersect(polygonPts.get(0),polygonPts.get(1),polygonPts.get(2),polygonPts.get(3),
					iPolyPts.get(4),iPolyPts.get(5),iPolyPts.get(6),iPolyPts.get(7)));
			else if(Line2D.linesIntersect(polygonPts.get(4),polygonPts.get(5),polygonPts.get(6),polygonPts.get(7),
					iPolyPts.get(0),iPolyPts.get(1),iPolyPts.get(2),iPolyPts.get(3)));
			else if(Line2D.linesIntersect(polygonPts.get(4),polygonPts.get(5),polygonPts.get(6),polygonPts.get(7),
					iPolyPts.get(4),iPolyPts.get(5),iPolyPts.get(6),iPolyPts.get(7)));
			else
			 intersects++;
			
		}
		if(intersects==intersected.size()) return true;
		return false;
	}

	boolean pointIntersects(List<XYPair> polyPairs,XYPair point){
		boolean result = false;
		for (int i = 0, j = polyPairs.size()-1; i < polyPairs.size(); j = i++) {
			 if ((polyPairs.get(i).getY() > point.getY()) != (polyPairs.get(j).getY() > point.getY()) &&
			            (point.getX()< (polyPairs.get(j).getX() - polyPairs.get(i).getX()) * 
			            		(point.getY() - polyPairs.get(i).getY()) / (polyPairs.get(j).getY()-polyPairs.get(i).getY())
			            		+polyPairs.get(i).getX())) {
			          result = !result;
			         }
		}
		return result;
	}
	List<XYPair> getPairs(List<Double> values){
		List<XYPair> pairList = new ArrayList<XYPair>();
		for(int i=0;i<values.size();i=i+2){
			XYPair pair = new XYPair(values.get(i),values.get(i+1));
			pairList.add(pair);
		}
		
		return pairList;
		
	}
	/*
	 * returns true if the given polygon intersects the already intersected.
	 */
	boolean intersects(List<Polygon> intersected,Polygon polygon){
		int i=0;
		for(Polygon iPoly: intersected)
			if(polygon.intersects(iPoly.getBoundsInLocal()))
				i++;
			else{
//				for( int j=0; j< iPoly.getPoints().size();j=j+2){
//					double x = iPoly.getPoints().get(j);
//					double y = iPoly.getPoints().get(j+1);
//					if(polygon.contains(x,y)){
//						i++;
//						break;
//					}
//				}
			}
			
		if(i==intersected.size()) return true;
		return false;
	}


	void printPolygon(Polygon... polygons){
		for(Polygon polygon:polygons){
			double minx = polygon.getBoundsInLocal().getMinX();
			double miny = polygon.getBoundsInLocal().getMinY();
			double maxx = polygon.getBoundsInLocal().getMaxX();
			double maxy = polygon.getBoundsInLocal().getMaxY();
			System.err.println("("+minx+","+miny+")-("+maxx+","+maxy+")");
		}
	}
	Polygon findMinAreaOfIntersection(List<Polygon> polygons){
		List<Double> areaList = new ArrayList<Double>();
		for(Polygon polygon: polygons){
			double minx = polygon.getBoundsInLocal().getMinX();
			double miny = polygon.getBoundsInLocal().getMinY();
			double maxx = polygon.getBoundsInLocal().getMaxX();
			double maxy = polygon.getBoundsInLocal().getMaxY();
			Double area = Math.abs((minx*maxy)-(miny*maxx))/2.0;
			areaList.add(area);
		}
		return polygons.get(areaList.indexOf(Collections.min(areaList)));

	}
	void addDataToLine(MMLine line,List<List<XYPair>> pairList, String name){
		ObservableList<XYChart.Series<Number, Number>> seriesList = line.getSeriesList();
		for(List<XYPair> signList: pairList){
			XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
			for(XYPair pair: signList){
				series.getData().add(new Data<Number,Number>(pair.getX(),pair.getY()));
				
			}
			series.setName(name);
			seriesList.add(series);
		}

	}

	public List<Boolean> getActiveList() {
		return activeList;
	}

	public void setActiveList(List<Boolean> activeList) {
		this.activeList = activeList;
	}


	public PlotInputs getPlotInputs() {
		return plotInputs;
	}

	public void setPlotInputs(PlotInputs plotInputs) {
		this.plotInputs = plotInputs;
	}

	public ObservableList<MMLine> getLineList() {
		return lineList;
	}

	public void setLineList(ObservableList<MMLine> lineList) {
		this.lineList = lineList;
	}

	public int getSigmaPbDot() {
		return sigmaPbDot;
	}

	public void setSigmaPbDot(int sigmaPbDot) {
		this.sigmaPbDot = sigmaPbDot;
	}

	public int getSigmaOmDot() {
		return sigmaOmDot;
	}

	public void setSigmaOmDot(int sigmaOmDot) {
		this.sigmaOmDot = sigmaOmDot;
	}

	public int getSigmaGamma() {
		return sigmaGamma;
	}

	public void setSigmaGamma(int sigmaGamma) {
		this.sigmaGamma = sigmaGamma;
	}

	public int getSigmaMassRatio() {
		return sigmaMassRatio;
	}

	public void setSigmaMassRatio(int sigmaMassRatio) {
		this.sigmaMassRatio = sigmaMassRatio;
	}

	public List<Polygon> getIntersection() {
		return intersection;
	}

	public void setIntersection(List<Polygon> intersection) {
		this.intersection = intersection;
	}



}
