package bdl.build.javafx.scene.control;

import java.util.List;

import bdl.build.GObject;
import bdl.build.properties.PanelProperty;
import bdl.view.right.PropertyEditPane;
import di.menubuilder.MenuBuilder;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.MenuBar;

public class GMenuBar extends MenuBar implements GObject {
	private List<PanelProperty> properties;
	private PropertyEditPane pep;
	private StringProperty fieldNameProperty = new SimpleStringProperty();
	private MenuBuilder menuBuilder;

	public GMenuBar(){
    	super();
    	menuBuilder= new MenuBuilder(this);
    	fieldNameProperty.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {	
				if (menuBuilder.getFieldName()!=null&&menuBuilder.getFieldName().equals(newValue)){
					return;
				}
				menuBuilder.setFieldName(newValue);				
			}
		});
    }

	public MenuBuilder getMenuBuilder() {
		return menuBuilder;
	}

	

	@Override
	public String getFieldName() {
		return fieldNameProperty.getValue();
	}

	@Override
	public void setFieldName(String fieldName) {
		if (getFieldName()!=null && getFieldName().equals(fieldName)) {
			return;
		}
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
		return "MenuBar";
	}
}
