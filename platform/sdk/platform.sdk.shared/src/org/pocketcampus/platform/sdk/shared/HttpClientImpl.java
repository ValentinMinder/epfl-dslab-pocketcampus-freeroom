package org.pocketcampus.platform.sdk.shared;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Implementation of HttpClient.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class HttpClientImpl implements HttpClient {
	@Override
	public String getString(String url, Charset charset)
			throws Exception {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new URL(url).openStream(), charset.name());
			// HACK: "\\A" == "beginning of input"
			scanner.useDelimiter("\\A"); 
			return scanner.hasNext() ? scanner.next() : "";
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}
}