package org.pocketcampus.platform.shared;

import java.util.Arrays;
import java.util.HashSet;

public class PCConstants {
	public static final String HTTP_HEADER_PUSHNOTIF_OS = "X-PC-PUSHNOTIF-OS";
	public static final String HTTP_HEADER_PUSHNOTIF_TOKEN = "X-PC-PUSHNOTIF-TOKEN";
	public static final String HTTP_HEADER_AUTH_PCSESSID = "X-PC-AUTH-PCSESSID";
	public static final String HTTP_HEADER_USER_LANG_CODE = "X-PC-LANG-CODE";
	
	public static final HashSet<String> PC_ACCEPTED_LANGUAGES = new HashSet<String>(Arrays.asList("en", "fr"));
	public static final String PC_DEFAULT_LANGUAGE = "en";
}