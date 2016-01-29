package bdl.build.javafx.scene.control;

import bdl.build.GObject;
import bdl.view.right.PropertyEditPane;
import bdl.build.properties.PanelProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;

import java.util.List;

public class GButton extends Button implements GObject {
    private List<PanelProperty> properties;
    private PropertyEditPane pep;
    private StringProperty fieldNameProperty = new SimpleStringProperty();

    

	@Override
    public String getFieldName() {
//		setStyle("-fx-text-fill: rgba(100%,0%,0%,1); -fx-background-color: blue");
//   			setStyle("-fx-border-color:green;-fx-border-width:4; -fx-font-size: 11pt;"
//   					+ "  -fx-text-fill: white;    -fx-opacity: 0.6;");
        return fieldNameProperty.getValue();
    }

    @Override
    public void setFieldName(String fieldName) {
//    	setStyle("-fx-text-fill: red");
    	
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
		return "Button";
	}
}
