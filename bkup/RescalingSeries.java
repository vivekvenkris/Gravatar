package bkup;

import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Set;
import javafx.application.Application; 
import javafx.beans.property.SimpleDoubleProperty; 
import javafx.event.ActionEvent;
import javafx.event.EventHandler; 
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.chart.NumberAxis; 
import javafx.scene.chart.XYChart; 
import javafx.stage.Stage; 
import javafx.scene.Scene; 
import javafx.scene.Node; 
import javafx.scene.chart.*; 
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent; 
import javafx.scene.layout.BorderPane; 
import javafx.scene.layout.StackPane;

public class RescalingSeries extends Application {

StackPane               mainGraphStackPane = null;
Button btnAdd;
BorderPane pane; 

XYChart.Series series1 = new XYChart.Series(); 

SimpleDoubleProperty rectinitX = new SimpleDoubleProperty(); 
SimpleDoubleProperty rectinitY = new SimpleDoubleProperty(); 

protected static Axis _duplicateAxis(Axis axis, Axis result) {

    result.setAnimated(axis.animatedProperty().get());
    result.setAutoRanging(axis.isAutoRanging());
    result.setLabel(axis.getLabel());
    result.setSide(axis.getSide());
    result.setTickLabelFill(axis.getTickLabelFill());
    result.setTickLabelFont(axis.getTickLabelFont());
    result.setTickLabelGap(axis.getTickLabelGap());
    result.setTickLength(axis.getTickLength());
    return result;
}

protected static ValueAxis _duplicateValueAxis(ValueAxis axis, ValueAxis result) {
    _duplicateAxis(axis, result);
    result.setLowerBound(axis.getLowerBound());
    result.setUpperBound(axis.getUpperBound());
    result.setMinorTickCount(axis.getMinorTickCount());
    result.setMinorTickLength(axis.getMinorTickLength());
    result.setTickLabelFormatter(axis.getTickLabelFormatter());
    return result;
}

/**
 * Duplicate a number axis.
 * @param axis The source axis.
 * @return A {@code NumberAxis}, never {@code null}.
 */
public static NumberAxis duplicateNumberAxis(NumberAxis axis) {
    NumberAxis result = new NumberAxis();
    _duplicateValueAxis(axis, result);
    result.setTickUnit(axis.getTickUnit());
    result.setForceZeroInRange(axis.isForceZeroInRange());
    return result;
}

/**
 * Duplicate a category axis.
 * @param axis The source axis.
 * @return A {@code CategoryAxis}, never {@code null}.
 */
public static CategoryAxis duplicateCategoryAxis(CategoryAxis axis) {
    CategoryAxis result = new CategoryAxis(axis.getCategories());
    _duplicateAxis(axis, result);
    result.setStartMargin(axis.getStartMargin());
    result.setEndMargin(axis.getEndMargin());
    result.setGapStartAndEnd(axis.gapStartAndEndProperty().get());
    return result;
}

@Override 
public void start(Stage stage) { 

final NumberAxis xAxisLC = new NumberAxis(1, 12, 1); 
final NumberAxis yAxisLC = new NumberAxis(0.53000, 0.53910, 0.0005);
yAxisLC.setSide(Side.RIGHT);

yAxisLC.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxisLC) { 

    @Override 
    public String toString(Number object) { 
        return String.format("%7.5f", object); 
    } 
}); 

final LineChart<Number, Number> lineChart = new LineChart<>(xAxisLC, yAxisLC); 

lineChart.setCreateSymbols(false); 
lineChart.setAlternativeRowFillVisible(false); 
lineChart.setAnimated(true); 
lineChart.setLegendVisible(false);

series1.getData().add(new XYChart.Data(1, 0.53185)); 
series1.getData().add(new XYChart.Data(2, 0.532235)); 
series1.getData().add(new XYChart.Data(3, 0.53234)); 
series1.getData().add(new XYChart.Data(4, 0.538765)); 
series1.getData().add(new XYChart.Data(5, 0.53442)); 
series1.getData().add(new XYChart.Data(6, 0.534658)); 
series1.getData().add(new XYChart.Data(7, 0.53023)); 
series1.getData().add(new XYChart.Data(8, 0.53001)); 
series1.getData().add(new XYChart.Data(9, 0.53589)); 
series1.getData().add(new XYChart.Data(10, 0.53476)); 
series1.getData().add(new XYChart.Data(11, 0.530123)); 
series1.getData().add(new XYChart.Data(12, 0.531035)); 

pane = new BorderPane(); 
pane.setCenter(lineChart);
mainGraphStackPane = new StackPane();
mainGraphStackPane.getChildren().add(pane);
Scene scene = new Scene(mainGraphStackPane, 800, 600); 
lineChart.getData().addAll(series1); 

stage.setScene(scene);         

scene.setOnMouseClicked(mouseHandler); 
scene.setOnMouseDragged(mouseHandler); 
scene.setOnMouseEntered(mouseHandler); 
scene.setOnMouseExited(mouseHandler); 
scene.setOnMouseMoved(mouseHandler); 
scene.setOnMousePressed(mouseHandler); 
scene.setOnMouseReleased(mouseHandler); 

Group root = new Group();
btnAdd = new Button();
btnAdd.setText("Add serie");
root.getChildren().add(btnAdd);
pane.getChildren().add(root);              

btnAdd.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {        
        NumberAxis xAxisBC = duplicateNumberAxis(xAxisLC);
        NumberAxis yAxisBC = duplicateNumberAxis(yAxisLC);
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxisBC, yAxisBC); 

        scatterChart.setAlternativeRowFillVisible(false); 
        scatterChart.setAnimated(true); 
        scatterChart.setLegendVisible(false);

        XYChart.Series series2 = new XYChart.Series();

        series2.getData().add(new XYChart.Data(1, 0.53185)); 
        series2.getData().add(new XYChart.Data(2, 0.532235)); 
        series2.getData().add(new XYChart.Data(3, 0.53234)); 
        series2.getData().add(new XYChart.Data(4, 0.538765)); 
        series2.getData().add(new XYChart.Data(5, 0.53442)); 
        series2.getData().add(new XYChart.Data(6, 0.534658)); 
        series2.getData().add(new XYChart.Data(7, 0.53023)); 
        series2.getData().add(new XYChart.Data(8, 0.53001)); 
        series2.getData().add(new XYChart.Data(9, 0.53589)); 
        series2.getData().add(new XYChart.Data(10, 0.53476)); 
        series2.getData().add(new XYChart.Data(11, 0.530123)); 
        series2.getData().add(new XYChart.Data(12, 0.531035));

        scatterChart.getData().addAll(series2);

        Set<Node> chartNode = scatterChart.lookupAll(".chart-plot-background");
        for(final Node chr : chartNode){
            chr.setStyle("-fx-background-color: transparent;");                           
        }                                                
        chartNode = lineChart.lookupAll(".chart-plot-background");
        for(final Node chr : chartNode){
            chr.setStyle("-fx-background-color: transparent");                            
        }
        mainGraphStackPane.getChildren().add(scatterChart);

        xAxisBC.lowerBoundProperty().bind(xAxisLC.lowerBoundProperty());
        yAxisBC.lowerBoundProperty().bind(yAxisLC.lowerBoundProperty());      
    }
});

stage.show(); 
} 

EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() { 

@Override 
public void handle(MouseEvent mouseEvent) { 
    boolean XScaling=false;
    boolean YScaling=false;

   if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED || mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED ){ 
        LineChart<Number, Number> lineChart = (LineChart<Number, Number>) pane.getCenter(); 
        NumberAxis yAxis = (NumberAxis) lineChart.getYAxis(); 
        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis(); 

        double Tgap = xAxis.getWidth()/(xAxis.getUpperBound() - xAxis.getLowerBound()); 
        double newXlower=xAxis.getLowerBound(), newXupper=xAxis.getUpperBound(); 
        double newYlower=yAxis.getLowerBound(), newYupper=yAxis.getUpperBound(); 

        double xAxisShift = xAxis.localToScene(0, 0).getX();
        double yAxisShift = yAxis.localToScene(0, 0).getY();

        double yAxisStep=yAxis.getHeight()/(yAxis.getUpperBound()-yAxis.getLowerBound());
        double CurrentPrice=yAxis.getUpperBound()-((mouseEvent.getY()-yAxisShift)/yAxisStep);

        double Delta=0.3;
        if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED && mouseEvent.getX()<xAxisShift+yAxis.getHeight() && mouseEvent.getY()<yAxisShift+yAxis.getHeight() && (XScaling==false || YScaling==false)){

  //==================================================== X-Axis Moving ==================================

            if(rectinitX.get() < mouseEvent.getX()){    
                newXlower=xAxis.getLowerBound()-Delta;
                newXupper=xAxis.getUpperBound()-Delta;
            }
            else if(rectinitX.get() > mouseEvent.getX()){    
                newXlower=xAxis.getLowerBound()+Delta;
                newXupper=xAxis.getUpperBound()+Delta;
            }    
            xAxis.setLowerBound( newXlower ); 
            xAxis.setUpperBound( newXupper ); 

//===================================================== Y-Axis Moving ====================================

            if(rectinitY.get() < mouseEvent.getY()){    
                newYlower=yAxis.getLowerBound()+Delta/1000;
                newYupper=yAxis.getUpperBound()+Delta/1000;
            }
            else if(rectinitY.get() > mouseEvent.getY()){    
                newYlower=yAxis.getLowerBound()-Delta/1000;
                newYupper=yAxis.getUpperBound()-Delta/1000;
            }
            yAxis.setLowerBound(newYlower);
            yAxis.setUpperBound(newYupper);
        }

 //----------------------------- Re-Scale the X-Axis when dragging below it ---------------------------------

        else if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED && mouseEvent.getY()>yAxisShift+yAxis.getHeight() ){
            if(rectinitX.get() < mouseEvent.getX()){    
                newXlower=xAxis.getLowerBound()+Delta;
                newXupper=xAxis.getUpperBound()-Delta;
            }
            else if(rectinitX.get() > mouseEvent.getX()){    
                newXlower=xAxis.getLowerBound()-Delta;
                newXupper=xAxis.getUpperBound()+Delta;
            }    
            xAxis.setLowerBound( newXlower ); 
            xAxis.setUpperBound( newXupper );           
        }

//--------------------------------- Re-Scale the Y-Axis when dragging to the left of it --------------------------

        else if(mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED && mouseEvent.getX()> (xAxisShift + xAxis.getWidth())){
            if(rectinitY.get() < mouseEvent.getY()){    
                newYlower=yAxis.getLowerBound()-Delta/1000;
                newYupper=yAxis.getUpperBound()+Delta/1000;
            }
            else if(rectinitY.get() > mouseEvent.getY()){    
                newYlower=yAxis.getLowerBound()+Delta/1000;
                newYupper=yAxis.getUpperBound()-Delta/1000;
            }
            yAxis.setLowerBound(newYlower);
            yAxis.setUpperBound(newYupper);                
        }             
        rectinitX.set(mouseEvent.getX()); 
        rectinitY.set(mouseEvent.getY()); 

        if(mouseEvent.getEventType() == MouseEvent.MOUSE_MOVED && mouseEvent.getY()>yAxisShift && mouseEvent.getY()<yAxisShift+yAxis.getHeight() && mouseEvent.getX()>xAxisShift && mouseEvent.getX()<xAxisShift+xAxis.getWidth()){

        double XX=((mouseEvent.getX() - xAxisShift) / Tgap) + xAxis.getLowerBound();
        double YY=CurrentPrice;
        series1.setName(String.format("%.2g%n",XX) + ", " + String.format("%.4g%n",YY));
        }          
     } 
} 
}; 

public static void main(String[] args) { 
launch(args);  
} 
}