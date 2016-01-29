package bdl.build.properties;

import bdl.build.GObject;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Double1DPProperty implements PanelProperty {

    private GObject gObj;
    private String setter;
    private String getter;
    private String fxml;
    private TextField textField;
    private DecimalFormat format = new DecimalFormat("#.##",new DecimalFormatSymbols(Locale.US));
    private final HistoryManager historyManager;

    public Double1DPProperty(final GObject gObj, String name, final String observedProperty, final String getter, final String setter, String fxml, String defaultValue, GridPane gp, int row, Node settingsNode, HistoryManager hm) {
        this.gObj = gObj;
        this.setter = setter;
        this.getter = getter;
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
        
        
        
        textField.setText(format.format(Double.parseDouble(defaultValue))); //TODO - Handle bad defaultValue values

        setValue();

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
}
