/**
 * @author Georg Dick
 */
package di.mover;

import bdl.view.left.RasterPane;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class MoverSet {

	private double parentwidth;
	private double parentheight;
	private double parentx;
	private double parenty;
	private double neux;
	private double neuy;
	private double altx;
	private double alty;
	double px;
	double py;
	double wx;
	double wy;
	private double rux;
	private double ruy;
	private Node selectedComponent;
	private Rectangle mover[];

	private int size = 8;// must not be odd

	
	public MoverSet() {
		selectedComponent = null;
		this.generateMover();
		update();
	}

	public void mousePressed(javafx.scene.input.MouseEvent e) {

		parentwidth = wx = selectedComponent.getBoundsInParent().getWidth();
		parentheight = wy = selectedComponent.getBoundsInParent().getHeight();
		parentx = px = selectedComponent.getBoundsInParent().getMinX();
		parenty = py = selectedComponent.getBoundsInParent().getMinY();
		rux = px + wx;
		ruy = py + wy;
		altx = e.getScreenX();
		alty = e.getScreenY();
		e.consume();// to avoid clearselection

	}

	public void mouseReleased(javafx.scene.input.MouseEvent e) {

		setVisible(true);
		update();
	}

	public void mouseDragged(javafx.scene.input.MouseEvent e) {

		neux = e.getScreenX();
		neuy = e.getScreenY();

		switch (Integer.parseInt(((Rectangle) e.getSource()).getId())) {
		case 0: // '\0' links oben
			px = neux - altx + px;
			py = neuy - alty + py;
			wx = parentx + parentwidth - px;
			wy = parenty + parentheight - py;
			break;

		case 1: // '\001' mitte oben
			px = parentx;
			py = neuy - alty + py;
			wx = parentwidth;
			wy = parenty + parentheight - py;
			break;

		case 2: // '\002' rechts oben
			px = parentx;
			py = neuy - alty + py;
			wx = neux - altx + wx;
			wy = parenty + parentheight - py;
			break;

		case 3: // '\003' mitte links
			px = neux - altx + px;
			py = parenty;
			wx = parentx + parentwidth - px;
			wy = parentheight;
			break;

		case 4: // '\004' mitte rechts
			px = parentx;
			py = parenty;
			wx = neux - altx + wx;
			wy = parentheight;
			break;

		case 5: // '\005' links unten
			px = neux - altx + px;
			py = parenty;
			wx = parentx + parentwidth - px;
			wy = neuy - alty + wy;
			break;

		case 6: // '\006' mitte unten
			px = parentx;
			py = parenty;
			wx = parentwidth;
			wy = neuy - alty + wy;
			break;

		case 7: // '\007' rechts unten
			px = parentx;
			py = parenty;
			wx = neux - altx + wx;
			wy = neuy - alty + wy;

			break;
		}
		rux = px + wx;
		ruy = py + wy;
		// System.out.println(px + " " + py + " " + wx + " " + wy);

		if (wx > 0 && wy > 0) {
			selectedComponent.setLayoutX(RasterPane.getRasterPosX(px));
			selectedComponent.setLayoutY(RasterPane.getRasterPosY(py));
			if (selectedComponent instanceof AnchorPane) {
				Region region = (Region) selectedComponent;
				region.setPrefWidth(RasterPane.getRasterPosX(rux) - RasterPane.getRasterPosX(px));
				region.setPrefHeight(RasterPane.getRasterPosY(ruy) - RasterPane.getRasterPosY(py));
			} else if (selectedComponent instanceof ListView) {
				ListView lview = (ListView) selectedComponent;
				lview.setPrefWidth(RasterPane.getRasterPosX(rux) - RasterPane.getRasterPosX(px));
				lview.setPrefHeight(RasterPane.getRasterPosY(ruy) - RasterPane.getRasterPosY(py));
			} else 	if (selectedComponent instanceof Region) {
				Region region = (Region) selectedComponent;
				region.setMinWidth(RasterPane.getRasterPosX(rux) - RasterPane.getRasterPosX(px));
				region.setMaxWidth(RasterPane.getRasterPosX(rux) - RasterPane.getRasterPosX(px));
				region.setMinHeight(RasterPane.getRasterPosY(ruy) - RasterPane.getRasterPosY(py));
				region.setMaxHeight(RasterPane.getRasterPosY(ruy) - RasterPane.getRasterPosY(py));
			} else if (selectedComponent instanceof Rectangle) {
				Rectangle rect = (Rectangle) selectedComponent;
				rect.setWidth(RasterPane.getRasterPosX(rux) - RasterPane.getRasterPosX(px));
				rect.setHeight(RasterPane.getRasterPosY(ruy) - RasterPane.getRasterPosY(py));
			} else if (selectedComponent instanceof Canvas) {
				Canvas canvas = (Canvas) selectedComponent;
				canvas.setWidth(RasterPane.getRasterPosX(rux) - RasterPane.getRasterPosX(px));
				canvas.setHeight(RasterPane.getRasterPosY(ruy) - RasterPane.getRasterPosY(py));
			} else if (selectedComponent instanceof SubScene) {
				SubScene subscene = (SubScene) selectedComponent;
				subscene.setWidth(RasterPane.getRasterPosX(rux) - RasterPane.getRasterPosX(px));
				subscene.setHeight(RasterPane.getRasterPosY(ruy) - RasterPane.getRasterPosY(py));
			}
		}
		altx = neux;
		alty = neuy;
		update();
		e.consume();// else problems in nested Panes

	}

	private void generateMover() {

		if (mover == null) {
			mover = new Rectangle[8];

		}
		for (int i = 0; i < 8; i++) {
			if (mover[i] == null) {
				mover[i] = new Rectangle();
				// System.out.println(i);
			}
			mover[i].setWidth(size);
			mover[i].setHeight(size);
			mover[i].relocate(-10, -10);
			mover[i].setFill(Color.BLUE);
			mover[i].setId(i + "");
			mover[i].setOnMousePressed(e -> mousePressed(e));
			mover[i].setOnMouseReleased(e -> mouseReleased(e));
			mover[i].setOnMouseDragged(e -> mouseDragged(e));
			if (selectedComponent != null)
				((Pane) selectedComponent.getParent()).getChildren().add(mover[i]);
		}
		mover[0].setCursor(Cursor.NW_RESIZE);
		mover[1].setCursor(Cursor.N_RESIZE);
		mover[2].setCursor(Cursor.NE_RESIZE);
		mover[3].setCursor(Cursor.W_RESIZE);
		mover[4].setCursor(Cursor.E_RESIZE);
		mover[5].setCursor(Cursor.SW_RESIZE);
		mover[6].setCursor(Cursor.S_RESIZE);
		mover[7].setCursor(Cursor.SE_RESIZE);

	}

	public void setVisible(boolean visible) {

		for (int i = 0; i < 8; i++) {
			mover[i].setVisible(visible);
			if (selectedComponent != null) {
				if (visible) {
					selectedComponent.setCursor(Cursor.MOVE);
				} else {
					selectedComponent.setCursor(Cursor.DEFAULT);
				}
			}
		}
		update();
	}

	public boolean istSichtbar() {
		return mover[0].isVisible();
	}

	public void update() {

		try {
			if (selectedComponent != null) {
				mover[0].relocate(selectedComponent.getLayoutX() - size, selectedComponent.getLayoutY() - size);
				mover[1].relocate(
						(selectedComponent.getLayoutX() + selectedComponent.getLayoutBounds().getWidth() / 2) - 3,
						selectedComponent.getLayoutY() - size);
				mover[2].relocate(selectedComponent.getLayoutX() + selectedComponent.getLayoutBounds().getWidth(),
						selectedComponent.getLayoutY() - size);
				mover[3].relocate(selectedComponent.getLayoutX() - size,
						(selectedComponent.getLayoutY() + selectedComponent.getLayoutBounds().getHeight() / 2)
								- size / 2);
				mover[4].relocate(selectedComponent.getLayoutX() + selectedComponent.getLayoutBounds().getWidth(),
						(selectedComponent.getLayoutY() + selectedComponent.getLayoutBounds().getHeight() / 2)
								- size / 2);
				mover[5].relocate(selectedComponent.getLayoutX() - size,
						selectedComponent.getLayoutY() + selectedComponent.getLayoutBounds().getHeight());
				mover[6].relocate(
						(selectedComponent.getLayoutX() - size / 2)
								+ selectedComponent.getLayoutBounds().getWidth() / 2,
						selectedComponent.getLayoutY() + selectedComponent.getLayoutBounds().getHeight());
				mover[7].relocate(selectedComponent.getLayoutX() + selectedComponent.getLayoutBounds().getWidth(),
						selectedComponent.getLayoutY() + selectedComponent.getLayoutBounds().getHeight());
				// for (int i=0;i<8;i++){ //Strange behaviour : No selection in
				// Hierarchytree ???
				// mover[i].toFront();
				// }

			}
		} catch (Exception e) {

		}

	}

	public void removeFromParent() {
		for (int i = 0; i < 8; i++) {
			if (mover[i].getParent() != null) {
				((Pane) mover[i].getParent()).getChildren().remove(mover[i]);
			}
		}
		selectedComponent = null;

	}

	public void setNode(Node gObject, Pane parent) {
		

		if (gObject instanceof Circle) {
			setVisible(false);
			this.selectedComponent=null;
			return;
		}

		this.selectedComponent = gObject;
		for (int i = 0; i < 8; i++) {
			if (selectedComponent != null) {
				if (!parent.getChildren().contains(mover[i])) {
					parent.getChildren().add(mover[i]);
				}
			}
		}
		update();
		setVisible(true);
	}
	
	public Node getNode(){
		return this.selectedComponent;
	}
	

}
