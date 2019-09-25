package bdl.build.properties;

import bdl.build.GObject;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.lang.reflect.Method;

public class FillObservedProperty implements PanelProperty {

    private GObject gObj;
    private String setter;
    private String getter;
    private String fxml;
    private ColorPicker colorPicker;
    private final HistoryManager historyManager;

    public FillObservedProperty(final GObject gObj, String name, final String observedProperty,  String fxml, String defaultValue, GridPane gp, int row, Node settingsNode, HistoryManager hm) {
        this.gObj = gObj;
//        this.setter = setter;
//        this.getter = getter;
        String property = observedProperty;
		property = property.replace("Property", "");	property = property.substring(0, 1).toUpperCase() + property.substring(1);
		this.setter = "set" + property;
		this.getter = "get" + property;
        this.fxml = fxml;
        this.historyManager = hm;

        gp.add(new Label(name + ":"), 0, row);
        colorPicker = new ColorPicker();

        //Grab value from settingsNode if given
        if (settingsNode != null) {
            try {
                Method method = settingsNode.getClass().getMethod(getter);
                String value = method.invoke(settingsNode).toString();
                if (value != null) {
                    defaultValue = value;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        colorPicker.setValue(Color.web(defaultValue));//TODO - Handle bad defaultValue values

        try {
            setValue();
        } catch (Exception e) {
            e.printStackTrace();
            return;//TODO: Probably need some better behavior here.
        }

        gp.add(colorPicker, 1, row);

        //On action, save to the GObject
        colorPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    setValue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setValue() throws Exception {
        final Method setMethod = gObj.getClass().getMethod(setter, Paint.class);
        final Method getMethod = gObj.getClass().getMethod(getter);
        final Color old = (Color) getMethod.invoke(gObj);
        final Color nnew = colorPicker.getValue();
        if(!historyManager.isPaused())
        historyManager.addHistory(new HistoryItem() {
            @Override
            public void revert() {
                try {
                    setMethod.invoke(gObj, old);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                colorPicker.setValue(old);
                historyManager.pause();
                // work around to update colorpicker's displayed selection
                colorPicker.fireEvent(new ActionEvent(null, colorPicker));
                historyManager.unpause();
            }

            @Override
            public void restore() {
                try {
                    setMethod.invoke(gObj, nnew);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                colorPicker.setValue(nnew);
                historyManager.pause();
                // work around to update colorpicker's displayed selection
                colorPicker.fireEvent(new ActionEvent(null, colorPicker));
                historyManager.unpause();
            }

            @Override
            public String getAppearance() {
                return gObj.getFieldName() + " color changed!";
            }
        });
        setMethod.invoke(gObj, colorPicker.getValue());
    }

    @Override
    public String getJavaCode() {
        return gObj.getFieldName() + "." + setter + "(Color.web(\"" + colorPicker.getValue().toString() + "\"));";
    }

    @Override
    public String getFXMLCode() {
        return fxml + "=\"" + colorPicker.getValue().toString().replace("0x","#") + "\"";
    }
}
