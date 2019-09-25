package bdl.build.properties;

import bdl.build.GObject;

public class StyleUtility {
	public static String getChangedStyle(GObject gObj,String style, String value) {
		if (value == null || value.isEmpty()) {
			// remove Style
			String oldStyle = gObj.getStyle();
			if (oldStyle == null || oldStyle.isEmpty()) {
				return oldStyle;
			}
			String[] oldStyles = oldStyle.split(";");
			style = style.replace(" ", "");
			String newStyle = "";
			for (int i = 0; i < oldStyles.length; i++) {
				if (!oldStyles[i].contains(style)) {
					newStyle = newStyle + oldStyles[i] + ";";
				}
			}
			return newStyle;

		} else {
			// replace or add
			String oldStyle = gObj.getStyle();
			if (oldStyle == null || oldStyle.isEmpty()) {
				return style + ":" + value + ";";
			}
			String[] oldStyles = oldStyle.split(";");
			style = style.replace(" ", "");
			String newStyle = "";
			for (int i = 0; i < oldStyles.length; i++) {
				if (!oldStyles[i].contains(style)) {
					newStyle = newStyle + oldStyles[i] + ";";
				}
			}
			newStyle = newStyle + style + ":" + value + ";";
			return newStyle;
		}
	}

	public static String getStylevalue(GObject gObj,String style) {
		// remove Style
		String value = "";
		String styles = gObj.getStyle();
		if (styles == null || styles.isEmpty()) {
			return value;
		}
		String[] stylearray = styles.split(";");
		style = style.replace(" ", "");

		for (int i = 0; i < stylearray.length; i++) {
			if (stylearray[i].replace(" ", "").contains(style)) {
				String s = stylearray[i];
				value = s.substring(s.indexOf(':') + 1, s.length());
				if (value.endsWith(";")) {
					value = value.substring(0, value.length() - 1);
				}
			}
		}
		return value;
	}

	public static String getStylevalue(String style, String styles) {
		// remove Style
		String value = "";

		if (styles == null || styles.isEmpty()) {
			return value;
		}
		String[] stylearray = styles.split(";");
		style = style.replace(" ", "");

		for (int i = 0; i < stylearray.length; i++) {
			if (stylearray[i].replace(" ", "").contains(style)) {
				String s = stylearray[i];
				value = s.substring(s.indexOf(':') + 1, s.length());
				if (value.endsWith(";")) {
					value = value.substring(0, value.length() - 1);
				}
			}
		}
		return value;
	}
}
