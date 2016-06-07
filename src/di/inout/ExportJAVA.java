package di.inout;

import java.io.File;
import java.util.Optional;

import bdl.controller.Controller;
import bdl.lang.LabelGrabber;
import bdl.view.View;
import di.blueJLink.Bezeichnertester;
import di.blueJLink.Dateipfade;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;

public class ExportJAVA {
	private static DirectoryChooser dirchooser = new DirectoryChooser();
	private static File basisDirectory = null;
	
	public static void saveJavaFile(View view, Controller controller) {
		String classname="ClassABC";
		if (basisDirectory == null ){
			basisDirectory=new File(".");
		}
		dirchooser.setInitialDirectory(basisDirectory);
		dirchooser.setTitle(LabelGrabber.getLabel("javaexport.directory"));
		basisDirectory=dirchooser.showDialog(null);
		if (basisDirectory == null ){return;}
		boolean inputOK=false;		
		while (!inputOK) {

			TextInputDialog dialog = new TextInputDialog(LabelGrabber.getLabel("bluejlink.dialog.fieldname"));
			dialog.setTitle(LabelGrabber.getLabel("bluejlink.dialog.inputfieldnameheader"));
			dialog.setContentText(LabelGrabber.getLabel("bluejlink.dialog.inputfieldname") + ":");

			
			Optional<String> result = dialog.showAndWait();
			if (!result.isPresent()) {
				return;
			}
			 classname = result.get();

			if (!Bezeichnertester.variablenBezeichnerOK(result.get())) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle(LabelGrabber.getLabel("bluejlink.dialog.infoheader"));
				alert.setHeaderText(null);
				alert.setContentText(result.get() + ":" + LabelGrabber.getLabel("bluejlink.dialog.wrongfieldname"));
				alert.showAndWait();
			} else {
				inputOK = true;
				//controller.generateJavaCode(classname,false);
			}
		}
		
		
		new Dateipfade().erzeugeKlassendatei(basisDirectory.getAbsolutePath(), "", classname, controller.generateJavaCode(classname,false), true, controller);
		
		
	}

}
