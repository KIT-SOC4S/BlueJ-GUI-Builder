/**
 * @author Georg Dick
 */
package di.blueJLink;

import java.io.FileWriter;
import java.util.Optional;

import bdl.lang.LabelGrabber;
import bluej.extensions.BClass;
import bluej.extensions.BPackage;
import bluej.extensions.BProject;
import bluej.extensions.ClassNotFoundException;
import bluej.extensions.MissingJavaFileException;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import bluej.extensions.editor.Editor;
import bluej.extensions.editor.TextLocation;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * @author Georg Dick
 * 
 */
public class BlueJExporter {
	BProject blueJProjekt;

	/**
	 * @param blueJProjekt
	 */
	public BlueJExporter(BProject blueJProjekt, String klassenname, String source) {
		super();
		this.blueJProjekt = blueJProjekt;

		this.exportiereKlasse(klassenname, source);
		try {

		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	public boolean exportiereKlasse(String klasse, String source) {
		// JOptionPane.showMessageDialog(null, klasse);
		boolean vorhanden = false;
		boolean ueberschreiben = false;
		BClass gefunden = null;
		try {

			BPackage bpaket = blueJProjekt.getPackage("");

			try {
				BClass[] vorhandeneKlassen = bpaket.getClasses();
				for (BClass kl : vorhandeneKlassen) {
					if (kl.getName().equals(klasse)) {
						vorhanden = true;
						gefunden = kl;
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setContentText(LabelGrabber.getLabel("bluejexport.condialog1") + " " + klasse + " "
								+ LabelGrabber.getLabel("bluejexport.condialog2") + "\n"
								+ LabelGrabber.getLabel("bluejexport.condialog3"));

						Optional<ButtonType> result = alert.showAndWait();
						ueberschreiben = result.get() == ButtonType.OK;
						break;
					}
				}
				if (vorhanden && !ueberschreiben) {
					return false;
				}
			} catch (PackageNotFoundException e2) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(e2.getMessage());
				alert.showAndWait();
				e2.printStackTrace();
			}

			// if (!(vorhanden && ueberschreiben)) {
			// JOptionPane.showMessageDialog(null, gefunden == null);
			try {
				if (gefunden != null) {
					Dateipfade dp = new Dateipfade();
					try {
						// Alert alert = new Alert(AlertType.INFORMATION);
						// alert.setTitle("Info");
						// alert.setContentText("Sicherheitskopie wird
						// angelegt");
						// alert.showAndWait();
						dp.erzeugeSicherungskopie(bpaket.getDir().getAbsolutePath() + "\\" + klasse + ".java");
						// alert = new Alert(AlertType.INFORMATION);
						// alert.setTitle("Info");
						// alert.setContentText("Sicherheitskopie wurde
						// angelegt");
						// alert.showAndWait();
					} catch (PackageNotFoundException e) {
						// TODO Auto-generated catch block
						// JOptionPane.showMessageDialog(null, "Fehler 0");
						// e.printStackTrace();
					}
					try {
						gefunden.remove();
						// Alert alert = new Alert(AlertType.INFORMATION);
						// alert.setTitle("Info");
						// alert.setContentText("Klasse wurde entfernt");
						// alert.showAndWait();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				}
				FileWriter fi;
				try {
					// JOptionPane.showMessageDialog(null, "Neue datei wird
					// erzeugt:"+bpaket.getDir().getAbsolutePath()
					// + "\\" + klasse + ".java");
					fi = new FileWriter(bpaket.getDir().getAbsolutePath() + "\\" + klasse + ".java");
					fi.close();
					// Alert alert = new Alert(AlertType.INFORMATION);
					// alert.setTitle("Info");
					// alert.setContentText("Klassendatei wurde angelegt");
					// alert.showAndWait();
				} catch (Exception e1) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("Fehler!" + e1.getMessage());
					alert.showAndWait();
					e1.printStackTrace();
					return false;
				}

				// Alert alert = new Alert(AlertType.INFORMATION);
				// alert.setContentText("Neue Klasse wird erzeugt");
				// alert.showAndWait();
				bpaket.newClass(klasse);
				// alert = new Alert(AlertType.INFORMATION);
				// alert.setContentText("Neue Klasse wurde erzeugt");
				// alert.showAndWait();

				this.schreibeInEditor(klasse, source);

			} catch (PackageNotFoundException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(
						"Klasse " + klasse + " wurde nicht angelegt: PackageNotFoundException" + e.getMessage());
				alert.showAndWait();

			} catch (MissingJavaFileException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(
						"Klasse " + klasse + " wurde nicht angelegt: MissingJavaFileException" + e.getMessage());
				alert.showAndWait();

			}
			// }
			// else {
			// Dateipfade dp = new Dateipfade();
			// try {
			// dp.erzeugeSicherungskopie(bpaket.getDir().getAbsolutePath()
			// + "\\" + klasse.getBezeichner() + ".java");
			// } catch (PackageNotFoundException e) {
			// // TODO Auto-generated catch block
			// JOptionPane.showMessageDialog(null, "Fehler 0");
			// e.printStackTrace();
			// }
			//
			// JOptionPane.showMessageDialog(null, "loesche Editor 0");
			// this.loescheUndSchreibeInEditor(klasse);
			//
			// }
		} catch (ProjectNotOpenException e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(
					"Fehler: Klasse " + klasse + " wurde nicht angelegt: ProjectNotOpenException" + e.getMessage());
			alert.showAndWait();

		}
		return false;
	}

	public boolean schreibeInEditor(String klasse, String source) {
		try {
			Alert alert;
			BPackage bpaket = blueJProjekt.getPackage("");
			// alert = new Alert(AlertType.INFORMATION);
			// alert.setContentText("bpaket " + bpaket);
			// alert.showAndWait();
			BClass aktKlasse = bpaket.getBClass(klasse);
			// alert = new Alert(AlertType.INFORMATION);
			// alert.setContentText("aktKlasse " + aktKlasse.getName());
			// alert.showAndWait();
			Editor aktuell = aktKlasse.getEditor();
			// Ohne diese Verzögerungs dialog bleibt das System hängen es sei
			// denn man benutzt Runnable//
			// alert = new Alert(AlertType.INFORMATION);
			// alert.setContentText("Editor " + aktuell);
			// alert.showAndWait();
			// Ohne Runnable gehts nicht sauber

			Platform.runLater(new Runnable() {
				// getLineColumnFromOffset(getTextLength())
				@Override
				public void run() {
//					aktuell.setVisible(true);
//
//					Alert alert = new Alert(AlertType.ERROR);
//					alert.setContentText(aktuell.getTextLength() + " lang");
//					alert.showAndWait();
//					String text = aktuell.getText(new TextLocation(0, 0),
//							aktuell.getTextLocationFromOffset(aktuell.getTextLength()));
//					alert = new Alert(AlertType.ERROR);
//					alert.setContentText(text);
//					alert.showAndWait();

					aktuell.setText(new TextLocation(0, 0), new TextLocation(0, 0), source);
					aktuell.setVisible(true);
//					text = aktuell.getText(new TextLocation(0, 0),
//							aktuell.getTextLocationFromOffset(aktuell.getTextLength()));
//					alert = new Alert(AlertType.ERROR);
//					alert.setContentText(text);
//					alert.showAndWait();
//					aktuell.setText(new TextLocation(0, 0), new TextLocation(0, 0), "otto");
//					text = aktuell.getText(new TextLocation(0, 0),
//							aktuell.getTextLocationFromOffset(aktuell.getTextLength()));
//					alert = new Alert(AlertType.ERROR);
//					alert.setContentText(text);
//					alert.showAndWait();
				}

			});

			// SwingUtilities.invokeLater(new Runnable() {
			// public void run() {
			//
			//
			//
			// aktuell.setText(new TextLocation(0, 0), new TextLocation(0, 0),
			// source);
			// aktuell.setVisible(true);
			//
			// }
			//
			// });

		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Error exporting " + klasse + " " + e.getMessage());
			alert.showAndWait();

			return false;
		}
		return true;
	}

	public boolean loescheUndSchreibeInEditor(String klasse, String source) {
		try {
			BPackage bpaket = blueJProjekt.getPackage("");
			BClass aktKlasse = bpaket.getBClass(klasse);

			Editor aktuell = aktKlasse.getEditor();
			String text = (aktuell.getText(new TextLocation(0, 0),
					new TextLocation(aktuell.getLineCount(), aktuell.getLineLength(aktuell.getLineCount()))));

			// Alert alert = new Alert(AlertType.ERROR);
			// alert.setContentText(text);
			// alert.showAndWait();
			// System.err.println(aktuell.getText(new TextLocation(0, 0), new
			// TextLocation(aktuell.getLineCount(),
			// aktuell.getLineLength(aktuell.getLineCount()))));
			aktuell.setText(new TextLocation(0, 0),
					new TextLocation(aktuell.getLineCount(), aktuell.getLineLength(aktuell.getLineCount())), "");
			aktuell.setText(new TextLocation(0, 0), new TextLocation(0, 0), source);
			aktuell.setVisible(true);
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Error exporting " + klasse);
			alert.showAndWait();
			return false;
		}
		return true;
	}

}
