package bdl.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class will parse the xml file that provides the configuration for every
 * component that the GUI will support, and provides static methods to return
 * all or individual settings per component
 *
 */
public class ComponentSettingsStore {

	private Collection<ComponentSettings> allComponentSettings;
	private Collection<ComponentSettings> externalComponentSettings;

	private void addExternalSettingsToAllSettings() {
		for (ComponentSettings cs : externalComponentSettings) {
			ComponentSettings internCS = findSettings(cs.getType());
			if (internCS != null) {
				this.addSettings(internCS, cs);
			} else {
				System.out.println("+");
				allComponentSettings.add(cs);
			}
		}

	}

	private void addSettings(ComponentSettings internCS, ComponentSettings cs) {
		for (Property pr : cs.getProperties()) {
			if (!containsProperty(internCS, pr)) {
				System.out.println("+");
				internCS.getProperties().add(pr);
			}
		}

		for (ListenerProperty pr : cs.getListenerProperties()) {
			if (!containsListenerProperty(internCS, pr)) {
				System.out.println("+");
				internCS.getListenerProperties().add(pr);
			}
		}

	}

	boolean containsListenerProperty(ComponentSettings internCS, ListenerProperty property) {
		for (ListenerProperty pr : internCS.getListenerProperties()) {
			if (pr.getName().equals(property.getName())) {
				return true;
			}

		}
		return false;
	}

	boolean containsProperty(ComponentSettings internCS, Property property) {
		for (Property pr : internCS.getProperties()) {
			if (pr.getName().equals(property.getName())) {
				return true;
			}

		}
		return false;
	}

	private ComponentSettings findSettings(String type) {
		for (ComponentSettings ac : allComponentSettings) {
			String alltype = ac.getType();
			if (alltype.equals(type)) {
				return ac;
			}
		}
		return null;
	}

	public ComponentSettingsStore(String path) throws Exception {
		allComponentSettings = new ArrayList<>();
		externalComponentSettings = new ArrayList<>();
		if (!parseExternalComponentSettings(allComponentSettings)) {
			parseComponentSettings(path, allComponentSettings);
			parseAdditionalExternalComponentSettings(externalComponentSettings);
			if (!externalComponentSettings.isEmpty()) {
				addExternalSettingsToAllSettings();
			}
		}

	}

	/**
	 * Takes a name of a component and returns the associated Component object
	 * for it.
	 *
	 * @param componentName
	 *            The name of the component to look up
	 * @return The Component file associated with that component or null if no
	 *         component exists with the provided name
	 */
	public ComponentSettings getComponent(String componentName) {
		for (ComponentSettings componentSettings : allComponentSettings) {
			if (componentSettings.getType().equals(componentName)) {
				return componentSettings;
			}
		}
		return null;
	}

	/**
	 * Returns all the components currently supported by the GUI as a collection
	 * of Component objects
	 *
	 * @return A collection of every component in it's Component state
	 */
	public Collection<ComponentSettings> getComponents() {
		return allComponentSettings;
	}

	/**
	 * Returns all the names of components currently supported
	 *
	 * @return A collection of Strings of names of components supported
	 */
	public Collection<String> getComponentNames() {
		ArrayList<String> al = new ArrayList<>();
		for (ComponentSettings cs : allComponentSettings) {
			al.add(cs.getType());
		}
		return al;
	}

	private boolean parseExternalComponentSettings(Collection<ComponentSettings> csettings) throws Exception {

		File file = new File("externalComponentSettings.xml");
		if (!file.exists()) {
			System.out.println("keine externen Settings");
			return false;
		}
		Document document;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		document = builder.parse(file);

		Element root = document.getDocumentElement();

		root.normalize();
		// System.out.print(root);

		NodeList components = root.getElementsByTagName("component");

		for (int i = 0; i < components.getLength(); i++) {
			ComponentSettings componentSettings = new ComponentSettings();
			boolean enabled = parseComponent(componentSettings, (Element) components.item(i));
			if (enabled)
				csettings.add(componentSettings);
		}
		return true;
	}

	private void parseAdditionalExternalComponentSettings(Collection<ComponentSettings> csettings) throws Exception {

		File file = new File("additionalComponentSettings.xml");
		if (!file.exists()) {
			System.out.println("keine weiteren externen Settings");
			return;
		}
		Document document;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		document = builder.parse(file);

		Element root = document.getDocumentElement();

		root.normalize();
		// System.out.print(root);

		NodeList components = root.getElementsByTagName("component");

		for (int i = 0; i < components.getLength(); i++) {
			ComponentSettings componentSettings = new ComponentSettings();
			boolean enabled = parseComponent(componentSettings, (Element) components.item(i));
			if (enabled)
				csettings.add(componentSettings);
		}
	}

	/**
	 * Reads in the xml properties file located at
	 * bdl.model.component-settings.xml and parses the file creating a list of
	 * Component with all properties initialised.
	 * 
	 * @param csettings
	 *            TODO
	 */
	private void parseComponentSettings(String path, Collection<ComponentSettings> csettings) throws Exception {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document d = db.parse(getClass().getResourceAsStream(path));

		Element root = d.getDocumentElement();

		root.normalize();
		// System.out.print(root);

		NodeList components = root.getElementsByTagName("component");

		for (int i = 0; i < components.getLength(); i++) {
			ComponentSettings componentSettings = new ComponentSettings();
			boolean enabled = parseComponent(componentSettings, (Element) components.item(i));
			if (enabled)
				csettings.add(componentSettings);
		}
	}

	private String getString(Element element, String tagname, String defaultText) {
		NodeList nlist = element.getElementsByTagName(tagname);
		return nlist.getLength() > 0 ? nlist.item(0).getTextContent() : defaultText;
	}

	private boolean parseComponent(ComponentSettings componentSettings, Element element) {
		String enabled = getString(element, "enabled", "true");
		if (!getString(element, "enabled", "true").equals("true")) {
			return false;
		}

		componentSettings.setType(getString(element, "type", "UnknownType"));
		componentSettings.setPackageName(getString(element, "package", ""));
		componentSettings.setIcon(getString(element, "icon", ""));

		Element propertiesElement = (Element) element.getElementsByTagName("properties").item(0);
		parseProperties(componentSettings, propertiesElement);
		Element listenerElement = (Element) element.getElementsByTagName("listeners").item(0);
		parseListeners(componentSettings, listenerElement);
		return true;
	}

	private void parseProperties(ComponentSettings componentSettings, Element propertiesElement) {
		if (propertiesElement == null) {
			return;
		}
		NodeList properties = propertiesElement.getElementsByTagName("property");
		if (properties.getLength() > 0) {
			for (int i = 0; i < properties.getLength(); i++) {
				Element property = (Element) properties.item(i);
				String name = getString(property, "name", "");
				String enabled = getString(property, "enabled", "true");
				String pseudotype = getString(property, "pseudotype", "");
				String defaultValue = getString(property, "default", "");
				String fxml = getString(property, "fxml", "");
				String observedProperty = getString(property, "observedProperty", "");
				String javaCodeGeneration = getString(property, "javaCodeGeneration", "");
				String styleProperty = getString(property, "styleProperty", "");
				if (styleProperty.equals("")){
					componentSettings.addProperty(name, enabled, pseudotype, defaultValue, observedProperty, fxml,javaCodeGeneration);
				} else {
				 componentSettings.addStyleProperty(name, enabled, pseudotype, defaultValue, styleProperty);
				}
				
				
			}
		}
	}

	private void parseListeners(ComponentSettings componentSettings, Element listenerElement) {
		if (listenerElement == null) {
			return;
		}
		NodeList listeners = listenerElement.getElementsByTagName("listener");
		if (listeners.getLength() > 0) {
			for (int i = 0; i < listeners.getLength(); i++) {
				Element listener = (Element) listeners.item(i);
				String packageName = getString(listener, "package", "");
				String event = getString(listener, "event", "");
				String isActive = getString(listener, "isActive", "");
				String listenerType = getString(listener, "listenertype", "standard");
				String name = getString(listener, "name", "");
				String method = getString(listener, "method", "");
				String propertyname = getString(listener, "propertyname", "");
				String propertytype = getString(listener, "propertytype", "");
				componentSettings.addListenerProperty(name, method, event, isActive, packageName, listenerType,
						propertyname, propertytype);
			}
		}

	}
}
