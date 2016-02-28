package bdl.model;

/**
 *
 * @author Ben Goodwin
 */
public class ListenerHint {
    public String getListenerName() {
		return listenerName;
	}

	public String getListenerMethod() {
		return listenerMethod;
	}

	public String getListenerEvent() {
		return listenerEvent;
	}

	private String listenerName;
    private String listenerMethod;
    private String listenerEvent;
    private String listenerText;
    private String defaultValue;// Is true iff this listener should be implemented
    //as is by example setOnAction for Button 
    private String packageName="";
    private String listenertype="standard";
	private String propertytype;
	private String propertyname;
    
    public String getPropertyname() {
		return propertyname;
	}

	public String getPropertytype() {
		return propertytype;
	}

	public String getListenertype() {
		return listenertype;
	}

//	public ListenerHint(String name, String method, String event, String defaultValue, String packageName) {    	
//        listenerName = name;
//        listenerMethod = method;
//        listenerEvent = event;
//        listenerText = buildText();
//        this.defaultValue=defaultValue;
//        this.packageName=packageName;
//    }
    
    public ListenerHint(String name, String method, String event, String defaultValue, String packageName, String listenerType, String propertyName, String propertyType) {    	
        listenerName = name;
        listenerMethod = method;
        listenerEvent = event;
        listenerText = buildText();
        this.defaultValue=defaultValue;
        this.packageName=packageName;
        this.listenertype=listenerType;
        this.propertyname = propertyName;
        this.propertytype =propertyType; 
    }
    
    public String getPackageName() {
		return packageName;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getName() {
        return listenerName;
    }

    public String getText() {
        return listenerText;
    }

    private String buildText() {
        return "." + listenerMethod + "(new EventHandler<" + listenerEvent + ">() {\n"
                + "    @Override\n" + "    public void handle(" + listenerEvent + " e) {\n"
                + "        //TODO\n" + "    }\n" + "});";
    }
}