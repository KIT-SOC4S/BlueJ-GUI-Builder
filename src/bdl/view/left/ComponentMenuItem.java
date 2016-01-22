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

package bdl.view.left;

import bdl.model.ComponentSettings;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class ComponentMenuItem extends Label {

    private ComponentSettings componentSettings;

    public ComponentMenuItem(String s, Node graphic, ComponentSettings componentSettings) {
        super(s, graphic);
        this.componentSettings = componentSettings;
    }

    public ComponentSettings getComponentSettings() {
        return componentSettings;
    }

}
