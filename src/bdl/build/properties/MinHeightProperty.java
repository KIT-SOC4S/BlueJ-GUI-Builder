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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class MinHeightProperty implements PanelProperty {

    
    private Region node;
    private TextField width;
    private TextField height;
    private DecimalFormat format = new DecimalFormat("#.##",new DecimalFormatSymbols(Locale.US));
    private HistoryManager historyManager;
    private GObject gnObj ;

    public MinHeightProperty(final GObject gnObj, String name, final String observedProperty, final String getter, final String setter, String fxml, String defaultValue, GridPane gp, int row, Node settingsNode, HistoryManager hm) {
    	this.gnObj=gnObj;
    	if (!(gnObj instanceof Region)){
    		return;
    	}
    	
    	this.node = (Region)gnObj;
        historyManager = hm;
        int row1 = row;
       

     
        Label ly = new Label("MinHeight:");

        gp.add(ly, 0, row1);
        
        height = new TextField();

        //Grab value from settingsNode if given
        if (settingsNode != null && settingsNode instanceof Region) {
      
            node.setMinHeight(((Region)settingsNode).getMinHeight());
        } else {
        	
        	node.setMinHeight(100);
        }

      
        height.setText(format.format(node.getHeight()));
        height.setOnAction(e -> {
			ObservableList<Node> children = height.getParent().getChildrenUnmodifiable();
			int ci = children.indexOf(height);
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
      
        gp.add(height, 1, row1);

       
       
        node.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
            	height.setText(format.format(number2));
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
                            node.setMinHeight(value);
                        }
                    } catch (Exception e) {
                        //Reset value
                    	height.setText(format.format(node.getHeight()));
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
                            node.setMinHeight(value);
                        }
                    } catch (Exception e) {
                        //Reset value
                    	height.setText(format.format(node.getHeight()));
                    }
                }
            }
        });
        height.textProperty().addListener(new ChangeListener<String>() {
	        @Override
	        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
	        	if (!(istZahl(newValue)||newValue.equals("")||newValue.equals("-"))){
	        		height.setText(oldValue);
	        	}
	           
	        }
	    });
      
    }

    
  
    
    
    @Override
    public String getJavaCode() {
        return gnObj.getFieldName() + ".setMinHeight(" + node.getHeight() + ");";
    }

    @Override
    public String getFXMLCode() {
        return " minHeight=\"" + node.getHeight() + "\"";
    }

    public void updateHistory(final double yvalue, final double xvalue, final double yhistory, final double xhistory) {
        historyManager.addHistory(new HistoryItem() {
            @Override
            public void restore() {
                node.setMinHeight(yvalue);
            }

            @Override
            public void revert() {
                node.setMinHeight(yhistory);
            }

            @Override
            public String getAppearance() {
                return gnObj.getFieldName() + " minHeight changed!";
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
