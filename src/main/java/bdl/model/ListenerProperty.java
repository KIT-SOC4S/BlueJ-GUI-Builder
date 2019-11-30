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

package bdl.model;

import bdl.lang.LabelGrabber;

/**
 * @author Ben Goodwin
 */
public class ListenerProperty {
    private String listenerName;
    private String listenerMethod;
    private String listenerEvent;
    private String listenerText;

    public ListenerProperty(String name, String method, String event) {
        listenerName = name;
        listenerMethod = method;
        listenerEvent = event;
        listenerText = buildText();
    }

    public String getName() {
        return listenerName;
    }

    public String getText() {
        return listenerText;
    }

    private String buildText() {
        return "." + listenerMethod + "(" + listenerEvent.replaceFirst(".", String.valueOf(listenerEvent.charAt(0)).toLowerCase()) + " -> {\n" + LabelGrabber.getLabel("generatedCodePlaceholder") + "\n});";
    }
}