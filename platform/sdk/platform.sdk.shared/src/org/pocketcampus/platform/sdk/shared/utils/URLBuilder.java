package org.pocketcampus.platform.sdk.shared.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;



/**********
 * 
 * @author amer (amer.chamseddine@epfl.ch)
 *
 */
public class URLBuilder {
	private List<String> params = new LinkedList<String>();
	private String mainPart;
	public URLBuilder(String mainPart) {
		this.mainPart = mainPart;
	}
	public URLBuilder addParam(String key, String val) {
		try {
			key = URLEncoder.encode(key, "UTF-8");
			val = URLEncoder.encode(val, "UTF-8");
			params.add(key + "=" + val);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return this;
	}
	@Override
	public String toString() {
		return mainPart + "?" + StringUtils.join(params.toArray(), "&");
	}

}
