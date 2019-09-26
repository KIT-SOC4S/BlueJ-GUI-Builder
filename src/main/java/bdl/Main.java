/*
 * This file is part of JavaFX-GUI-Builder.
 *
 * Copyright (C) 2014  Leon Atherton, Ben Goodwin, David Hodgson
 *
 * JavaFX-GUI-Builder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * JavaFX-GUI-Builder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaFX-GUI-Builder.  If not, see <http://www.gnu.org/licenses/>.
 */

package bdl;

import bdl.controller.Controller;
import bdl.lang.LabelGrabber;
import bdl.model.ComponentSettingsStore;
import bdl.view.View;
import javafx.application.Application;
import javafx.application.Platform;
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
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/BlueJ_Orange_64.png")));
        new LabelGrabber();

        //Allow user to specify their own file
        String componentSettingsLocation = System.getProperty("bdl.guibuilder.componentSettings");
        if (componentSettingsLocation == null) {
            componentSettingsLocation = "/model/component-settings.xml";//Default file
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
            Platform.setImplicitExit(false);
        }

        Scene scene = new Scene(view, 1024, 600);

        stage.setTitle(LabelGrabber.getLabel("default.gui.title"));
        stage.setScene(scene);

        // Don't show() if using BlueJ, we'll show when we want it with blueJInterface.show().
        //if (blueJInterface == null) {
            stage.show();
        //}
    }

    public void setInterface(Interface blueJInterface) {
        this.blueJInterface = blueJInterface;
    }
}
