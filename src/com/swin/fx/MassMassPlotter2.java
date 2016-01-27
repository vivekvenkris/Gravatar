package com.swin.fx;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Camera;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
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
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.sg.prism.NGNode;
import com.swin.bean.MMBean2;
import com.swin.bean.MMLine;
import com.swin.bean.PKBean;
import com.swin.bean.PlotInputs;
import com.swin.bean.XYPair;
import com.swin.exception.InvalidBoundsException;
import com.swin.manager.Calculator;
import com.swin.manager.ParParser;
import com.swin.manager.ScalarTensor;
import com.swin.manager.ScalarTensorCalc;
import com.swin.util.Constants;


public class MassMassPlotter2 extends Application {
	static PlotInputs plotInputs;
	Calculator calculations = new ScalarTensorCalc();
	MMBean2 mmBean;
	BorderPane pane;
	Rectangle rect;
	SimpleDoubleProperty rectinitX = new SimpleDoubleProperty();
	SimpleDoubleProperty rectinitY = new SimpleDoubleProperty();
	SimpleDoubleProperty rectX = new SimpleDoubleProperty();
	SimpleDoubleProperty rectY = new SimpleDoubleProperty();
	BufferedReader txtFileReader = null;
	private Color intersectionColor = Color.DARKBLUE;
	private boolean intersectionColorChanged;
	Label hoverLabel = new Label();
	boolean loaded = false;

	double initXLowerBound = 0, initXUpperBound = 0, initYLowerBound = 0, initYUpperBound = 0;
	@Override
	public void start(Stage stage) {


		stage.setTitle("2 எடை-எடை வரைபடம்");
		Double xmin=0.,ymin=0.;
		Double xmax=3.,ymax=3.;

		final NumberAxis xAxis = new NumberAxis(xmin,xmax,(xmax-xmin)/10.0);
		final NumberAxis yAxis = new NumberAxis(ymin,ymax,(ymax-ymin)/10.0);


		xAxis.setLabel("Pulsar Mass (M\u2609)");
		yAxis.setLabel("Companion Mass (M\u2609)");
		xAxis.setAutoRanging(false);
		yAxis.setAutoRanging(false);


		final MyLineChart<Number, Number> lineChart = new MyLineChart<Number,Number>(xAxis, yAxis);
		lineChart.setTitle("J1141-6545");
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
					plotInputs.setMassRatio(1.27/0.98);
					plotInputs.seteMassFunc(0.000006);
					

					// plotInputs.setMassFunc(0.29096571);
					mmBean= new MMBean2(plotInputs);
					plotInputs.setPlotGamma(true);
					plotInputs.setPlotOmDot(true);
					plotInputs.setPlotMassFunc(true);
					plotInputs.setPlotPbDot(true);
					plotInputs.setPlotIntersection(true);
					mmBean.initMMBean();
					mmBean.populateData();
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
					TitledPane controlPane = new TitledPane("Select lines and Sigma", selectBoxes);
					//controlPane.setCollapsible(false);   
					Map<String,MMLine> mmLinesMap = mmBean.getMmLinesMap();
					Set<Entry<String, MMLine>> mmLinesEntrySet = mmLinesMap.entrySet();
					for(Entry<String, MMLine> mmLinesEntry: mmLinesEntrySet){
						MMLine line = mmLinesEntry.getValue();	        	//System.out.println(line.getName());
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

						TextField tf = new TextField("1");

						RadioButton userSigma = new RadioButton();
						userSigma.setToggleGroup(group);
						userSigma.setGraphic(tf);
						userSigma.setUserData(new String(line.getName()));


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
							//	MMLine newLine = mmBean.getLineList().get(mmBean.getLineList().indexOf(line));
							line.setColor(cp1.getValue());
							//newLine.setColor(cp1.getValue());
							line.setColorChanged(true);
							System.err.println("color changed"+line);
							lineChart.getData().clear();
							populateData(mmBean, lineChart);
							// System.out.println(mmBean.getLineList());

							StringBuilder styleString2 = new StringBuilder("-fx-stroke-width: 5; -fx-stroke: #"
									+ line.getColor().toString().replace("0x", "").toUpperCase()+";");
							lineseg.setStyle(styleString2.toString());
							box.setGraphic(lineseg);



						});

						HBox hbox;
						if(line.getName().equals(Constants.massfunc))  hbox = new HBox(10,box,cp1);
						else  hbox = new HBox(10,box,cp1,onesigma,twoSigma,threeSigma,userSigma);
						group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
							public void changed(ObservableValue<? extends Toggle> ov,
									Toggle old_toggle, Toggle new_toggle) {
								userSigma.setUserData(((String)userSigma.getUserData()).split("_")[0] +"_"+((TextField)userSigma.getGraphic()).getText());
								if (group.getSelectedToggle() != null) {
									String name_sigma =(String) group.getSelectedToggle().getUserData();
									System.out.println(name_sigma);
									String name = name_sigma.split("_")[0];
									Double sigma = Double.parseDouble(name_sigma.split("_")[1]);
									switch(name){
									case Constants.pbdot:
										mmBean.getMmLinesMap().get(Constants.pbdot).setSigma(sigma);
										break;
									case Constants.gamma:
										mmBean.getMmLinesMap().get(Constants.gamma).setSigma(sigma);
										break;
									case Constants.omdot:
										mmBean.getMmLinesMap().get(Constants.omdot).setSigma(sigma);
										break;
									case Constants.massratio:
										mmBean.getMmLinesMap().get(Constants.massratio).setSigma(sigma);
										break;
									}
									try {
										mmBean.populateData();
									} catch (InvalidBoundsException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									lineChart.getData().clear();
									populateData(mmBean, lineChart);

								}                
							}
						});
						selectBoxes.getChildren().add(hbox);
						box.setOnAction(action -> {
							//System.out.println(mmBean.getLineList().indexOf(line));
							//mmBean.getActiveList().set(mmBean.getLineList().indexOf(line), box.isSelected());
							line.setActive(box.isSelected());
							//line.setActive(box.isSelected());
							lineChart.getData().clear();
							populateData(mmBean, lineChart);
							//styleSeries(events, lineChart);
						}
								);
					}

					final CheckBox intersectionCheckBox = new CheckBox(Constants.intersection);
					intersectionCheckBox.setSelected(true);
					Line lineseg = new Line(0, 10, 50, 10);
					StringBuilder styleString = new StringBuilder("-fx-stroke-width: 5; -fx-stroke: #"
							+ intersectionColor.toString().replace("0x", "").toUpperCase()+";");
					lineseg.setStyle(styleString.toString());

					intersectionCheckBox.setGraphic(lineseg);
					final ColorPicker cp1 = new ColorPicker(intersectionColor);
					cp1.getStyleClass().add("button");
					cp1.setOnAction((ActionEvent t) -> {
						intersectionColor=cp1.getValue();
						intersectionColorChanged = true;

						StringBuilder styleString2 = new StringBuilder("-fx-stroke-width: 5; -fx-stroke: #"
								+ intersectionColor.toString().replace("0x", "").toUpperCase()+";");
						lineseg.setStyle(styleString2.toString());
						intersectionCheckBox.setGraphic(lineseg);
						try {
							plotInputs.setPlotIntersection(true);
							mmBean.setInp(plotInputs);
							lineChart.getData().clear();
							mmBean.populateData();
							populateData(mmBean, lineChart);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


					});
					intersectionCheckBox.setOnAction(action -> {
						if(intersectionCheckBox.isSelected()){
							try {
								plotInputs.setPlotIntersection(true);
								mmBean.setInp(plotInputs);
								lineChart.getData().clear();
								mmBean.populateData();
								populateData(mmBean, lineChart);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else{
							plotInputs.setPlotIntersection(false);
							mmBean.setInp(plotInputs);
							try {
								mmBean.populateData();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							lineChart.getData().clear();
							populateData(mmBean, lineChart);		        }
					});
					TextField intersectionNumPts = new TextField(mmBean.getIntersectionNumPts().toString());
					Button recomputeIntersection = new Button("Recompute");
					recomputeIntersection.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							mmBean.setIntersectionNumPts(Integer.parseInt(intersectionNumPts.getText()));
							try {
								plotInputs.setPlotIntersection(true);
								mmBean.setInp(plotInputs);
								lineChart.getData().clear();
								mmBean.populateData();
								populateData(mmBean, lineChart);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();

							}
						}
					});


					selectBoxes.getChildren().add(new HBox(10,intersectionCheckBox,cp1, intersectionNumPts,recomputeIntersection));

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
							mmBean.setInp(plotInputs);
							try {
								mmBean.recompute();
								mmBean.populateData();
							} catch (InvalidBoundsException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
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
					TextField alphaP = new TextField("0");
					TextField betaP = new TextField("0");
					Label alphaPLabel = new Label("\u03B1\u209A");
					Label betaPLabel = new Label("\u03B2\u209A");
					
					HBox alphaPBox = new HBox(10);
					alphaPBox.getChildren().addAll(alphaPLabel,alphaP); 
					HBox betaPBox = new HBox(10);
					betaPBox.getChildren().addAll(betaPLabel,betaP);

					Label alphaLabel = new Label("\u03B1\u2080");
					Label betaLabel = new Label("\u03B2\u2080");
					Label kaLabel = new Label("Ka");
					HBox kaBox = new HBox(10,kaLabel,ka);
					HBox alphaBox = new HBox(10);
					alphaBox.getChildren().addAll(alphaLabel,alpha); 
					HBox betaBox = new HBox(10);
					betaBox.getChildren().addAll(betaLabel,beta);
					VBox scalars = new VBox(10);
					scalars.getChildren().addAll(alphaBox,betaBox,alphaPBox,betaPBox,kaBox);
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
								
								double alphaPulsar = Double.parseDouble(alphaP.getText());
								double betaPulsar = Double.parseDouble(betaP.getText());
								plotInputs.setAlphaPulsar(alphaPulsar);
								plotInputs.setBetaPulsar(betaPulsar);
								plotInputs.setAlphaCompanion(alphaDouble);
								plotInputs.setBetaCompanion(betaDouble);
								
								plotInputs.setAlpha(alphaDouble);
								plotInputs.setBeta(betaDouble);
								plotInputs.setKa(kaDouble);
								mmBean.setInp(plotInputs);
								mmBean.recompute();
								mmBean.populateData();
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


//					TextField alphaMin = new TextField();
//					TextField alphaMax = new TextField();
//					TextField alphaPts = new TextField();
//
//					Label alphaMinLabel = new Label("\u03B1\u2080 min");
//					Label alphaMaxLabel = new Label("\u03B1\u2080 max");
//					Label alphaPtsLabel = new Label("\u03B1\u2080 pts");
//
//					TextField betaMin = new TextField();
//					TextField betaMax = new TextField();
//					TextField betaPts = new TextField();
//
//					Label betaMinLabel = new Label("\u03B2\u2080 min");
//					Label betaMaxLabel = new Label("\u03B2\u2080 max");
//					Label betaPtsLabel = new Label("\u03B2\u2080 pts");
//
//					HBox alphaParams = new HBox(10,alphaMinLabel,alphaMin,alphaMaxLabel,alphaMax);
//					HBox betaParams =  new HBox(10,betaMinLabel,betaMin,betaMaxLabel,betaMax);
//					HBox pts  = new HBox(10,alphaPtsLabel,alphaPts,betaPtsLabel,betaPts);
					Label labelsLabel = new Label();
					Label valuesLabel = new Label();
					TextField txtFileText = new TextField();
					Button compute = new Button("Load text file");
					Button next = new Button("Next");
					Button prev = new Button("Prev");
					Label beta0Label = new Label("\u03B2\u2080");
					TextField beta0 = new TextField();
					VBox scalarParams = new VBox(10,new HBox(10,beta0Label,beta0),new HBox(10,txtFileText,compute),new HBox(10,prev,next),new HBox(10,labelsLabel,valuesLabel));

					final TitledPane scalarParamsPane = new TitledPane("Constrain \u03B1\u2080-\u03B2\u2080 space ",scalarParams);

					compute.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							//ScalarTensor sc = new ScalarTensor(alphaMinValue, alphaMaxValue, alphaPtsValue, betaMinValue, betaMaxValue, betaPtsValue,plotInputs);
							//sc.start();
							try {
								FileChooser txtFileChooser = new FileChooser();
								txtFileChooser.setTitle("Open txt file");
								File txtFile = fileChooser.showOpenDialog(stage);	
								txtFileText.setText(txtFile.getName());
								txtFileReader = new BufferedReader(new FileReader(txtFile));
								String comment = txtFileReader.readLine();
								comment = comment.replace("#", "");
								labelsLabel.setText(comment.replaceAll(",", "\n"));
								plotInputs.setBeta(Double.parseDouble(beta0.getText()));
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								errorLabel.setText(e.getMessage());

							}
						}

					});
					
					next.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							try {
								String line = txtFileReader.readLine();
								String values[] = line.split(",");
								valuesLabel.setText(line.replaceAll(",", "\n"));
								plotInputs.setAlpha(Double.parseDouble(values[1]));
								plotInputs.setAlphaPulsar(Double.parseDouble(values[2]));
								plotInputs.setBetaPulsar(Double.parseDouble(values[3]));
								plotInputs.setKa(Double.parseDouble(values[4]));
								mmBean.setInp(plotInputs);
								mmBean.recompute();
								mmBean.populateData();
								lineChart.getData().clear();
								populateData(mmBean, lineChart);
							} catch (Exception e) {
								e.printStackTrace();
								errorLabel.setText(e.getMessage());

							}
							
						}
					});
					TextField scale = new TextField("1");
					TextField name = new TextField("Name");
					Button savePNG = new Button("save PNG");
					savePNG.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							double pixelScale = Double.parseDouble(scale.getText());
							SnapshotParameters spa = new SnapshotParameters();
							//WritableImage writableImage = new WritableImage((int)Math.rint(4096*2), (int)Math.rint(2610*2));
						    //spa.setTransform(Transform.scale(4096*2.0/lineChart.getWidth(),2610*2.0/lineChart.getHeight()));
						    System.err.println(lineChart.getWidth() + " "+ lineChart.getHeight());
						    WritableImage writableImage = new WritableImage((int)Math.rint(pixelScale*lineChart.getWidth()), (int)Math.rint(pixelScale*lineChart.getHeight()));
						    spa.setTransform(Transform.scale(pixelScale,pixelScale));
						    WritableImage img= lineChart.snapshot(spa, writableImage);     
							try {
								ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", new File(name.getText()));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
					controlPane.setExpanded(false);
					pbDotPane.setExpanded(false);
					pane.setRight(new VBox(10,new Label(" "),controlPane,pbDotPane,scalarsPane,scalarParamsPane,new HBox(10,scale,name,savePNG)));
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
					loaded = true;
				}
			});

		Scene scene = new Scene(pane, 1366,768);

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
				if(!loaded) return;
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

					DecimalFormat f = new DecimalFormat("#0.000000");
					DecimalFormat f2 = new DecimalFormat("#0.000");


					Double pbdot = (PKBean.getValue(Constants.pbdot, ptX, ptY) - plotInputs.getPbDotFixed())/plotInputs.getePbDotFixed();
					Double omdot = (PKBean.getValue(Constants.omdot, ptX, ptY) - plotInputs.getOmDot())/plotInputs.geteOmDot();
					Double gamma = (PKBean.getValue(Constants.gamma, ptX, ptY) - plotInputs.getGamma())/plotInputs.geteGamma();
					Double massfunc = PKBean.getValue(Constants.massfunc, ptX, ptY);


					hoverLabel.setText("(X= "+f.format(ptX) + ", Y= "+ f.format(ptY)+"): "+Constants.pbdot+": "+f2.format(pbdot)+Constants.sigma+"   "
							+Constants.omdot + ": "+f2.format(omdot)+Constants.sigma+"   "
							+ Constants.gamma + ": "+f2.format(gamma)+Constants.sigma+"  "
							+Constants.massfunc + ": "+f.format(massfunc));
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


		void populateData(MMBean2 mmBean, MyLineChart<Number, Number> lineChart){
			List<Polygon> intersection = mmBean.getIntersectionList();
			Set<XYPair> circlePoints = mmBean.getCirclePoints();
			Map<String,MMLine> mmLinesMap = mmBean.getMmLinesMap();
			Set<Entry<String, MMLine>> mmLinesEntrySet = mmLinesMap.entrySet();
			for(Entry<String, MMLine> mmLinesEntry: mmLinesEntrySet){
				MMLine line = mmLinesEntry.getValue();
				if(line.isActive()){
					lineChart.getData().addAll(line.getSeriesList());
					for(Series<Number, Number> series: line.getSeriesList()){
						String c = line.getColor().toString().replace("0x", "").toUpperCase();
						series.nodeProperty().get().setStyle("-fx-stroke:#"+c+";");// -fx-stroke-dash-array:"+mmLine.getStrokeDashArray()+";");
					}
				}
			}
			lineChart.setIntersection(intersection);
			if(plotInputs.isPlotIntersection()){
				lineChart.setCirclePoints(circlePoints);
				lineChart.setIntersectionColor(intersectionColor);
			}
		}


		public static void main(String[] args) {
			System.err.println(com.sun.javafx.runtime.VersionInfo.getRuntimeVersion());
			launch(args);
		}


	}