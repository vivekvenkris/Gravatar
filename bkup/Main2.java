package bkup;



import java.io.File;

import org.scenicview.ScenicView;

import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import manager.Calculations;
import manager.ParParser;
import utilities.Constants;
import bean.MMBean;
import bean.MMLine;
import bean.ParFile;


public class Main2 extends Application {
	static ParFile parFile;
	Calculations calculations = new Calculations();
	MMBean mmBean;
	private Pane layout = new HBox();
	Stage stage;
	
	@Override
	public void init() throws Exception { }
	@Override
	public void start(Stage primaryStage) {
		System.out.println(com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());

		Double xmin=0.0,ymin=0.0;
		Double xmax=2.0,ymax=2.0;
		final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Pulsar Mass");
        yAxis.setLabel("Companion Mass");
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        xAxis.setLowerBound(xmin);
        xAxis.setUpperBound(xmax);
        yAxis.setLowerBound(ymin);
        yAxis.setUpperBound(ymax);
//        xAxis.setLowerBound(1.2);
//        xAxis.setUpperBound(1.4);
//        yAxis.setLowerBound(0.9);
//        yAxis.setUpperBound(1.1);
        final LineChart<Number, Number> lineChart = new LineChart<Number,Number>(xAxis, yAxis);
        
        final StackPane chartContainer = new StackPane();
		chartContainer.getChildren().add(lineChart);
        final Rectangle zoomRect = new Rectangle();
        chartContainer.getChildren().add(zoomRect);
		zoomRect.setManaged(false);
		zoomRect.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
		setUpZooming(zoomRect, lineChart);
        
		
		final Button zoomButton = new Button("Zoom");
		final Button resetButton = new Button("Reset");
		zoomButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                doZoom(zoomRect, lineChart);
            }
        });
		resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final NumberAxis xAxis = (NumberAxis)lineChart.getXAxis();
                xAxis.setLowerBound(0);
                xAxis.setUpperBound(2.5);
                final NumberAxis yAxis = (NumberAxis)lineChart.getYAxis();
                yAxis.setLowerBound(0);
                yAxis.setUpperBound(2.5);
                
                zoomRect.setWidth(0);
                zoomRect.setHeight(0);
            }
        });
		final BooleanBinding disableControls = 
		        zoomRect.widthProperty().lessThan(5)
		        .or(zoomRect.heightProperty().lessThan(5));
		zoomButton.disableProperty().bind(disableControls);
           

        lineChart.setTitle("mass-mass diagram");
        lineChart.setCreateSymbols(false);	
        lineChart.setAnimated(false);
        Button parButton = new Button("select par");
        TextField parText = new TextField("select par");
        HBox parBox = new HBox(10,parText,parButton);
        HBox.setHgrow(parText, Priority.ALWAYS);
        HBox.setHgrow(parButton, Priority.ALWAYS);
        
        
        VBox box2 = new VBox(10,parBox,zoomButton,resetButton);
        HBox controlledChart =  new HBox(10,
        		chartContainer,box2
          );
          controlledChart.setAlignment(Pos.CENTER);
          HBox.setHgrow(lineChart, Priority.ALWAYS);
          layout.setStyle(" -fx-padding: 10;");
          layout.getChildren().addAll(controlledChart);
          chartContainer.setVisible(false);
        

        parButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
		        fileChooser.setTitle("Open Par File");
		        File parIOFile = fileChooser.showOpenDialog(primaryStage);
		        if(parIOFile!=null) parText.setText(parIOFile.getName());
		        ParParser parParser = new ParParser(parIOFile);
				parFile = parParser.parsePar();
				parFile.setMassFunc(0.171706);
				mmBean= new MMBean(parFile);
				calculations.calculate(mmBean);
		        populateData(mmBean, lineChart);   
		        final VBox selectBoxes = new VBox(10);
		        selectBoxes.setStyle("-fx-padding: 10;");
		        final TitledPane controlPane = new TitledPane("PKline Selection", selectBoxes);
		        controlPane.setCollapsible(false);   
		        for(MMLine line: mmBean.getLineList()){
		        	System.out.println(line.getName());
		        	final ToggleGroup group = new ToggleGroup();
		        	RadioButton onesigma = new RadioButton("1"+Constants.sigma);
		        	onesigma.setToggleGroup(group);
		        	onesigma.setSelected(true);
		        	onesigma.setUserData(new String(line.getName()+"_"+new Integer(1)));
		        	
		        	RadioButton twoSigma = new RadioButton("2"+Constants.sigma);
		        	twoSigma= new RadioButton("2"+Constants.sigma);
		        	twoSigma.setToggleGroup(group);
		        	twoSigma.setUserData(new String(line.getName()+"_"+new Integer(2)));

		        	
		        	RadioButton threeSigma = new RadioButton("3"+Constants.sigma);
		        	threeSigma= new RadioButton("3"+Constants.sigma);
		        	threeSigma.setToggleGroup(group);
		        	threeSigma.setUserData(new String(line.getName()+"_"+new Integer(3)));

		        	

			        final CheckBox box = new CheckBox(line.getName());
			        box.setSelected(true);
			        Line lineseg = new Line(0, 10, 50, 10);
			        StringBuilder styleString = new StringBuilder("-fx-stroke-width: 3; -fx-stroke: gray;");
			        if (line.getStrokeDashArray() != null && !line.getStrokeDashArray().isEmpty()) {
			          styleString.append("-fx-stroke-dash-array: ").append(line.getStrokeDashArray()).append(";");
			        }
			        lineseg.setStyle(styleString.toString());
			        
			        box.setGraphic(lineseg);
			        HBox hbox;
			        if(line.getName().equals(Constants.massfunc))  hbox = new HBox(10,box);
			        else  hbox = new HBox(10,box,onesigma,twoSigma,threeSigma);
			        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			            public void changed(ObservableValue<? extends Toggle> ov,
			                Toggle old_toggle, Toggle new_toggle) {
			                    if (group.getSelectedToggle() != null) {
			                        String name_sigma =(String) group.getSelectedToggle().getUserData();
			                        System.out.println(name_sigma);
			                        String name = name_sigma.split("_")[0];
			                        Integer sigma = Integer.parseInt(name_sigma.split("_")[1]);
			                        switch(name){
			                        case Constants.pbdot:
			                        	mmBean.setSigmaPbDot(sigma);
			                        	break;
			                        case Constants.gamma:
			                        	mmBean.setSigmaGamma(sigma);
			                        	break;
			                        case Constants.omdot:
			                        	mmBean.setSigmaOmDot(sigma);
			                        	break;
			                        }
			                        mmBean.populateData();
			        	            lineChart.getData().clear();
			        	            populateData(mmBean, lineChart);

			                    }                
			                }
			        });
			        selectBoxes.getChildren().add(hbox);
			        box.setOnAction(action -> {
			        	System.out.println(mmBean.getLineList().indexOf(line));
			        	mmBean.getActiveList().set(mmBean.getLineList().indexOf(line), box.isSelected());
			        	//line.setActive(box.isSelected());
			            lineChart.getData().clear();
			            populateData(mmBean, lineChart);
			            //styleSeries(events, lineChart);
			          }
			        );
		        }
		        box2.getChildren().add(box2.getChildren().size()-2,controlPane);
		        chartContainer.setVisible(true);
			}
		});
       	
	
		try {
			primaryStage.setTitle("Mass - Mass Diagram");
	          Scene scene = new Scene(layout, 800, 600);
	          primaryStage.setScene(scene);
	        //  ScenicView.show(scene);
	          primaryStage.show();
	        
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String file = "jumpy.par";

//	    parFile = new ParFile();
//
//		Double massFunc=0.171706;
//		Double eccintricity=0.17188097150810268052;
//		Double pb= 0.19765096237922123941;
//		Double omDot=5.3099519151404161437;
//		Double eOmDot=0.00024106232441522529;
//		Double gamma=0.00074464069051630476359;
//		Double eGamma=0.00000746451486205490;
//		Double pbDot=-3.9462909083283078272e-13 + 6.71e-15 -5.05e-15 ;
//		Double ePbDot= 1.0003284543358517209e-14 +1.73e-15 +0.44e-15;
//
//		parFile.setMassFunc(massFunc);  //mass function
//		parFile.setEccintricity(eccintricity);
//		parFile.seteGamma(eGamma);
//		parFile.seteOmDot(eOmDot);
//		parFile.setePbDot(ePbDot);
//		parFile.setGamma(gamma);
//		parFile.setOmDot(omDot);
//		parFile.setPb(pb);
//		parFile.setPbDot(pbDot);
		
		ParParser parParser = new ParParser(file);
		parFile = parParser.parsePar();
		parFile.setMassFunc(0.171706);
		launch(args);
	}
	void populateData(MMBean mmBean, LineChart<Number, Number> lineChart){
		ObservableList<MMLine> lineList = mmBean.getLineList();
		for(MMLine mmLine:lineList){
			if(mmBean.getActiveList().get(lineList.indexOf(mmLine)))
				lineChart.getData().addAll(mmLine.getSeriesList());
		}
	}
	private void setUpZooming(final Rectangle rect, final Node zoomingNode) {
        final ObjectProperty<Point2D> mouseAnchor = new SimpleObjectProperty<>();
        zoomingNode.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                mouseAnchor.set(new Point2D(event.getX(), event.getY()));
                rect.setWidth(0);
                rect.setHeight(0);
            }
        });
        zoomingNode.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();
                rect.setX(Math.min(x, mouseAnchor.get().getX()));
                rect.setY(Math.min(y, mouseAnchor.get().getY()));
                rect.setWidth(Math.abs(x - mouseAnchor.get().getX()));
                rect.setHeight(Math.abs(y - mouseAnchor.get().getY()));
            }
        });
    }
	
	private void doZoom(Rectangle zoomRect, LineChart<Number, Number> chart) {
        Point2D zoomTopLeft = new Point2D(zoomRect.getX(), zoomRect.getY());
        Point2D zoomBottomRight = new Point2D(zoomRect.getX() + zoomRect.getWidth(), zoomRect.getY() + zoomRect.getHeight());
        final NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        Point2D yAxisInScene = yAxis.localToScene(0, 0);
        final NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        Point2D xAxisInScene = xAxis.localToScene(0, 0);
        double xOffset = zoomTopLeft.getX() - yAxisInScene.getX() ;
        double yOffset = zoomBottomRight.getY() - xAxisInScene.getY();
        double xAxisScale = xAxis.getScale();
        double yAxisScale = yAxis.getScale();
        xAxis.setLowerBound(xAxis.getLowerBound() + xOffset / xAxisScale);
        xAxis.setUpperBound(xAxis.getLowerBound() + zoomRect.getWidth() / xAxisScale);
        yAxis.setLowerBound(yAxis.getLowerBound() + yOffset / yAxisScale);
        yAxis.setUpperBound(yAxis.getLowerBound() - zoomRect.getHeight() / yAxisScale);
        System.out.println(yAxis.getLowerBound() + " " + yAxis.getUpperBound());
        zoomRect.setWidth(0);
        zoomRect.setHeight(0);
    }
//	private void styleSeries(ObservableList<MMLine> events, final LineChart<Number, Number> lineChart) {
//	    // force a css layout pass to ensure that subsequent lookup calls work.
//	    lineChart.applyCss();
//	    // mark different series with different depending on whether they are above or below average.
//	    int nSeries = 0;
//	      for (MMLine mmLine : events) {
//	          if (!mmLine.isActive()) continue;
//	          for (int j = 0; j < mmLine.getSeriesList().size(); j++) {
//	              XYChart.Series<Number, Number> series = mmLine.getSeriesList().get(j);
//	              Set<Node> nodes = lineChart.lookupAll(".series" + nSeries);
//	              for (Node n : nodes) {
//	                  StringBuilder style = new StringBuilder();
//	                  if (mmLine.isBelowAverage(series)) {
//	                      style.append("-fx-stroke: black; -fx-background-color: red, white; ");
//	                  } else {
//	                      style.append("-fx-stroke: blue; -fx-background-color: blue, white; ");
//	                  }
//	                  if (mmLine.getStrokeDashArray() != null && !mmLine.getStrokeDashArray().isEmpty()) {
//	                      style.append("-fx-stroke-dash-array: ").append(mmLine.getStrokeDashArray()).append(";");
//	                  }
//	 
//	                  n.setStyle(style.toString());
//	              }
//	              nSeries++;
//	          }
//	      }
//	  }
}
