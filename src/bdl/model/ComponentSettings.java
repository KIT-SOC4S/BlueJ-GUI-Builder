package bdl.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Component Settings stores editable values for components. Stores the settings
 * and properties, layout and listener options for each component that will be
 * used by the property editing panes to list the options available to edit for
 * each component. Will be initialised after the associated xml file has been
 * read by ComponentSettingsStore.
 */
public class ComponentSettings {

    private String type;
    private String packageName;
    private String layoutType;
    private String icon;
    private List<Property> properties = new ArrayList<>();
    private List<ListenerHint> listenerHints = new ArrayList<>();
    
    /**
     * Returns a collection of Properties properties associated with the this
     * component.
     * 
     * @return A collection of Properties objects associated with this component
     */
    public List<Property> getProperties() {
        return properties;
    }
    
    /**
     * Returns a collection of ListenerHint objects associated with the this
     * component.
     * 
     * @return A collection of ListenerHint objects associated with this component
     */
    public List<ListenerHint> getListenerHints() {
        return listenerHints;
    }

    /**
     * Returns the name of the component portrayed by this Component
     * 
     * @return The name of the component portrayed by this Component
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the name of the component to be represented
     * 
     * @param type The Component name to be represented
     */
    public void setType(String type) {
        this.type = type;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(String layoutType) {
        this.layoutType = layoutType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Adds the given Properties into a new Properties object, then adds it to
     * the collection
     *
     * @param name Name of the property read from XML
     * @param enabled Enabled (true or false) of the property read from XML
     * @param type Type of the property read from XML
     * @param defaultValue
     * @param getter
     * @param setter
     * @param javaCodeGeneration 
     */
    public void addProperty(String name, String enabled, String type, String defaultValue, String observedProperty, String getter, String setter, String fxml, String javaCodeGeneration) {
        properties.add(new Property(name, enabled, type, defaultValue, observedProperty,getter, setter, fxml, javaCodeGeneration));
    }

    public void addListenerHint(String name, String method, String event,String defaultValue, String packageName,String listenerType, String propertyName, String propertyType) {
        listenerHints.add(new ListenerHint(name, method, event,defaultValue,packageName,listenerType, propertyName, propertyType));
    }
    

}