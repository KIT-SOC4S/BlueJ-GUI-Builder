package di.menubuilder;

import bdl.build.GObject;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuTreeItem {
	static int minr = 0;
	static int munr = 0;
	MenuThing menuThing;

	public MenuTreeItem(final MenuThing menuThing) {
		this.menuThing = menuThing;
		if (menuThing.getTyp().equals("MenuBar")) {
			menuThing.setFieldName("menuBar");
		} else if (menuThing.getTyp().equals("Menu")) {
			menuThing.setFieldName("menu" + (munr++));
		} else if (menuThing.getTyp().equals("MenuItem")) {
			menuThing.setFieldName("menuItem" + (minr++));
		}
	}

	public MenuThing getMenuThing	() {
		return menuThing;
	}

	@Override
	public String toString() {
		if (menuThing == null) {
			return "null#null";
		}
		if (menuThing.getFieldName() == null || menuThing.getFieldName().equals("")) {
			menuThing.setFieldName("menufoo" + (minr++));
		}
		if (menuThing.getTyp().equals("MenuBar")){
			return menuThing.getTyp()+ '#' + menuThing.getFieldName();
		} else  {
			return menuThing.getTyp()+'#' + menuThing.getText()+"#"+ menuThing.getFieldName();
		} 
	}
}
