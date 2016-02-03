package di.inout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javafx.scene.control.ChoiceDialog;

public class LanguageChooser {
	static String language = "";

	public static String getLanguage(String l) {
		Properties einstellungen = new Properties();
		language = l;
		try {
			//try to read ini-File for Languagechoice        	
			String filename = "fxgui.xml";
			File inifile = new File(filename);
			if (inifile.exists()) {
				einstellungen.loadFromXML(new FileInputStream(filename));
				language = einstellungen.getProperty("language");
			} else {
				//otherwise Dialog
				List<String> choices = new ArrayList<>();
				choices.add("english");
				choices.add("german");
				ChoiceDialog<String> dialog = new ChoiceDialog<>("english", choices);
				dialog.setTitle("Language Dialog");
				dialog.setContentText("Choose your language:");
				Optional<String> result = dialog.showAndWait();
				result.ifPresent(choice -> {
					language = choice;
				});
				einstellungen.setProperty("language", language);
				FileOutputStream fos = new FileOutputStream(filename);
				einstellungen.storeToXML(fos, "IniFXGUI");
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return language;
	}

	public static void getLanguageByDialog() {
		Properties einstellungen = new Properties();
		
		try {
			String filename = "fxgui.xml";
			List<String> choices = new ArrayList<>();
			choices.add("english");
			choices.add("german");
			ChoiceDialog<String> dialog = new ChoiceDialog<>("english", choices);
			dialog.setTitle("Language Selection");
			dialog.setContentText("Choose your language:");
			Optional<String> result = dialog.showAndWait();
			result.ifPresent(choice -> {
				language = choice;
			});
			einstellungen.setProperty("language", language);
			FileOutputStream fos = new FileOutputStream(filename);
			einstellungen.storeToXML(fos, "IniFXGUI");
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
		
	}
}
