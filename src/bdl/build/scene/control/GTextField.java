package bdl.build.scene.control;

import bdl.build.GObject;
import bdl.build.GType;
import javafx.scene.control.TextField;

public class GTextField extends TextField implements GObject {
    private String fieldName;

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public GType getType() {
        return GType.TextField;
    }
}
