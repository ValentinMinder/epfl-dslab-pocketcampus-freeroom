package org.pocketcampus.platform.sdk.shared;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PCConfig extends Properties {
	
	public static PCConfig getConfigFromFile(String f) {
		try {
			PCConfig cfg = new PCConfig();
			if(new File(f).exists()) {
				cfg.load(new FileInputStream(f));
			}
			return cfg;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static final long serialVersionUID = Long.parseLong("41216938188");
	
	public String getString(String k) {
		return (String) get((Object) k);
	}
	
	public Integer getInteger(String k) {
		return (k != null ? Integer.parseInt((String) get((Object) k)) : null);
	}
	
	public void putString(String k, String v) {
		put((Object) k, (Object) v);
	}
	
	public void putInteger(String k, Integer v) {
		put((Object) k, (Object) (v != null ? v.toString() : null));
	}
	
	public void putStringIfNull(String k, String v) {
		if(get((Object) k) == null)
			put((Object) k, (Object) v);
	}
	
	public void putIntegerIfNull(String k, Integer v) {
		if(get((Object) k) == null)
			put((Object) k, (Object) (v != null ? v.toString() : null));
	}
	
}
