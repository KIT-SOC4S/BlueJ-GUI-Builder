package bdl.build.properties;

import bdl.build.GObject;
import bdl.build.GUIObject;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class ListenerEnabledProperty implements PanelProperty {

    private GObject gObj;
    private CheckBox checkBox;
    private GUIObject guiObject;
    private String listenerMethod;
	private String eventtype;

    public ListenerEnabledProperty(final GObject gObj,final GUIObject guiObject, String listenerMethod, String eventname, String eventtype, String defaultValue, GridPane gp, int row) {
        this.gObj = gObj;
        this.guiObject=guiObject;
        this.listenerMethod=listenerMethod;
        this.eventtype=eventtype;
        gp.add(new Label(listenerMethod + ":"), 0, row);
        checkBox = new CheckBox();
        if (defaultValue!=null){
        checkBox.setSelected(Boolean.parseBoolean(defaultValue));//TODO - Handle bad defaultValue values
        } else {
        	checkBox.setSelected(false);
        } 
        gp.add(checkBox, 1, row);
       
    }

    private String firstLetterUpcase(String text){
    	
    	String newSt=(text.substring(0, 1)).toUpperCase();
    	if (text.length()>1){
    		newSt+=text.substring(1);
    	}
    	return newSt;
    }
    
    @Override
    public String getJavaCode() {
        if (checkBox.isSelected()) {
            return guiObject.getClassName() + "."+gObj.getFieldName() + "." + listenerMethod+"("
            		+ "e-> handle" + firstLetterUpcase(eventtype)+firstLetterUpcase(gObj.getFieldName())+"(e);";
        } else {
            return "";
        }
    }
    
    public String getJavaCode2() {
        if (checkBox.isSelected()) {
            return "public void handle" + firstLetterUpcase(eventtype)+firstLetterUpcase(gObj.getFieldName())+"("
            		+ eventtype +"event){\n        //TODO\n" + "    }\n" + "});";
        } else {
            return "";
        }
    }
    

    @Override
    public String getFXMLCode() {
        if (checkBox.isSelected()) {
            return "disable=\"" + checkBox.isSelected() + "\"";
        } else {
            return "";
        }
    }
}