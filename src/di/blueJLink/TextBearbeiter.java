package di.blueJLink;

public class TextBearbeiter {
	
	public static String ersetzeTextbereich(String startsequenz, String endsequenz, String text, String ersatztext ){
		
		int istart,iende;
		istart = text.indexOf(startsequenz);  
		iende =  text.indexOf(endsequenz); 
		if (istart<0 || iende <0 || istart >=iende){
			return text;
		}
		String prefix = text.substring(0,istart);
		String postfix = text.substring(iende+endsequenz.length());
		
		return prefix+ersatztext+postfix;
	}
	
public static String ersetzeText(String suchtext, String text, String ersatztext ){				
		return text.replaceFirst(suchtext, ersatztext);
}
	
	public static void main(String[] x){
		System.out.println(ersetzeTextbereich("eva","nix","das ist eva und sonst nix mehr","kein apfel für adam"));
		System.out.println(ersetzeText("meine","Alle meine Entchen","deine"));
		System.out.println(ersetzeText("deine","Alle meine Entchen","unsere"));
	}

}
