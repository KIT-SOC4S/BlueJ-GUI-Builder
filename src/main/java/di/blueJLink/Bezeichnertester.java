/**
 * @author Georg Dick
 */package di.blueJLink;

public class Bezeichnertester {
	static String[] javaKeys = {
			"abstract","assert","break","case","catch","class","const","continue",
			"default","do","else","enum","extends","false","final","finally","for","goto",
			"if","implements","import","instanceof","interface","native","new","null","package",
			"private","protected","public","return","static",
			"strictfp","super","switch","synchronized","this","throw","throws","transient","true","try","volatile","while"
			};
	static String[] javaKeysMitDatentypen = {
		"abstract","assert","boolean","break","byte","case","catch","char","class","const","continue",
		"default","do","double","else","enum","extends","false","final","finally","float","for","goto",
		"if","implements","import","instanceof","int","interface","long","native","new","null","package",
		"private","protected","public","return","short","static",
		"strictfp","super","switch","synchronized","this","throw","throws","transient","true","try","void","volatile","while"
		};
	static String[] javaKeysMitDatentypenUndEntwurfstypen = {
		"abstract","assert","boolean","break","byte","case","catch","char","class","const","continue",
		"default","do","double","else","enum","extends","false","final","finally","float","for","goto",
		"if","implements","import","instanceof","int","interface","long","native","new","null","package",
		"private","protected","public","return","short","static",
		"strictfp","super","switch","synchronized","this","throw","throws","transient","true","try","void","volatile","while",
		"Zahl","Zeichenkette","Zeichen","Wahrheitswert"
		};
	

	public static boolean variablenBezeichnerOK(String neu) {
		if (neu == null || neu.equals("") || istJAVAKeywortInclDatentypen(neu,false)){
			return false;
		}
		for (int i = 0; i < neu.length(); i++) {
			if (!erlaubtesZeichen(neu.charAt(i), i)) {
				return false;
			}
		}		
		return true;
	}
	
	public static boolean bezeichnerOK(String neu) {
		if (neu == null || neu.equals("") || istJAVAKeywort(neu)){
			return false;
		}
		for (int i = 0; i < neu.length(); i++) {
			if (!erlaubtesZeichen(neu.charAt(i), i)) {
				return false;
			}
		}		
		return true;
	}
	
	public static boolean bezeichnerMitPaketenOK(String neu) {
		if (neu == null || neu.equals("") || istJAVAKeywort(neu)){
			return false;
		}
		for (String b: neu.split("\\.")){
			if (!bezeichnerOK(b)){
				return false;
			}
		}
		return true;
		
	}
	
	public static boolean bezeichnerMitPaketenOhnePrimtypenOK(String neu, boolean istEntwurf) {
		if (neu == null || neu.equals("") || istJAVAKeywortInclDatentypen(neu, istEntwurf)){
			return false;
		}
		
		for (String b: neu.split("\\.")){
			if (!bezeichnerOK(b)){
				return false;
			}
		}
		return true;
		
	}
	/**
	 * @param neu
	 * @return
	 */
	private  static boolean istJAVAKeywort(String neu) {		
		for (String key: javaKeys){
			if (key.equals(neu)){
				return true;
			}
		}
		return false;
	}
	/**
	 * @param neu
	 * @param istEntwurf TODO
	 * @return
	 */
	private  static boolean istJAVAKeywortInclDatentypen(String neu, boolean istEntwurf) {	
		if (!istEntwurf){
		for (String key: javaKeysMitDatentypen){
			if (key.equals(neu)){
				return true;
			}
		}
		} else {
			for (String key: javaKeysMitDatentypenUndEntwurfstypen){
				if (key.equals(neu)){
					return true;
				}
			}
		}
		return false;
	}
	private static boolean erlaubtesZeichen(char c, int anStelle) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
				|| (c >= '0' && c <= '9' && anStelle > 0) || (c == '_')
				|| c=='\u00F6' || c=='\u00E4'|| c== '\u00FC' || c=='\u00D6' || c=='\u00C4'|| c== '\u00DC';

	}
}
