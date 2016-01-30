package bdl.view.right;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import bdl.build.GObject;
import bdl.build.GUIObject;
import bdl.build.javafx.scene.control.GMenuBar;
import bdl.build.properties.CanvasSizeProperty;
import bdl.build.properties.FieldName;
import bdl.build.properties.GUISizeProperty;
import bdl.build.properties.LayoutProperty;
import bdl.build.properties.ListenerEnabledProperty;
import bdl.build.properties.ListenerHintProperty;
import bdl.build.properties.ObservableNumberListenerEnabledProperty;
import bdl.build.properties.PanelProperty;
import bdl.build.properties.StrokeProperty;
import bdl.lang.LabelGrabber;
import bdl.model.ComponentSettings;
import bdl.model.ListenerHint;
import bdl.model.Property;
import bdl.model.history.HistoryManager;
import di.menubuilder.MenuBuilder;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PropertyEditPane extends GridPane {

	/**
	 * Constructor for "No component selected" pane
	 */
	public PropertyEditPane() {
		add(new Label(LabelGrabber.getLabel("no.component.text")), 0, 0);
	}

	public PropertyEditPane(GUIObject guiObj) {
		int currentRow = 0;
		this.getChildren().clear();
		//this.setMaxWidth(200);
		Label propertiesHeading = new Label(LabelGrabber.getLabel("properties.text") + ":");
		//propertiesHeading.setMinWidth(90);
		propertiesHeading.setFont(Font.font(propertiesHeading.getFont().getFamily(), FontWeight.BOLD,
				propertiesHeading.getFont().getSize() + 0.5));
		add(propertiesHeading, 0, currentRow++);
		GUISizeProperty guisp = new GUISizeProperty(guiObj, guiObj.getGUITitle(), this, currentRow);
	}

	/**
	 * Constructor for gObject pane
	 */
	public PropertyEditPane(GObject gObj, ComponentSettings componentSettings, ArrayList<String> fieldNames,
			GUIObject guiObject, Node settingsNode, HistoryManager historyManager) {
		// For reference, old properties panel: http://i.imgur.com/UBb7P4k.png
		int currentRow = 0;
		this.getChildren().clear();
		//this.setMaxWidth(300);

		Label propertiesHeading = new Label(LabelGrabber.getLabel("properties.text") + ":");
		propertiesHeading.setMinWidth(120);
		propertiesHeading.setFont(Font.font(propertiesHeading.getFont().getFamily(), FontWeight.BOLD,
				propertiesHeading.getFont().getSize() + 0.5));
		add(propertiesHeading, 0, currentRow++);
		
		new FieldName(gObj, fieldNames, componentSettings.getType(), this, currentRow++, historyManager);
        
		List<PanelProperty> panelPropertyList = new ArrayList<>();
		for (Property property : componentSettings.getProperties()) {
			String type = property.getType();
			try {
				Class panelPropertyClass = Class.forName("bdl.build.properties." + type + "Property");
				Constructor constructor = panelPropertyClass.getConstructor(GObject.class, String.class, String.class,String.class,
						String.class, String.class, String.class, GridPane.class, int.class, Node.class,
						HistoryManager.class);
				PanelProperty panelProperty = (PanelProperty) constructor.newInstance(gObj, property.getName(),property.getObservedProperty(),
						property.getGetter(), property.getSetter(), property.getFxml(), property.getDefaultValue(),
						this, currentRow++, settingsNode, historyManager);
				if (panelProperty instanceof LayoutProperty || panelProperty instanceof StrokeProperty
						|| panelProperty instanceof CanvasSizeProperty) {
					currentRow++;
				}
				panelPropertyList.add(panelProperty);
			} catch (Exception e) {
				System.out.println(type + "Property failed.");
				e.printStackTrace();
			}
		}
		for (ListenerHint lhint : componentSettings.getListenerHints()) {
			String name = lhint.getName();
			String text = lhint.getText();
			try {
				PanelProperty panelProperty = new ListenerHintProperty(gObj, guiObject, name, text, this, currentRow++);
				panelPropertyList.add(panelProperty);
				if (lhint.getListenertype().equals("standard")) {
					panelProperty = new ListenerEnabledProperty(gObj, lhint.getListenerMethod(), name,
							lhint.getListenerEvent(), lhint.getDefaultValue(), lhint.getPackageName(), this,
							currentRow++);
				} else if (lhint.getListenertype().equals("observableNumber")) {
					panelProperty = new ObservableNumberListenerEnabledProperty(gObj, lhint.getListenerMethod(), name,
							lhint.getListenerEvent(), lhint.getDefaultValue(), lhint.getPackageName(), this,
							currentRow++);
				}
				panelPropertyList.add(panelProperty);
			} catch (Exception e) {
				System.out.println(name + "Listener failed.");
				e.printStackTrace();
			}
		}
		if (gObj instanceof GMenuBar){
			MenuBuilder mb = ((GMenuBar)gObj).getMenuBuilder();
			this.add(mb.getPane(),0,currentRow,4,6);
		}
		gObj.setPanelProperties(panelPropertyList);
	}
}
