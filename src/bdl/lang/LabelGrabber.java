package bdl.lang;

import java.io.File;
import java.util.Properties;

import bdl.Main;

/**
 *
 * @author Ben Goodwin
 */
public class LabelGrabber {

	private static Properties lang;
	private File languageFile;

	public LabelGrabber(String language) {
		lang = new Properties();
		// languageFile = new File("lang/gb.lang"); //TODO update later with
		// more languages
		// if(languageFile == null) {
		// System.err.println("Missing language file at: lang/gb.lang");
		// return;
		// }
		// try(BufferedReader reader = new BufferedReader(new
		// InputStreamReader(new FileInputStream(languageFile), "UTF-8"))) {
		// lang.load(reader);
		// }
		// catch(IOException e) {
		// e.printStackTrace();
		// }
		try {
			String filename = "";
			switch (language) {
			case "german":
				filename = "/bdl/lang/de.lang";
				break;
			case "english":
				filename = "/bdl/lang/gb.lang";
				break;
			default:
				filename = "/bdl/lang/gb.lang";
				break;

			}
			lang.load(getClass().getResourceAsStream(filename));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getLabel(String key) {
		if (lang == null) {
			new LabelGrabber(Main.getLanguage());
		}
		String string = lang.getProperty(key);
		if (string == null) {
			System.err.println("Missing value for key: " + key);
			return "";
		}
		return string;
	}
}
