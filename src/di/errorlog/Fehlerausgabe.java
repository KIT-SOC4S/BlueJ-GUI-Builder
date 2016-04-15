/**
	@author Georg Dick
    
 */
package di.errorlog;

import java.io.PrintStream;

import bdl.lang.LabelGrabber;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class Fehlerausgabe {
	private Alert alert ;
	private TextArea textArea;
	public Fehlerausgabe() {
		alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(LabelGrabber.getLabel("errorlog.title"));	
		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		GridPane content = new GridPane();
		content.setMaxWidth(Double.MAX_VALUE);
		content.add(textArea, 0, 1);
		alert.getDialogPane().setContent(content);
		PrintStream out = new PrintStream(new StringOutputStream(this));
//		System.setErr(out);
		
	}

	public void show(){
		alert.show();
	}

	public void ergaenzeFehlermeldungen(char f) {
		textArea.appendText(String.valueOf(f));
	}

}
