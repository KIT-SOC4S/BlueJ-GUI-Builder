package bdl.build.properties;

public interface PanelProperty {
	public default String getImport(){return"";};

    public String getJavaCode();

    public String getFXMLCode();
    
    public default String getPackageName(){
    	return "";
    }
    
    public default void disableJavaCodeGeneration(){
    	
    }
    
    

}
