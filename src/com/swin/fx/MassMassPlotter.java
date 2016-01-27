package com.swin.fx;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.swin.bean.MMBean;
import com.swin.bean.MMLine;
import com.swin.bean.PlotInputs;
import com.swin.manager.Calculator;
import com.swin.manager.ParParser;
import com.swin.manager.ScalarTensor;
import com.swin.manager.ScalarTensorCalc;
import com.swin.util.Constants;


public class MassMassPlotter extends Application {
static PlotInputs plotInputs;
Calculator calculations = new ScalarTensorCalc();
MMBean mmBean;
BorderPane pane;
Rectangle rect;
SimpleDoubleProperty rectinitX = new SimpleDoubleProperty();
SimpleDoubleProperty rectinitY = new SimpleDoubleProperty();
SimpleDoubleProperty rectX = new SimpleDoubleProperty();
SimpleDoubleProperty rectY = new SimpleDoubleProperty();
Label hoverLabel = new Label();

double initXLowerBound = 0, initXUpperBound = 0, initYLowerBound = 0, initYUpperBound = 0;
@Override
public void start(Stage stage) {
	
	
    stage.setTitle("எடை-எடை வரைபடம்");
    Double xmin=0.,ymin=0.;
	Double xmax=3.,ymax=3.;
	
    final NumberAxis xAxis = new NumberAxis(xmin,xmax,(xmax-xmin)/10.0);
    final NumberAxis yAxis = new NumberAxis(ymin,ymax,(ymax-ymin)/10.0);
   
    
    xAxis.setLabel("Pulsar Mass (M\u2609)");
    yAxis.setLabel("Companion Mass (M\u2609)");
    xAxis.setAutoRanging(false);
    yAxis.setAutoRanging(false);


    final MyLineChart<Number, Number> lineChart = new MyLineChart<Number,Number>(xAxis, yAxis);
    lineChart.setTitle("PSR NAME");
    lineChart.setCursor(Cursor.CROSSHAIR);

    lineChart.setCreateSymbols(false);
    lineChart.setAlternativeRowFillVisible(false);
    lineChart.setAnimated(false);
    lineChart.setLegendVisible(false);
//    lineChart.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
//    lineChart.setPrefSize(600, 600);
//    lineChart.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

    pane = new BorderPane();
    
    Button parButton = new Button("select par");
    TextField parText = new TextField("select par");
    HBox parBox = new HBox(10,parText,parButton);
    parBox.setAlignment(Pos.CENTER_RIGHT);
    pane.setTop(parBox);
    parBox.autosize();
    pane.setCenter(lineChart);
    lineChart.setVisible(false);
    pane.setStyle(" -fx-padding: 10;");
    Label errorLabel = new Label("Error messages will be displayed here.");
    errorLabel.setTextFill(Color.MEDIUMVIOLETRED);

    HBox bottom = new HBox(10);
    bottom.getChildren().add(errorLabel);
    bottom.setStyle(" -fx-padding: 10;");
    pane.setBottom(bottom);
    parButton.setOnAction(new EventHandler<ActionEvent>() {
		
		@Override
		public void handle(ActionEvent event) {
			try {
			FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Open Par File");
	        File parIOFile = fileChooser.showOpenDialog(stage);
	       // parIOFile = new File("fullpar1.par");
	        if(parIOFile!=null) parText.setText(parIOFile.getName());
	        ParParser parParser = new ParParser(parIOFile);
	        plotInputs = parParser.parsePar();
	        plotInputs.setMassFunc(0.171706);
	       // plotInputs.setMassFunc(0.29096571);
			mmBean= new MMBean(plotInputs);
			//calculations.calculate(mmBean);
			lineChart.getData().clear();
			xAxis.setLowerBound(xmin);
		    xAxis.setUpperBound(xmax);
		    yAxis.setLowerBound(ymin);
		    yAxis.setUpperBound(ymax);
		    xAxis.setTickUnit((xmax-xmin)/10.0);
		    yAxis.setTickUnit((ymax-ymin)/10.0);
	        populateData(mmBean, lineChart);   
	        final VBox selectBoxes = new VBox(10);
	        selectBoxes.setStyle("-fx-padding: 10;");
	        final TitledPane controlPane = new TitledPane("Select lines and Sigma", selectBoxes);
	        //controlPane.setCollapsible(false);   
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
		                        case Constants.massratio:
			                        	mmBean.setSigmaMassRatio(sigma);
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
		        	//System.out.println(mmBean.getLineList().indexOf(line));
		        	mmBean.getActiveList().set(mmBean.getLineList().indexOf(line), box.isSelected());
		        	line.setActive(box.isSelected());
		        	//line.setActive(box.isSelected());
		            lineChart.getData().clear();
		            populateData(mmBean, lineChart);
		            //styleSeries(events, lineChart);
		          }
		        );
	        }
	        
	        Label pbDotKinLabel = new Label(Constants.pbdot+" Kin ");
	        Label pbDotGalLabel = new Label(Constants.pbdot+" Gal ");  
	        TextField pbDotKinField = new TextField(plotInputs.getPbKin().toString());
	        TextField pbDotGalField = new TextField(plotInputs.getPbGal().toString());
	        TextField pbDotKinErrField = new TextField(plotInputs.getePbKin().toString());
	        TextField pbDotGalErrField = new TextField(plotInputs.getePbGal().toString());
	        
	        Label PbDotKinErrLabel = new Label(Constants.pbdot+" Kin Err ");
	        Label PbDotGalErrLabel = new Label(Constants.pbdot+" Gal Err ");
	        
	        Button recomputePb = new Button("Recompute");
	        recomputePb.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					
					double PbDotKin = Double.parseDouble(pbDotKinField.getText());
					double PbDotKinErr = Double.parseDouble(pbDotKinErrField.getText());
					double PbDotGal = Double.parseDouble(pbDotGalField.getText());
					double PbDotGalErr = Double.parseDouble(pbDotGalErrField.getText());
					
					plotInputs.setPbGal(PbDotGal);
					plotInputs.setPbKin(PbDotKin);
					plotInputs.setePbGal(PbDotGalErr);
					plotInputs.setePbKin(PbDotKinErr);
					
					
					//plotInputs.setPbDot(plotInputs.getPbDot()+plotInputs.getPbGal()-plotInputs.getPbKin());
					//plotInputs.setePbDot(plotInputs.getePbDot()+ plotInputs.getePbGal()+plotInputs.getePbKin());
					mmBean.setPlotInputs(plotInputs);
					mmBean.recompute();
					lineChart.getData().clear();
     	            populateData(mmBean, lineChart);
					
				}
	        });
	        
	        VBox pbBox = new VBox(10, new HBox(10,pbDotKinLabel,pbDotKinField,PbDotKinErrLabel,pbDotKinErrField),
	        		new HBox(10,pbDotGalLabel,pbDotGalField,PbDotGalErrLabel,pbDotGalErrField),recomputePb);
	        final TitledPane pbDotPane = new TitledPane("Input covariant "+Constants.pbdot+" Contributions",pbBox);
	        
	        TextField alpha = new TextField("0");
	        TextField beta = new TextField("0");
	        TextField ka = new TextField("0.3");

	        Label alphaLabel = new Label("\u03B1\u2080");
	        Label betaLabel = new Label("\u03B2\u2080");
	        Label kaLabel = new Label("Ka");
	        HBox kaBox = new HBox(10,kaLabel,ka);
	        HBox alphaBox = new HBox(10);
	        alphaBox.getChildren().addAll(alphaLabel,alpha); 
	        HBox betaBox = new HBox(10);
	        betaBox.getChildren().addAll(betaLabel,beta);
	        HBox scalars = new HBox(10);
	        scalars.getChildren().addAll(alphaBox,betaBox,kaBox);
	        Button recompute = new Button("Recompute");
	        recompute.setAlignment(Pos.CENTER);
	        recompute.autosize();
	        recompute.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					try{
						double alphaDouble = Double.parseDouble(alpha.getText());
						double betaDouble = Double.parseDouble(beta.getText());
						double kaDouble = Double.parseDouble(ka.getText());
						plotInputs.setAlpha(alphaDouble);
						plotInputs.setBeta(betaDouble);
						plotInputs.setKa(kaDouble);
						mmBean.setPlotInputs(plotInputs);
						mmBean.recompute();
						lineChart.getData().clear();
	     	            populateData(mmBean, lineChart);
					}
					catch(Exception e){
						e.printStackTrace();
						errorLabel.setText(e.getMessage());
					}
					
				}
	        	
	        });
	        VBox scalarsBox = new VBox(10);
	        scalarsBox.getChildren().addAll(scalars,recompute);
	        final TitledPane scalarsPane = new TitledPane("Recompute with scalar taylor coefficients",scalarsBox);
	        
	        
	        TextField alphaMin = new TextField();
	        TextField alphaMax = new TextField();
	        TextField alphaPts = new TextField();
	        
	        Label alphaMinLabel = new Label("\u03B1\u2080 min");
	        Label alphaMaxLabel = new Label("\u03B1\u2080 max");
	        Label alphaPtsLabel = new Label("\u03B1\u2080 pts");
	        
	        TextField betaMin = new TextField();
	        TextField betaMax = new TextField();
	        TextField betaPts = new TextField();
	        
	        Label betaMinLabel = new Label("\u03B2\u2080 min");
	        Label betaMaxLabel = new Label("\u03B2\u2080 max");
	        Label betaPtsLabel = new Label("\u03B2\u2080 pts");
	        
	        HBox alphaParams = new HBox(10,alphaMinLabel,alphaMin,alphaMaxLabel,alphaMax);
	        HBox betaParams =  new HBox(10,betaMinLabel,betaMin,betaMaxLabel,betaMax);
	        HBox pts  = new HBox(10,alphaPtsLabel,alphaPts,betaPtsLabel,betaPts);
	        Button compute = new Button("compute");
	        VBox scalarParams = new VBox(10,alphaParams,betaParams,pts,compute);
	        
	        final TitledPane scalarParamsPane = new TitledPane("Constrain \u03B1\u2080-\u03B2\u2080 space ",scalarParams);
	        
	        compute.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					Double alphaMinValue = Double.parseDouble(alphaMin.getText());
					Double alphaMaxValue = Double.parseDouble(alphaMax.getText());
					Double alphaPtsValue = Double.parseDouble(alphaPts.getText());
				
					Double betaMinValue = Double.parseDouble(betaMin.getText());
					Double betaMaxValue = Double.parseDouble(betaMax.getText());
					Double betaPtsValue = Double.parseDouble(betaPts.getText());
					
					ScalarTensor sc = new ScalarTensor(alphaMinValue, alphaMaxValue, alphaPtsValue, betaMinValue, betaMaxValue, betaPtsValue,plotInputs);
					sc.start();
					
				}
	        	
	        });
	        
	       
	       pane.setRight(new VBox(10,new Label(" "),controlPane,pbDotPane,scalarsPane,scalarParamsPane));
	       HBox bottom = (HBox) pane.getBottom();
	       bottom.getChildren().remove(hoverLabel);
	       bottom.getChildren().add(hoverLabel);
	       //pane.setBottom(hoverLabel);
	       hoverLabel.setAlignment(Pos.BOTTOM_RIGHT);
	       //controlPane.setAlignment(Pos.BOTTOM_CENTER);
	       lineChart.setVisible(true);
		}
			catch(Exception e){
				e.printStackTrace();
				errorLabel.setText(e.getMessage());
			}
		}
		
	});
    
    Scene scene = new Scene(pane, 1400,900);
    
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
    	if(mouseEvent.getEventType().equals(MouseEvent.MOUSE_MOVED)){
    		LineChart<Number, Number> lineChart = (MyLineChart<Number, Number>) pane.getCenter();
            double X = mouseEvent.getX();
            double Y = mouseEvent.getY();
            
            NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
            double Tgap = yAxis.getHeight()/(yAxis.getUpperBound() - yAxis.getLowerBound());
            double axisShift = getSceneShiftY(yAxis);
            double ptY = yAxis.getUpperBound() - (( Y - axisShift) / Tgap);
            
            NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();


            Tgap = xAxis.getWidth()/(xAxis.getUpperBound() - xAxis.getLowerBound());            
            axisShift = getSceneShiftX(xAxis);                        
            double ptX = ((X - axisShift) / Tgap) + xAxis.getLowerBound();    
            
            if(ptX > xAxis.getUpperBound() || ptX < xAxis.getLowerBound()|| ptY > yAxis.getUpperBound() || ptY < yAxis.getLowerBound()){
            	hoverLabel.setText("");
            	return;
            }
            
            DecimalFormat format = new DecimalFormat("#0.000000");
            
            
            hoverLabel.setText("(X= "+format.format(ptX) + ", Y= "+ format.format(ptY)+")");
    	}
    	if(mouseEvent.getButton() == MouseButton.SECONDARY){
    		 @SuppressWarnings("unchecked")
				LineChart<Number, Number> lineChart = (MyLineChart<Number, Number>) pane.getCenter();

             ((NumberAxis) lineChart.getXAxis()).setLowerBound(initXLowerBound);
             ((NumberAxis) lineChart.getXAxis()).setUpperBound(initXUpperBound);
             ((NumberAxis) lineChart.getXAxis()).setTickUnit((initXUpperBound - initXLowerBound)/10.0);

             ((NumberAxis) lineChart.getYAxis()).setLowerBound(initYLowerBound);
             ((NumberAxis) lineChart.getYAxis()).setUpperBound(initYUpperBound);
             ((NumberAxis) lineChart.getYAxis()).setTickUnit((initXUpperBound - initXLowerBound)/10.0);
             
    		
    	}
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
                        @SuppressWarnings("unchecked")
						LineChart<Number, Number> lineChart = (MyLineChart<Number, Number>) pane.getCenter();
                        double X = mouseEvent.getX();
                        double Y = mouseEvent.getY();
                        
                        NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
                        double Tgap = yAxis.getHeight()/(yAxis.getUpperBound() - yAxis.getLowerBound());
                        double axisShift = getSceneShiftY(yAxis);
                        double ptY = yAxis.getUpperBound() - (( Y - axisShift) / Tgap);
                        
                        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();


                        Tgap = xAxis.getWidth()/(xAxis.getUpperBound() - xAxis.getLowerBound());            
                        axisShift = getSceneShiftX(xAxis);                        
                        double ptX = ((X - axisShift) / Tgap) + xAxis.getLowerBound();                    
                        DecimalFormat format = new DecimalFormat("#.000000");
                        
                        
                        hoverLabel.setText(format.format(ptX) + " "+ format.format(ptY));
                        
                        

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
                        MyLineChart<Number, Number> lineChart = (MyLineChart<Number, Number>) pane.getCenter();
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
                      //  newLowerBound = Math.round(1000*newLowerBound/5.0)*5/1000.0;
                       // newUpperBound =  Math.round(1000*newUpperBound/5.0)*5/1000.0;
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
                    //    newLowerBound = Math.round(1000*newLowerBound/5.0)*5/1000.0;
                     //   newUpperBound =  Math.round(1000*newUpperBound/5.0)*5/1000.0;
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


void populateData(MMBean mmBean, MyLineChart<Number, Number> lineChart){
	ObservableList<MMLine> lineList = mmBean.getLineList();
	List<Polygon> intersection = mmBean.getIntersection();
	for(MMLine mmLine:lineList){
		if(mmBean.getActiveList().get(lineList.indexOf(mmLine))) {}
		if(mmLine.isActive()) {
			lineChart.getData().addAll(mmLine.getSeriesList());
			for(Series<Number, Number> series: mmLine.getSeriesList()){
				String c = mmLine.getColor().toString().replace("0x", "").toUpperCase();
				series.nodeProperty().get().setStyle("-fx-stroke:#"+c+";");// -fx-stroke-dash-array:"+mmLine.getStrokeDashArray()+";");
			}
			}
	}
	lineChart.setIntersection(intersection);
}


    public static void main(String[] args) {
    	System.err.println(com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());
        launch(args);
    }
    
    
}