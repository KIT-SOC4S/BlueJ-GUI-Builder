package bdl.build.properties;

import bdl.build.GObject;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class StyleAlignmentProperty implements StyleProperty {

	private GObject gObj;
	private ComboBox<String> cbAlignmentSettings;
	private final HistoryManager historyManager;
	private CheckBox chooseAlignment;
	private String alignment = "left";
	private String styleString = "-fx-text-alignment";

	public StyleAlignmentProperty(final GObject gObj, String name, String styleString, String defaultValue, GridPane gp,
			int row, Node settingsNode, HistoryManager hm) {
		this.gObj = gObj;
		this.historyManager = hm;
		gp.add(new Label(name + ":"), 0, row);
		cbAlignmentSettings = new ComboBox<String>();
		cbAlignmentSettings.getItems().addAll("left","right","center","justify");
		cbAlignmentSettings.setEditable(false);
		chooseAlignment = new CheckBox();

		HBox hbox = new HBox();
		gp.add(hbox, 1, row);
		hbox.getChildren().add(chooseAlignment);
		hbox.getChildren().add(cbAlignmentSettings);
		// Grab value from settingsNode if given
		if (settingsNode != null) {
			try {

				String value = StyleUtility.getStylevalue(styleString, settingsNode.getStyle());
				if (value != null && !value.isEmpty()) {
					defaultValue = value;
					alignment = value;
					chooseAlignment.setSelected(true);
					cbAlignmentSettings.getSelectionModel().select(value);
					setValue();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (defaultValue==null||defaultValue.isEmpty()){
			defaultValue="left";
			alignment=defaultValue;
		}
		cbAlignmentSettings.getSelectionModel().select(defaultValue);
		try {
			setValue();
		} catch (Exception e) {
			e.printStackTrace();
			return;// TODO: Probably need some better behavior here.
		}

		chooseAlignment.setOnAction((a) -> setValue());
		cbAlignmentSettings.setOnAction((v) -> {
		    setValue();
		});
		
	}

	private void setValue() {
//		String old = StyleUtility.getStylevalue(gObj, styleString);
		String nnew = !chooseAlignment.isSelected() ? "" : cbAlignmentSettings.getSelectionModel().getSelectedItem();
		alignment = nnew;

		gObj.setStyle(StyleUtility.getChangedStyle(gObj, styleString, nnew));
		System.out.println(gObj.getStyle());
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
		if (alignment == null || alignment.isEmpty()) {
			return "";
		}
		return styleString + ":" + alignment + ";";
	}


}
