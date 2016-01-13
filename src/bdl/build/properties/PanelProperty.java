package bdl.build.properties;

public interface PanelProperty {

    public String getJavaCode();

    public String getFXMLCode();
    
    public default String getPackageName(){
    	return "";
    }

}
