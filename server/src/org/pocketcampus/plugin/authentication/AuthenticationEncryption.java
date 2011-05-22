package org.pocketcampus.plugin.authentication;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class AuthenticationEncryption {
	private final String algorithme_;

	public AuthenticationEncryption(String algorithme) {
		algorithme_ = algorithme;
	}

	public String encrypt(String text, String key) throws Exception {
		return operate(text, key, Cipher.ENCRYPT_MODE);
	}

	public String decrypt(String code, String key) throws Exception {
		return operate(code, key, Cipher.DECRYPT_MODE);
	}

	private String operate(String text, String key, int mode) throws Exception {
		Key _key = new SecretKeySpec(format(key), algorithme_);
		Cipher cipher = Cipher.getInstance(algorithme_);
		cipher.init(mode, _key);
		byte[] target = (mode == Cipher.DECRYPT_MODE) ? new BASE64Decoder().decodeBuffer(text) : text.getBytes();
		byte[] result = cipher.doFinal(target);
		return (mode == Cipher.ENCRYPT_MODE) ? new BASE64Encoder().encode(result) : new String(result);
	}
	
	private byte[] format(String key) {
		return key.substring(0, 16).getBytes();
	}
}
