package bdl.view.left;

import bdl.lang.LabelGrabber;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

public class RasterPane extends VBox {

	private DoubleProperty rasterx, rastery;

	private static Slider sliderRasterX;
	private static Slider sliderRasterY;


	public RasterPane() {
		Label lrh=new Label(LabelGrabber.getLabel("rasterpane.header"));
		Label lrx=new Label(LabelGrabber.getLabel("rasterpane.rasterx"));
		sliderRasterX = new Slider(0, 50, 10);
		sliderRasterX.setMajorTickUnit(10);
		sliderRasterX.setMinorTickCount(1);
		sliderRasterX.setShowTickMarks(true);
		sliderRasterX.setShowTickLabels(true);
//		sliderRasterX.setBlockIncrement(5);
		sliderRasterX.setSnapToTicks(true);
		Label lry=new Label(LabelGrabber.getLabel("rasterpane.rastery"));
		sliderRasterY = new Slider(0, 50, 10);
		sliderRasterY.setMajorTickUnit(10);
		sliderRasterY.setMinorTickCount(1);
		sliderRasterY.setShowTickMarks(true);
		sliderRasterY.setShowTickLabels(true);
//		sliderRasterY.setBlockIncrement(5);
		sliderRasterY.setSnapToTicks(true);
		this.getChildren().addAll(lrh,lrx,sliderRasterX, lry,sliderRasterY);
	}

	public static int getRasterPosX(double pos) {
		int rx = (int) sliderRasterX.getValue();
		int ipos = (int)pos;
		return rx == 0 ? ipos : ipos - ipos % rx;
	}

	public static int getRasterPosY(double pos) {
		int ry = (int) sliderRasterY.getValue();
		int ipos = (int)pos;
		return ry == 0 ? ipos : ipos - ipos % ry;
	}
}
