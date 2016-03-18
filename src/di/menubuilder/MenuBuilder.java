/**
 * @author Georg Dick
 */
package di.menubuilder;

import java.util.ArrayList;

import bdl.build.javafx.scene.control.GMenuBar;
import bdl.controller.Controller;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class MenuBuilder {
	static int nr = 0;
	TreeView<MenuTreeItem> tree;
	final BorderPane pane = new BorderPane();

	public BorderPane getPane() {
		return pane;
	}

	GMenuBar correspondingBar;

	public MenuBuilder(GMenuBar menuBar) {
		final HBox box = new HBox();
		correspondingBar = menuBar;
		tree = this.getStartTree();
		tree.setLayoutX(0);
		tree.setLayoutY(50);
		Button ausgabe = new Button("Ausgabe");
		ausgabe.setOnAction(e -> {
			System.out.println(getFXML());
		});
//		box.getChildren().addAll(ausgabe, createDragLabelMenu(), new Label("     "),createDragLabelMenuItem());
		box.getChildren().addAll(createDragLabelMenu(), new Label("     "),createDragLabelMenuItem());
		pane.setTop(box);
		pane.setCenter(tree);
		pane.setBorder(new Border(
				new BorderStroke(Color.BLUE, new BorderStrokeStyle(null, null, null, 10, 0, null), null, null)));
	    
	}

	public void deleteTree() {
		removeSubtree(tree.getRoot());
		correspondingBar = null;
		pane.getChildren().clear();

	}

	/**
	 * deletes Subtree from me, not me itself!
	 * 
	 * @param me
	 */
	public void removeSubtree(TreeItem<MenuTreeItem> me) {
		if (me.getValue().getMenuThing().getTyp().equals("MenuBar")) {
			MenuBar corresponding = (MenuBar) me.getValue().getMenuThing().getCorrespondingMenuObject();
			corresponding.getMenus().clear();
			for (TreeItem<MenuTreeItem> menuItem : me.getChildren()) {
				Menu menu = (Menu) menuItem.getValue().getMenuThing().getCorrespondingMenuObject();
				menu.getItems().clear();
				this.removeSubtree(menuItem);
				Controller.getFieldNames().remove(menuItem.getValue().getMenuThing().getFieldName());

			}
			me.getChildren().clear();
		} else if (me.getValue().getMenuThing().getTyp().equals("Menu")) {
			Menu corresponding = (Menu) me.getValue().getMenuThing().getCorrespondingMenuObject();
			corresponding.getItems().clear();
			for (TreeItem<MenuTreeItem> menuItem : me.getChildren()) {
				Object object = menuItem.getValue().getMenuThing().getCorrespondingMenuObject();
				if (object instanceof Menu) {
					Menu menu = (Menu) object;
					menu.getItems().clear();
					this.removeSubtree(menuItem);
					Controller.getFieldNames().remove(menuItem.getValue().getMenuThing().getFieldName());
				} else if (object instanceof MenuItem) {
					Controller.getFieldNames().remove(menuItem.getValue().getMenuThing().getFieldName());
				}
			}
			me.getChildren().clear();
		}

	}

	public Node createDragLabelMenuItem() {
		Label l = new Label();
		l.setText("Add MenuItem");
		final ObjectProperty<Cursor> cp = l.cursorProperty();
		l.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				cp.setValue(Cursor.MOVE);
			}
		});
		l.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Dragboard dragBoard = l.startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.put(DataFormat.PLAIN_TEXT, "MenuItem" + "#" + (nr++) + "#NEW");
				dragBoard.setContent(content);
				event.consume();
			}
		});
		return l;
	}

	public Node createDragLabelMenu() {
		Label l = new Label();
		l.setText("Add Menu");
		final ObjectProperty<Cursor> cp = l.cursorProperty();
		l.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				cp.setValue(Cursor.MOVE);
			}
		});
		l.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

				Dragboard dragBoard = l.startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.put(DataFormat.PLAIN_TEXT, "Menu" + "#" + (nr++) + "#NEW");
				dragBoard.setContent(content);
				event.consume();
			}
		});
		return l;
	}

	MenuThing menuRoot;

	private TreeView<MenuTreeItem> getStartTree() {
		menuRoot = new MenuThing("MenuBar");
		menuRoot.setFieldName(correspondingBar.getFieldName());
		menuRoot.setCorrespondingMenuObject(correspondingBar);
		menuRoot.getFieldNameProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (correspondingBar.getFieldName() != null && correspondingBar.getFieldName().equals(newValue)) {
					return;
				}
				correspondingBar.setFieldName(newValue);
			}
		});
		MenuTreeItem rootNode = new MenuTreeItem(menuRoot);
		TreeView<MenuTreeItem> treeView = new TreeView<MenuTreeItem>();

		treeView.setCellFactory(treeview -> new MenuTreeCell(treeview));
//		treeView.setPrefSize(200, 200);
		TreeItem<MenuTreeItem> rootItem = new TreeItem<MenuTreeItem>(rootNode);
		treeView.setShowRoot(true);
		treeView.setRoot(rootItem);
		rootItem.setExpanded(true);
		return treeView;
	}

	public String getImport() {
		return "";
	}
	/*
	public String getJAVADeclaration() {
		String declaration = "";
		TreeItem<MenuTreeItem> root = tree.getRoot();
		String name = root.getValue().getMenuThing().getFieldName();
		declaration = "private MenuBar " + name + " = new MenuBar();\n";
		for (TreeItem<MenuTreeItem> menuItem : root.getChildren()) {
			declaration += getJAVADeclaration(menuItem);
		}
		return declaration;
	}

	private String getJAVADeclaration(TreeItem<MenuTreeItem> mi) {
		String declaration = "";
		MenuThing gob = mi.getValue().getMenuThing();
		String name = gob.getFieldName();
		String typ = gob.getTyp();
		declaration = "private " + typ + " " + name + " = new " + typ + "(\"" + gob.getText() + "\");\n";
		for (TreeItem<MenuTreeItem> menuItem : mi.getChildren()) {
			declaration += getJAVADeclaration(menuItem);
		}
		return declaration;
	}
	*/
	public String getJAVAConstruction() {
		String declaration = "";
		TreeItem<MenuTreeItem> root = tree.getRoot();
		String name = root.getValue().getMenuThing().getFieldName();
		declaration = name + " = new MenuBar();\n";
		for (TreeItem<MenuTreeItem> menuItem : root.getChildren()) {
			declaration += getJAVAConstruction(menuItem);
		}
		return declaration;
	}

	private String getJAVAConstruction(TreeItem<MenuTreeItem> mi) {
		String declaration = "";
		MenuThing gob = mi.getValue().getMenuThing();
		String name = gob.getFieldName();
		String typ = gob.getTyp();
		declaration =name + " = new " + typ + "(\"" + gob.getText() + "\");\n";
		for (TreeItem<MenuTreeItem> menuItem : mi.getChildren()) {
			declaration += getJAVAConstruction(menuItem);
		}
		return declaration;
	}
	public String getJAVADeclarationOnly() {
		String declaration = "";
		TreeItem<MenuTreeItem> root = tree.getRoot();
		String name = root.getValue().getMenuThing().getFieldName();
		declaration = "private MenuBar " + name +";\n";
		for (TreeItem<MenuTreeItem> menuItem : root.getChildren()) {
			declaration += getJAVADeclarationOnly(menuItem);
		}
		return declaration;
	}

	private String getJAVADeclarationOnly(TreeItem<MenuTreeItem> mi) {
		String declaration = "";
		MenuThing gob = mi.getValue().getMenuThing();
		String name = gob.getFieldName();
		String typ = gob.getTyp();
		declaration = "private " + typ + " " + name + ";\n";
		for (TreeItem<MenuTreeItem> menuItem : mi.getChildren()) {
			declaration += getJAVADeclarationOnly(menuItem);
		}
		return declaration;
	}
	
	public String getJAVAMenuStructure() {
		String declaration = "";
		ArrayList<String> submenus = new ArrayList<>();
		TreeItem<MenuTreeItem> root = tree.getRoot();
		String name = root.getValue().getMenuThing().getFieldName();
		for (TreeItem<MenuTreeItem> menuItem : root.getChildren()) {
			submenus.add(menuItem.getValue().getMenuThing().getFieldName());
			if (menuItem.getValue().getMenuThing().getTyp().equals("Menu")) {
				declaration += getJAVASubMenus(menuItem);
			}
		}
		if (submenus.size() > 0) {
			declaration += name + ".getMenus().addAll(";
			for (int i = 0; i < submenus.size(); i++) {
				if (i > 0) {
					declaration += ",";
				}
				declaration += submenus.get(i);
			}
			declaration += ");\n";
		}
		return declaration;
	}

	private String getJAVASubMenus(TreeItem<MenuTreeItem> mi) {
		ArrayList<String> submenus = new ArrayList<>();
		String declaration = "";
		MenuThing gob = mi.getValue().getMenuThing();
		String name = gob.getFieldName();
		String typ = gob.getTyp();
		if (typ.equals("Menu")) {
			for (TreeItem<MenuTreeItem> menuItem : mi.getChildren()) {
				submenus.add(menuItem.getValue().getMenuThing().getFieldName());
				if (menuItem.getValue().getMenuThing().getTyp().equals("Menu")) {
					declaration += getJAVASubMenus(menuItem);
				}
			}
		}
		if (submenus.size() > 0) {
			declaration += name + ".getItems().addAll(";
			for (int i = 0; i < submenus.size(); i++) {
				if (i > 0) {
					declaration += ",";
				}
				declaration += submenus.get(i);
			}
			declaration += ");\n";
		}

		return declaration;
	}

	public String getActionListenerDeclaration() {
		String declaration = "";
		TreeItem<MenuTreeItem> root = tree.getRoot();
		for (TreeItem<MenuTreeItem> menuItem : root.getChildren()) {
			declaration += getActionListenerDeclaration(menuItem);
		}
		return declaration;
	}

	private String getActionListenerDeclaration(TreeItem<MenuTreeItem> mi) {
		String declaration = "";
		for (TreeItem<MenuTreeItem> menuItem : mi.getChildren()) {
			if (menuItem.getValue().getMenuThing().getTyp().equals("Menu")) {
				declaration += getActionListenerDeclaration(menuItem);
			} else if (menuItem.getValue().getMenuThing().getTyp().equals("MenuItem")) {
				String itemname = menuItem.getValue().getMenuThing().getFieldName();
				declaration += itemname + ".setOnAction(a -> handleOnAction" + firstLetterUpcase(itemname) + "(a));\n";
			}
		}
		return declaration;
	}

	public String getActionListenerMethods() {
		String declaration = "";
		TreeItem<MenuTreeItem> root = tree.getRoot();
		for (TreeItem<MenuTreeItem> menuItem : root.getChildren()) {
			declaration += getActionListenerMethods(menuItem);
		}
		return declaration;
	}
	
	public String getAdditionalActionListenerMethods(String existing) {
		String declaration = "";
		TreeItem<MenuTreeItem> root = tree.getRoot();
		for (TreeItem<MenuTreeItem> menuItem : root.getChildren()) {
			declaration += getAdditionalActionListenerMethods(menuItem,existing);
		}
		return declaration;
	}
	private String getAdditionalActionListenerMethods(TreeItem<MenuTreeItem> mi, String existing) {
		String declaration = "";
		for (TreeItem<MenuTreeItem> menuItem : mi.getChildren()) {
			if (menuItem.getValue().getMenuThing().getTyp().equals("Menu")) {
				declaration += getAdditionalActionListenerMethods(menuItem,existing);//Sinn???
			} else if (menuItem.getValue().getMenuThing().getTyp().equals("MenuItem")) {
				String itemname = menuItem.getValue().getMenuThing().getFieldName();
				String handlerprefix = "public void handleOnAction" + firstLetterUpcase(itemname);				
				if (!existing.contains(handlerprefix)){
				declaration += handlerprefix+ "(ActionEvent a){\n    //TODO\n}\n";
				}
			}
		}
		return declaration;
	}
	
	private String getActionListenerMethods(TreeItem<MenuTreeItem> mi) {
		String declaration = "";
		for (TreeItem<MenuTreeItem> menuItem : mi.getChildren()) {
			if (menuItem.getValue().getMenuThing().getTyp().equals("Menu")) {
				declaration += getActionListenerMethods(menuItem);
			} else if (menuItem.getValue().getMenuThing().getTyp().equals("MenuItem")) {
				String itemname = menuItem.getValue().getMenuThing().getFieldName();
				
				declaration += "public void handleOnAction" + firstLetterUpcase(itemname) + "(ActionEvent a){\n"
						+ "    //TODO\n}\n";
			}
		}
		return declaration;
	}

	public String getMenuImportString() {
		return "import javafx.event.*";
	}

	public void refreshMenuBar() {
		TreeItem<MenuTreeItem> root = tree.getRoot();
		correspondingBar.getMenus().clear();
		for (TreeItem<MenuTreeItem> menuItem : root.getChildren()) {
			Menu menu = (Menu) menuItem.getValue().getMenuThing().getCorrespondingMenuObject();
			menu.getItems().clear();
			correspondingBar.getMenus().add(menu);
			this.refreshMenuBar(menuItem, menu);
		}
	}

	private void refreshMenuBar(TreeItem<MenuTreeItem> item, Menu mu) {
		for (TreeItem<MenuTreeItem> menuItem : item.getChildren()) {
			Object mthing = menuItem.getValue().getMenuThing().getCorrespondingMenuObject();
			if (mthing != null) {
				if (mthing instanceof Menu) {
					Menu menu = (Menu) mthing;
					menu.getItems().clear();
					mu.getItems().add(menu);
					this.refreshMenuBar(menuItem, menu);
				} else if (mthing instanceof MenuItem) {
					MenuItem mitem = (MenuItem) mthing;
					mu.getItems().add(mitem);
				}
			}
		}

	}

	public String getFXML() {
		TreeItem<MenuTreeItem> root = tree.getRoot();
		String name = root.getValue().getMenuThing().getFieldName();
		double x = correspondingBar.getLayoutX();
		double y = correspondingBar.getLayoutY();
		String fxml = "<MenuBar fx:id=\"" + name + "\" layoutX=\"" + x + "\" layoutY=\"" + y + "\">\n";
		fxml += "  <menus>\n";
		for (TreeItem<MenuTreeItem> menuItem : root.getChildren()) {
			fxml += getFXML(menuItem, "  ");
		}
		fxml += "  </menus>\n";
		fxml += "</MenuBar>\n";
		return fxml;
	}

	private String getFXML(TreeItem<MenuTreeItem> mi, String dl) {
		dl += "  ";
		String text = mi.getValue().getMenuThing().getText();
		String fieldname = mi.getValue().getMenuThing().getFieldName();
		String fxml = dl + "<Menu fx:id=\"" + fieldname + "\" mnemonicParsing=\"false\" text=\" " + text + "\">\n";
		fxml += dl + "  <items>\n";
		for (TreeItem<MenuTreeItem> menuItem : mi.getChildren()) {
			if (menuItem.getValue().getMenuThing().getTyp().equals("Menu")) {
				fxml += getFXML(menuItem, dl + "  ");
			} else if (menuItem.getValue().getMenuThing().getTyp().equals("MenuItem")) {
				String itemname = menuItem.getValue().getMenuThing().getFieldName();
				String action = "handleOnAction" + firstLetterUpcase(itemname);
				String itemtext = menuItem.getValue().getMenuThing().getText();
				//onAction geht nur mit Controllerklasse
//				fxml += dl + "    <MenuItem fx:id=\"" + itemname + "\" mnemonicParsing=\"false\" onAction=\"#" + action
//						+ "\"" + " text=\"" + itemtext + "\" />\n";
				fxml += dl + "    <MenuItem fx:id=\"" + itemname + "\" mnemonicParsing=\"false\" text=\"" + itemtext + "\" />\n";
			}
		}
		fxml += dl + "  </items>\n";
		fxml += dl + "</Menu>\n";
		return fxml;
	}

	protected String firstLetterUpcase(String text) {

		String newSt = (text.substring(0, 1)).toUpperCase();
		if (text.length() > 1) {
			newSt += text.substring(1);
		}
		return newSt;
	}

	public MenuThing getMenuBarThing() {
		return menuRoot;
	}

	public String getFieldName() {
		return menuRoot.getFieldName();
	}

	public void setFieldName(String name) {
		menuRoot.setFieldName(name);
		tree.refresh();
	}

	/** 
	 * 
	 */
	public void buildTreeFromNode(Node nodeFromFXML) {
		if (nodeFromFXML == null) {
			return;
		}

		String classname = nodeFromFXML.getClass().getSimpleName();
		MenuTreeItem newItem;
		if (classname.equals("MenuBar")) {
			for (Menu menu : ((MenuBar) nodeFromFXML).getMenus()) {
				newItem = new MenuTreeItem(new MenuThing("Menu"));
				if (menu.getId() == null || menu.getId().isEmpty()) {
					newItem.getMenuThing().setFieldName(MenuTreeCell.getStandardName(newItem));
				} else {
					newItem.getMenuThing().setFieldName(menu.getId());
				}
				Controller.getFieldNames().add(newItem.getMenuThing().getFieldName());
				newItem.getMenuThing().setText(menu.getText());
				TreeItem<MenuTreeItem> treeItem = new TreeItem<MenuTreeItem>(newItem);
				tree.getRoot().getChildren().add(treeItem);
				correspondingBar.getMenus().add((Menu) newItem.getMenuThing().getCorrespondingMenuObject());
				buildTreeFromNode(menu, treeItem);
			}

		} else {
			System.out.println("Error reading Menu");
		}

	}

	private void buildTreeFromNode(MenuItem parentFromFXML, TreeItem<MenuTreeItem> parentItem) {
		if (parentFromFXML == null) {
			return;
		}
		String classname = parentFromFXML.getClass().getSimpleName();
		MenuTreeItem newItem;
		if (classname.equals("Menu")) {
			for (MenuItem mi : ((Menu) parentFromFXML).getItems()) {
				if (mi.getClass().getSimpleName().equals("Menu")) {
					Menu menu = (Menu) mi;
					newItem = new MenuTreeItem(new MenuThing("Menu"));
					if (menu.getId() == null || menu.getId().isEmpty()) {
						newItem.getMenuThing().setFieldName(MenuTreeCell.getStandardName(newItem));
					} else {
						newItem.getMenuThing().setFieldName(menu.getId());
					}
					Controller.getFieldNames().add(newItem.getMenuThing().getFieldName());
					
					newItem.getMenuThing().setText(menu.getText());
					TreeItem<MenuTreeItem> treeItem = new TreeItem<MenuTreeItem>(newItem);
					parentItem.getChildren().add(treeItem);
					Menu m = (Menu) parentItem.getValue().getMenuThing().getCorrespondingMenuObject();
					m.getItems().add((Menu) newItem.getMenuThing().getCorrespondingMenuObject());
					buildTreeFromNode(menu, treeItem);
				} else if (mi.getClass().getSimpleName().equals("MenuItem")) {
					MenuItem menu = (MenuItem) mi;
					newItem = new MenuTreeItem(new MenuThing("MenuItem"));
					if (menu.getId() == null || menu.getId().isEmpty()) {
						newItem.getMenuThing().setFieldName(MenuTreeCell.getStandardName(newItem));
					} else {
						newItem.getMenuThing().setFieldName(menu.getId());
					}
					Controller.getFieldNames().add(newItem.getMenuThing().getFieldName());
					newItem.getMenuThing().setText(menu.getText());
					TreeItem<MenuTreeItem> treeItem = new TreeItem<MenuTreeItem>(newItem);
					parentItem.getChildren().add(treeItem);
					Menu m = (Menu) parentItem.getValue().getMenuThing().getCorrespondingMenuObject();
					m.getItems().add((MenuItem) newItem.getMenuThing().getCorrespondingMenuObject());
					buildTreeFromNode(menu, treeItem);
				}
			}
		}
	}

}