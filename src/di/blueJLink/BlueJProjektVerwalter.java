/**
 * @author Georg Dick
 */
package di.blueJLink;


import javax.swing.JFileChooser;

import bdl.lang.LabelGrabber;
import bluej.extensions.BPackage;
import bluej.extensions.BProject;
import bluej.extensions.BlueJ;
import bluej.extensions.editor.Editor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class BlueJProjektVerwalter implements BlueJInterface {
	private BlueJ blueJ = null;
	private BProject aktuellesProjekt = null;

	/**
	 * @param blueJ
	 */
	public BlueJProjektVerwalter(BlueJ blueJ) {
		super();
		this.blueJ = blueJ;
	}

	/**
	 * @return liefert blueJ
	 */
	public BlueJ getBlueJ() {
		return blueJ;
	}

	/**
	 * @param blueJ
	 *            setzt blueJ
	 */
	public void setBlueJ(BlueJ blueJ) {
		this.blueJ = blueJ;
	}
	/**
	 * @see di.blueJLink.BlueJInterface#getBlueJProjekte()
	 */
	@Override
	public BProject[] getBlueJProjekte() {		
			return getBlueJ().getOpenProjects();			
	}
	/**
	 * @return liefert aktuellesProjekt
	 */
	public BProject getAktuellesProjekt() {
		return aktuellesProjekt;
	}

	/**
	 * @param aktuellesProjekt
	 *            setzt aktuellesProjekt
	 */
	public void setAktuellesProjekt(BProject aktuellesProjekt) {
		this.aktuellesProjekt = aktuellesProjekt;
	}

	public BPackage erzeugePaket(String paketname) {
		// TODO
		return null;

	}

	public boolean erzeugeKlasse(String klassenname) {
		// TODO
		return false;
	}

	public Editor oeffneEditorFuerKlasse(String klassenname) {
		// TODO
		return null;
	}

	final JFileChooser fc = new JFileChooser();
	String dateiname;

	/**
	 * @see di.blueJLink.BlueJInterface#erzeugeProjekt(java.lang.String)
	 */
	@Override
	public boolean erzeugeProjekt() {
		aktuellesProjekt = null;
		try {
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showDialog(null, "Projekt anlegen");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				dateiname = fc.getSelectedFile().getAbsolutePath();

				if (fc.getSelectedFile().exists()) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setContentText("Eine Datei oder ein Verzeichnis\nmit diesem Namen ist schon vorhanden");

					alert.showAndWait();
					

				} else {
					aktuellesProjekt = this.blueJ.newProject(fc
							.getSelectedFile());
				}
			}
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText(e.getMessage());

			alert.showAndWait();
			
			return false;
		}
		return aktuellesProjekt != null;
	}

	@Override
	public boolean oeffneProjekt() {
		aktuellesProjekt = null;
		try {
			
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showDialog(null, LabelGrabber.getLabel("menu.bluej.openproject"));
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				dateiname = fc.getSelectedFile().getAbsolutePath();
				//JOptionPane.showMessageDialog(null, dateiname);
				if (fc.getSelectedFile().exists()) {
					aktuellesProjekt = this.blueJ.openProject(fc.getSelectedFile());
					} else {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setContentText(LabelGrabber.getLabel("bluejexport.missingproject"));

						alert.showAndWait();
						
								
				}
			}
		} catch (Exception e) {
			return false;
		}
		//JOptionPane.showMessageDialog(null, (aktuellesProjekt != null)+"");
		return aktuellesProjekt != null;
	}

	/**
	 * @see di.blueJLink.BlueJInterface#schreibeTextInEditor(java.lang.String)
	 */
	@Override
	public void schreibeTextInEditor(String string) {
		// TODO Auto-generated method stub

	}

}
