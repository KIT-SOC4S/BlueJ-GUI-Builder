package bdl.build.properties;

import bdl.build.GObject;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class StringObservedProperty implements PanelProperty {

    private GObject gObj;
    private String setter;
    private String getter;
    private String fxml;
    private TextField textField;
    private final HistoryManager historyManager;

    public StringObservedProperty(final GObject gObj, String name, final String observedProperty, String fxml, String defaultValue, GridPane gp, int row, Node settingsNode, HistoryManager hm) {
        this.gObj = gObj;
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
                String value = (String) method.invoke(settingsNode);
                if (value != null) {
                    defaultValue = value;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        textField.setText(defaultValue);
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
        try {
			final Method getPropMethod = gObj.getClass().getMethod(observedProperty);
			((ObservableValue<String>) getPropMethod.invoke(gObj)).addListener(new ChangeListener<String>() {    
					@Override
		            public void changed(ObservableValue<? extends String> observableValue, String number, String newValue) {						
						textField.setText(newValue);
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
        
        

        try {
            setValue();
        } catch (Exception e) {
            e.printStackTrace();
            return;//TODO: Probably need some better behavior here.
        }

        gp.add(textField, 1, row);

        //Upon losing focus, save to the GObject
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                if (!aBoolean2) {
                    try {
                        setValue();
                    } catch (Exception e) {
                        return;//TODO: Probably need some better behavior here.
                    }
                }
            }
        });
    }

    private void setValue() throws Exception {
        final Method setMethod = gObj.getClass().getMethod(setter, String.class);
        final Method getMethod = gObj.getClass().getMethod(getter);
        final String old = (String) getMethod.invoke(gObj);
        final String nnew = textField.getText();
        if (!old.equals(nnew) && !historyManager.isPaused()) {
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
                    return gObj.getFieldName() + " string changed!";
                }
            });
        }
        setMethod.invoke(gObj, textField.getText());
    }

    @Override
    public String getJavaCode() {
        return gObj.getFieldName() + "." + setter + "(\"" + textField.getText().replace("\\", "\\\\").replace("\"", "\\\"") + "\");";
    }

    @Override
    public String getFXMLCode() {
        return fxml + "=\"" + textField.getText().replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
