package bdl.build;

import bdl.build.javafx.scene.layout.GAnchorPane;
import bdl.controller.Controller;
import bdl.lang.LabelGrabber;
import javafx.scene.layout.AnchorPane;

public class GUIObject extends GAnchorPane {

    private String className = "UntitledGUI";
    private String title = LabelGrabber.getLabel("default.gui.title");
    private double width;
    private double height;
	private AnchorPane setVPD;

    public GUIObject() {
        setFieldName("root");
        Controller.getFieldNames().add("root");
    }

    public String getGUITitle() {
        return title;
    }

    public void setGUITitle(String title) {
        this.title = title;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public double getGUIWidth() {
        return width;
    }

    public void setGUIWidth(double width) {
        this.width = width;
        if (setVPD!=null){
        	setVPD.setMinWidth(width);
        	setVPD.setMaxWidth(width);
        }
        this.setMinWidth(width);
        this.setMaxWidth(width);
    }

    public double getGUIHeight() {
        return height;
    }

    public void setGUIHeight(double height) {
        this.height = height;
        if (setVPD!=null){
        	setVPD.setMinHeight(height);
        	setVPD.setMaxHeight(height);
        }
        this.setMinHeight(height);
        this.setMaxHeight(height);
    }

	public void setVPD(AnchorPane viewPaneDecorator) {
		this.setVPD = viewPaneDecorator;
		
		
	}
}
