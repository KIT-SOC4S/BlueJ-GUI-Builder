package bdl.controller;

import bdl.build.GObject;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import bdl.model.selection.SelectionManager;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;

public class ViewListeners {

    private SelectionManager selectionManager;
    HistoryManager historyManager;
    double historyX, historyY;
    boolean isMousePressed = false;
    double curX = 0, curY = 0;

    public ViewListeners(HistoryManager hm, SelectionManager selectionManager) {
        historyManager = hm;
        this.selectionManager = selectionManager;
    }

    public void onMousePressed(Node node, MouseEvent mouseEvent) {
        isMousePressed = true;
        curX = mouseEvent.getX();
        curY = mouseEvent.getY();
        historyX = node.getLayoutX();
        historyY = node.getLayoutY();
    }

    public void onMouseDragged(Node node, MouseEvent mouseEvent) {
        if (isMousePressed && selectionManager.getCurrentlySelected() == node
        		&& mouseEvent.isPrimaryButtonDown()) {
//        	System.out.println("MD");
            double x = node.getLayoutX() + (mouseEvent.getX() - curX);
            double y = node.getLayoutY() + (mouseEvent.getY() - curY);
            node.setLayoutX(x);
            node.setLayoutY(y);
        } else
        if (isMousePressed && selectionManager.getCurrentlySelected() == node
        		&& mouseEvent.isSecondaryButtonDown()) {
        	if (node instanceof Canvas){
        	Canvas c=	(Canvas)node;
//        	System.out.println("MD");
            double x = c.getWidth() + (mouseEvent.getX() - curX);
            double y = c.getHeight() + (mouseEvent.getY() - curY);
            curX=mouseEvent.getX();
            curY=mouseEvent.getY();
            c.setHeight(y);
            c.setWidth(x);
        	}
        }
    }

    public void onMouseReleased(final Node node, final MouseEvent mouseEvent) {
        isMousePressed = false;
        final double finalX = node.getLayoutX();
        final double finalY = node.getLayoutY();
        if (finalX != historyX || finalY != historyY) {
            historyManager.addHistory(new HistoryItem() {
                double hFinalX = finalX;
                double hFinalY = finalY;
                double hHistoryX = historyX;
                double hHistoryY = historyY;

                @Override
                public void restore() {
                    node.setLayoutY(hFinalY);
                    node.setLayoutX(hFinalX);
                }

                @Override
                public void revert() {
                    node.setLayoutY(hHistoryY);
                    node.setLayoutX(hHistoryX);
                }

                @Override
                public String getAppearance() {
                    return ((GObject) node).getFieldName() + " layout changed!";
                }
            });
        }
    }
}
