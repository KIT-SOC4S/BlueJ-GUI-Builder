package bdl.build.properties;

import java.text.DecimalFormat;

import bdl.build.GObject;
import bdl.build.javafx.scene.canvas.GCanvas;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class CanvasWidthProperty implements PanelProperty {

    private GCanvas gObj;
    private GCanvas node;
    private TextField width;
    private TextField height;
    private DecimalFormat format = new DecimalFormat("#.##");
    private HistoryManager historyManager;

    public CanvasWidthProperty(final GCanvas gObj, String name, final String getter, final String setter, String fxml, String defaultValue, GridPane gp, int row, Node settingsNode, HistoryManager hm) {
       // this.node = (Node) gObj;
    	this.node = gObj;
        this.gObj = gObj;
        historyManager = hm;
        int row1 = row;
        int row2 = row + 1;

        Label lx = new Label("Width:");
        Label ly = new Label("Height:");

        gp.add(lx, 0, row1);
        gp.add(ly, 0, row2);
        width = new TextField();
        height = new TextField();

        //Grab value from settingsNode if given
        if (settingsNode != null && settingsNode instanceof Canvas) {
            node.setWidth(((Canvas)settingsNode).getWidth());
            node.setHeight(((Canvas)settingsNode).getHeight());
        }

        width.setText(format.format(node.getWidth()));
        height.setText(format.format(node.getHeight()));

        gp.add(width, 1, row1);
        gp.add(height, 1, row2);

       
        node.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
            	width.setText(format.format(number2));
            }
        });
        node.layoutYProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
            	height.setText(format.format(number2));
            }
        });

        //Upon losing focus, save to the GObject
        width.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                if (!aBoolean2) {
                    try {
                        double value = Double.parseDouble(width.getText());
                        if (value != node.getWidth()) {
                            updateHistory(node.getHeight(), value, node.getHeight(), node.getWidth());
                           node.setWidth(value);
                        }
                    } catch (Exception e) {
                        //Reset value
                    	width.setText(format.format(node.getWidth()));
                    }
                }
            }
        });
        height.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                if (!aBoolean2) {
                    try {
                        double value = Double.parseDouble(height.getText());
                        if (value != node.getHeight()) {
                            updateHistory(value, node.getWidth(), node.getHeight(), node.getWidth());
                            node.setHeight(value);
                        }
                    } catch (Exception e) {
                        //Reset value
                    	height.setText(format.format(node.getHeight()));
                    }
                }
            }
        });

        width.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    try {
                        double value = Double.parseDouble(width.getText());
                        if (value != node.getWidth()) {
                            updateHistory(node.getHeight(), value, node.getHeight(), node.getWidth());
                            node.setWidth(value);
                        }
                    } catch (Exception e) {
                        //Reset value
                    	width.setText(format.format(node.getWidth()));
                    }
                }
            }
        });

        height.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    try {
                        double value = Double.parseDouble(height.getText());
                        if (value != node.getHeight()) {
                            updateHistory(value, node.getWidth(), node.getHeight(), node.getWidth());
                            node.setHeight(value);
                        }
                    } catch (Exception e) {
                        //Reset value
                    	height.setText(format.format(node.getHeight()));
                    }
                }
            }
        });
    }

    @Override
    public String getJavaCode() {
        return gObj.getFieldName() + ".setWidth(" + node.getWidth() + ");\n"
                + gObj.getFieldName() + ".setHeight(" + node.getHeight() + ");";
    }

    @Override
    public String getFXMLCode() {
        return "width=\"" + node.getWidth() + "\" height=\"" + node.getHeight() + "\"";
    }

    public void updateHistory(final double yvalue, final double xvalue, final double yhistory, final double xhistory) {
        historyManager.addHistory(new HistoryItem() {
            @Override
            public void restore() {
                ((GCanvas)node).setHeight(yvalue);
                ((GCanvas)node).setWidth(xvalue);
            }

            @Override
            public void revert() {
            	((GCanvas)node).setHeight(yhistory);
            	((GCanvas)node).setWidth(xhistory);
            }

            @Override
            public String getAppearance() {
                return gObj.getFieldName() + " Size changed!";
            }
        });
    }
}
