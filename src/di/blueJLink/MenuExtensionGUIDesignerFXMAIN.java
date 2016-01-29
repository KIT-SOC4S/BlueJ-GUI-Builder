package di.blueJLink;

import java.awt.EventQueue;

import bdl.Main;
import bluej.extensions.BlueJ;
import bluej.extensions.Extension;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

public class MenuExtensionGUIDesignerFXMAIN extends Extension {

	@Override
	public String getName() {
		return "GUIDesignerFX";
	}

	@Override
	public String getVersion() {
		return "V1";
	}

	@Override
	public boolean isCompatible() {
		return true;
	}

	@Override
	public void startup(BlueJ bluej) {
		MenueFXGUIDesignerFenster myMenus = new MenueFXGUIDesignerFenster(bluej);
		bluej.setMenuGenerator(myMenus);
	}

	public static void main(String[] s) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new JFXPanel();
					
					
					Platform.runLater(() -> {
						try {
							new Main().start(new Stage());
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
					
					/*
					 * JOptionPane.showInputDialog("Start");
					 * Application.launch(Main.class);
					 */
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
