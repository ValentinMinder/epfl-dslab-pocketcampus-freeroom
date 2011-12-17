package org.pocketcampus.platform.sdk.shared.utils;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.regex.Pattern;

public class StringUtils {
	private static HashMap<String,String> htmlEntities_;

	static {
		htmlEntities_ = new HashMap<String,String>();
		htmlEntities_.put("&lt;","<")    ; htmlEntities_.put("&gt;",">");
		htmlEntities_.put("&amp;","&")   ; htmlEntities_.put("&quot;","\"");
		htmlEntities_.put("&agrave;","à"); htmlEntities_.put("&Agrave;","À");
		htmlEntities_.put("&acirc;","â") ; htmlEntities_.put("&auml;","ä");
		htmlEntities_.put("&Auml;","Ä")  ; htmlEntities_.put("&Acirc;","Â");
		htmlEntities_.put("&aring;","å") ; htmlEntities_.put("&Aring;","Å");
		htmlEntities_.put("&aelig;","æ") ; htmlEntities_.put("&AElig;","Æ" );
		htmlEntities_.put("&ccedil;","ç"); htmlEntities_.put("&Ccedil;","Ç");
		htmlEntities_.put("&eacute;","é"); htmlEntities_.put("&Eacute;","É" );
		htmlEntities_.put("&egrave;","è"); htmlEntities_.put("&Egrave;","È");
		htmlEntities_.put("&ecirc;","ê") ; htmlEntities_.put("&Ecirc;","Ê");
		htmlEntities_.put("&euml;","ë")  ; htmlEntities_.put("&Euml;","Ë");
		htmlEntities_.put("&iuml;","ï")  ; htmlEntities_.put("&Iuml;","Ï");
		htmlEntities_.put("&ocirc;","ô") ; htmlEntities_.put("&Ocirc;","Ô");
		htmlEntities_.put("&ouml;","ö")  ; htmlEntities_.put("&Ouml;","Ö");
		htmlEntities_.put("&oslash;","ø") ; htmlEntities_.put("&Oslash;","Ø");
		htmlEntities_.put("&szlig;","ß") ; htmlEntities_.put("&ugrave;","ù");
		htmlEntities_.put("&Ugrave;","Ù"); htmlEntities_.put("&ucirc;","û");
		htmlEntities_.put("&Ucirc;","Û") ; htmlEntities_.put("&uuml;","ü");
		htmlEntities_.put("&Uuml;","Ü")  ; htmlEntities_.put("&nbsp;"," ");
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

	public static String removeAccents(String string) {
		String temp = Normalizer.normalize(string, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(temp).replaceAll("");
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
	 * Unescape the the HTML characters from a String, eg "&agrave;" to "à".
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

	/**
	 * Returns the first substring from <code>text</code> start with <code>start</code> and
	 * ending with <code>end</code>, both excluded.
	 * @param text
	 * @param start
	 * @param end
	 * @return
	 */
	public static String stringBetween(String text, String start, String end) {
		int startIndex = text.indexOf(start) + start.length();
		int endIndex = text.indexOf(end);

		if(startIndex>0 && endIndex>0 && startIndex<endIndex) {
			return text.substring(startIndex, endIndex);
		}

		return "";
	}
}













