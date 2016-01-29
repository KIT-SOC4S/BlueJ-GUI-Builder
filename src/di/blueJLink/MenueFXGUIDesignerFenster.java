
package di.blueJLink;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import bdl.Main;
import bluej.extensions.BClass;
import bluej.extensions.BObject;
import bluej.extensions.BPackage;
import bluej.extensions.BlueJ;
import bluej.extensions.MenuGenerator;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class MenueFXGUIDesignerFenster extends MenuGenerator {
	private BPackage curPackage;
	private BClass curClass;
	private BObject curObject;
	private BlueJ bluej;

	public MenueFXGUIDesignerFenster(BlueJ bluej) {
		super();
		this.bluej = bluej;
		
	}

	public JMenuItem getToolsMenuItem(BPackage aPackage) {
		return new JMenuItem(new SimpleAction("FX GUI Designer"));
	}

	// public JMenuItem getClassMenuItem(BClass aClass) {
	// return new JMenuItem(new SimpleAction("Click Class", "Class menu:"));
	// }
	//               
	// public JMenuItem getObjectMenuItem(BObject anObject) {
	// return new JMenuItem(new SimpleAction("Click Object", "Object menu:"));
	// }

	// These methods will be called when
	// each of the different menus are about to be invoked.
	public void notifyPostToolsMenu(BPackage bp, JMenuItem jmi) {
		curPackage = bp;
		curClass = null;
		curObject = null;
	}

	// public void notifyPostClassMenu(BClass bc, JMenuItem jmi) {
	// System.out.println("Post on Class menu");
	// curPackage = null ; curClass = bc ; curObject = null;
	// }
	//               
	// public void notifyPostObjectMenu(BObject bo, JMenuItem jmi) {
	// System.out.println("Post on Object menu");
	// curPackage = null ; curClass = null ; curObject = bo;
	// }
	//               
	// A utility method which pops up a dialog detailing the objects
	// involved in the current (SimpleAction) menu invocation.
	private void showCurrentStatus(String header) {
		try {
			if (curObject != null)
				curClass = curObject.getBClass();
			if (curClass != null)
				curPackage = curClass.getPackage();

			String msg = header;
			if (curPackage != null)
				msg += "\nCurrent Package = " + curPackage;
			if (curClass != null)
				msg += "\nCurrent Class = " + curClass;
			if (curObject != null)
				msg += "\nCurrent Object = " + curObject;
		} catch (Exception exc) {
		}
	}

	// The nested class that instantiates the different (simple) menus.
	class SimpleAction extends AbstractAction {
     
		public SimpleAction(String menuName) {
			putValue(AbstractAction.NAME, menuName);
			
		}

		public void actionPerformed(ActionEvent anEvent) {	
			
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						 new JFXPanel();
						 Platform.setImplicitExit(false);
					        Platform.runLater(() -> {
					            try {
					            	
									Main m;
									m = new Main();
									m.setBlueJ(bluej);
									m.start(new Stage());
									
							    
									
								} catch (Exception e) {
									e.printStackTrace();
								}
					        });
					
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
//			new Thread() {
//				public void run() {
//					Application.launch(GUIAnwendung.class,null);   
//				}
//			}.start();
		}
	}
	
}
