package bdl.build.properties;

import bdl.build.GObject;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class StyleColorProperty implements StyleProperty {

	private GObject gObj;
	private ColorPicker colorPicker;
	private final HistoryManager historyManager;
	private CheckBox chooseColor;
	private String color = "";
	private String styleString = "";

	public StyleColorProperty(final GObject gObj, String name, String styleString, String defaultValue, GridPane gp,
			int row, Node settingsNode, HistoryManager hm) {
		this.gObj = gObj;
		this.historyManager = hm;
		this.styleString = styleString;
		gp.add(new Label(name + ":"), 0, row);
		colorPicker = new ColorPicker();
		chooseColor = new CheckBox();

		HBox hbox = new HBox();
		gp.add(hbox, 1, row);
		hbox.getChildren().add(chooseColor);
		hbox.getChildren().add(colorPicker);
		// Grab value from settingsNode if given
		if (settingsNode != null) {
			try {

				String value = StyleUtility.getStylevalue(styleString, settingsNode.getStyle());
				if (value != null && !value.isEmpty()) {
					defaultValue = value;
					color = value;
					colorPicker.setValue(Color.web(color));
					chooseColor.setSelected(true);
					setValue();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (defaultValue==null||defaultValue.isEmpty()){
			defaultValue="0xFFFFFF";
			color=defaultValue;
		}
		colorPicker.setValue(Color.web(defaultValue));
		try {
			setValue();
		} catch (Exception e) {
			e.printStackTrace();
			return;// TODO: Probably need some better behavior here.
		}

		chooseColor.setOnAction((a) -> setValue());
		colorPicker.setOnAction((a) -> setValue());
	}

	private void setValue() {
//		String old = StyleUtility.getStylevalue(gObj, styleString);
		String nnew = !chooseColor.isSelected() ? "" : colorPicker.getValue().toString().replace("0x", "#");
		color = nnew;
//		if (!old.equals(nnew) && !historyManager.isPaused())
//			historyManager.addHistory(new HistoryItem() {
//				@Override
//				public void revert() {
//					try {
//						gObj.setStyle(StyleUtility.changeStyle(gObj, styleString, old));
//					} catch (Exception ex) {
//						ex.printStackTrace();
//					}
//					if (!(old == null || old.isEmpty())) {
//						colorPicker.setValue(Color.web(old));
//						historyManager.pause();
//						// work around to update colorpicker's displayed
//						// selection
//						colorPicker.fireEvent(new ActionEvent(null, colorPicker));
//						historyManager.unpause();
//					}
//				}
//
//				@Override
//				public void restore() {
//					try {
//						gObj.setStyle(StyleUtility.changeStyle(gObj, styleString, nnew));
//					} catch (Exception ex) {
//						ex.printStackTrace();
//					}
//					if (nnew != null && !nnew.isEmpty()) {
//						colorPicker.setValue(Color.web(nnew));
//					}
//					historyManager.pause();
//					// work around to update colorpicker's displayed selection
//					colorPicker.fireEvent(new ActionEvent(null, colorPicker));
//					historyManager.unpause();
//				}
//
//				@Override
//				public String getAppearance() {
//					return gObj.getFieldName() + "color changed!";
//				}
//			});

		gObj.setStyle(StyleUtility.getChangedStyle(gObj, styleString, nnew));
	}

	@Override
	public String getJavaCode() {
		return "";
	}

	@Override
	public String getFXMLCode() {
		return "";
	}

	@Override
	public String getStyleDescription() {
		if (color == null || color.isEmpty()) {
			return "";
		}
		return styleString + ":" + color + ";";
	}
}
