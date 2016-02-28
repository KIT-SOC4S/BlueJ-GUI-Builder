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

public class ObservableNumberListenerEnabledProperty extends ListenerEnabledProperty {

    public ObservableNumberListenerEnabledProperty(final GObject gObj, String listenerMethod,String eventname, String eventtype, String implementIt, String packageName, GridPane gp, int row)  {
    	super(gObj,listenerMethod,eventname,eventtype,implementIt,packageName,gp,row);
       System.out.println(eventname+" "+eventtype);
    }
 
    @Override
    public String getJavaCode() {
        if (isToImplement()) {
            return getgObj().getFieldName() + "." + getListenerMethod()+"("
            		+ "(ov,oldV,newV)-> handle" + firstLetterUpcase(getEventname())+firstLetterUpcase(getgObj().getFieldName())+"(ov,oldV,newV));";
        } else {
            return "";
        }
    }
    
    @Override
    public String getJavaCodeHandler() {
        if (isToImplement()) {
            return "public void handle" + firstLetterUpcase(getEventname())+firstLetterUpcase(getgObj().getFieldName())+"(ObservableValue<? extends Number> ov,Number old_val, Number new_val) {\n        //TODO\n" + "  }\n" ;
        } else {
            return "";
        }
    }
   

}