/**
 * 
 */
package blueJLink;

import java.io.FileWriter;
import java.util.Optional;

import javax.swing.JOptionPane;

import bluej.extensions.BClass;
import bluej.extensions.BPackage;
import bluej.extensions.BProject;
import bluej.extensions.ClassNotFoundException;
import bluej.extensions.MissingJavaFileException;
import bluej.extensions.PackageNotFoundException;
import bluej.extensions.ProjectNotOpenException;
import bluej.extensions.editor.Editor;
import bluej.extensions.editor.TextLocation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * @author Georg Dick
 * 
 */
public class BlueJExporter {
	BProject blueJProjekt;
	private String source;

	/**
	 * @param blueJProjekt
	 */
	public BlueJExporter(BProject blueJProjekt, String klassenname, String source) {
		super();
		this.blueJProjekt = blueJProjekt;
		
		this.exportiereKlasse(klassenname,source);
		try {
			
		} catch (Exception e) {			
			// e.printStackTrace();
		}
	}

	

	public boolean exportiereKlasse(String klasse, String source) {
//		JOptionPane.showMessageDialog(null, klasse);
		boolean vorhanden=false;
		boolean ueberschreiben = false;
		BClass gefunden=null;
		try {

			BPackage bpaket = blueJProjekt
					.getPackage("");
		
			try {
				BClass[] vorhandeneKlassen = bpaket.getClasses();
				for (BClass kl : vorhandeneKlassen) {
					if (kl.getName().equals(klasse)) {
						vorhanden=true;
						gefunden = kl;
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Confirmation Dialog");
//						alert.setHeaderText("Look, a Confirmation Dialog");
						alert.setContentText("Die Klasse "
								+ klasse
								+ " ist vorhanden.\n"
								+ "Soll sie überschrieben werden?");

						Optional<ButtonType> result = alert.showAndWait();
						ueberschreiben= result.get() ==  ButtonType.OK;
						break;
					}
				}
				if (vorhanden && !ueberschreiben){
					return false;
				}
			} catch (PackageNotFoundException e2) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, e2.getMessage());
				e2.printStackTrace();
			}

			//if (!(vorhanden && ueberschreiben)) {
			//JOptionPane.showMessageDialog(null, gefunden == null);
				try {
					if (gefunden!=null){
						
						Dateipfade dp = new Dateipfade();
						try {
							dp.erzeugeSicherungskopie(bpaket.getDir().getAbsolutePath()
									+ "\\" + klasse + ".java");
						} catch (PackageNotFoundException e) {
							// TODO Auto-generated catch block
							//JOptionPane.showMessageDialog(null, "Fehler 0");
							//e.printStackTrace();
						}
						try {
							gefunden.remove();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}
					}
					FileWriter fi;
					try {
//						JOptionPane.showMessageDialog(null, "Neue datei wird erzeugt:"+bpaket.getDir().getAbsolutePath()
//								+ "\\" + klasse + ".java");
						fi = new FileWriter(bpaket.getDir().getAbsolutePath()
								+ "\\" + klasse + ".java");
						fi.close();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, "Fehler!"); 
						return false;
					}
//					JOptionPane.showMessageDialog(null, "Neue Klasse wird erzuegt");
					bpaket.newClass(klasse);
//					JOptionPane.showMessageDialog(null, "Neue Klasse wurde erzuegt");
					this.schreibeInEditor(klasse,source);
//					JOptionPane.showMessageDialog(null, "Neuer Quelltext wird erzuegt"+source);
				} catch (PackageNotFoundException e) {
					JOptionPane
							.showMessageDialog(
									null,
									"Klasse "
											+ klasse
											+ " wurde nicht angelegt: PackageNotFoundException"
											+ e.getMessage());
				} catch (MissingJavaFileException e) {
					JOptionPane
							.showMessageDialog(
									null,
									"Klasse "
											+ klasse
											+ " wurde nicht angelegt: MissingJavaFileException"
											+ e.getMessage());
				}
//			}
//		else {
//				Dateipfade dp = new Dateipfade();
//				try {
//					dp.erzeugeSicherungskopie(bpaket.getDir().getAbsolutePath()
//							+ "\\" + klasse.getBezeichner() + ".java");
//				} catch (PackageNotFoundException e) {
//					// TODO Auto-generated catch block
//					JOptionPane.showMessageDialog(null, "Fehler 0");
//					e.printStackTrace();
//				}
//				
//				JOptionPane.showMessageDialog(null, "loesche Editor 0");
//				this.loescheUndSchreibeInEditor(klasse);
//				
//			}
		} catch (ProjectNotOpenException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Fehler: Klasse " + klasse
							+ " wurde nicht angelegt: ProjectNotOpenException"
							+ e.getMessage());
		}
		return false;
	}

	public boolean schreibeInEditor(String klasse, String source) {
		try {
			BPackage bpaket = blueJProjekt
					.getPackage("");
			BClass aktKlasse = bpaket.getBClass(klasse);
			Editor aktuell = aktKlasse.getEditor();
			aktuell.setText(new TextLocation(0, 0), new TextLocation(0, 0),
					source);
			aktuell.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Fehler beim Export der Klasse "+klasse);
			return false;
		}
		return true;
	}

	public boolean loescheUndSchreibeInEditor(String klasse, String source) {
		try {
			BPackage bpaket = blueJProjekt
					.getPackage("");
			BClass aktKlasse = bpaket.getBClass(klasse);
			
			Editor aktuell = aktKlasse.getEditor();
			aktuell.setText(
					new TextLocation(0, 0),
					new TextLocation(aktuell.getLineCount(), aktuell
							.getLineLength(aktuell.getLineCount())), "");
			aktuell.setText(new TextLocation(0, 0), new TextLocation(0, 0),
					source);
			aktuell.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Fehler beim Export der Klasse "+klasse);
			return false;
		}
		return true;
	}

}
