package di.settings;


	import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import bluej.extensions.ProjectNotOpenException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

	public class Settings extends Application {

	    private AnchorPane root;
		private Stage stage;

	    public Settings(){
	        this.go();
	    }

	   // If used by the constructor, BlueJ can inspect the application
	    public void go() {
	        new JFXPanel();
	        Platform.runLater( ()->{start(new Stage());});
	    }

	    private Parent getRoot() {
	        root = new AnchorPane();
	        root.getChildren().addAll();
	        return root;
	    }
	    @Override
	    public void start(Stage primaryStage) {
	        Scene scene = new Scene(getRoot(), 600.0, 400.0);
	        primaryStage.setTitle("FX GUI Designer");
	        primaryStage.setOnCloseRequest(e->System.exit(0));//should be used in BlueJ. Otherwise you have to reset the VM manually after closing
	        primaryStage.setScene(scene);
	        primaryStage.show();
	        this.stage= primaryStage;
	        openFileAsXML();
	    }


	     /* @param args the command line arguments
	     */
	    public static void main(String[] args) {
           launch(args);
	        
	    }
	    
	    
	    private void openFileAsXML() {
	    	FileChooser fileChooser = new FileChooser();
			
			FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
			fileChooser.getExtensionFilters().add(filter);
			File file = fileChooser.showOpenDialog(this.stage);
	    	
			Document document;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				document = builder.parse(file);
				document.normalize();
				Element root = document.getDocumentElement();
				readXML(root);

			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException | IOException e) {
				e.printStackTrace();
			}

		}

		private void readXML(Element e) {
			String nodename = e.getNodeName();
			if (nodename.equals("component")){
				System.out.println(e.getNodeName());
			
			
			
				NodeList childs = e.getChildNodes();
				for (int k = 0; k < childs.getLength(); k++) {
					if (childs.item(k) instanceof Element) {
						readXML((Element) childs.item(k));
					}
				}
			}
		}
		}

	

