/**
 * @author Georg Dick
 */
package di.menubuilder;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MenuThing {
	private StringProperty typ = new SimpleStringProperty();
	private StringProperty fieldNameProperty = new SimpleStringProperty();
	private StringProperty textProperty = new SimpleStringProperty();
	private Object correspondingMenuObject = null;

	public Object getCorrespondingMenuObject() {
		return correspondingMenuObject;
	}

	public void setCorrespondingMenuObject(Object correspondingMenuObject) {
		this.correspondingMenuObject = correspondingMenuObject;
	}

	public String getText() {
		return textProperty.getValue();
	}

	public void setText(String text) {
		if (getText() != null && getText().equals(text)) {
			return;
		}
		textProperty.setValue(text);
//		if (getTyp() == null) {
//			return;
//		}
//		if (getTyp().equals("Menu")) {
//			((Menu) this.correspondingMenuObject).setText(text);
//		} else if (getTyp().equals("MenuItem")) {
//			((MenuItem) this.correspondingMenuObject).setText(text);
//		}
	}

	public StringProperty textProperty() {
		return textProperty;
	}

	public String getTyp() {
		return typ.getValue();
	}

	public void setTyp(String typ) {
		this.typ.setValue(typ);
	}

	public MenuThing(String typ) {
		this.typ.setValue(typ);
		if (getTyp().equals("Menu")) {
			Menu menu = new Menu();
			this.correspondingMenuObject = menu;
			menu.textProperty().bind(textProperty);
		} else if (getTyp().equals("MenuItem")) {
			MenuItem menuitem = new MenuItem();
			this.correspondingMenuObject = menuitem;
			menuitem.textProperty().bind(textProperty);
		}

	}

	public String getFieldName() {
		return fieldNameProperty.getValue();
	}

	public void setFieldName(String fieldName) {
		if (getFieldName() != null && getFieldName().equals(fieldName)) {
			return;
		}
		fieldNameProperty.setValue(fieldName);

	}

	public StringProperty fieldNameProperty() {
		return fieldNameProperty;
	}

	public StringProperty getFieldNameProperty() {

		return fieldNameProperty;
	}
}
