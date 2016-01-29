package bdl;

import bdl.controller.Controller;
import bdl.lang.LabelGrabber;
import bdl.model.ComponentSettingsStore;
import bdl.view.View;
import bluej.extensions.BlueJ;
import di.blueJLink.BlueJInterface;
import di.blueJLink.BlueJProjektVerwalter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main class.
 */
public class Main extends Application implements Runnable {
    

    public Interface blueJInterface;
    private BlueJ blueJ;
	private BlueJInterface blueJInterface2;
	public static String language="english";
    public void run() {
        launch();
    }
    public static String getLanguage(){
    	return language;
    }
    @Override
    public void start(final Stage stage) throws Exception {
    	
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/bdl/icons/BlueJ_Orange_64.png")));
        if (blueJ!=null){
        	language = blueJ.getBlueJPropertyString("bluej.language", "english");
        }
        new LabelGrabber(language);
        //stage.setOnCloseRequest(value);
        //Allow user to specify their own file
        String componentSettingsLocation = System.getProperty("bdl.guibuilder.componentSettings");
        if (componentSettingsLocation == null) {
            componentSettingsLocation = "/bdl/model/component-settings.xml";//Default file
        }

        ComponentSettingsStore model = null;
        try {
             model = new ComponentSettingsStore(componentSettingsLocation);
        } catch (Exception e) {
            e.printStackTrace();
            //System.exit(1); - We don't want to kill BlueJ! The user might lose all their work!
            throw new Exception("GUI Builder: Problem with component settings");
        }
        final View view = new View(stage, blueJInterface2 != null);
        Controller controller = new Controller(view, model, blueJInterface,blueJInterface2);
        if (blueJInterface != null) {
            blueJInterface.setGUIBuilderController(controller);
            Platform.setImplicitExit(false);
        }

        Scene scene = new Scene(view, 1024, 600);

        stage.setTitle(LabelGrabber.getLabel("default.gui.title"));
        stage.setScene(scene);

        // Don't show() if using BlueJ, we'll show when we want it with blueJInterface.show().
        if (blueJInterface == null) { stage.show(); }
    }

    public void setInterface(Interface blueJInterface) {
        this.blueJInterface = blueJInterface;
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    public void setBlueJ(BlueJ bluej) {
		this.blueJ = bluej;
		blueJInterface2 = new BlueJProjektVerwalter(bluej);
	}
}
