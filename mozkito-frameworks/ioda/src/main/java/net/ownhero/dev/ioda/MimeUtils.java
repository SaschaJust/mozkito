package net.ownhero.dev.ioda;

import java.io.IOException;
import java.net.URI;

import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.MIMETypeDeterminationException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

public class MimeUtils {
	
	/**
	 * @param data
	 * @return
	 * @throws MIMETypeDeterminationException
	 */
	public static String determineMIME(final byte[] data) throws MIMETypeDeterminationException {
		MagicMatch match;
		try {
			match = Magic.getMagicMatch(data);
			return match.getMimeType();
		} catch (final MagicParseException e) {
			throw new MIMETypeDeterminationException(e);
		} catch (final MagicMatchNotFoundException e) {
			throw new MIMETypeDeterminationException(e);
		} catch (final MagicException e) {
			throw new MIMETypeDeterminationException(e);
		}
	}
	
	public static String determineMIME(final URI uri) throws MIMETypeDeterminationException,
	                                                 IOException,
	                                                 UnsupportedProtocolException,
	                                                 FetchException {
		
		final byte[] data = IOUtils.binaryfetch(uri);
		return determineMIME(data);
	}
}
