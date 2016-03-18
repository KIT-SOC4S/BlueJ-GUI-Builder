/**
 * @author Georg Dick
 */
package di.blueJLink;

import java.io.FileWriter;
import java.util.Optional;

import bdl.controller.Controller;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

/**
 * @author Georg Dick
 * 
 */
public class BlueJExporter {
	BProject blueJProjekt;
	private Controller controller;

	/**
	 * @param blueJProjekt
	 */
	public BlueJExporter(BProject blueJProjekt, String klassenname, String source, Controller controller) {
		this.controller = controller;
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
		boolean modifizieren = false;
		BClass gefundeneKlasse = null;
		try {

			BPackage bpaket = blueJProjekt.getPackage("");

			try {
				BClass[] vorhandeneKlassen = bpaket.getClasses();
				for (BClass kl : vorhandeneKlassen) {
					if (kl.getName().equals(klasse)) {
						vorhanden = true;
						gefundeneKlasse = kl;
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setContentText(LabelGrabber.getLabel("bluejexport.condialog1") + " " + klasse + " "
								+ LabelGrabber.getLabel("bluejexport.condialog2") + "\n"
								+ LabelGrabber.getLabel("bluejexport.condialog3"));
						ButtonType buttonTypeOverwrite = new ButtonType(
								LabelGrabber.getLabel("bluejexport.button.replace"));
						ButtonType buttonTypeModify = new ButtonType(
								LabelGrabber.getLabel("bluejexport.button.modify"));
						ButtonType buttonTypeCancel = new ButtonType(LabelGrabber.getLabel("bluejexport.button.cancel"),
								ButtonData.CANCEL_CLOSE);

						alert.getButtonTypes().setAll(buttonTypeOverwrite, buttonTypeModify, buttonTypeCancel);

						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == buttonTypeOverwrite) {
							ueberschreiben = true;
						} else if (result.get() == buttonTypeModify) {
							modifizieren = true;
						}
						break;
					}
				}

				if (vorhanden && !ueberschreiben && !modifizieren) {
					return false;
				}
//				Alert alert = new Alert(AlertType.INFORMATION);
//				alert.setTitle("Info 1");
//				alert.setContentText(vorhanden + " " + ueberschreiben + " " + modifizieren);
//				alert.showAndWait();

			} catch (PackageNotFoundException e2) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText(e2.getMessage());
				alert.showAndWait();
				e2.printStackTrace();
			}

			try {

				if (gefundeneKlasse != null) {
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

					}
					if (modifizieren) {
						Editor aktuell;
						try {
							aktuell = gefundeneKlasse.getEditor();
							String existingCode = (aktuell.getText(new TextLocation(0, 0),
									aktuell.getTextLocationFromOffset(aktuell.getTextLength())));
							source = controller.getModifiedJavaCode(klasse, existingCode);							
//							Alert alert = new Alert(AlertType.INFORMATION);
//							alert.setTitle("Info");
//							alert.setContentText(source);
//							alert.showAndWait();
						} catch (PackageNotFoundException e) {
							e.printStackTrace();
						}

					}

					try {
						gefundeneKlasse.remove();
						// Alert alert = new Alert(AlertType.INFORMATION);
						// alert.setTitle("Info");
						// alert.setContentText("Klasse wurde entfernt");
						// alert.showAndWait();
					} catch (ClassNotFoundException e) {

					}
				}

				FileWriter fi;
				try {

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
		} catch (ProjectNotOpenException e)	{
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
//			Alert alert;
			BPackage bpaket = blueJProjekt.getPackage("");
			// alert = new Alert(AlertType.INFORMATION);
			// alert.setContentText("bpaket " + bpaket);
			// alert.showAndWait();
			BClass aktKlasse = bpaket.getBClass(klasse);
			// alert = new Alert(AlertType.INFORMATION);
			// alert.setContentText("aktKlasse " + aktKlasse.getName());
			// alert.showAndWait();
			Editor aktuell = aktKlasse.getEditor();
			
			// denn man benutzt Runnable//
			// alert = new Alert(AlertType.INFORMATION);
			// alert.setContentText("Editor " + aktuell);
			// alert.showAndWait();
			// Ohne Runnable gehts nicht sauber

			Platform.runLater(new Runnable() {
				// getLineColumnFromOffset(getTextLength())
				@Override
				public void run() {
					aktuell.setVisible(false);
					//
					// Alert alert = new Alert(AlertType.ERROR);
					// alert.setContentText(aktuell.getTextLength() + " lang");
					// alert.showAndWait();
					// String text = aktuell.getText(new TextLocation(0, 0),
					// aktuell.getTextLocationFromOffset(aktuell.getTextLength()));
					// alert = new Alert(AlertType.ERROR);
					// alert.setContentText(text);
					// alert.showAndWait();

					aktuell.setText(new TextLocation(0, 0), new TextLocation(0, 0), source);
					aktuell.setVisible(true);
					// text = aktuell.getText(new TextLocation(0, 0),
					// aktuell.getTextLocationFromOffset(aktuell.getTextLength()));
					// alert = new Alert(AlertType.ERROR);
					// alert.setContentText(text);
					// alert.showAndWait();
					// aktuell.setText(new TextLocation(0, 0), new
					// TextLocation(0, 0), "otto");
					// text = aktuell.getText(new TextLocation(0, 0),
					// aktuell.getTextLocationFromOffset(aktuell.getTextLength()));
					// alert = new Alert(AlertType.ERROR);
					// alert.setContentText(text);
					// alert.showAndWait();
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

	
}
