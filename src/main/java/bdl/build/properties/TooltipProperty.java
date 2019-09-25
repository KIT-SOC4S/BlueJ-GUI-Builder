package bdl.build.properties;

import bdl.build.GObject;
import bdl.model.history.HistoryManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

public class TooltipProperty implements PanelProperty {

    private GObject gObj;
    private TextField textField;
    private Control control;

    public TooltipProperty(final GObject gObj, String name, final String observedProperty, String fxml, String defaultValue, GridPane gp, int row, Node settingsNode, HistoryManager hm) {
        this.gObj = gObj;

        gp.add(new Label(name + ":"), 0, row);
        textField = new TextField();

        //Grab value from settingsNode if given
        if (settingsNode != null) {
            Tooltip tooltip = ((Control)settingsNode).getTooltip();
            if (tooltip != null) {
                defaultValue = tooltip.getText();
            }
        }

        textField.setText(defaultValue);
        textField.setOnAction(e -> {
			ObservableList<Node> children = textField.getParent().getChildrenUnmodifiable();
			int ci = children.indexOf(textField);
			int maxi = children.size()-1;
			int i = ci + 1;
			while (i != ci) {
				if (i<=maxi) {
					if (children.get(i).isFocusTraversable()) {
						children.get(i).requestFocus();
						return;
					} else {
						i++;
					}
				} else {
					i=0;
				}
			}
		});

        control = (Control) gObj;

        setValue();

        gp.add(textField, 1, row);

        //Upon losing focus, save to the GObject
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                if (!aBoolean2) {
                    setValue();
                }
            }
        });
    }

    private void setValue() {
        if (!textField.getText().isEmpty()) {
            control.setTooltip(new Tooltip(textField.getText()));
        } else {
        	control.setTooltip(null);
        }
    }

    @Override
    public String getJavaCode() {
        Tooltip tooltip = control.getTooltip();
        String tooltipText = "";
        if (tooltip != null) {
            tooltipText = tooltip.getText().replace("\\", "\\\\").replace("\"", "\\\"");
            return gObj.getFieldName() + ".setTooltip(new Tooltip(\"" + tooltipText + "\"));";
        } else {
            return "";
        }
    }

    @Override
    public String getFXMLCode() {
        return "";//TODO Complete this
    }
}
