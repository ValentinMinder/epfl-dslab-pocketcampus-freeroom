package org.pocketcampus.shared.utils;

import java.util.HashMap;

public class StringUtils {
	private static HashMap<String,String> htmlEntities_;
	static {
		htmlEntities_ = new HashMap<String,String>();
		htmlEntities_.put("&lt;","<")    ; htmlEntities_.put("&gt;",">");
		htmlEntities_.put("&amp;","&")   ; htmlEntities_.put("&quot;","\"");
		htmlEntities_.put("&agrave;","�"); htmlEntities_.put("&Agrave;","�");
		htmlEntities_.put("&acirc;","�") ; htmlEntities_.put("&auml;","�");
		htmlEntities_.put("&Auml;","�")  ; htmlEntities_.put("&Acirc;","�");
		htmlEntities_.put("&aring;","�") ; htmlEntities_.put("&Aring;","�");
		htmlEntities_.put("&aelig;","�") ; htmlEntities_.put("&AElig;","�" );
		htmlEntities_.put("&ccedil;","�"); htmlEntities_.put("&Ccedil;","�");
		htmlEntities_.put("&eacute;","�"); htmlEntities_.put("&Eacute;","�" );
		htmlEntities_.put("&egrave;","�"); htmlEntities_.put("&Egrave;","�");
		htmlEntities_.put("&ecirc;","�") ; htmlEntities_.put("&Ecirc;","�");
		htmlEntities_.put("&euml;","�")  ; htmlEntities_.put("&Euml;","�");
		htmlEntities_.put("&iuml;","�")  ; htmlEntities_.put("&Iuml;","�");
		htmlEntities_.put("&ocirc;","�") ; htmlEntities_.put("&Ocirc;","�");
		htmlEntities_.put("&ouml;","�")  ; htmlEntities_.put("&Ouml;","�");
		htmlEntities_.put("&oslash;","�"); htmlEntities_.put("&Oslash;","�");
		htmlEntities_.put("&szlig;","�") ; htmlEntities_.put("&ugrave;","�");
		htmlEntities_.put("&Ugrave;","�"); htmlEntities_.put("&ucirc;","�");
		htmlEntities_.put("&Ucirc;","�") ; htmlEntities_.put("&uuml;","�");
		htmlEntities_.put("&Uuml;","�")  ; htmlEntities_.put("&nbsp;"," ");
		htmlEntities_.put("&copy;","\u00a9");
		htmlEntities_.put("&reg;","\u00ae");
		htmlEntities_.put("&euro;","\u20a0");
	}


	/**
	 * Formats a number as a String of nbChar characters,
	 * padding it with 0.
	 * @param number
	 * @param nbChar
	 * @return
	 */
	public static String pad(int number, int nbChar) {
		return String.format("%0" + nbChar + "d", number);
	}

	/**
	 * Capitalize a String.
	 * @param
	 * @return
	 */
	public static String capitalize(String s) {
		if (s.length() == 0) return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	/**
	 * Fixes the case of a String
	 * @param input tHE POOrLY CASed String
	 * @return a cleaner String
	 * @status WIP
	 */
	public static String fixCase(String input) {

		if(!input.toUpperCase().equals(input)) {
			if(!input.toLowerCase().equals(input)) {
				// mixed case: probably ok
				return input;
			}
		}

		input = input.toLowerCase();
		String[] words = input.split(" |'|,|-|\n");

		String output = "";
		String word;
		for (int i = 0; i < words.length; i++) {
			word = words[i];

			if(word.length() > 2) {
				word = capitalize(word);
			}

			output += word+" ";
		}

		return output;
	}
	
	/**
	 * Unescape the the HTML characters from a String, eg "&agrave;" to "�".
	 * @param source
	 * @return
	 */
	public static final String unescapeHTML(String source) {
		int i, j;

		boolean continueLoop;
		int skip = 0;
		do {
			continueLoop = false;
			i = source.indexOf("&", skip);
			if (i > -1) {
				j = source.indexOf(";", i);
				if (j > i) {
					String entityToLookFor = source.substring(i, j + 1);
					String value = (String) htmlEntities_.get(entityToLookFor);
					if (value != null) {
						source = source.substring(0, i)
						+ value + source.substring(j + 1);
						continueLoop = true;
					}
					else if (value == null){
						skip = i+1;
						continueLoop = true;
					}
				}
			}
		} while (continueLoop);
		return source;
	}
}













