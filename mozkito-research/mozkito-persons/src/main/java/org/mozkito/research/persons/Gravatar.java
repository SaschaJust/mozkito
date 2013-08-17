/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.research.persons;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import jgravatar.GravatarDownloadException;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.io.IOUtils;

/**
 * The Class Gravatar.
 */
public class Gravatar {
	
	/** The default size. */
	public static int          DEFAULT_SIZE = 120;
	
	/** The Constant BASE_URL. */
	public static final String BASE_URL     = "https://1.gravatar.com/avatar/";
	
	/**
	 * Gets the.
	 * 
	 * @param email
	 *            the email
	 * @return the gravatar
	 */
	public static Gravatar fromEmail(final String email) {
		final jgravatar.Gravatar gravatar = new jgravatar.Gravatar();
		byte[] download = null;
		try {
			download = gravatar.download(email);
		} catch (final GravatarDownloadException e) {
			if (Logger.logWarn()) {
				Logger.warn(e);
			}
		}
		
		if (download != null) {
			return new Gravatar(email, DEFAULT_SIZE, download);
		}
		
		return null;
	}
	
	/**
	 * From hash.
	 * 
	 * @param hash
	 *            the hash
	 * @return the gravatar
	 */
	public static Gravatar fromHash(final String hash) {
		try {
			final URL url = new URL(BASE_URL + hash + "?size=" + DEFAULT_SIZE);
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			final InputStream inputStream = connection.getInputStream();
			
			final byte[] download = IOUtils.toByteArray(inputStream);
			
			if (download != null) {
				return new Gravatar(null, DEFAULT_SIZE, download);
			}
		} catch (final IOException e) {
			return null;
		}
		
		return null;
	}
	
	/**
	 * Gets the.
	 * 
	 * @param url
	 *            the url
	 * @return the gravatar
	 */
	@Deprecated
	public static Gravatar fromURL(final URL url) {
		try {
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			final InputStream inputStream = connection.getInputStream();
			
			final byte[] download = IOUtils.toByteArray(inputStream);
			
			if (download != null) {
				return new Gravatar(null, DEFAULT_SIZE, download);
			}
		} catch (final IOException e) {
			return null;
		}
		
		return null;
	}
	
	/** The email. */
	private String email;
	
	/** The size. */
	private int    size;
	
	/** The data. */
	private byte[] data;
	
	/**
	 * Instantiates a new gravatar.
	 * 
	 * @param email
	 *            the email
	 * @param size
	 *            the size
	 * @param data
	 *            the data
	 */
	private Gravatar(final String email, final int size, final byte[] data) {
		// PRECONDITIONS
		
		try {
			this.email = email;
			this.size = size;
			this.data = data;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Gravatar other = (Gravatar) obj;
		if (!Arrays.equals(this.data, other.data)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the data.
	 * 
	 * @return the data
	 */
	public byte[] getData() {
		// PRECONDITIONS
		
		try {
			return this.data;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.data, "Field '%s' in '%s'.", "data", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the email.
	 * 
	 * @return the email
	 */
	public String getEmail() {
		// PRECONDITIONS
		
		try {
			return this.email;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.email, "Field '%s' in '%s'.", "email", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public int getSize() {
		// PRECONDITIONS
		
		try {
			return this.size;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.size, "Field '%s' in '%s'.", "size", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + Arrays.hashCode(this.data);
		return result;
	}
}
