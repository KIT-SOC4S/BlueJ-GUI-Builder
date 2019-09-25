package di.menubuilder;

import bdl.controller.Controller;

public class MenuTreeItem {
	static int minr = 0;
	MenuThing menuThing;

	public MenuTreeItem( MenuThing menuThing) {
		this.menuThing = menuThing;
		String name = menuThing.getTyp().substring(0, 1).toLowerCase() + menuThing.getTyp().substring(1);
		int count = 1;
		while (Controller.getFieldNames().contains(name + count)) {
			count++;
		}
		Controller.getFieldNames().add(name+count);
		this.menuThing.setFieldName(name+count);
	
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
			menuThing.setFieldName("menufoo" + (minr++));//should not happen
		}
		if (menuThing.getTyp().equals("MenuBar")){
			return menuThing.getTyp()+ '#' + menuThing.getFieldName();
		} else  {
			return menuThing.getTyp()+'#' + menuThing.getText()+"#"+ menuThing.getFieldName();
		} 
	}
}
