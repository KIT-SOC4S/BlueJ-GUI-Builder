package bdl.build.properties;

import bdl.build.GObject;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class StyleFillObservedProperty implements PanelProperty,StyleProperty {

    private GObject gObj;
    private String fxml;
    private ColorPicker colorPicker;
    private final HistoryManager historyManager;
	private CheckBox chooseColor;
	private String color="";

    public StyleFillObservedProperty(final GObject gObj, String name, final String observedProperty, String getter,  String setter, String fxml, String defaultValue, GridPane gp, int row, Node settingsNode, HistoryManager hm) {
        this.gObj = gObj;
        this.fxml = fxml;
        this.historyManager = hm;

        gp.add(new Label(name + ":"), 0, row);
        colorPicker = new ColorPicker();
        chooseColor = new CheckBox();
        
        HBox hbox= new HBox();
        gp.add(hbox,1,row);
        hbox.getChildren().add(chooseColor);
        hbox.getChildren().add(colorPicker);
        //Grab value from settingsNode if given
        if (settingsNode != null) {
            try {
                
                String value = getStylevalue("-fx-text-fill",settingsNode.getStyle());
                if (value != null && !value.isEmpty()) {
                    color = value;
                    colorPicker.setValue(Color.web(color));
                    chooseColor.setSelected(true);
                    setValue();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

       

        try {
            setValue();
        } catch (Exception e) {
            e.printStackTrace();
            return;//TODO: Probably need some better behavior here.
        }

        chooseColor.setOnAction((a)->allowPickColor());

        
        
        //On action, save to the GObject
        colorPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    setValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    
    private void allowPickColor(){
    	try {
			setValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    private String changeStyle(String style , String value){
    	if (value == null || value.isEmpty()){
    		//remove Style
    		String oldStyle = gObj.getStyle();
    		if (oldStyle == null || oldStyle.isEmpty()){
    			return oldStyle;
    		}
    		String[] oldStyles = oldStyle.replace(" ","").split(";");
    		style=style.replace(" ","");
    		String newStyle = "";
    		for (int i=0;i<oldStyles.length;i++){
    			if (!oldStyles[i].contains(style)){
    				newStyle = newStyle+ oldStyles[i]+";";
    			}
    		}
    		return newStyle;
    			
    	} else {
    		//replace or add 
    		String oldStyle = gObj.getStyle();
    		if (oldStyle == null || oldStyle.isEmpty()){
    			return style+":"+value+";";
    		}
    		String[] oldStyles = oldStyle.replace(" ","").split(";");
    		style=style.replace(" ","");
    		String newStyle = "";
    		for (int i=0;i<oldStyles.length;i++){
    			if (!oldStyles[i].contains(style)){
    				newStyle = newStyle+ oldStyles[i]+";";
    			}
    		}
    		newStyle = newStyle+ style+":"+value+";";
    		return newStyle;
    	}    	
    }
    
    private String getStylevalue(String style){    	
    		//remove Style
    	    String value = null;
    		String styles = gObj.getStyle();
    		if (styles == null || styles.isEmpty()){
    			return value;
    		}
    		String[] stylearray = styles.replace(" ","").split(";");
    		style=style.replace(" ","");
    		
    		for (int i=0;i<stylearray.length;i++){
    			if (stylearray[i].contains(style)){
    				String s = stylearray[i];
    				value = s.substring(s.indexOf(':')+1,s.length());
    				if (value.endsWith(";")){
    					value = value.substring(0,value.length()-1);
    				}
    			}
    		}
    		return value;
    }
    private String getStylevalue(String style,String styles){    	
		//remove Style
	    String value = null;
	
		if (styles == null || styles.isEmpty()){
			return value;
		}
		String[] stylearray = styles.replace(" ","").split(";");
		style=style.replace(" ","");
		
		for (int i=0;i<stylearray.length;i++){
			if (stylearray[i].contains(style)){
				String s = stylearray[i];
				value = s.substring(s.indexOf(':')+1,s.length());
				if (value.endsWith(";")){
					value = value.substring(0,value.length()-1);
				}
			}
		}
		return value;
}
    private void setValue() throws Exception {
        final String old = getStylevalue("-fx-text-fill"); 
        String nnew=!chooseColor.isSelected()?"":colorPicker.getValue().toString().replace("0x","#");  
        color = nnew;
        
        if(!historyManager.isPaused())
        historyManager.addHistory(new HistoryItem() {
            @Override
            public void revert() {
                try {
                    gObj.setStyle(changeStyle("-fx-text-fill",old));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                colorPicker.setValue(Color.web(old));
                historyManager.pause();
                // work around to update colorpicker's displayed selection
                colorPicker.fireEvent(new ActionEvent(null, colorPicker));
                historyManager.unpause();
            }

            @Override
            public void restore() {
                try {                	
                	 gObj.setStyle(changeStyle("-fx-text-fill",nnew));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (nnew!=null && !nnew.isEmpty()){
                colorPicker.setValue(Color.web(nnew));
                }
                historyManager.pause();
                // work around to update colorpicker's displayed selection
                colorPicker.fireEvent(new ActionEvent(null, colorPicker));
                historyManager.unpause();
            }

            @Override
            public String getAppearance() {
                return gObj.getFieldName() + "background color changed!";
            }
        });
        
        gObj.setStyle(changeStyle("-fx-text-fill",nnew));
    }

    @Override
    public String getJavaCode() {
    	return "";
    	/*
    	if (color==null || color.isEmpty() ){
    		return "";
    	}    	
        return gObj.getFieldName() + "." + "setStyle" + "(\"-fx-text-fill:" + color + ";\");";
        */
    }
    @Override
    public String getStyleDescription() {
    	if (color==null || color.isEmpty() ){
    		return "";
    	}    	
        return "-fx-text-fill:" + color + ";";
    }
    
    @Override
    public String getFXMLCode() {
    	return "";
    	/*
    	System.out.println("Style");
    	if (color==null || color.isEmpty() ){
    		return "";
    	}
        return fxml + "=\"-fx-text-fill:" + color + ";\"";
        */
    }
}
