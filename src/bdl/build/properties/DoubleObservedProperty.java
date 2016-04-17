package bdl.build.properties;

import bdl.build.GObject;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DoubleObservedProperty implements PanelProperty {

    private GObject gObj;
    private String setter;
    private String getter;
    private String fxml;
    private TextField textField;
    private DecimalFormat format = new DecimalFormat("#.##",new DecimalFormatSymbols(Locale.US));
    private final HistoryManager historyManager;

    public DoubleObservedProperty(final GObject gObj, String name, final String observedProperty,  String fxml, String defaultValue, GridPane gp, int row, Node settingsNode, HistoryManager hm) {
        this.gObj = gObj;
//        this.setter = setter;
//        this.getter = getter;
        String property=observedProperty.replace("Property","");
        property=property.substring(0,1).toUpperCase()+property.substring(1);
        this.setter = "set"+property;
        this.getter = "get"+property;
        this.fxml = fxml;
        this.historyManager = hm;
        gp.add(new Label(name + ":"), 0, row);        
        textField = new TextField();

        //Grab value from settingsNode if given
        if (settingsNode != null) {
            try {
                Method method = settingsNode.getClass().getMethod(getter);
                String value = method.invoke(settingsNode).toString();
                if (value != null) {
                    defaultValue = value;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

      
        try {
			final Method getPropMethod = gObj.getClass().getMethod(observedProperty);
			((ObservableValue<Number>) getPropMethod.invoke(gObj)).addListener(new ChangeListener<Number>() {    
					@Override
		            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newValue) {						
						textField.setText(format.format(newValue));					
		            }
		        });
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
        
        
        
        
        textField.setText(format.format(Double.parseDouble(defaultValue))); //TODO - Handle bad defaultValue values

        setValue();
        textField.setOnAction(e -> {
			ObservableList<Node> children = textField.getParent().getChildrenUnmodifiable();
			int ci = children.indexOf(textField);
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
        gp.add(textField, 1, row);

        //Upon losing focus, save to the GObject
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                if (!aBoolean2) {
                    setValue();
                }
            }
        });

        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    setValue();
                }
            }
        });
        textField.textProperty().addListener(new ChangeListener<String>() {
	        @Override
	        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
	        	if (!(istZahl(newValue)||newValue.equals("")||newValue.equals("-"))){
	        		textField.setText(oldValue);
	        	}
	           
	        }
	    });
      
    }

    private void setValue() {
        try {
            final double dValue = (double) ((int) (0.5 + (Double.parseDouble(textField.getText()) * 10))) / 10;
            textField.setText(format.format(dValue));

            final Method setMethod = gObj.getClass().getMethod(setter, double.class);
            final Method getMethod = gObj.getClass().getMethod(getter);
            final double old = (double) getMethod.invoke(gObj);
            final double nnew = dValue;
            if (old != nnew && !historyManager.isPaused()) {
                historyManager.addHistory(new HistoryItem() {
                    @Override
                    public void revert() {
                        try {
                            setMethod.invoke(gObj, old);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void restore() {
                        try {
                            setMethod.invoke(gObj, nnew);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public String getAppearance() {
                        return gObj.getFieldName() + " double changed!";
                    }
                });
            }
            setMethod.invoke(gObj, dValue);
        } catch (Exception e) {
            // If value entered is not a double, then revert to the previous value
            Method method;
            try {
                method = gObj.getClass().getMethod(getter);
                textField.setText(format.format((Double) method.invoke(gObj)));
            } catch (Exception ee) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getJavaCode() {
    	if (!generateJavaCode){
    		return "";
    	}
    	if (setter.toLowerCase().contains("height")||setter.toLowerCase().contains("width")){
    		double wert = Double.parseDouble(textField.getText());
    		if (wert <=0 && wert >=-2){
    			return "";
    		}
    	}
        return gObj.getFieldName() + "." + setter + "(" + textField.getText() + ");";
    }

    @Override
    public String getFXMLCode() {
    	
    	if (setter.toLowerCase().contains("height")||setter.toLowerCase().contains("width")){
    		double wert = Double.parseDouble(textField.getText());
    		if (wert <=0 && wert >=-2){
    			return "";
    		}
    	}
        return fxml + "=\"" + textField.getText() + "\"";
    }
    
    private boolean istZahl(String s) {
		try {
			Double.valueOf(s).doubleValue();
			return true;
		} catch (Exception nfe) {
			return false;
		}
	}
    boolean generateJavaCode = true;
    @Override
    public void disableJavaCodeGeneration(){
    	generateJavaCode=false;
    }
}
