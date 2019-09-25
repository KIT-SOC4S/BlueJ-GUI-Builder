package bdl.build.properties;

import java.util.ArrayList;

import com.sun.javafx.scene.KeyboardShortcutsHandler;
import com.sun.javafx.scene.traversal.Direction;

import bdl.build.GObject;
import bdl.lang.LabelGrabber;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import di.blueJLink.Bezeichnertester;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class FieldName {

	private ArrayList<String> fieldNames;
	private HistoryManager historyManager;
	private final TextField textField;
	private final GObject gObj;

	public FieldName(final GObject gObj, ArrayList<String> fieldNames, String type, GridPane gp, int row,
			HistoryManager hm) {
		gp.add(new Label(LabelGrabber.getLabel("field.name.text") + ":"), 0, row);
		// textField = new FieldNameTextField();
		
		textField = new TextField();
		this.fieldNames = fieldNames;
		this.historyManager = hm;
		this.gObj = gObj;

		// Grab the fieldname if already set (which should be the case when loading
		// from FXML).
		if (gObj.getFieldName() != null) {
			textField.setText(gObj.getFieldName());
			fieldNames.add(gObj.getFieldName());
		} else {
			// Set default field name
			type = type.substring(0, 1).toLowerCase() + type.substring(1);
			int count = 1;
			while (fieldNames.contains(type + count)) {
				count++;
			}
			textField.setText(type + count);
			gObj.setFieldName(type + count);
			fieldNames.add(type + count);
		}

		gp.add(textField, 1, row);

		gObj.fieldNameProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.equals(textField.getText())) {
					return;
				}
				textField.setText(newValue);
				handleChangeHistory(oldValue);
			}
		});

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

		// Upon losing focus, save to the GObject
		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean,
					Boolean aBoolean2) {

				if (!aBoolean2) {
					String newname = textField.getText();
					if (!textField.getText().equals(gObj.getFieldName())) {
						if (Bezeichnertester.variablenBezeichnerOK(newname) && !fieldNames.contains(newname)) {							
							handleChangeHistory(gObj.getFieldName());
						} else {
							if (Bezeichnertester.variablenBezeichnerOK(gObj.getFieldName())) {
								textField.setText(gObj.getFieldName());
							} else {
								// Sollte nicht vorkommen
							}
						}
					}
				}
			}
		});

		// new KeyboardShortcutsHandler().traverse(textField, Direction.NEXT)

	}

	public void handleChangeHistory(String old) {
		historyManager.addHistory(new HistoryItem() {
			String nnew = textField.getText();

			@Override
			public void revert() {
				gObj.setFieldName(old);
				textField.setText(old);
			}

			@Override
			public void restore() {
				gObj.setFieldName(nnew);
				textField.setText(nnew);
			}

			@Override
			public String getAppearance() {
				return old + " -> " + nnew;
			}
		});
		gObj.setFieldName(textField.getText());
		fieldNames.add(textField.getText());
	}

	
	
}
