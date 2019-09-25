package bdl.build.properties;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;

import bdl.build.GObject;
import bdl.controller.Controller;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import di.blueJLink.Bezeichnertester;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

public class ToggleGroupObservedProperty implements PanelProperty {

	static HashSet<String> toggleGroupNames = new HashSet<String>();
	private GObject gObj;
	private String fxml;
	private TextField textField;
	private final HistoryManager historyManager;
	private String toggleGroupName = "";

	public ToggleGroupObservedProperty(final GObject gObj, String name, final String observedProperty,  String fxml, String defaultValue, GridPane gp, int row, Node settingsNode,
			HistoryManager hm) {
		this.gObj = gObj;
		this.fxml = fxml;
		this.historyManager = hm;

		gp.add(new Label(name + ":"), 0, row);
		textField = new TextField();

		// Grabbing value from settingsNode not possible
		

		textField.setText(defaultValue);
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
		try {
			final Method getPropMethod = gObj.getClass().getMethod(observedProperty);
			((ObservableValue<String>) getPropMethod.invoke(gObj)).addListener(new ChangeListener<String>() {
				@Override
				public void changed(ObservableValue<? extends String> observableValue, String number, String newValue) {
					textField.setText(newValue);
				}
			});
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		try {
			setValue();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		gp.add(textField, 1, row);

		// Upon losing focus, save to the GObject
		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean,
					Boolean aBoolean2) {
				if (!aBoolean2) {
					String newname = textField.getText();
					if (!textField.getText().equals(toggleGroupName)) {
						if (textField.getText().isEmpty()) {
							setValue();
							return;
						}
						if (Bezeichnertester.variablenBezeichnerOK(newname)) {
							if (toggleGroupNames.contains(newname)) {
								setValue();
								return;
							}
							if (!Controller.getFieldNames().contains(newname)) {
								Controller.getFieldNames().add(newname);
								toggleGroupNames.add(newname);
								setValue();
								return;
							}							
						}
						textField.setText(toggleGroupName);
					}
				}

			}
		});
	}

	private void setValue() {
		final String old = this.toggleGroupName;
		final String nnew = textField.getText();
		if (!old.equals(nnew) && !historyManager.isPaused()) {
			historyManager.addHistory(new HistoryItem() {
				@Override
				public void revert() {
					try {
						ToggleGroupObservedProperty.this.toggleGroupName = old;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				@Override
				public void restore() {
					try {
						ToggleGroupObservedProperty.this.toggleGroupName = nnew;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				@Override
				public String getAppearance() {
					return gObj.getFieldName() + " ToggleGroup changed!";
				}
			});
		}
		this.toggleGroupName = textField.getText();
	}

	@Override
	public String getJavaCode() {
		if (toggleGroupName.isEmpty()){
			return "";
		}
		return gObj.getFieldName() + ".setToggleGroup("+toggleGroupName+");";
	}

	@Override
	public String getFXMLCode() {
		if (toggleGroupName.isEmpty()){
			return "";
		}
		return fxml + "=\"$" + textField.getText().replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
	}

	public String getToggleGroupName() {		
		return toggleGroupName;
	}

	public void setToggleGroupName(String value) {
		if (value==null ){
			return;
		}
		textField.setText(value);
		this.toggleGroupName=value;
		if (!value.isEmpty()){
		   toggleGroupNames.add(value);
		   if (!Controller.getFieldNames().contains(value)){
		      Controller.getFieldNames().add(value);
		   }
		}
	}
}
