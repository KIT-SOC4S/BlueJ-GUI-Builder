package bdl.build.javafx.scene.control;

import bdl.build.GObject;
import bdl.view.right.PropertyEditPane;
import bdl.build.properties.PanelProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ComboBox;

import java.util.List;

public class GComboBox extends ComboBox implements GObject{
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
    public void show() {
        //Prevents the menu from showing if you click on it
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
		return "ComboBox";
	}
    public String getJavaConstructor() {
		return fieldNameProperty.getValue() + " = new ComboBox<String>();\n" ;

	}
    @Override
	public StringBuilder getAdditionalMethodInvokations(){
		StringBuilder methodsString = new StringBuilder(); 
		methodsString.append("        //simple samplecode ComboBox\n"+
		"        this."+fieldNameProperty.getValue() +".getItems().addAll(\"Item A\",\"Item B\",\"Item C\");\n");
		return methodsString;
	}
}
