package bdl.build.javafx.scene.canvas;

import java.util.List;

import bdl.build.GObject;
import bdl.build.properties.PanelProperty;
import bdl.view.right.PropertyEditPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GCanvas extends Canvas implements GObject {
	private List<PanelProperty> properties;
	private PropertyEditPane pep;
	private StringProperty fieldNameProperty = new SimpleStringProperty();

	public GCanvas() {
		super();
		heightProperty().addListener(n -> setBackground());
		widthProperty().addListener(n -> setBackground());
	}

	public GCanvas(double width, double height) {
		super(width, height);
		heightProperty().addListener(n -> setBackground());
		widthProperty().addListener(n -> setBackground());
	}

	public void setBackground() {
		GraphicsContext gc = getGraphicsContext2D();
		gc.setFill(Color.rgb(0xF6, 0xF6, 0xF6));
		gc.fillRect(0, 0, getWidth(), getHeight());
	}

	@Override
	public String getFieldName() {
		return fieldNameProperty.getValue();
	}

	@Override
	public void setFieldName(String fieldName) {
		setStyle("-fx-border-color:green;-fx-border-width:1");

		fieldNameProperty.setValue(fieldName);
	}

	@Override
	public StringProperty fieldNameProperty() {

		return fieldNameProperty;
	}

	@Override
	public void setPanelProperties(List<PanelProperty> properties) {
		this.properties = properties;
	}

	@Override
	public List<PanelProperty> getPanelProperties() {
		return properties;
	}

	@Override
	public void setPEP(PropertyEditPane propertyEditPane) {
		pep = propertyEditPane;
	}

	@Override
	public PropertyEditPane getPEP() {
		return pep;

	}

	@Override
	public String getNodeClassName() {
		return "Canvas";
	}

}
