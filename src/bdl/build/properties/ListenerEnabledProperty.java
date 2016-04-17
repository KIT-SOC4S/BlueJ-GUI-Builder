package bdl.build.properties;

import bdl.build.GObject;
import bdl.build.GUIObject;
import bdl.controller.Controller;
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
    private String listenerMethod;
	private String eventtype;
	private String packageName;
	private boolean toImplement;
	private String eventname;

    public ListenerEnabledProperty(final GObject gObj, String listenerMethod, String eventname, String eventtype, String implementIt, String packageName, GridPane gp, int row) {
        this.setgObj(gObj);
        this.setListenerMethod(listenerMethod);
        this.eventtype=eventtype;
        this.packageName=packageName;       
        this.setEventname(eventname);
        gp.add(new Label(listenerMethod + ":"), 0, row);
        checkBox = new CheckBox();
        if (implementIt!=null){
           checkBox.setSelected(Boolean.parseBoolean(implementIt));
        } else {
        	checkBox.setSelected(false);
        } 
        
        setToImplement(checkBox.isSelected());
        checkBox.setOnAction(e->handleCheckboxevent(e));
        gp.add(checkBox, 1, row);      
        
    }

    private void handleCheckboxevent(ActionEvent e) {
		setToImplement(checkBox.isSelected());
	}

	protected String firstLetterUpcase(String text){
    	
    	String newSt=(text.substring(0, 1)).toUpperCase();
    	if (text.length()>1){
    		newSt+=text.substring(1);
    	}
    	return newSt;
    }
    
    @Override
    public String getJavaCode() {
        if (isToImplement()) {
        	if (Controller.createSimpleCode){
        		 return getgObj().getFieldName() + "." + getListenerMethod()+"("
                 		+ "e-> handle" + firstLetterUpcase(getEventname())+firstLetterUpcase(getgObj().getFieldName())+"());";
        	} 
            return getgObj().getFieldName() + "." + getListenerMethod()+"("
            		+ "e-> handle" + firstLetterUpcase(getEventname())+firstLetterUpcase(getgObj().getFieldName())+"(e));";
        } else {
            return "";
        }
    }
    
    public String getJavaCodeHandler() {
        if (isToImplement()) {
        	if (Controller.createSimpleCode){
        		return "public void handle" + firstLetterUpcase(getEventname())+firstLetterUpcase(getgObj().getFieldName())+"() {\n        //TODO\n" + "  }\n" ;
        	}
            return "public void handle" + firstLetterUpcase(getEventname())+firstLetterUpcase(getgObj().getFieldName())+"("
            		+ eventtype +" event) {\n        //TODO\n" + "  }\n" ;
        } else {
            return "";
        }
    }
    @Override
    public   String getPackageName() {
    	 if (isToImplement()&&!Controller.createSimpleCode) {
    		 return packageName;
    	 } else {
    		 return "";
    	 }
    }
    

    @Override
    public String getFXMLCode() {   
    	return "";
    	//vorläufig mal weglassen, ansonsten benötigt man wohl eine Controllerklassenangabe im FXML Header
//            return getEventname()+"=\"#handle" + firstLetterUpcase(getEventname())+firstLetterUpcase(getgObj().getFieldName()+"\"");        
    }

	public boolean isToImplement() {
		return toImplement;
	}

	public void setToImplement(boolean toImplement) {
		this.toImplement = toImplement;
	}

	public String getListenerMethod() {
		return listenerMethod;
	}

	public void setListenerMethod(String listenerMethod) {
		this.listenerMethod = listenerMethod;
	}

	public GObject getgObj() {
		return gObj;
	}

	public void setgObj(GObject gObj) {
		this.gObj = gObj;
	}

	public String getEventname() {
		return eventname;
	}

	public void setEventname(String eventname) {
		this.eventname = eventname;
	}
}