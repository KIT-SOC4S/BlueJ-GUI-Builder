/**
	@author Georg Dick
 */
package di.errorlog;



import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;

public class StringOutputStream extends OutputStream {
    private Fehlerausgabe fa;
 
    public StringOutputStream( Fehlerausgabe pfa ) {
        fa=pfa;
    }    
  
    public void write( int b ) throws IOException {
        fa.ergaenzeFehlermeldungen( ( char )b ) ;
    }  
}