package bdl.build.javafx.scene.control;

import java.util.List;

import bdl.build.GObject;
import bdl.build.properties.PanelProperty;
import bdl.view.right.PropertyEditPane;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.control.Slider;

public class GSlider extends Slider implements GObject {
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
    public void setPEP(PropertyEditPane propertyEditPane) {
        pep = propertyEditPane;
    }
    
    @Override
    public PropertyEditPane getPEP() {
        return pep;
    }
    
    @Override
	public String getNodeClassName() {		
		return "Slider";
	}
}
