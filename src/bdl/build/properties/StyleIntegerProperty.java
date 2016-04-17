package bdl.build.properties;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import bdl.build.GObject;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class StyleIntegerProperty implements StyleProperty {

	private GObject gObj;
	private final HistoryManager historyManager;
	private CheckBox chooseInt;
	private String wert = "";
	private String styleString = "";
	private TextField textField;
	private DecimalFormat format = new DecimalFormat("#", new DecimalFormatSymbols(Locale.US));

	public StyleIntegerProperty(final GObject gObj, String name, String styleString, String defaultValue, GridPane gp,
			int row, Node settingsNode, HistoryManager hm) {
		this.gObj = gObj;
		this.historyManager = hm;
		this.styleString = styleString;
		gp.add(new Label(name + ":"), 0, row);
		textField = new TextField();
		chooseInt = new CheckBox();
		if (defaultValue==null || defaultValue.isEmpty()){
			defaultValue="0";
		}
		textField.setText(format.format(Integer.parseInt(defaultValue))); 

		
		
		textField.setOnAction(e -> {
			ObservableList<Node> children = textField.getParent().getChildrenUnmodifiable();
			int ci = children.indexOf(textField);
			int maxi = children.size() - 1;
			int i = ci + 1;
			while (i != ci) {
				if (i <= maxi) {
					if (children.get(i).isFocusTraversable()) {
						children.get(i).requestFocus();
						return;
					} else {
						i++;
					}
				} else {
					i = 0;
				}
			}
		});
		HBox hbox = new HBox();
		gp.add(hbox, 1, row);
		hbox.getChildren().add(chooseInt);
		hbox.getChildren().add(textField);
		// Grab value from settingsNode if given
		if (settingsNode != null) {
			try {
				String value = StyleUtility.getStylevalue(styleString, settingsNode.getStyle());
				if (value != null && !value.isEmpty()) {
					wert = value;
					textField.setText(wert);
					chooseInt.setSelected(true);
					setValue();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			setValue();
		} catch (Exception e) {
			e.printStackTrace();
			return;// TODO: Probably need some better behavior here.
		}

		chooseInt.setOnAction((a) -> setValue());

		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean,
					Boolean aBoolean2) {
				if (!aBoolean2) {
					setValue();
				}
			}
		});

		textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (ke.getCode().equals(KeyCode.ENTER)) {
					setValue();
				}
			}
		});
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!(istGanzZahl(newValue) || newValue.equals("") || newValue.equals("-"))) {
					textField.setText(oldValue);
				}

			}
		});

	}


	private void setValue(){
//		final String old = StyleUtility.getStylevalue(gObj, styleString);
		String nnew = !chooseInt.isSelected() ? "" : textField.getText();
		wert = nnew;
////
//		if (!old.equals(nnew) && !historyManager.isPaused())
//			historyManager.addHistory(new HistoryItem() {
//				@Override
//				public void revert() {
//					try {
//						gObj.setStyle(StyleUtility.changeStyle(gObj, styleString, old));
//					} catch (Exception ex) {
//						ex.printStackTrace();
//					}
//					textField.setText(old);
//					historyManager.pause();
//
//					historyManager.unpause();
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
//						textField.setText(nnew);
//					}
//					historyManager.pause();
//
//					historyManager.unpause();
//				}
//
//				@Override
//				public String getAppearance() {
//					return gObj.getFieldName() + "int changed!";
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
		if (wert == null || wert.isEmpty()) {
			return "";
		}
		return styleString + ":" + wert + ";";
	}

	private boolean istGanzZahl(String s) {
		try {
			Integer.valueOf(s).intValue();
			return true;
		} catch (Exception nfe) {
			return false;
		}
	}
}
