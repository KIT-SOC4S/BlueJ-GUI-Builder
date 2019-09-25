package bdl.build.properties;

import bdl.build.GObject;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BooleanObservedProperty implements PanelProperty {

	private GObject gObj;
	private String getter;
	private String setter;
	private String fxml;
	private CheckBox checkBox;
	private final HistoryManager historyManager;
	
	private String defaultValue;

	public BooleanObservedProperty(final GObject gObj, String name, final String observedProperty, String fxml,
			String defaultValue, GridPane gp, int row, Node settingsNode, HistoryManager hm) {
		this.gObj = gObj;
		this.defaultValue = defaultValue;
		String property = observedProperty.replace("Property", "");
		property = property.substring(0, 1).toUpperCase() + property.substring(1);
		this.setter = "set" + property;
		this.getter = "is" + property;
		this.fxml = fxml;
		this.historyManager = hm;
		gp.add(new Label(name + ":"), 0, row);
		checkBox = new CheckBox();
		checkBox.setOnAction(e -> {
			ObservableList<Node> children = checkBox.getParent().getChildrenUnmodifiable();
			int ci = children.indexOf(checkBox);
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
		// Grab value from settingsNode if given
		if (settingsNode != null) {
			try {
				Method method = settingsNode.getClass().getMethod(getter);
				String value = Boolean.toString(((Boolean) method.invoke(settingsNode)));
				if (value != null) {
					defaultValue = value;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {

			final Method getPropMethod = gObj.getClass().getMethod(observedProperty);
			((ObservableValue<Boolean>) getPropMethod.invoke(gObj)).addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observableValue, Boolean old, Boolean newValue) {
					checkBox.setSelected(newValue.booleanValue());
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

		checkBox.setSelected(Boolean.parseBoolean(defaultValue));
		try {
			setValue();
		} catch (Exception e) {
			e.printStackTrace();
			return;// TODO: Probably need some better behavior here.
		}

		gp.add(checkBox, 1, row);

		// Upon change, save to the GObject
		checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean,
					Boolean aBoolean2) {
				try {
					setValue();
				} catch (Exception e) {
					e.printStackTrace();
					return;// TODO: Probably need some better behavior here.
				}
			}
		});
	}

	private void setValue() throws Exception {
		final Method setMethod = gObj.getClass().getMethod(setter, boolean.class);
		final Method getMethod = gObj.getClass().getMethod(getter);
		final boolean old = (boolean) getMethod.invoke(gObj);
		final boolean nnew = checkBox.isSelected();
		if (old != nnew && !historyManager.isPaused()) {
			historyManager.addHistory(new HistoryItem() {
				@Override
				public void revert() {
					try {
						setMethod.invoke(gObj, old);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				@Override
				public void restore() {
					try {
						setMethod.invoke(gObj, nnew);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

				@Override
				public String getAppearance() {
					return gObj.getFieldName() + " checkbox changed!";
				}
			});
		}
		setMethod.invoke(gObj, checkBox.isSelected());
	}

	@Override
	public String getJavaCode() {
		if (defaultValue == null || defaultValue.isEmpty()
				|| checkBox.isSelected() != Boolean.parseBoolean(defaultValue)) {
			return gObj.getFieldName() + "." + setter + "(" + checkBox.isSelected() + ");";
		}
		return "";
	}

	@Override
	public String getFXMLCode() {
		if (defaultValue == null || defaultValue.isEmpty()
				|| checkBox.isSelected() != Boolean.parseBoolean(defaultValue)) {
			return fxml + "=\"" + checkBox.isSelected() + "\"";
		}
		return "";
	}
}
