/**
 * 
 */
package blueJLink;


import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


import bluej.extensions.BPackage;
import bluej.extensions.BProject;
import bluej.extensions.BlueJ;
import bluej.extensions.editor.Editor;

/**
 * @author Georg Dick
 * 
 */
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
	 * @see blueJLink.BlueJInterface#getBlueJProjekte()
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
	 * @see blueJLink.BlueJInterface#erzeugeProjekt(java.lang.String)
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
					JOptionPane
							.showMessageDialog(null,
									"Eine Datei oder ein Verzeichnis\nmit diesem Namen ist schon vorhanden");

				} else {
					aktuellesProjekt = this.blueJ.newProject(fc
							.getSelectedFile());
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,e.getMessage());
			return false;
		}
		return aktuellesProjekt != null;
	}

	@Override
	public boolean oeffneProjekt() {
		aktuellesProjekt = null;
		try {
			
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showDialog(null, "Projekt öffnen");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				dateiname = fc.getSelectedFile().getAbsolutePath();
				//JOptionPane.showMessageDialog(null, dateiname);
				if (fc.getSelectedFile().exists()) {
					aktuellesProjekt = this.blueJ.openProject(fc.getSelectedFile());
					} else {
						JOptionPane.showMessageDialog(null,
								"Eine Projektverzeichnis mit diesem Namen ist nicht vorhanden");			
				}
			}
		} catch (Exception e) {
			return false;
		}
		//JOptionPane.showMessageDialog(null, (aktuellesProjekt != null)+"");
		return aktuellesProjekt != null;
	}

	/**
	 * @see blueJLink.BlueJInterface#schreibeTextInEditor(java.lang.String)
	 */
	@Override
	public void schreibeTextInEditor(String string) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see blueJLink.BlueJInterface#getBlueJProjekt()
	 */
//	@Override
//	public BProject getBlueJProjekt() {
//		return aktuellesProjekt;
//	}

}
