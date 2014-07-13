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
public class PostDataBuilder {
	private List<String> params = new LinkedList<String>();
	public PostDataBuilder() {
	}
	public PostDataBuilder addParam(String key, String val) {
		try {
			key = URLEncoder.encode(key, "UTF-8");
			val = URLEncoder.encode(val, "UTF-8");
			params.add(key + "=" + val);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return this;
	}

	public String toString() {
		return StringUtils.join(params.toArray(), "&");
	}
	public byte[] toBytes() {
		return toString().getBytes();
	}

}
