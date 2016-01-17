package bdl.view.left.hierarchy;

import bdl.build.GObject;
import bdl.build.javafx.scene.control.GMenuBar;
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
                if (t.getButton().equals(MouseButton.SECONDARY)) {
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
                                    view.middleTabPane.viewPane.getChildren().add((Node) gObject);
                                    selectionManager.updateSelected(gObject);
                                }

                                @Override
                                public void restore() {
                                    view.middleTabPane.viewPane.getChildren().remove(gObject);
                                    selectionManager.clearSelection();
                                }

                                @Override
                                public String getAppearance() {
                                    return gObject.getFieldName() + " deleted!";
                                }
                            });
//                            if (gObject instanceof GMenuBar){
//                            	((GMenuBar)gObject).clearTree();
//                            }
                            view.middleTabPane.viewPane.getChildren().remove(gObject);
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
