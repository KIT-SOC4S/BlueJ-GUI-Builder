package bdl.controller;

import bdl.build.GObject;
import bdl.model.history.HistoryItem;
import bdl.model.history.HistoryManager;
import bdl.model.selection.SelectionManager;
import bdl.view.View;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class ViewListeners {

	private SelectionManager selectionManager;
	HistoryManager historyManager;
	double historyX, historyY;
	boolean isMousePressed = false;
	double curX = 0, curY = 0;
	double breite, hoehe;
	private View view;

	public ViewListeners(HistoryManager hm, SelectionManager selectionManager, View view) {
		historyManager = hm;
		this.selectionManager = selectionManager;
		this.view = view;
	}

	public void onMousePressed(Node node, MouseEvent mouseEvent) {
		System.out.println(mouseEvent);
		isMousePressed = true;
		curX = mouseEvent.getX();
		curY = mouseEvent.getY();
		historyX = node.getLayoutX();
		historyY = node.getLayoutY();

		if (mouseEvent.isPrimaryButtonDown() && mouseEvent.isAltDown())
			if (node instanceof Region) {
				Region c = (Region) node;
				System.out.println("MDown");
				if (c.getMinWidth() != c.getWidth()) {
					c.setMinWidth(c.getWidth());
				}
				if (c.getMinHeight() != c.getHeight()) {
					c.setMinHeight(c.getHeight());
				}
				breite = c.getWidth();
				hoehe = c.getHeight();
			} else if (node instanceof Canvas) {
				Canvas c = (Canvas) node;
				breite = c.getWidth();
				hoehe = c.getHeight();
			} else if (node instanceof Rectangle) {
				Rectangle c = (Rectangle) node;

				breite = c.getWidth();
				hoehe = c.getHeight();
			}
	}

	

	public void onMouseDragged(Node node, MouseEvent mouseEvent) {
		if (isMousePressed && selectionManager.getCurrentlySelected() == node && mouseEvent.isPrimaryButtonDown()
				&& !mouseEvent.isAltDown()) {
			// System.out.println("MD");
			double x = node.getLayoutX() + (mouseEvent.getX() - curX);
			double y = node.getLayoutY() + (mouseEvent.getY() - curY);
			
			node.setLayoutX(view.leftPanel.getRasterPane().getRasterPosX(x));
			node.setLayoutY(view.leftPanel.getRasterPane().getRasterPosY(y));
			mouseEvent.consume();
		} else if (isMousePressed && selectionManager.getCurrentlySelected() == node && mouseEvent.isPrimaryButtonDown()
				&& mouseEvent.isAltDown()) {
			if (node instanceof Canvas) {
				Canvas c = (Canvas) node;
				System.out.println("breite" + breite);
				breite = breite + mouseEvent.getX() - curX;
				hoehe = hoehe + mouseEvent.getY() - curY;
//
//				double x = c.getWidth() + (mouseEvent.getX() - curX);
//				double y = c.getHeight() + (mouseEvent.getY() - curY);
				curX = mouseEvent.getX();
				curY = mouseEvent.getY();
				c.setHeight(view.leftPanel.getRasterPane().getRasterPosY(hoehe));
				c.setWidth(view.leftPanel.getRasterPane().getRasterPosX(breite));
			} else if (node instanceof Region) {
				Region c = (Region) node;
				// System.out.println("MD");
				// if (c.getMinWidth()!=c.getWidth()){
				// c.setMinWidth(c.getWidth());
				// }
				// if (c.getMinHeight()!=c.getHeight()){
				// c.setMinHeight(c.getHeight());
				// }
				breite = breite + mouseEvent.getX() - curX;
				hoehe = hoehe + mouseEvent.getY() - curY;
//				double x = c.getMinWidth() + (mouseEvent.getX() - curX);
//				double y = c.getMinHeight() + (mouseEvent.getY() - curY);
				curX = mouseEvent.getX();
				curY = mouseEvent.getY();

				c.setMinHeight(view.leftPanel.getRasterPane().getRasterPosY(hoehe));
				c.setMinWidth(view.leftPanel.getRasterPane().getRasterPosX(breite));
			} else if (((GObject) node) instanceof Rectangle) {
				Rectangle c = (Rectangle) node;
				// System.out.println("MD");
				breite = breite + mouseEvent.getX() - curX;
				hoehe = hoehe + mouseEvent.getY() - curY;
//				double x = c.getWidth() + (mouseEvent.getX() - curX);
//				double y = c.getHeight() + (mouseEvent.getY() - curY);
				curX = mouseEvent.getX();
				curY = mouseEvent.getY();
				c.setHeight(view.leftPanel.getRasterPane().getRasterPosY(hoehe));
				c.setWidth(view.leftPanel.getRasterPane().getRasterPosX(breite));
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
