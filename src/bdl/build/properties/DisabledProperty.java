package bdl.build.properties;

import bdl.build.GObject;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class DisabledProperty implements PanelProperty {

    private GObject gObj;
    private CheckBox checkBox;
    private final HistoryManager historyManager;

    public DisabledProperty(final GObject gObj, String name, final String observedProperty, String fxml, String defaultValue, GridPane gp, int row, Node settingsNode, HistoryManager hm) {
        this.gObj = gObj;
        this.historyManager = hm;

        gp.add(new Label(name + ":"), 0, row);
        checkBox = new CheckBox();

        //Grab value from settingsNode if given
        if (settingsNode != null) {
            defaultValue = Boolean.toString(settingsNode.isDisabled());
        }

        checkBox.setSelected(Boolean.parseBoolean(defaultValue));
        setValue(true, checkBox.isSelected());

        gp.add(checkBox, 1, row);

        //Upon change, save to the GObject
        checkBox.setOnAction((x)-> setValue(false, checkBox.isSelected()));
    }

    
    private void setValue(boolean init, boolean value) {
        if (value) {
        	gObj.setStyle(StyleUtility.getChangedStyle(gObj, "-fx-opacity", "0.4"));
          
        } else {
        	gObj.setStyle(StyleUtility.getChangedStyle(gObj, "-fx-opacity", "1"));
        }
//        System.out.println(gObj.getStyle());
        if (!init) {
            historyManager.addHistory(new HistoryItem() {
                boolean newValue = checkBox.isSelected();
                boolean oldValue = !newValue;

                @Override
                public void revert() {
                    setValue(true, oldValue);
                }

                @Override
                public void restore() {
                    setValue(true, newValue);
                }

                @Override
                public String getAppearance() {
                    return gObj.getFieldName() + " disabled set to " + newValue;
                }
            });
        } else {
            checkBox.setSelected(value);
        }
    }

    @Override
    public String getJavaCode() {
        if (checkBox.isSelected()) {
            return gObj.getFieldName() + ".setDisable(" + checkBox.isSelected() + ");";
        } else {
            return "";
        }
    }

    @Override
    public String getFXMLCode() {
        if (checkBox.isSelected()) {
            return "disable=\"" + checkBox.isSelected() + "\"";
        } else {
            return "";
        }
    }
}