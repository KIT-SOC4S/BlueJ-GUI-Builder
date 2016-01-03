package blueJLink;





public interface BlueJInterface {
    public boolean erzeugeProjekt();
	public boolean erzeugeKlasse(String klname);
	public void schreibeTextInEditor(String string);
	/**
	 * @return
	 */
	public boolean oeffneProjekt();
	/**
	 * @return
	 */
	public bluej.extensions.BProject[] getBlueJProjekte();
	/**
	 * @return
	 */
	public bluej.extensions.BlueJ getBlueJ();
	

}
