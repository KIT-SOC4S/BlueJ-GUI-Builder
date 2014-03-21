package bdl.build;

import bdl.build.javafx.scene.layout.GAnchorPane;
import bdl.lang.LabelGrabber;

public class GUIObject extends GAnchorPane {

    private String className = "UntitledGUI";
    private String title = LabelGrabber.getLabel("default.gui.title");
    private double width;
    private double height;

    public GUIObject() {
        setFieldName(title);
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
        this.setWidth(width);
    }

    public double getGUIHeight() {
        return height;
    }

    public void setGUIHeight(double height) {
        this.height = height;
        this.setHeight(height);
    }
}
