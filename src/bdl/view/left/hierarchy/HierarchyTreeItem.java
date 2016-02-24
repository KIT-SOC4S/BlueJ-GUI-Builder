package bdl.view.left.hierarchy;

import bdl.build.GObject;
import bdl.build.GUIObject;
import bdl.lang.LabelGrabber;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import bdl.model.selection.SelectionManager;
import bdl.view.View;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class HierarchyTreeItem extends Label {

	GObject gObject;
	public final View view;
	public final SelectionManager selectionManager;
	public final HistoryManager historyManager;

	public HierarchyTreeItem(final GObject gObject, View v, SelectionManager sm, HistoryManager hm) {
		this.gObject = gObject;
		this.view = v;
		this.selectionManager = sm;
		this.historyManager = hm;

		this.textProperty().bind(gObject.fieldNameProperty());
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				if (gObject instanceof GUIObject) {
					return;
				}
				if (t.getButton().equals(MouseButton.SECONDARY)) {
					final Pane contentpane;
					if (((Node) gObject).getParent() instanceof Pane) {
						contentpane = (Pane) ((Node) gObject).getParent();
					} else {
						return;
					}
					ContextMenu popUp = new ContextMenu();
					MenuItem button = new MenuItem(LabelGrabber.getLabel("delete.node.text"));
					popUp.getItems().add(button);
					popUp.show((Node) t.getSource(), Side.RIGHT, 0, 0);

					button.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent t) {
							historyManager.addHistory(new HistoryItem() {
								@Override
								public void revert() {
									contentpane.getChildren().add((Node) gObject);
									// view.middleTabPane.viewPane.getChildren().add((Node)
									// gObject);
									selectionManager.updateSelected(gObject);
								}

								@Override
								public void restore() {
									contentpane.getChildren().remove(gObject);
									// view.middleTabPane.viewPane.getChildren().remove(gObject);
									selectionManager.clearSelection();
								}

								@Override
								public String getAppearance() {
									return gObject.getFieldName() + " deleted!";
								}
							});

							contentpane.getChildren().remove(gObject);
							// view.middleTabPane.viewPane.getChildren().remove(gObject);
							selectionManager.clearSelection();
						}
					});
				}
			}
		});

	}

	public GObject getGObject() {
		return gObject;
	}
}
