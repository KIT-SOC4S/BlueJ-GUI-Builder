package bdl.view.hierarchy;

import bdl.build.GObject;
import bdl.controller.ViewListeners;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Ben Goodwin
 */
public class HierarchyTreeCellFactory extends TreeCell<GObject> {

    public ViewListeners viewlisteners;

    public HierarchyTreeCellFactory() {
        super();
//        if (getItem() != null) {
//            setText(getItem().getFieldName());
//            this.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent t) {
//                    viewlisteners.redraw((Node) getItem());
//                    System.out.println(getItem().getFieldName());
//                }
//            });
//        }
    }

    @Override
    public void updateItem(GObject item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            System.out.println("null triggered");
            setText(null);
            return;
        }
        setGraphic(new Label(item.getFieldName()));
        System.out.println(item.getFieldName());
        
//        if (empty) {
//            setText(null);
//            setGraphic(null);
//        } else {
//            setText(item.getFieldName());
//            Node currentNode = getGraphic();
//            Node newNode = (Node) item;
//            if (currentNode == null || !currentNode.equals(newNode)) {
//                setGraphic(newNode);
//            }
//        }
    }
}