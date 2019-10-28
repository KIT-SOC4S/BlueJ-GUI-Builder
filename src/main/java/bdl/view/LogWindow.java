package bdl.view;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class LogWindow extends Alert {
    private TextArea textArea;
    private OutputStream outputStream;
    private OutputStream outputErrStream;

    public LogWindow(String windowtitle) {
        super(AlertType.INFORMATION);
        this.setTitle(windowtitle);
        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane content = new GridPane();
        content.setMaxWidth(Double.MAX_VALUE);
        content.add(textArea, 0, 1);
        this.getDialogPane().setContent(content);
        this.outputStream = new AreaOutputStream(System.out);
        this.outputErrStream = new AreaOutputStream(System.err);
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public OutputStream getOutputErrStream() {
        return outputErrStream;
    }

    public void appendText(String text) {
        textArea.appendText(text);
    }

    class AreaOutputStream extends OutputStream {
        private PrintStream otherStream;

        AreaOutputStream(PrintStream otherStream) {
            this.otherStream = otherStream;
        }

        @Override
        public void write(int b) throws IOException {
            textArea.appendText(String.valueOf((char) b));
            textArea.end();
            otherStream.write(b);
        }
    }
}
