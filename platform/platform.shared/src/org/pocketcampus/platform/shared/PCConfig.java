package org.pocketcampus.platform.shared;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/** Abstraction over configuration files. */
public class PCConfig extends Properties {
	public static final long serialVersionUID = 41216938188L;

	public static PCConfig getConfigFromFile(String f) {
		try {
			PCConfig cfg = new PCConfig();
			if (new File(f).exists()) {
				cfg.load(new FileInputStream(f));
			}
			return cfg;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getString(final String k) {
		return (String) get((Object) k);
	}

	public Integer getInteger(final String k) {
		return (k != null ? Integer.parseInt((String) get((Object) k)) : null);
	}
}