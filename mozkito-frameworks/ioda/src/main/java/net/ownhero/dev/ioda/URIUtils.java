package net.ownhero.dev.ioda;

import java.net.URI;
import java.net.URISyntaxException;

import net.ownhero.dev.kisa.Logger;

public class URIUtils {
	
	/**
	 * Check if in the given URI the user name is set to the
	 * <code>username</code> argument. If this is not the case, try to replace
	 * the user name info in the authority part with the specified user name.
	 * 
	 * @param address
	 *            The original URI to be checked and modified if necessary
	 * @param username
	 *            the user name to be encoded into the URI
	 * @return the URI with encoded user name. If the encoding fails, the
	 *         original URI will be returned.
	 */
	public static URI encodeUsername(final URI address,
	                                 final String username) {
		// [scheme:][//authority][path][?query][#fragment]
		// [user-info@]host[:port]
		
		if (username == null) {
			return address;
		}
		
		URI uri = address;
		String authority = address.getAuthority();
		if ((address.getUserInfo() == null) || (!address.getUserInfo().equals(username))) {
			if (Logger.logWarn()) {
				Logger.warn("Username provided and username specified in URI are not equal. Using username explicitely provided by method argument.");
			}
			authority = username + "@" + address.getHost();
			if (address.getPort() > -1) {
				authority += ":" + address.getPort();
			}
			StringBuilder uriString = new StringBuilder();
			uriString.append(address.getScheme());
			uriString.append("://");
			uriString.append(authority);
			uriString.append(address.getPath());
			if ((address.getQuery() != null) && (!address.getQuery().equals(""))) {
				uriString.append("?");
				uriString.append(address.getQuery());
			}
			if ((address.getFragment() != null) && (!address.getFragment().equals(""))) {
				uriString.append("#");
				uriString.append(address.getFragment());
			}
			try {
				uri = new URI(uriString.toString());
			} catch (URISyntaxException e1) {
				if (Logger.logError()) {
					Logger.error("Newly generated URI using the specified username cannot be parsed. URI = `"
					        + uriString.toString() + "`");
				}
				if (Logger.logWarn()) {
					Logger.warn("Falling back original URI.");
				}
				uri = address;
			}
		}
		return uri;
	}
	
	/**
	 * Uri2 string converts a URI to a string that conforms RFC 1738. Java
	 * refuses to implement this (see
	 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6351751)
	 * 
	 * @return the string
	 */
	public static String Uri2String(final URI uri) {
		String result = uri.toString();
		if (result.startsWith("file:/") && (!result.startsWith("file:///"))) {
			result = result.substring(6);
			while (result.startsWith("/")) {
				result = result.substring(1);
			}
			result = "file:///" + result;
		}
		return result;
	}
	
}
