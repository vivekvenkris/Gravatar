package firstjavafx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import bean.XYPair;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;

public class CopyOfMyLineChart<X,Y> extends LineChart<X, Y> {
	int i=1;
	public CopyOfMyLineChart(Axis<X> xAxis, Axis<Y> yAxis) {
		super(xAxis, yAxis);
	}
	@Override
	protected void layoutPlotChildren() {
		super.layoutPlotChildren();
		
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
			System.err.println("lst size"+lst.size());
			if(lst.size()==2){
				Series<X, Y> a = lst.get(0);
				Series<X, Y> b = lst.get(1);
				System.err.println("size:"+a.getData().size() + " "+b.getData().size());
				String str = a.getNode().getStyle().substring(a.getNode().getStyle().indexOf("-fx-stroke:"),a.getNode().getStyle().indexOf("-fx-stroke:")+20);
				str = str.split(":")[1].toLowerCase().substring(0,7);
				System.out.println("name:"+a.getName() +"color:"+str);
				ObservableList<Data<X, Y>> aDataList = a.getData();
				ObservableList<Data<X, Y>> bDataList = b.getData();
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
					polygon.setStrokeWidth(0);
					polygon.setFill(Color.web(str,0.2));
					polygon.getPoints().addAll(new Double[]{
							x1,y1,
							x1_2, y1_2,
							x2_2,y2_2,
							x2,y2
					});
					getPlotChildren().add(polygon);
					//polygon.toFront();
				}
			}
			else if(lst.size()==1){
				Series<X, Y> a = lst.get(0);
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

					MyPolygon polygon = new MyPolygon();
					polygon.setStrokeWidth(0);
					polygon.setFill(Color.web(str,0.2));
					polygon.getPoints().addAll(new Double[]{
							x1,y1,
							x1_2, y1_2,
							x1_2,y_0,
							x1,y_0

					});
					getPlotChildren().add(polygon);
					polygon.toFront();

				}

			}
		}
	}
}