/*******************************************************************************
 * Copyright 2013 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
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

/**
 * The Class MimeUtils.
 */
public class MimeUtils {
	
	/**
	 * Determine mime.
	 * 
	 * @param data
	 *            the data
	 * @return the string
	 * @throws MIMETypeDeterminationException
	 *             the mIME type determination exception
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
	
	/**
	 * Determine mime.
	 * 
	 * @param uri
	 *            the uri
	 * @return the string
	 * @throws MIMETypeDeterminationException
	 *             the mIME type determination exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws UnsupportedProtocolException
	 *             the unsupported protocol exception
	 * @throws FetchException
	 *             the fetch exception
	 */
	public static String determineMIME(final URI uri) throws MIMETypeDeterminationException,
	                                                 IOException,
	                                                 UnsupportedProtocolException,
	                                                 FetchException {
		
		final byte[] data = IOUtils.binaryfetch(uri);
		return determineMIME(data);
	}
}
