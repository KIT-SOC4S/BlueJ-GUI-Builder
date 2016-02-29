/**
 * @author Georg Dick
 */
package di.menubuilder;

import java.util.Optional;

import bdl.controller.Controller;
import bdl.lang.LabelGrabber;
import di.blueJLink.Bezeichnertester;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class MenuTreeCell extends TreeCell<MenuTreeItem> {

	private TreeView<MenuTreeItem> parentTree;
	private MenuTreeItem item;

	public MenuTreeCell(final TreeView<MenuTreeItem> parentTree) {
		// setBackground(new Background(new BackgroundFill(Color.BLACK, null,
		// null)));

		this.parentTree = parentTree;
//		setBorder(new Border(
//				new BorderStroke(Color.BLUE, new BorderStrokeStyle(null, null, null, 10, 0, null), null, null)));

		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				if (t.getButton().equals(MouseButton.SECONDARY)) {
					ContextMenu popUp = new ContextMenu();
					MenuItem mITtext = new MenuItem(LabelGrabber.getLabel("menu.node.text"));
					MenuItem mITFieldname = new MenuItem(LabelGrabber.getLabel("menu.node.fieldname"));
					MenuItem mITdelete = new MenuItem(LabelGrabber.getLabel("delete.node.text"));
					if (item.getMenuThing().getTyp().equals("MenuBar")) {
						popUp.getItems().addAll(mITFieldname);
					} else {
						popUp.getItems().addAll(mITtext, mITFieldname, mITdelete);
					}
					popUp.show((Node) t.getSource(), Side.LEFT, 0, 0);
					mITdelete.setOnAction(e -> {
						TreeItem<MenuTreeItem> me = search(parentTree.getRoot(), item.toString());
						TreeItem<MenuTreeItem> parent = me.getParent();
						if (parent != null) {
							removeSubtree(me);							
							Controller.getFieldNames().remove(me.getValue().getMenuThing().getFieldName());
							Object meParent=parent.getValue().getMenuThing().getCorrespondingMenuObject();
							if (meParent!=null){
								if (meParent  instanceof MenuBar){
									((MenuBar)meParent).getMenus().remove(me.getValue().getMenuThing().getCorrespondingMenuObject());
									
								} else if (meParent  instanceof Menu){
									((Menu)meParent).getItems().remove(me.getValue().getMenuThing().getCorrespondingMenuObject());
								}
								
							}
							
							parent.getChildren().remove(me);
							parent.setExpanded(true);
						}
					});
					mITFieldname.setOnAction(e -> {
						TreeItem<MenuTreeItem> me = search(parentTree.getRoot(), item.toString());
						TreeItem<MenuTreeItem> parent = me.getParent();
						TextInputDialog dialog = new TextInputDialog(item.getMenuThing().getFieldName());
						dialog.setTitle(LabelGrabber.getLabel("input.dialog"));
						dialog.setHeaderText(LabelGrabber.getLabel("menu.node.fieldname"));
						Optional<String> result = dialog.showAndWait();
						if (result.isPresent()) {
							String newName = result.get();
							if (Bezeichnertester.variablenBezeichnerOK(newName)
									&& !Controller.getFieldNames().contains(newName)) {
								Controller.getFieldNames().remove(item.getMenuThing().getFieldName());
								item.getMenuThing().setFieldName(newName);
								Controller.getFieldNames().add(item.getMenuThing().getFieldName());
							}
							parentTree.refresh();
						}
					});
					mITtext.setOnAction(e -> {
						if (item.getMenuThing().getTyp().equals("MenuItem")
								|| item.getMenuThing().getTyp().equals("Menu")) {
							TextInputDialog dialog = new TextInputDialog(item.getMenuThing().getText());
							dialog.setTitle(LabelGrabber.getLabel("input.dialog"));
							dialog.setHeaderText(LabelGrabber.getLabel("menu.node.text"));
							Optional<String> result = dialog.showAndWait();
							if (result.isPresent()) {
								item.getMenuThing().setText(result.get());
								parentTree.refresh();
							}
						}
					});

				}
			}
		});
		setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (item == null) {
					return;
				}
				Dragboard dragBoard = startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.put(DataFormat.PLAIN_TEXT, item.toString());
				dragBoard.setContent(content);
				event.consume();
			}
		});
		setOnDragDone(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent dragEvent) {
				dragEvent.consume();
			}
		});

		setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent dragEvent) {
				// System.out.println("Drag over on " + item);
				if (dragEvent.getDragboard().hasString()) {
					String valueToMove = dragEvent.getDragboard().getString();
					if (dragAllowed(valueToMove)) {
						dragEvent.acceptTransferModes(TransferMode.MOVE);
					}
				}
				dragEvent.consume();
			}
		});

		setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent dragEvent) {

				String valueToMove = dragEvent.getDragboard().getString();
				System.out.println(valueToMove + " -> " + item.toString());

				if (valueToMove.endsWith("NEW")) {
					MenuTreeItem newItem;
					Optional<String[]> result;
					if (valueToMove.startsWith("MenuItem")) {
						newItem = new MenuTreeItem(new MenuThing("MenuItem"));
						result = inputTextAndFieldname("menuItemText", newItem);
					} else {
						newItem = new MenuTreeItem(new MenuThing("Menu"));
						result = inputTextAndFieldname("menuText", newItem);
					}
					result.ifPresent(textFieldname -> {
						newItem.getMenuThing().setText(result.get()[0]);
						Controller.getFieldNames().remove(newItem.getMenuThing().getFieldName());
						newItem.getMenuThing().setFieldName(result.get()[1]);
						Controller.getFieldNames().add(newItem.getMenuThing().getFieldName());

						// System.out.println("text= " + result.get()[0] + ",
						// fieldname =" + result.get()[1]);
					});

					TreeItem<MenuTreeItem> insertItem = new TreeItem<MenuTreeItem>(newItem);
					if (!(valueToMove.startsWith("MenuItem") && item.toString().startsWith("MenuItem"))) {
						TreeItem<MenuTreeItem> newParent = search(parentTree.getRoot(), item.toString());
						newParent.getChildren().add(insertItem);
						newParent.setExpanded(true);
					} else {
						// insert Menuitem before the targets Menuitem
						TreeItem<MenuTreeItem> parentTarget = search(parentTree.getRoot(), item.toString()).getParent();
						// Find Index of Targets within the Targets Children
						int searchPosition = searchPos(item.toString());
						parentTarget.getChildren().add(searchPosition, insertItem);
						parentTarget.setExpanded(true);
					}

				} else if (!(valueToMove.startsWith("MenuItem") && item.toString().startsWith("MenuItem"))) {
					TreeItem<MenuTreeItem> itemToMove = search(parentTree.getRoot(), valueToMove);
					TreeItem<MenuTreeItem> newParent = search(parentTree.getRoot(), item.toString());
					itemToMove.getParent().getChildren().remove(itemToMove);
					newParent.getChildren().add(itemToMove);
					newParent.setExpanded(true);
				} else {
					// bewege Menuitem vor Menuitem des dropziels
					TreeItem<MenuTreeItem> parentTarget = search(parentTree.getRoot(), item.toString()).getParent();
					// Find Index of Targets within the Targets Children
					int searchPosition = searchPos(item.toString());
					TreeItem<MenuTreeItem> itemToMove = search(parentTree.getRoot(), valueToMove);
					itemToMove.getParent().getChildren().remove(itemToMove);
					parentTarget.getChildren().add(searchPosition, itemToMove);
					parentTarget.setExpanded(true);
				}

				dragEvent.consume();
				refreshMenuBar();

			}

		});
	}
/** deletes Subtree from me, not me itself!
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
				Menu menu = (Menu) menuItem.getValue().getMenuThing().getCorrespondingMenuObject();
				menu.getItems().clear();
				this.removeSubtree(menuItem);
				Controller.getFieldNames().remove(menuItem.getValue().getMenuThing().getFieldName());				
			}
			me.getChildren().clear();
		}

	}

	// brute force
	public void refreshMenuBar() {
		TreeItem<MenuTreeItem> root = parentTree.getRoot();
		MenuBar correspondingBar = (MenuBar) root.getValue().getMenuThing().getCorrespondingMenuObject();
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

	public static String getStandardName(MenuTreeItem newItem) {
		String nameStart = newItem.getMenuThing().getTyp();
		int nr = 1;
		while (!Bezeichnertester.variablenBezeichnerOK(nameStart + nr) || Controller.getFieldNames().contains(nameStart + nr)) {
			nr++;
		}
		return nameStart + nr;
	}

	private TreeItem<MenuTreeItem> search(final TreeItem<MenuTreeItem> currentNode, final String valueToSearch) {
		TreeItem<MenuTreeItem> result = null;
		if (currentNode.getValue().toString().equals(valueToSearch)) {
			result = currentNode;
		} else if (!currentNode.isLeaf()) {
			for (TreeItem<MenuTreeItem> child : currentNode.getChildren()) {
				result = search(child, valueToSearch);
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

	public static Optional<String[]> inputTextAndFieldname(String textVorgabe, MenuTreeItem newItem) {

		String fieldnameVorgabe;

		if (Bezeichnertester.variablenBezeichnerOK(newItem.getMenuThing().getFieldName())) {
			fieldnameVorgabe = newItem.getMenuThing().getFieldName();
		} else {
			fieldnameVorgabe = getStandardName(newItem);
		}

		Dialog<String[]> dialog = new Dialog<>();
		dialog.setTitle(LabelGrabber.getLabel("input.dialog"));

		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField textInput = new TextField();
		textInput.setText(textVorgabe);
		TextField fieldnameInput = new TextField();
		fieldnameInput.setText(fieldnameVorgabe);

		grid.add(new Label(LabelGrabber.getLabel("menu.text")+":"), 0, 0);
		grid.add(textInput, 1, 0);
		grid.add(new Label(LabelGrabber.getLabel("field.name.text")+":"), 0, 1);
		grid.add(fieldnameInput, 1, 1);
		dialog.getDialogPane().setContent(grid);

		// Request focus on the textInput field by default.
		Platform.runLater(() -> textInput.requestFocus());

		// Convert the result to a textInput-fieldnameInput-Array when
		// the OK button is clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				String[] st = new String[2];
				st[0] = textInput.getText();
				if (Bezeichnertester.variablenBezeichnerOK(fieldnameInput.getText())) {
					st[1] = fieldnameInput.getText();
				} else {
					st[1] = getStandardName(newItem);
				}
				return st;
			}
			return null;
		});

		Optional<String[]> result = dialog.showAndWait();
		return result;
	}

	// Versetzungsregeln für Menu:
	// auf Menu oder Menubar dann als letztes darunter

	// Versetzungsregeln für Menuitem:
	// Ziehen auf MenuItem, dann davor
	// Ziehen auf Menu dann darunter als letztes Child

	private int searchPos(final String valueToSearch) {
		TreeItem<MenuTreeItem> startNode = parentTree.getRoot();
		if (startNode.getValue().toString().equals(valueToSearch)) {
			return -1;
		}
		return (searchPos(startNode, valueToSearch));
	}

	private int searchPos(final TreeItem<MenuTreeItem> currentNode, final String valueToSearch) {
		if (!currentNode.isLeaf()) {
			for (int i = 0; i < currentNode.getChildren().size(); i++) {
				TreeItem<MenuTreeItem> child = currentNode.getChildren().get(i);
				if (child.getValue().toString().equals(valueToSearch)) {
					return i;
				}
				int sp = searchPos(child, valueToSearch);
				if (sp != -1) {
					return sp;
				}
			}
		}
		return -1;
	}

	@Override
	protected void updateItem(MenuTreeItem item, boolean empty) {
		super.updateItem(item, empty);
		this.item = item;
		if (empty || item == null) {
			setText(null);
			setGraphic(null);
		} else {
			setText(item.toString());
		}
	}

	private boolean dragAllowed(String valueToMove) {
		// target = MenuBar: only Menu allowed
		if (item.toString().startsWith("MenuBar")) {
			return valueToMove.startsWith("Menu") && !valueToMove.startsWith("MenuItem");
		}
		// target = MenuItem: only MenuItem allowed
		if (item.toString().startsWith("MenuItem")) {
			return valueToMove.startsWith("MenuItem");
		} else
			return true;
	}

}