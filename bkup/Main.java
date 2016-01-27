package bkup;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import manager.Calculations;
import manager.ParParser;
import utilities.Constants;
import bean.MMBean;
import bean.MMLine;
import bean.ParFile;


public class Main extends Application {
	static ParFile parFile;
	Calculations calculations = new Calculations();
	MMBean mmBean;
	private Pane layout = new HBox();
	Stage stage;
	
	@Override
	public void init() throws Exception { }
	@Override
	public void start(Stage primaryStage) {
		mmBean= new MMBean(parFile);
		MMLine pbdot= new MMLine();
		MMLine gamma= new MMLine();
		MMLine omdot= new MMLine();
		MMLine massfunc= new MMLine();
		calculations.calculate(mmBean);
		final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Pulsar Mass");
        yAxis.setLabel("Companion Mass");
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        xAxis.setLowerBound(0.0);
        xAxis.setUpperBound(2.5);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(2.5);
//        xAxis.setLowerBound(1.2);
//        xAxis.setUpperBound(1.4);
//        yAxis.setLowerBound(0.9);
//        yAxis.setUpperBound(1.1);
        final LineChart<Number, Number> lineChart = new LineChart<Number,Number>(xAxis, yAxis);
        lineChart.setTitle("mass-mass");
        lineChart.setCreateSymbols(false);	
        lineChart.setAnimated(false);
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
        Button parButton = new Button("select par");
        TextField parText = new TextField("select par");
        parButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
		        fileChooser.setTitle("Open Par File");
		        File parFile = fileChooser.showOpenDialog(primaryStage);
		        if(parFile!=null) parText.setText(parFile.getName());
				
			}
		});
        HBox parBox = new HBox(10,parText,parButton);
        HBox.setHgrow(parText, Priority.ALWAYS);
        HBox.setHgrow(parButton, Priority.ALWAYS);
        
        VBox box2 = new VBox(10,parBox,controlPane);
        HBox controlledChart =  new HBox(10,
               lineChart,box2
          );
          controlledChart.setAlignment(Pos.CENTER);
          HBox.setHgrow(lineChart, Priority.ALWAYS);
          layout.setStyle(" -fx-padding: 10;");
          layout.getChildren().addAll(controlledChart);
        
	
	
		try {
			primaryStage.setTitle("Mass - Mass Diagram");
	          Scene scene = new Scene(layout, 800, 600);
	          primaryStage.setScene(scene);
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
