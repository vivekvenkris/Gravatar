package firstjavafx;
import java.io.File;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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


public class MassMassFX extends Application {
static ParFile parFile;
Calculations calculations = new Calculations();
MMBean mmBean;
BorderPane pane;
Rectangle rect;
SimpleDoubleProperty rectinitX = new SimpleDoubleProperty();
SimpleDoubleProperty rectinitY = new SimpleDoubleProperty();
SimpleDoubleProperty rectX = new SimpleDoubleProperty();
SimpleDoubleProperty rectY = new SimpleDoubleProperty();

double initXLowerBound = 0, initXUpperBound = 0, initYLowerBound = 0, initYUpperBound = 0;
@Override
public void start(Stage stage) {

    stage.setTitle("Mass-Mass Diagram");
    Double xmin=0.0,ymin=0.0;
	Double xmax=3.0,ymax=3.0;
	
    final NumberAxis xAxis = new NumberAxis(xmin,xmax,(xmax-xmin)/10.0);
    final NumberAxis yAxis = new NumberAxis(ymin,ymax,(ymax-ymin)/10.0);
    
    xAxis.setLabel("Pulsar Mass (M\u2609)");
    yAxis.setLabel("Companion Mass (M\u2609)");
    xAxis.setAutoRanging(false);
    yAxis.setAutoRanging(false);
//    xAxis.setLowerBound(xmin);
//    xAxis.setUpperBound(xmax);
//    yAxis.setLowerBound(ymin);
//    yAxis.setUpperBound(ymax);

    final LineChart<Number, Number> lineChart = new LineChart<Number,Number>(xAxis, yAxis);
    final AreaChart<Number,Number> areaChart = new AreaChart<Number,Number>(xAxis,yAxis);
    
    areaChart.setCreateSymbols(false);
    areaChart.setAlternativeRowFillVisible(false);
    areaChart.setAnimated(false);
    areaChart.setLegendVisible(false);
    areaChart.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
    areaChart.setPrefSize(600, 600);
    areaChart.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);


    lineChart.setCreateSymbols(false);
    lineChart.setAlternativeRowFillVisible(false);
    lineChart.setAnimated(false);
    lineChart.setLegendVisible(false);
    lineChart.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
    lineChart.setPrefSize(600, 600);
    lineChart.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
    
    StackPane stackPane = new StackPane();
    stackPane.getChildren().addAll(lineChart,areaChart);
    areaChart.setAlternativeRowFillVisible(false);
    areaChart.setAlternativeColumnFillVisible(false);
    areaChart.setHorizontalGridLinesVisible(false);
    areaChart.setVerticalGridLinesVisible(false);
    areaChart.getXAxis().setVisible(false);
    areaChart.getYAxis().setVisible(false);
    areaChart.getStylesheets().addAll(getClass().getResource("application.css").toExternalForm());
   // areaChart.setStyle("-fx-background-color: transparent");
   // areaChart.setBackground(null);
    pane = new BorderPane();
    
    Button parButton = new Button("select par");
    TextField parText = new TextField("select par");
    HBox parBox = new HBox(10,parText,parButton);
    parBox.setAlignment(Pos.CENTER_RIGHT);
    pane.setTop(parBox);
    parBox.autosize();
    pane.setCenter(stackPane);
    lineChart.setVisible(false);
    areaChart.setVisible(false);
    pane.setStyle(" -fx-padding: 10;");
    parButton.setOnAction(new EventHandler<ActionEvent>() {
		
		@Override
		public void handle(ActionEvent event) {
			FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Open Par File");
	        File parIOFile = fileChooser.showOpenDialog(stage);
	       // parIOFile = new File("fullpar1.par");
	        if(parIOFile!=null) parText.setText(parIOFile.getName());
	        ParParser parParser = new ParParser(parIOFile);
			parFile = parParser.parsePar();
			//parFile.setMassFunc(0.171706); // for 1141
			parFile.setMassFunc(0.290174); // for double psr
			mmBean= new MMBean(parFile);
			//calculations.calculate(mmBean);
			lineChart.getData().clear();
			areaChart.getData().clear();
			xAxis.setLowerBound(xmin);
		    xAxis.setUpperBound(xmax);
		    yAxis.setLowerBound(ymin);
		    yAxis.setUpperBound(ymax);
		    xAxis.setTickUnit((xmax-xmin)/10.0);
		    yAxis.setTickUnit((ymax-ymin)/10.0);
	        populateData(mmBean, lineChart); 
	        populateData(mmBean, areaChart); 
	        final VBox selectBoxes = new VBox(10);
	        selectBoxes.setStyle("-fx-padding: 10;");
	        final TitledPane controlPane = new TitledPane("Lines", selectBoxes);
	        controlPane.setCollapsible(false);   
	        for(MMLine line: mmBean.getLineList()){
	        	//System.out.println(line.getName());
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
		        StringBuilder styleString = new StringBuilder("-fx-stroke-width: 5; -fx-stroke: #"
		        		+ line.getColor().toString().replace("0x", "").toUpperCase()+";");
		        lineseg.setStyle(styleString.toString());
		        
		        box.setGraphic(lineseg);
		        final ColorPicker cp1 = new ColorPicker(line.getColor());
		        cp1.getStyleClass().add("button");
		        cp1.setOnAction((ActionEvent t) -> {
		        	MMLine newLine = mmBean.getLineList().get(mmBean.getLineList().indexOf(line));
		        	line.setColor(cp1.getValue());
		        	newLine.setColor(cp1.getValue());
		        	newLine.setColorChanged(true);
		        	System.err.println("color changed"+line);
		        	lineChart.getData().clear();
     	            populateData(mmBean, lineChart);
     	            
     	            areaChart.getData().clear();
     	            populateData(mmBean, areaChart);
     	            
     	            System.out.println(mmBean.getLineList());
     	            
     	          StringBuilder styleString2 = new StringBuilder("-fx-stroke-width: 5; -fx-stroke: #"
   		        		+ line.getColor().toString().replace("0x", "").toUpperCase()+";");
     	          lineseg.setStyle(styleString2.toString());
     	          box.setGraphic(lineseg);

     	            

		          });

		        HBox hbox;
		        if(line.getName().equals(Constants.massfunc))  hbox = new HBox(10,box,cp1);
		        else  hbox = new HBox(10,box,cp1,onesigma,twoSigma,threeSigma);
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
		        	            
		        	            areaChart.getData().clear();
		        	            populateData(mmBean, areaChart);

		                    }                
		                }
		        });
		        selectBoxes.getChildren().add(hbox);
		        box.setOnAction(action -> {
		        	//System.out.println(mmBean.getLineList().indexOf(line));
		        	mmBean.getActiveList().set(mmBean.getLineList().indexOf(line), box.isSelected());
		        	//line.setActive(box.isSelected());
		            lineChart.getData().clear();
		            populateData(mmBean, lineChart);
		            
		            areaChart.getData().clear();
    	            populateData(mmBean, areaChart);
		            //styleSeries(events, lineChart);
		          }
		        );
	        }
	       pane.setRight(new VBox(10,new Label(" "),controlPane));
	       controlPane.setAlignment(Pos.BOTTOM_CENTER);
	       lineChart.setVisible(true);
	       areaChart.setVisible(true);
		}
	});
    
    Scene scene = new Scene(pane, 1024,768);
    
    initXLowerBound = ((NumberAxis) lineChart.getXAxis()).getLowerBound();
    initXUpperBound = ((NumberAxis) lineChart.getXAxis()).getUpperBound();
    initYLowerBound = ((NumberAxis) lineChart.getYAxis()).getLowerBound();
    initYUpperBound = ((NumberAxis) lineChart.getYAxis()).getUpperBound();

    stage.setScene(scene);        
    

    scene.setOnMouseClicked(mouseHandler);
    scene.setOnMouseDragged(mouseHandler);
    scene.setOnMouseEntered(mouseHandler);
    scene.setOnMouseExited(mouseHandler);
    scene.setOnMouseMoved(mouseHandler);
    scene.setOnMousePressed(mouseHandler);
    scene.setOnMouseReleased(mouseHandler);


    rect = new Rectangle();
    rect.setFill(Color.web("LIGHTBLUE", 0.1));
    rect.setStroke(Color.LIGHTBLUE);
    rect.setStrokeDashOffset(50);

    rect.widthProperty().bind(rectX.subtract(rectinitX));
    rect.heightProperty().bind(rectY.subtract(rectinitY));
    pane.getChildren().add(rect);

    stage.show();
   
}


EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

    @Override
    public void handle(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY)
        {
                if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    rect.setX(mouseEvent.getX());
                    rect.setY(mouseEvent.getY());
                    rectinitX.set(mouseEvent.getX());
                    rectinitY.set(mouseEvent.getY());
                } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                    rectX.set(mouseEvent.getX());
                    rectY.set(mouseEvent.getY());
                } else if (mouseEvent.getEventType() == MouseEvent.MOUSE_RELEASED) {

                    if ((rectinitX.get() >= rectX.get())&&(rectinitY.get() >= rectY.get()))
                    {
                        //Condizioni Iniziali
						StackPane stackPane =  (StackPane) pane.getCenter();
						@SuppressWarnings("unchecked")
						LineChart<Number, Number> lineChart = (LineChart<Number, Number>) stackPane.getChildren().get(0);

                        ((NumberAxis) lineChart.getXAxis()).setLowerBound(initXLowerBound);
                        ((NumberAxis) lineChart.getXAxis()).setUpperBound(initXUpperBound);
                        ((NumberAxis) lineChart.getXAxis()).setTickUnit((initXUpperBound - initXLowerBound)/10.0);

                        ((NumberAxis) lineChart.getYAxis()).setLowerBound(initYLowerBound);
                        ((NumberAxis) lineChart.getYAxis()).setUpperBound(initYUpperBound);
                        ((NumberAxis) lineChart.getYAxis()).setTickUnit((initXUpperBound - initXLowerBound)/10.0);
                        

                    }
                    else
                    {
                        //Zoom In

                        double Tgap = 0;
                        double newLowerBound, newUpperBound, axisShift;
                        double xScaleFactor, yScaleFactor;
                        double xaxisShift, yaxisShift;
                        //System.out.println("Zoom bounds : [" + rectinitX.get()+", "+rectinitY.get()+"] ["+ rectX.get()+", "+rectY.get()+"]");                
                        //System.out.println("TODO: Determine bound ranges according these zoom coordinates.\n");

                        // TODO: Determine bound ranges according this zoom coordinates.
                        //LineChart<String, Number> lineChart = (LineChart<String, Number>) pane.getCenter();
                        StackPane stackPane =  (StackPane) pane.getCenter();
						@SuppressWarnings("unchecked")
						LineChart<Number, Number> lineChart = (LineChart<Number, Number>) stackPane.getChildren().get(0);
						
                        // Zoom in Y-axis by changing bound range.            
                        NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
                        Tgap = yAxis.getHeight()/(yAxis.getUpperBound() - yAxis.getLowerBound());
                        axisShift = getSceneShiftY(yAxis);
                        yaxisShift = axisShift;
                        
                        newUpperBound = yAxis.getUpperBound() - ((rectinitY.get() - axisShift) / Tgap);
                        newLowerBound = yAxis.getUpperBound() - (( rectY.get() - axisShift) / Tgap);

                       if (newUpperBound > yAxis.getUpperBound())
                            newUpperBound = yAxis.getUpperBound();

                        yScaleFactor = (yAxis.getUpperBound() - yAxis.getLowerBound())/(newUpperBound - newLowerBound);
                        yAxis.setLowerBound(newLowerBound);
                        yAxis.setUpperBound(newUpperBound);
                        yAxis.setTickUnit((newUpperBound-newLowerBound)/10.0);

                        // Zoom in X-axis by removing first and last data values.

                        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();


                        Tgap = xAxis.getWidth()/(xAxis.getUpperBound() - xAxis.getLowerBound());            
                        axisShift = getSceneShiftX(xAxis);                        
                        xaxisShift = axisShift;
                                
                        
                        
                        newLowerBound = ((rectinitX.get() - axisShift) / Tgap) + xAxis.getLowerBound();
                        newUpperBound = ((rectX.get() - axisShift) / Tgap) + xAxis.getLowerBound();                

                        if (newUpperBound > xAxis.getUpperBound())
                            newUpperBound = xAxis.getUpperBound();

                        xScaleFactor = (xAxis.getUpperBound() - xAxis.getLowerBound())/(newUpperBound - newLowerBound);
                        xAxis.setLowerBound( newLowerBound );
                        xAxis.setUpperBound( newUpperBound );
                        xAxis.setTickUnit((newUpperBound-newLowerBound)/10.0);


                    }
                    // Hide the rectangle
                    rectX.set(0);
                    rectY.set(0);
                }
        }
    }
   };
private static double getSceneShiftX(Node node) {
    double shift = 0;
    do { 
        shift += node.getLayoutX(); 
        node = node.getParent();
    } while (node != null);
    return shift;
}
private static double getSceneShiftY(Node node) {
    double shift = 0;
    do { 
        shift += node.getLayoutY(); 
        node = node.getParent();
    } while (node != null);
    return shift;
}


void populateData(MMBean mmBean, LineChart<Number, Number> lineChart){
	ObservableList<MMLine> lineList = mmBean.getLineList();
	for(MMLine mmLine:lineList){
		if(mmBean.getActiveList().get(lineList.indexOf(mmLine))) {
			lineChart.getData().addAll(mmLine.getSeriesList());
			for(Series<Number, Number> series: mmLine.getSeriesList()){
				String c = mmLine.getColor().toString().replace("0x", "").toUpperCase();
				series.nodeProperty().get().setStyle("-fx-stroke:#"+c+";");
			}
			}
	}
}

void populateData(MMBean mmBean, AreaChart<Number, Number> areaChart){
	MMLine line = mmBean.getLineList().get(mmBean.getLineList().indexOf(new MMLine(Constants.massfunc)));
	areaChart.getData().addAll(line.getSeriesList());
	for(Series<Number, Number> series: line.getSeriesList()){
		String c = line.getColor().toString().replace("0x", "").toUpperCase();
		System.err.println("color"+c);
		series.nodeProperty().get().setStyle("-fx-fill:#"+c+";"+"-fx-area-fill:#"+c+";");
		System.err.println(series.nodeProperty().get().getStyle());
	}
}

    public static void main(String[] args) {
        launch(args);
    }
    
    
}