package bdl.build.javafx.scene.image;

import java.util.HashSet;
import java.util.List;

import bdl.build.GObject;
import bdl.build.properties.PanelProperty;
import bdl.view.right.PropertyEditPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class GImageView extends ImageView implements GObject {
    private String fieldName;
    private List<PanelProperty> properties;
    private PropertyEditPane pep;
    private StringProperty fieldNameProperty = new SimpleStringProperty();

    
    
    
    public GImageView() {
    	WritableImage wi = new WritableImage(100,100);
    	for(int x=0;x<100;x++){
    		for(int y=0;y<100;y++){
    			wi.getPixelWriter().setColor(x, y, Color.AQUA);
        	}
    	}
    	
    	this.setImage(wi);
		
	}

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
   		return "ImageView";
   	}
    
    public  StringBuilder getAdditionalMethodInvokations(){    	
    	return new StringBuilder().append(getFieldName()+".setImage(new WritableImage(100,100));\n");
    }
}
