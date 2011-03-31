package org.pocketcampus.shared.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class URLLoader {
	
	public static String getSource(String url) throws IOException {
		URL page = new URL(url);
		InputStream is = page.openConnection().getInputStream();
		
		ArrayList<Byte> bytes = new ArrayList<Byte>(); 
		int bte = is.read();
		while(bte != -1) {
			bytes.add((byte)bte);
			bte = is.read();
		}
		
		byte[] btes = new byte[bytes.size()];
		for (int i = 0; i < btes.length; i++) {
			btes[i] = bytes.get(i);
		}
		
		return new String(btes);
	}
	
}
