package bdl.build.javafx.scene.control;

import java.util.List;

import bdl.build.GObject;
import bdl.build.properties.PanelProperty;
import bdl.build.properties.ToggleGroupObservedProperty;
import bdl.view.right.PropertyEditPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ToggleButton;

public class GToggleButton extends ToggleButton implements GObject {
    private String fieldName;
    private List<PanelProperty> properties;
    private PropertyEditPane pep;
    private StringProperty fieldNameProperty = new SimpleStringProperty();

    @Override
    public String getFieldName() {
        return fieldNameProperty.getValue();
    }

    @Override
    public void setFieldName(String fieldName) {
        fieldNameProperty.setValue(fieldName);
    }

    @Override
    public StringProperty fieldNameProperty() {
        return fieldNameProperty;
    }

    @Override
    public void setPanelProperties(List<PanelProperty> properties) {
        this.properties = properties;
    }

    @Override
    public List<PanelProperty> getPanelProperties() {
        return properties;
    }

    @Override
    public void fire() {
        //Prevents the togglebutton from being checked/unchecked if you click on it
    }

    @Override
    public void setPEP(PropertyEditPane propertyEditPane) {
        pep = propertyEditPane;
    }
    
    @Override
    public PropertyEditPane getPEP() {
        return pep;
    }
    @Override
   	public String getNodeClassName() {		
   		return "ToggleButton";
   	}

	public String getToggleGroupName() {
		for (PanelProperty pp:properties){
			if (pp instanceof ToggleGroupObservedProperty){
				return ((ToggleGroupObservedProperty)pp).getToggleGroupName();
			}
		}
		return "";
	}
	public void setToggleGroupName(String value) {
		for (PanelProperty pp:properties){
			if (pp instanceof ToggleGroupObservedProperty){
				 ((ToggleGroupObservedProperty)pp).setToggleGroupName(value);
				 return;
			}
		}
	
	}
}
