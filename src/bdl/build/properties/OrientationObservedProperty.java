package bdl.build.properties;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import bdl.build.GObject;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class OrientationObservedProperty implements PanelProperty {

    private GObject gObj;
    private String setter;
    private String fxml;
    private CheckBox checkBox;
    private final HistoryManager historyManager;
    private String getter;

    public OrientationObservedProperty(final GObject gObj, String name,  final String observedProperty,String getter, final String setter, String fxml, String defaultValue, GridPane gp, int row, Node settingsNode, HistoryManager hm) {
        this.gObj = gObj;
        this.setter = setter;
        this.getter = getter;
        this.fxml = fxml;
        this.historyManager = hm;

        gp.add(new Label(name + ":"), 0, row);
        checkBox = new CheckBox();
        checkBox.setOnAction(e -> {
			ObservableList<Node> children = checkBox.getParent().getChildrenUnmodifiable();
			int ci = children.indexOf(checkBox);
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
        if (defaultValue.endsWith("HORIZONTAL")){
            defaultValue = "true";
      	}
        if (defaultValue.endsWith("VERTICAL")){
      		 defaultValue = "false";
      	}
        //Grab value from settingsNode if given
        if (settingsNode != null) {
            try {
                Method method = settingsNode.getClass().getMethod(getter);
                Object value = ((Orientation) method.invoke(settingsNode));
               
                if (value != null) {
                	if (value.equals(Orientation.HORIZONTAL)){
                      defaultValue = "true";
                	} else {
                		 defaultValue = "false";
                	}
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
       
//        System.out.println(s.orientationProperty().getValue().getClass().getSimpleName()+" "+Orientation.valueOf("HORIZONTAL"));
        
        try {
			final Method getPropMethod = gObj.getClass().getMethod(observedProperty);
//			Class<? extends Object> propvalueClass = ((ObservableValue<?>)getPropMethod.invoke(gObj)).getValue().getClass();
			
			
//			 so gehts nicht
//			 ((ObservableValue<propvalueClass>) getPropMethod.invoke(gObj));
//			 .addListener(new ChangeListener<propvalueClass>() {    
//					@Override
//		            public void changed(ObservableValue<? extends propvalueClass> observableValue, propvalueClass old, propvalueClass newValue) {				
//						
//						checkBox.setSelected(newValue.equals(Orientation.HORIZONTAL));
//		            }
//		        });
			
			((ObservableValue<Orientation>) getPropMethod.invoke(gObj)).addListener(new ChangeListener<Orientation>() {    
					@Override
		            public void changed(ObservableValue<? extends Orientation> observableValue, Orientation old, Orientation newValue) {				
						
						checkBox.setSelected(newValue.equals(Orientation.HORIZONTAL));
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
       
       
        checkBox.setSelected(Boolean.parseBoolean(defaultValue));//TODO - Handle bad defaultValue values

        try {
            setValue();
        } catch (Exception e) {
            e.printStackTrace();
            return;//TODO: Probably need some better behavior here.
        }

        gp.add(checkBox, 1, row);

        //Upon change, save to the GObject
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean a, Boolean b) {
                try {
                    setValue();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;//TODO: Probably need some better behavior here.
                }
            }
        });
    }

    private void setValue() throws Exception {
        final Method setMethod = gObj.getClass().getMethod(setter, Orientation.class);
        final Method getMethod = gObj.getClass().getMethod(getter);
        final Orientation old = (Orientation) getMethod.invoke(gObj);
        final Orientation nnew = checkBox.isSelected()?Orientation.HORIZONTAL:Orientation.VERTICAL;
      
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
                    return gObj.getFieldName() + " orientation changed!";
                }
            });
        }
        setMethod.invoke(gObj, nnew);
    }

    @Override
    public String getImport(){
     return "javafx.geometry.Orientation";
    }
    @Override
    public String getJavaCode() {
    	String stnnew = checkBox.isSelected()?"Orientation.HORIZONTAL":"Orientation.VERTICAL";
        return gObj.getFieldName() + "." + setter + "(" + stnnew + ");";
    }

    @Override
    public String getFXMLCode() {
    	String stnnew = checkBox.isSelected()?"HORIZONTAL":"VERTICAL";
        return fxml + "=\"" + stnnew + "\"";
    }
}
