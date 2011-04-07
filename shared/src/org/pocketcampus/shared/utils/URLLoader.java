package org.pocketcampus.shared.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class URLLoader {
		
	public static String getSource(String url) throws IOException {
		URL page = new URL(url);
		InputStream is = page.openConnection().getInputStream();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b = is.read();
		while(b != -1) {
			baos.write(b);
			b = is.read();
		}
		
		return baos.toString();
	}
	
}
