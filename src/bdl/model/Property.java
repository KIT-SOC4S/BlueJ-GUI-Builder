package bdl.model;

/**
 * Simple Properties object to store name, type, value.
 */
public class Property {
    private String name;
    private boolean enabled;
    private String pseudotype;
    private String defaultValue;
    private String fxml;
    private String property;
    
    private boolean generateJavaCode = true;

    private boolean isStyleProperty;
    

	public boolean isStyleProperty() {
		return isStyleProperty;
	}

	public Property(String name, String enabled, String pseudotype, String defaultValue, String observedProperty,  String fxml, String javaCodeGeneration) {
        this.name = name;
        this.enabled = Boolean.parseBoolean(enabled);
        this.pseudotype = pseudotype;
        this.defaultValue = defaultValue;
        this.fxml = fxml;
        this.property =  observedProperty;
        if (javaCodeGeneration!=null && javaCodeGeneration.toLowerCase().equals("false")){
        	generateJavaCode=false;
        }
        isStyleProperty=false;
    }

    public Property(String name, String enabled, String pseudotype, String defaultValue, String styleProperty) {
    	this.name = name;
        this.enabled = Boolean.parseBoolean(enabled);
        this.pseudotype = pseudotype;
        this.defaultValue = defaultValue;
        this.property = styleProperty;
        isStyleProperty=true; 
        
	}

	public String getName() {
        return name;
    }

    public String getPseudotype() {
        return pseudotype;
    }

   
    public String getFxml() {
        return fxml;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

   
    public boolean isGenerateJavaCode() {
		return generateJavaCode;
	}

	public String getProperty() {
		return property;
	}
}
