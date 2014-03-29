package bdl;

import bdl.controller.Controller;
import bdl.lang.LabelGrabber;
import bdl.model.ComponentSettingsStore;
import bdl.view.View;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main class.
 */
public class Main extends Application implements Runnable {
    
    // Singleton
//    private static Main __instance;
//    public static Main getInstance() {
//        if (__instance == null) { __instance = new Main(); }
//        return __instance;
//    }

    public Interface blueJInterface;

    public void run() {
        launch();
    }
    
    @Override
    public void start(final Stage stage) throws Exception {
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/bdl/icons/BlueJ_Orange_64.png")));
        new LabelGrabber();

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
        final View view = new View(stage, blueJInterface != null);
        Controller controller = new Controller(view, model, blueJInterface);
        if (blueJInterface != null) {
            blueJInterface.setGUIBuilderController(controller);
        }

        Scene scene = new Scene(view, 1024, 600);

        stage.setTitle(LabelGrabber.getLabel("default.gui.title"));
        stage.setScene(scene);

        //Handlers that require access to the Stage
        view.topPanel.mItmClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        });
        view.topPanel.mItmFullScreen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (stage.isFullScreen()) {
                    stage.setFullScreen(false);
                    view.topPanel.mItmFullScreen.setText(LabelGrabber.getLabel("fullscreen.enable.text"));
                } else {
                    stage.setFullScreen(true);
                    view.topPanel.mItmFullScreen.setText(LabelGrabber.getLabel("fullscreen.disable.text"));
                }
            }
        });

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
}
