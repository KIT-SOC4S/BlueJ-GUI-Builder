/**
 * @author Georg Dick
 */
package di.blueJLink;
import java.io.FileWriter;

import javax.swing.JOptionPane;

import bluej.extensions.*;
import bluej.extensions.editor.Editor;
import bluej.extensions.editor.TextLocation;


public class KlassenGeneratorFuerBlueJ implements BlueJInterface{
    BlueJ bluej;
    String klassenname;
	private BClass aktKlasse;
//	private BProject aktuellesProjekt;
    
    public KlassenGeneratorFuerBlueJ(BlueJ b, String klassenname){
    	this.bluej=b;
    	this.klassenname=klassenname;
    	if ((klassenname.toLowerCase()).endsWith(".java")){
    		this.klassenname = klassenname.substring(0,klassenname.lastIndexOf("."));
    	}    	
    	this.erzeugeKlasse(klassenname);
    }
    
    public KlassenGeneratorFuerBlueJ(BlueJ b){
    	this.bluej=b;    	
    }
    
    
    public boolean erzeugeKlasse(String klname){    
//    	JOptionPane.showMessageDialog(null, "erzK-1");
    	this.klassenname = klname;
    	if ((klname.toLowerCase()).endsWith(".java")){
    		this.klassenname = klname.substring(0,klname.lastIndexOf("."));
    	}
//    	JOptionPane.showMessageDialog(null, "erzK0");
    	bluej.getUserConfigDir().getAbsolutePath();
//    	JOptionPane.showMessageDialog(null, "erzK1");
		FileWriter fi;
		try {
			fi = new FileWriter(bluej.getCurrentPackage().getDir().getAbsolutePath()+"\\"+klassenname+".java");
			fi.close();		
		} catch (Exception e1) {
//			JOptionPane.showMessageDialog(null, "23");
			return false;
		}		
		
		try {
			bluej.getCurrentPackage().newClass(klassenname);			
		} catch (Exception e) {
//			JOptionPane.showMessageDialog(null, "3");
			return false;
		} 
//		JOptionPane.showMessageDialog(null, "KEEnde");
		return true;
    }
    
    public boolean schreibeInEditor(String inhalt, String klassenname){
    	try {
			aktKlasse = bluej.getCurrentPackage().getBClass(klassenname);		
			Editor aktuell= aktKlasse.getEditor();
			aktuell.setText(new TextLocation(0,0), new TextLocation(0,0), inhalt);
			aktuell.setVisible(true);
		} catch (ProjectNotOpenException e) {
			return false;
		} catch (PackageNotFoundException e) {
			return false;
		}		
		return true;
    }
    
    public boolean loescheEditor(String klassenname){
    	try {
			aktKlasse = bluej.getCurrentPackage().getBClass(klassenname);		
			Editor aktuell= aktKlasse.getEditor();
			aktuell.setText(new TextLocation(0,0), 
			 new TextLocation(aktuell.getLineCount(),aktuell.getLineLength(aktuell.getLineCount())),
			 "");
		} catch (ProjectNotOpenException e) {
			return false;
		} catch (PackageNotFoundException e) {
			return false;
		}		
    	return true;
    }
    
    
    
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public String getKlassenname() {
		return klassenname;
	}


	@Override
	public void schreibeTextInEditor(String inhalt) {
		try {
			aktKlasse = bluej.getCurrentPackage().getBClass(klassenname);		
			Editor aktuell= aktKlasse.getEditor();
			aktuell.setText(new TextLocation(0,0), new TextLocation(0,0), inhalt);
			aktuell.setVisible(true);
		} catch (ProjectNotOpenException e) {
		} catch (PackageNotFoundException e) {			
		}		
		
		
	}

	/**
	 * @see di.blueJLink.BlueJInterface#erzeugeProjekt(java.lang.String)
	 */
	@Override
	public boolean erzeugeProjekt() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see di.blueJLink.BlueJInterface#oeffneProjekt()
	 */
	@Override
	public boolean oeffneProjekt() {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * @see di.blueJLink.BlueJInterface#getBlueJProjekte()
	 */
	@Override
	public BProject[] getBlueJProjekte() {		
			return getBlueJ().getOpenProjects();			
	}
	/**
	 * @see di.blueJLink.BlueJInterface#getBlueJ()
	 */
	@Override
	public BlueJ getBlueJ() {
		return bluej;
	}

}
