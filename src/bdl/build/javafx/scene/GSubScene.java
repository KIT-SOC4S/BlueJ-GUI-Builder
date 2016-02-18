package bdl.build.javafx.scene;

import java.util.HashSet;
import java.util.List;

import bdl.build.GObject;
import bdl.build.properties.PanelProperty;
import bdl.view.right.PropertyEditPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;

public class GSubScene extends SubScene implements GObject {
	private List<PanelProperty> properties;
	private PropertyEditPane pep;
	private StringProperty fieldNameProperty = new SimpleStringProperty();

	public GSubScene() {
		super(new Group(), 100, 100);
		setFill(Color.LIGHTGRAY);
	}

	public GSubScene(double width, double height) {
		super(new Group(), width, height);
	}

	@Override
	public String getFieldName() {
		return fieldNameProperty.getValue();
	}

	@Override
	public void setFieldName(String fieldName) {
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
		return "SubScene";
	}

	public String getJavaConstructor() {
		return fieldNameProperty.getValue() + " = new SubScene(new Group()," + this.getWidth() + "," + this.getHeight()
				+ ",true, SceneAntialiasing.BALANCED);\n" 
				+ "this.subsceneExample("+fieldNameProperty.getValue()+");\n";

	}

	

	

	@Override
	public String[] getAdditionalImports() {
		String[] imports= {"javafx.scene.shape","javafx.scene.transform","javafx.scene.paint"
		};
		return imports;
	}

	@Override
	public StringBuilder getAdditionalMethods() {
		String l6="      ";
		String l4 ="    ";
		StringBuilder methodsString = new StringBuilder(); 
		methodsString.append(
		l4+"public void subsceneExample(SubScene subscene){\n"
		        +l6+ "Box box;\n"
		        +l6+ "Group group;\n"
		        +l6+ "PerspectiveCamera camera;\n"
		        +l6+ "PhongMaterial redMaterial;\n"				
		        +l6+  "subscene.setFill(Color.LIGHTBLUE);\n" 
		        +l6+ "camera = new PerspectiveCamera(true);\n" 
		        +l6+  "subscene.setCamera(camera);\n" 
		        +l6+ "camera.setNearClip(0.1);\n" 
		        +l6+  "camera.setFarClip(10000);\n" 
		        +l6+ "camera.setFieldOfView(42);\n" 
		        +l6+ "camera.setTranslateZ(-300);\n" 
		        +l6+ "box = new Box(100,50,30);\n" 
		        +l6+ "group = new Group();\n" 
		        +l6+  "group.getChildren().add(box);\n" 
		        +l6+  "subscene.setRoot(group);\n" 
		        +l6+  "box.setRotate(30);\n" 
		        +l6+ "box.setRotationAxis(Rotate.Y_AXIS);\n" 
		        +l6+ "box.setRotate(60);\n"
				// +
				// bname+".setRotationAxis(javafx.scene.transform.Rotate.Z_AXIS);\n"
				// + bname+".setRotate(45);\n"
				+l6+ "redMaterial = new PhongMaterial(Color.RED);\n"
				+l6+  "box.setMaterial(redMaterial);\n"
				+l4+ "}\n");
		return methodsString;
	}

	

}
