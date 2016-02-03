package bdl.build.properties;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import bdl.build.GObject;
import bdl.build.javafx.scene.canvas.GCanvas;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class MinWidthProperty implements PanelProperty {

//    private Region gObj;
    private Region node;
    private TextField width;
    private DecimalFormat format = new DecimalFormat("#.##",new DecimalFormatSymbols(Locale.US));
    private HistoryManager historyManager;
    private GObject gnObj ;

    public MinWidthProperty(final GObject gnObj, String name, final String observedProperty, final String getter, final String setter, String fxml, String defaultValue, GridPane gp, int row, Node settingsNode, HistoryManager hm) {
        this.gnObj=gnObj;
    	if (!(gnObj instanceof Region)){
    		return;
    	}
    	
    	this.node = (Region)gnObj;
//        this.gObj = (Region)gnObj;
        historyManager = hm;
        int row1 = row;

        Label lx = new Label("MinWidth:");

        gp.add(lx, 0, row1);
        width = new TextField();

        //Grab value from settingsNode if given
        if (settingsNode != null && settingsNode instanceof Region) {
            ((Region) node).setMinWidth(((Region)settingsNode).getMinWidth());
        } else {
        	node.setMinWidth(100);
        }

        width.setText(format.format(node.getWidth()));
        width.setOnAction(e -> {
			ObservableList<Node> children = width.getParent().getChildrenUnmodifiable();
			int ci = children.indexOf(width);
			int maxi = children.size()-1;
			int i = ci + 1;
			while (i != ci) {
				if (i<=maxi) {
					if (children.get(i).isFocusTraversable()) {
						children.get(i).requestFocus();
						return;
					} else {
						i++;
					}
				} else {
					i=0;
				}
			}
		});
        gp.add(width, 1, row1);

       
        node.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
            	width.setText(format.format(number2));
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
//                            System.out.println("pw");
                           node.setMinWidth(value);
                           
                        }
                    } catch (Exception e) {
                        //Reset value
                    	width.setText(format.format(node.getWidth()));
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
                            node.setMinWidth(value);
                        }
                    } catch (Exception e) {
                        //Reset value
                    	width.setText(format.format(node.getWidth()));
                    }
                }
            }
        });
        width.textProperty().addListener(new ChangeListener<String>() {
	        @Override
	        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
	        	if (!(istZahl(newValue)||newValue.equals("")||newValue.equals("-"))){
	        		width.setText(oldValue);
	        	}
	           
	        }
	    });
    }

    @Override
    public String getJavaCode() {
        return gnObj.getFieldName() + ".setMinWidth(" + node.getWidth() + ");\n";
    }

    @Override
    public String getFXMLCode() {
        return "minWidth=\"" + node.getWidth() + "\"";
    }

    public void updateHistory(final double yvalue, final double xvalue, final double yhistory, final double xhistory) {
        historyManager.addHistory(new HistoryItem() {
            @Override
            public void restore() {
                node.setMinWidth(xvalue);
            }

            @Override
            public void revert() {
                node.setMinWidth(xhistory);
            }

            @Override
            public String getAppearance() {
                return gnObj.getFieldName() + " minWidth changed!";
            }
        });
    }
    private boolean istZahl(String s) {
		try {
			Double.valueOf(s).doubleValue();
			return true;
		} catch (Exception nfe) {
			return false;
		}
	}
}
