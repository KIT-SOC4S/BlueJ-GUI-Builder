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

public class PropertyListenerEnabledProperty extends ListenerEnabledProperty {

    private String propertyName;
	private String propertyType;

	public PropertyListenerEnabledProperty(final GObject gObj, String propertyName, String propertyType, String eventName, String implementIt, String packageName, GridPane gp, int row)  {
    	super(gObj,eventName,eventName,null,implementIt,packageName,gp,row);
    	this.propertyName = propertyName;
    	this.propertyType=propertyType;
       
    }
 
    @Override
    public String getJavaCode() {
        if (isToImplement()) {
            return getgObj().getFieldName() + "." + propertyName+"().addListener("
            		+ "(ov,oldV,newV)-> handle" +firstLetterUpcase(getgObj().getFieldName())+ firstLetterUpcase(getEventname())+"(ov,oldV,newV));";
        } else {
            return "";
        }
    }
    
    @Override
    public String getJavaCodeHandler() {
        if (isToImplement()) {
            return "public void handle" + firstLetterUpcase(getgObj().getFieldName())+ firstLetterUpcase(getEventname())+"(ObservableValue<? extends "+ propertyType+"> value, "+ propertyType+" oldValue, "+ propertyType+" newValue) {\n        //TODO\n" + "  }\n" ;
        } else {
            return "";
        }
    }
    
    
   

}