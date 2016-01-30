package bdl.view.top;

import bdl.lang.LabelGrabber;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class TopPanel extends MenuBar {

	public Menu menuFile;
	public MenuItem mItmClose;
	public MenuItem mItmFullScreen;
	public MenuItem mItmLoadFile;
	public MenuItem mItmSaveFXMLFile;
	public MenuItem mItmSaveJAVAFile;

	public Menu menuEdit;
	public MenuItem mItmUndo;
	public MenuItem mItmRedo;
	public MenuItem mItmDelete;
	public MenuItem mItmLanguage;

	public Menu menuView;
	public CheckMenuItem mItmHistory;
	public CheckMenuItem mItmHierarchy;
	public MenuItem mItmErrorlog;

	public Menu menuHelp;
	public MenuItem mItmAbout;
	public MenuItem mItmClearAll;
	public Menu menuBluej;
	public MenuItem mntmNeuesProjekt;
	public MenuItem mntmProjektffnen;
	public Menu mnClassInProjekt;
	public Menu mnModellAusProjekt;
	public Menu mnModellInProjekt;

	public TopPanel(boolean isBlueJAttached) {
		menuFile = new Menu(LabelGrabber.getLabel("menu.file"));
		mItmLoadFile = new MenuItem(LabelGrabber.getLabel("menu.file.open"));
		mItmSaveFXMLFile = new MenuItem(LabelGrabber.getLabel("menu.file.save"));
		mItmSaveJAVAFile = new MenuItem(LabelGrabber.getLabel("menu.filejava.save"));
		mItmClose = new MenuItem(LabelGrabber.getLabel("menu.file.close"));
		mItmFullScreen = new MenuItem(LabelGrabber.getLabel("fullscreen.enable.text"));

		menuEdit = new Menu(LabelGrabber.getLabel("menu.edit"));
		mItmUndo = new MenuItem(LabelGrabber.getLabel("menu.edit.undo"));
		mItmUndo.setDisable(true);
		mItmUndo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
		mItmRedo = new MenuItem(LabelGrabber.getLabel("menu.edit.redo"));
		mItmRedo.setDisable(true);
		mItmRedo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
		mItmDelete = new MenuItem(LabelGrabber.getLabel("menu.edit.delete"));
		mItmDelete.setDisable(true);
		mItmDelete.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
		mItmClearAll = new MenuItem(LabelGrabber.getLabel("menu.edit.clearall"));
		mItmClearAll.setDisable(true);
		mItmLanguage = new MenuItem(LabelGrabber.getLabel("menu.edit.language"));

		menuView = new Menu(LabelGrabber.getLabel("menu.view"));
		mItmHistory = new CheckMenuItem(LabelGrabber.getLabel("menu.view.history"));
		mItmHistory.setSelected(true);
		mItmHierarchy = new CheckMenuItem(LabelGrabber.getLabel("menu.view.hierarchy"));
		mItmHierarchy.setSelected(true);
		mItmErrorlog = new MenuItem(LabelGrabber.getLabel("menu.view.errorlog"));

		menuHelp = new Menu(LabelGrabber.getLabel("menu.help"));
		mItmAbout = new MenuItem(LabelGrabber.getLabel("menu.help.about"));

		menuBluej = new Menu("BlueJ");
		mntmNeuesProjekt = new MenuItem(LabelGrabber.getLabel("menu.bluej.newproject"));
		mntmProjektffnen = new MenuItem(LabelGrabber.getLabel("menu.bluej.openproject"));
		mnClassInProjekt = new Menu(LabelGrabber.getLabel("menu.bluej.savesource"));
		mnModellInProjekt = new Menu(LabelGrabber.getLabel("menu.bluej.exportfxml"));
		mnModellAusProjekt = new Menu(LabelGrabber.getLabel("menu.bluej.importfxml"));
		if (isBlueJAttached) {
			menuBluej.getItems().addAll(mntmNeuesProjekt, mntmProjektffnen);
			menuBluej.getItems().addAll(mnClassInProjekt, mnModellInProjekt, mnModellAusProjekt);

		}
		menuFile.getItems().addAll(mItmLoadFile, mItmSaveFXMLFile, mItmSaveJAVAFile, mItmFullScreen, mItmClose);

		menuEdit.getItems().addAll(mItmUndo, mItmRedo, mItmDelete, mItmClearAll, mItmLanguage);
		menuView.getItems().addAll(mItmHierarchy, mItmHistory, mItmErrorlog);
		menuHelp.getItems().addAll(mItmAbout);

		getMenus().addAll(menuFile, menuEdit, menuView);
		if (isBlueJAttached) {
			getMenus().addAll(menuBluej);
		}
		getMenus().addAll(menuHelp);
	}
}