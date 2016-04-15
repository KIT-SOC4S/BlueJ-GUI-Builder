package bdl.build;

import bdl.view.right.PropertyEditPane;
import bdl.build.properties.PanelProperty;
import javafx.beans.property.StringProperty;

import java.util.HashSet;
import java.util.List;

public interface GObject {

    /**
     * @return The name of this field in the java file
     */
	
	
    public String getFieldName();

    public void setFieldName(String fieldName);

    public void setPanelProperties(List<PanelProperty> properties);

    public List<PanelProperty> getPanelProperties();

    public void setPEP(PropertyEditPane propertyEditPane);
    
    public PropertyEditPane getPEP();

    public StringProperty fieldNameProperty();
    
    public String getNodeClassName();
    
    public  String getStyle();
//    public default void setStyle(String style){
//    	
//    }
//    public default void removeStyle(String style){
//    	
//    }
    public default String[] getAdditionalImports(){
    	return null;
    }
    public default StringBuilder getAdditionalMethods(){
    	return null;
    }
    public default HashSet<String> getAdditionalConstruction(){
    	return null;
    }
    public default HashSet<String> getAdditionalDeclaration(){
    	return null;
    }

	public default StringBuilder getAdditionalMethodInvokations(){
		return null;
	}

	public void setStyle(String newStyle);
}
