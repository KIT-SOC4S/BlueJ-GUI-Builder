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

    public ListenerHint(String name, String method, String event, String defaultValue) {
        listenerName = name;
        listenerMethod = method;
        listenerEvent = event;
        listenerText = buildText();
        this.defaultValue=defaultValue;
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