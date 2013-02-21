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
package org.mozkito.issues.messages;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class Messages.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class Messages {
	
	/** The Constant BUNDLE_NAME. */
	private static final String         BUNDLE_NAME     = "org.mozkito.issues.messages"; //$NON-NLS-1$
	                                                                                     
	/** The Constant RESOURCE_BUNDLE. */
	private static final ResourceBundle RESOURCE_BUNDLE = loadBundle();
	
	/**
	 * Gets the string.
	 * 
	 * @param key
	 *            the key
	 * @param arguments
	 *            the arguments
	 * @return the string
	 */
	public static String getString(final String key,
	                               final Object... arguments) {
		try {
			final String resource = Messages.RESOURCE_BUNDLE.getString(key);
			final String[] split = resource.split("^%s|[^%]%s"); //$NON-NLS-1$
			
			assert (arguments.length + 1) == split.length : "Given number of %s in the resource differs from the actual arguments: " + (split.length - 1) + " vs " + arguments.length; //$NON-NLS-1$ //$NON-NLS-2$
			
			if (split.length > 1) {
				return String.format(resource, arguments);
			} else {
				return resource;
			}
		} catch (final MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	/**
	 * Load bundle.
	 * 
	 * @return the resource bundle
	 */
	private static ResourceBundle loadBundle() {
		final Locale locale = Locale.getDefault();
		try {
			return ResourceBundle.getBundle(Messages.BUNDLE_NAME, locale);
		} catch (final MissingResourceException e) {
			if (Logger.logWarn()) {
				Logger.warn(String.format("Couldn't find property file for locale '%s'. Falling back to default.", //$NON-NLS-1$
				                          locale));
			}
			try {
				return ResourceBundle.getBundle(Messages.BUNDLE_NAME, Locale.US);
			} catch (final MissingResourceException ex) {
				throw new UnrecoverableError("Could not load string table.", ex); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * Instantiates a new messages.
	 */
	private Messages() {
	}
}
