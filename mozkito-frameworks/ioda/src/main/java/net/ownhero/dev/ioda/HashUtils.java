package net.ownhero.dev.ioda;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
	
	public static byte[] getMD5(String message) throws NoSuchAlgorithmException{
		final MessageDigest md = MessageDigest.getInstance("MD5");
		return md.digest(message.getBytes());
	}
	
}
