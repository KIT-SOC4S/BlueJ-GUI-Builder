package bdl.build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import bdl.build.javafx.scene.control.GMenuBar;
import bdl.build.properties.ListenerEnabledProperty;
import bdl.build.properties.PanelProperty;
import bdl.controller.Controller;
import bdl.model.ComponentSettings;
import bdl.view.left.ComponentMenuItem;
import di.menubuilder.MenuBuilder;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class CodeGenerator {

	public static String generateJavaCode(GUIObject guiObject, HashMap<String, String> allImports, boolean forPreview) {

		StringBuilder code = new StringBuilder();

		code.append(getJavaImports(guiObject, allImports)).append('\n');// Add
																		// imports

		String clName = guiObject.getClassName();
		String clExtends = "Application";
		code.append("public class ").append(clName).append(" extends ").append(clExtends).append(" {\n\n");// Open
																											// class
																											// tag

		// Add declarations
		for (Node node : guiObject.getChildren()) {
			if (node instanceof GObject) {
				declaration(node, code);
			}
		}
		code.append('\n');

		// Add properties
		code.append("    private Parent getRoot() {\n");
		code.append("        AnchorPane root = new AnchorPane();\n");
		code.append("        root.getChildren().addAll(");
		String prefix = "";
		for (Node node : guiObject.getChildren()) {
			if (node instanceof GObject) {
				GObject gObj = (GObject) node;
				code.append(prefix);
				prefix = ", ";
				code.append(gObj.getFieldName());
			}
		}
		code.append(");\n");

		// Build a list of panes so that we can add each node into that pane's
		// pane.
		ArrayList<Pane> paneList = buildPaneList(guiObject);
		for (Pane p : paneList) {
			code.append("        ").append(((GObject) p).getFieldName()).append(".getChildren().addAll(");
			prefix = "";
			for (Node node : p.getChildren()) {
				GObject gObj = (GObject) node;
				code.append(prefix);
				prefix = ", ";
				code.append(gObj.getFieldName());
			}
			code.append(");\n");
		}
		code.append("\n");

		for (Node node : guiObject.getChildren()) {
			panelProperties(node, code);
		}
		code.append("        return root;\n");
		code.append("    }\n\n");

		// Add show method
		code.append("    public void show() {\n" + "        new JFXPanel();\n"
				+ "        Platform.runLater(new Runnable() {\n" + "            @Override\n"
				+ "            public void run() {\n" + "                start(new Stage());\n" + "            }\n"
				+ "        });\n" + "    }\n\n");

		// Add start method
		String stPreview = forPreview ? ""
				: "        primaryStage.setOnCloseRequest(e->System.exit(0));//needed for (helps)BlueJ\n";

		code.append("    @Override\n" + "    public void start(Stage primaryStage) {\n"
				+ "        Scene scene = new Scene(getRoot(), " + guiObject.getWidth() + ", " + guiObject.getHeight()
				+ ");\n" + "        \n" + "        primaryStage.setTitle(\"" + guiObject.getGUITitle() + "\");\n"
				+ stPreview + "        primaryStage.setScene(scene);\n" + "        primaryStage.show();\n"
				+ "    }\n\n");
		// Add eventHandler

		for (Node node : guiObject.getChildren()) {
			listenerProperties(node, code);
		}

		// Add main method
		// code.append(" /**\n" + " * The main() method is ignored in correctly
		// deployed JavaFX application.\n"
		// + " * main() serves only as fallback in case the application can not
		// be\n"
		// + " * launched through deployment artifacts, e.g., in IDEs with
		// limited FX\n"
		// + " * support. NetBeans ignores main().\n" + " *\n"
		// + " * @param args the command line arguments\n" + " */\n"
		// + " public static void main(String[] args) {\n" + " launch(args);\n"
		// + " }\n");
		// ;
		code.append("     /* @param args the command line arguments\n" + "     */\n"
				+ "    public static void main(String[] args) {\n" + "        launch(args);\n" + "    }\n");
		;
		code.append('}');// Close class tag
		return code.toString();
	}
/*
	public static String generateFXMLCode(GUIObject guiObject, HashMap<String, String> allImports) {

		StringBuilder code = new StringBuilder();
		code.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n");

		code.append(getFXMLImports(guiObject, allImports)).append('\n');

		code.append("<AnchorPane fx:id=\"");
		code.append(guiObject.getClassName());
		code.append("\" prefWidth=\"");
		code.append(guiObject.getGUIWidth());
		code.append("\" prefHeight=\"");
		code.append(guiObject.getGUIHeight());
		// code.append("\" xmlns:fx=\"http://javafx.com/fxml/1\"
		// xmlns=\"http://javafx.com/javafx/2.2\"
		// fx:controller=\"DummyController\">\n");
		code.append("\" xmlns:fx=\"http://javafx.com/fxml/1\" xmlns=\"http://javafx.com/javafx/2.2\">\n");

		code.append("    <children>\n");

		for (Node node : guiObject.getChildren()) {
			fxmlOutput(node, code);
		}

		code.append("    </children>\n");
		code.append("</AnchorPane>");
		return code.toString();
	}
	*/
	
	public static String generateFXMLCode(GUIObject guiObject,Controller c) {

		StringBuilder code = new StringBuilder();
		code.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n");
		code.append(getFXMLImports(guiObject, c)).append('\n');
		code.append("<AnchorPane fx:id=\"");
		code.append(guiObject.getClassName());
		code.append("\" prefWidth=\"");
		code.append(guiObject.getGUIWidth());
		code.append("\" prefHeight=\"");
		code.append(guiObject.getGUIHeight());
		// code.append("\" xmlns:fx=\"http://javafx.com/fxml/1\"
		// xmlns=\"http://javafx.com/javafx/2.2\"
		// fx:controller=\"DummyController\">\n");
		code.append("\" xmlns:fx=\"http://javafx.com/fxml/1\" xmlns=\"http://javafx.com/javafx/2.2\">\n");

		code.append("    <children>\n");

		for (Node node : guiObject.getChildren()) {
			fxmlOutput(node, code);
		}

		code.append("    </children>\n");
		code.append("</AnchorPane>");
		return code.toString();
	}
	private static String getJavaImports(GUIObject guiObject, HashMap<String, String> allImports) {
		HashSet<String> imports = new HashSet<>();

		StringBuilder importsString = new StringBuilder();
		imports.add("import javafx.application.*;\n");
		imports.add("import javafx.embed.swing.*;\n");
		imports.add("import javafx.scene.*;\n");
		imports.add("import javafx.scene.layout.*;\n");
		imports.add("import javafx.stage.*;\n");
		imports.add("import javafx.scene.paint.*;\n");
		imports.add("import javafx.scene.control.*;\n");	
		
		for (Node node : guiObject.getChildren()) {
			javaImports(node, imports, allImports);
		}
		for (String s : imports) {
			importsString.append(s);
		}
		return importsString.toString();
	}

	private static String getFXMLImports(GUIObject guiObject, Controller c) {
		 HashMap<String, String> allImports;
		allImports = new HashMap<>();
		for (ComponentMenuItem componentMenuItem : c.getView().leftPanel.leftList.getItems()) {
			ComponentSettings componentSettings = componentMenuItem.getComponentSettings();
			allImports.put(componentSettings.getType(), componentSettings.getPackageName());			
		}
		HashSet<String> imports = new HashSet<>();
		
		StringBuilder importsString = new StringBuilder();
        imports.add("import java.lang.*");
        imports.add("import java.util.*");
        imports.add("import javafx.scene.control.*");
        imports.add("import javafx.scene.layout.*");
        imports.add("import javafx.scene.paint.*");
        imports.add("import javafx.scene.shape.*");
        imports.add("import javafx.scene.canvas.*");
        for (Node node : guiObject.getChildren()) {
			fxmlImports(node, imports, allImports);
		}		
		for (String s : imports) {
			importsString.append("<?"+s+"?>\n");
		}		
		return importsString.toString();
	}
	/*
	private static String getFXMLImports(GUIObject guiObject, HashMap<String, String> allImports) {
		
		StringBuilder importsString = new StringBuilder();

		importsString.append("<?import java.lang.*?>\n").append("<?import java.util.*?>\n")
				.append("<?import javafx.scene.control.*?>\n").append("<?import javafx.scene.layout.*?>\n")
				.append("<?import javafx.scene.paint.*?>\n").append("<?import javafx.scene.shape.*?>\n")
				.append("<?import javafx.scene.canvas.*?>\n");
		
		
		return importsString.toString();
	}
*/
	private static void declaration(Node node, StringBuilder code) {

		GObject gObj = (GObject) node;
		String nodeType = node.getClass().getSimpleName().substring(1);
		if (!(node instanceof GMenuBar)) {
			code.append("    private ").append(nodeType).append(" ").append(gObj.getFieldName()).append(" = new ")
					.append(nodeType).append("();\n");
		}
		if (node instanceof Pane) {
			for (Node node2 : ((Pane) node).getChildren()) {
				declaration(node2, code);
			}
		}
		if (node instanceof GMenuBar) {
			MenuBuilder mb = ((GMenuBar) node).getMenuBuilder();
			if (mb != null) {
				code.append("    ").append(mb.getJAVADeclaration().replace("\n", "\n    ")).append("\n");

			}
		}
	}

	private static void properties(String prefix, Node node, StringBuilder code) {
		GObject gObj = (GObject) node;
		code.append(prefix);
		prefix = ", ";
		code.append(gObj.getFieldName());
		if (node instanceof Pane) {
			code.append("        ").append(((GObject) node).getFieldName()).append(".getChildren().addAll(");
			prefix = "";
			for (Node node2 : ((Pane) node).getChildren()) {
				properties(prefix, node2, code);
			}
			code.append(");\n");
		}
	}

	private static void panelProperties(Node node, StringBuilder code) {
		if (!(node instanceof GObject)) {
			return;
		}
		GObject gObj = (GObject) node;
		for (PanelProperty property : gObj.getPanelProperties()) {
			String javaCode = property.getJavaCode();
			if (!javaCode.isEmpty()) {
				code.append("        ").append(javaCode.replace("\n", "\n        ")).append("\n");
			}
		}
		code.append('\n');
		if (node instanceof Pane) {
			for (Node node2 : ((Pane) node).getChildren()) {
				panelProperties(node2, code);
			}
		}
		if (node instanceof GMenuBar) {
			MenuBuilder mb = ((GMenuBar) node).getMenuBuilder();
			if (mb != null) {
				code.append("        ").append(mb.getJAVAMenuStructure().replace("\n", "\n        ")).append("\n");
				code.append("        ").append(mb.getActionListenerDeclaration().replace("\n", "\n        "))
						.append("\n");
			}
		}
	}

	private static void listenerProperties(Node node, StringBuilder code) {
		if (!(node instanceof GObject)) {
			return;
		}
		GObject gObj = (GObject) node;
		for (PanelProperty property : gObj.getPanelProperties()) {
			if (property instanceof ListenerEnabledProperty) {

				String javaCode = ((ListenerEnabledProperty) property).getJavaCodeHandler();
				if (!javaCode.isEmpty()) {
					code.append("     ").append(javaCode.replace("\n", "\n   ")).append("\n");
				}
			}
		}
		code.append('\n');
		if (node instanceof Pane) {
			for (Node node2 : ((Pane) node).getChildren()) {
				listenerProperties(node2, code);
			}
		}
		if (node instanceof GMenuBar) {
			MenuBuilder mb = ((GMenuBar) node).getMenuBuilder();
			if (mb != null) {
				code.append("        ").append(mb.getActionListenerMethods().replace("\n", "\n        ")).append("\n");
			}
		}
	}

	private static void fxmlOutput(Node node, StringBuilder code) {
		String nodeClass = node.getClass().getSuperclass().getSimpleName();
		if (node instanceof GObject) {
			GObject gObj = (GObject) node;
			if (node instanceof Pane) {
				code.append("        <").append(nodeClass);
				code.append(" fx:id=\"").append(gObj.getFieldName()).append("\" ");
				for (PanelProperty property : gObj.getPanelProperties()) {
					String fxmlCode = property.getFXMLCode();
					if (!fxmlCode.isEmpty()) {
						code.append(fxmlCode).append(' ');
					}
				}
				code.append(">\n");
				code.append("<children>\n");
				for (Node node2 : ((Pane) node).getChildren()) {
					fxmlOutput(node2, code);
				}
				code.append("</children>\n");
				code.append("</" + nodeClass + ">\n");
			} else if (node instanceof GMenuBar) {
				MenuBuilder mb = ((GMenuBar) node).getMenuBuilder();
				code.append(mb.getFXML());
			} else {
				code.append("        <").append(nodeClass);
				code.append(" fx:id=\"").append(gObj.getFieldName()).append("\" ");
				for (PanelProperty property : gObj.getPanelProperties()) {
					String fxmlCode = property.getFXMLCode();
					if (!fxmlCode.isEmpty()) {
						code.append(fxmlCode).append(' ');
					}
				}
				code.append("/>\n");
			}
		}

	}

	private static void javaImports(Node node, HashSet<String> imports, HashMap<String, String> allImports) {
		// String gClassName = node.getClass().getSimpleName().substring(1);
		if (node instanceof GObject) {
			GObject gnode = (GObject) node;

			String gClassName = gnode.getNodeClassName();
			// imports.add("import " + allImports.get(gClassName) + "." +
			// gClassName + ";\n");
			if (allImports.get(gClassName) != null) {
				imports.add("import " + allImports.get(gClassName) + ".*;\n");
			}
			// import from properties
			List<PanelProperty> pp = gnode.getPanelProperties();
			if (pp.size() > 0) {
				for (PanelProperty panprop : pp) {
					if (panprop.getPackageName() != null && panprop.getPackageName().length() > 0) {
						imports.add("import " + panprop.getPackageName() + ".*;\n");

					}
					if (panprop.getImport() != null && !panprop.getImport().isEmpty()) {
						imports.add("import " + panprop.getImport() + ";\n");
					}
				}
			}
			if (gnode instanceof GMenuBar) {
				MenuBuilder mb = ((GMenuBar) gnode).getMenuBuilder();
				imports.add(mb.getMenuImportString()+ ";\n");
			}
		}

		if (node instanceof Pane) {
			for (Node node2 : ((Pane) node).getChildren()) {
				javaImports(node2, imports, allImports);
			}
		}
	}
	private static void fxmlImports(Node node, HashSet<String> imports, HashMap<String, String> allImports) {
		// String gClassName = node.getClass().getSimpleName().substring(1);
		if (node instanceof GObject) {
			GObject gnode = (GObject) node;

			String gClassName = gnode.getNodeClassName();
			// imports.add("import " + allImports.get(gClassName) + "." +
			// gClassName + ";\n");
			if (allImports.get(gClassName) != null) {
				imports.add("import " + allImports.get(gClassName) + ".*");
			}
			// import from properties
			List<PanelProperty> pp = gnode.getPanelProperties();
			if (pp.size() > 0) {
				for (PanelProperty panprop : pp) {
					if (panprop.getPackageName() != null && panprop.getPackageName().length() > 0) {
						imports.add("import " + panprop.getPackageName() + ".*");

					}
					if (panprop.getImport() != null && !panprop.getImport().isEmpty()) {
						imports.add("import " + panprop.getImport() );
					}
				}
			}
			if (gnode instanceof GMenuBar) {
				MenuBuilder mb = ((GMenuBar) gnode).getMenuBuilder();
				imports.add(mb.getMenuImportString());
			}
		}

		if (node instanceof Pane) {
			for (Node node2 : ((Pane) node).getChildren()) {
				fxmlImports(node2, imports, allImports);
			}
		}
	}
	
	// Recursively build an arraylist of panes in the current gui.
	private static ArrayList<Pane> buildPaneList(GUIObject guiObject) {
		ArrayList<Pane> list = new ArrayList<>();
		for (Node n : guiObject.getChildren()) {
			if (n instanceof Pane) {
				list.add((Pane) n);
				buildList((Pane) n, list);
			}
		}
		return list;
	}

	private static void buildList(Pane pane, ArrayList<Pane> list) {
		for (Node n : pane.getChildren()) {
			if (n instanceof Pane) {
				list.add((Pane) n);
				buildList((Pane) n, list);
			}
		}
	}

}
