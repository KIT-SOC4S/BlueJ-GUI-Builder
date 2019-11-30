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

package bdl.build.properties;

import bdl.BlueJConnector;
import bdl.build.GObject;
import bdl.lang.LabelGrabber;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class ListenerProperty implements PanelProperty {
    private GObject gObj;
    private CheckBox checkBox;
    private Button jumpbutton;
    private String code;
    private boolean implemented;

    public ListenerProperty(final GObject gObj, String name, String code, GridPane gp, int row) {
        this.gObj = gObj;
        this.code = code;
        //this.implemented = implemented;
        gp.add(new Label(name + ":"), 0, row);

        jumpbutton = new Button();
        jumpbutton.setText(LabelGrabber.getLabel("listenerproperty.jump"));
        jumpbutton.setOnAction(event -> {
            try {
                BlueJConnector.getInstance().goToCode(getJavaCode());
            } catch (ProjectNotOpenException e) {
                e.printStackTrace();
            } catch (PackageNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        checkBox = new CheckBox();
        checkBox.setSelected(implemented);
        checkBox.setOnAction(event -> {
            setImplemented(checkBox.isSelected());
            if (checkBox.isSelected()) {
                jumpbutton.setDisable(false);
            } else {
                jumpbutton.setDisable(true);
            }
        });
        gp.add(checkBox, 1, row);
        gp.add(jumpbutton, 2, row);
    }

    @Override
    public String getJavaCode() {
        if (implemented) {
            return gObj.getFieldName() + code;
        } else {
            return "";
        }
    }

    @Override
    public String getFXMLCode() {
        // Currently not implemented, as proper Controller would be needed
        return "";
    }

    public void setImplemented(boolean implemented) {
        this.implemented = implemented;
    }
}
