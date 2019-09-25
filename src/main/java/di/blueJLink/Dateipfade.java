/**
 * @author Georg Dick
 */

package di.blueJLink;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import bdl.controller.Controller;
import bdl.lang.LabelGrabber;
import bluej.extensions.BClass;
import bluej.extensions.BPackage;
import bluej.extensions.ClassNotFoundException;
import bluej.extensions.MissingJavaFileException;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import bluej.extensions.editor.Editor;
import bluej.extensions.editor.TextLocation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

/**
 * @author Georg Dick
 * 
 */

public class Dateipfade {

	private String quelltextBasis;
	private File basisDirectory;

	public boolean kopiere(String srcAbsPath, String targetAbsPath, boolean ueberschreiben) {
		Path srcPath = Paths.get(srcAbsPath);
		Path targetPath = Paths.get(targetAbsPath);
		try {
			if (ueberschreiben) {
				Files.copy(srcPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
			} else {
				Files.copy(srcPath, targetPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean erzeugeDatei(String absPath, String inhalt) {

		Path path = Paths.get(absPath);
		Charset cs = StandardCharsets.UTF_8;
		List<String> ls = new ArrayList<String>();
		String[] text = inhalt.split("/n");
		for (String s : text) {
			ls.add(s);
		}
		try {
			Files.write(path, ls, cs);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean erzeugeOrdner(String absPath) {
		return new File(absPath).mkdir();
	}

	public boolean istOrdner(String absPath) {
		File f = new File(absPath);
		return f.exists() && f.isDirectory();
	}

	public boolean istDatei(String absPath) {
		File f = new File(absPath);
		return f.exists() && f.isFile();
	}

	public boolean existiert(String absPath) {
		return new File(absPath).exists();
	}

	/*
	 * *
	 * 
	 * @return liefert quelltextBasis
	 */
	public String getQuelltextBasis() {
		return quelltextBasis;
	}

	/**
	 * @param quelltextBasis
	 *            setzt quelltextBasis
	 */
	public void setQuelltextBasis(String quelltextBasis) {
		this.quelltextBasis = quelltextBasis;
	}

	public void setQuelltextdirectory(File basisDirectory) {
		this.basisDirectory = basisDirectory;
	}

	public void erzeugePaket(String basisDirectory, String paketname) {
		if (paketname == null || paketname.equals("")) {
			return;
		}

		paketname = paketname.replace('.', '#');// Kleiner Trick, da split mit .
												// nicht geht
		String[] pakethierarchie = paketname.split("#");
		String p = basisDirectory;
		for (String zuwachs : pakethierarchie) {
			p += "\\" + zuwachs;
			// System.out.println(p);
			if (!(this.existiert(p) && this.istOrdner(p))) {
				this.erzeugeOrdner(p);
			}
		}
	}

	public void erzeugeKlassendatei(String basisDirectory, String paketname, String klassenbezeichner,
			String klassenquelltext, boolean fallsDaUeberschreiben, boolean fallsDaKopieren) {

		this.erzeugePaket(basisDirectory, paketname);
		String pfad = basisDirectory;
		if (paketname != null && !paketname.equals("")) {
			pfad += "\\";
			char[] p1 = paketname.toCharArray();
			for (char c : p1) {
				if (c == '.') {
					pfad += "\\";
				} else {
					pfad += c;
				}
			}
		}
		pfad += "\\";
		pfad += klassenbezeichner + ".java";
		if (existiert(pfad)) {
			// System.out.println(pfad + " existiert");
			if (fallsDaKopieren) {
				int i = 1;
				String targetAbsPath = pfad + ".kopie_" + i;
				while (this.existiert(targetAbsPath)) {
					i++;
					targetAbsPath = pfad + ".kopie_" + i;
				}
				this.kopiere(pfad, targetAbsPath, false);
			}
			if (fallsDaUeberschreiben) {
				this.erzeugeDatei(pfad, klassenquelltext);
			}

		} else {
			this.erzeugeDatei(pfad, klassenquelltext);
		}

	}

	public void erzeugeKlassendatei(String basisDirectory, String paketname, String klassenbezeichner,
			String klassenquelltext, boolean fallsDaKopieren, Controller controller) {

		boolean modifizieren = false;

		this.erzeugePaket(basisDirectory, paketname);
		String pfad = basisDirectory;
		if (paketname != null && !paketname.equals("")) {
			pfad += "\\";
			char[] p1 = paketname.toCharArray();
			for (char c : p1) {
				if (c == '.') {
					pfad += "\\";
				} else {
					pfad += c;
				}
			}
		}
		pfad += "\\";
		pfad += klassenbezeichner + ".java";
		if (existiert(pfad)) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setContentText(LabelGrabber.getLabel("bluejexport.condialog1") + " " + klassenbezeichner + " "
					+ LabelGrabber.getLabel("bluejexport.condialog2") + "\n"
					+ LabelGrabber.getLabel("bluejexport.condialog3"));
			ButtonType buttonTypeOverwrite = new ButtonType(LabelGrabber.getLabel("bluejexport.button.replace"));
			ButtonType buttonTypeModify = new ButtonType(LabelGrabber.getLabel("bluejexport.button.modify"));
			ButtonType buttonTypeCancel = new ButtonType(LabelGrabber.getLabel("bluejexport.button.cancel"),
					ButtonData.CANCEL_CLOSE);

			alert.getButtonTypes().setAll(buttonTypeOverwrite, buttonTypeModify, buttonTypeCancel);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeModify) {
				modifizieren = true;
			} else if (result.get() == buttonTypeCancel) {
				return;
			}
			if (modifizieren) {
				Reader reader = null;
				String existingCode = "";
				try {
					reader = new FileReader(pfad);
					int c;
					while (true) {
						c = reader.read();
						if (c == -1) {
							break;
						}
						existingCode += (char) c;
					}
					klassenquelltext = controller.getModifiedJavaCode(klassenbezeichner, existingCode);
				} catch (IOException e) {
					System.err.println("Fehler beim Lesen der Datei!");
				} finally {
					try {
						reader.close();
					} catch (Exception e) {
					}

				}

			}
			if (fallsDaKopieren) {
				int i = 1;
				String targetAbsPath = pfad + LabelGrabber.getLabel("output.fileexists.copystring") + i;
				while (this.existiert(targetAbsPath)) {
					i++;
					targetAbsPath = pfad + LabelGrabber.getLabel("output.fileexists.copystring") + i;
				}
				this.kopiere(pfad, targetAbsPath, false);
			}

		}

		this.erzeugeDatei(pfad, klassenquelltext);

	}

	public void erzeugeKlassendatei_(String basisDirectory, String paketname, String klassenbezeichner,
			String klassenquelltext, boolean fallsDaKopieren, Controller controller) {

		this.erzeugePaket(basisDirectory, paketname);
		String pfad = basisDirectory;
		if (paketname != null && !paketname.equals("")) {
			pfad += "\\";
			char[] p1 = paketname.toCharArray();
			for (char c : p1) {
				if (c == '.') {
					pfad += "\\";
				} else {
					pfad += c;
				}
			}
		}
		pfad += "\\";
		pfad += klassenbezeichner + ".java";
		if (existiert(pfad)) {
			// System.out.println(pfad + " existiert");
			// Localisation.setUebersetzung("Die Datei ist bereits vorhanden.
			// Soll sie überschrieben werden?","File exists.
			// Overwrite?","englisch");
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setContentText(LabelGrabber.getLabel("output.fileexists.question") + "?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() != ButtonType.OK) {
				return;
			}
			if (fallsDaKopieren) {
				int i = 1;
				String targetAbsPath = pfad + LabelGrabber.getLabel("output.fileexists.copystring") + i;
				while (this.existiert(targetAbsPath)) {
					i++;
					targetAbsPath = pfad + LabelGrabber.getLabel("output.fileexists.copystring") + i;
				}
				this.kopiere(pfad, targetAbsPath, false);
			}

		}
		this.erzeugeDatei(pfad, klassenquelltext);

	}

	public void erzeugeSicherungskopie(String pfad) {
		if (existiert(pfad)) {
			int i = 1;
			String targetAbsPath = pfad + LabelGrabber.getLabel("output.fileexists.copystring") + i;
			while (this.existiert(targetAbsPath)) {
				i++;
				targetAbsPath = pfad + LabelGrabber.getLabel("output.fileexists.copystring") + i;
			}
			this.kopiere(pfad, targetAbsPath, false);
		}
	}

	public static void main(String[] s) {
		// String basis = ExportzielWaehlenDialog.getExportielDurchDialog(null,
		// 10, 10);
		// Dateipfade d = new Dateipfade();
		// // d.erzeugePaket(basis, "otto.fritz.src.marga");
		// d.erzeugeKlassendatei(basis, "otto.fritz2", "Klasse",
		// "Das ist ein noch Test", true, true);
	}
}
