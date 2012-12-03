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
package org.mozkito.issues.tracker;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.ownhero.dev.kisa.Logger;

/**
 * The Class Messages.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class Messages {
	
	/** The Constant BUNDLE_NAME. */
	private static final String         BUNDLE_NAME     = "org.mozkito.issues.tracker.messages"; //$NON-NLS-1$
	                                                                                             
	/** The Constant RESOURCE_BUNDLE. */
	private static final ResourceBundle RESOURCE_BUNDLE = loadBundle();
	
	/**
	 * Gets the string.
	 * 
	 * @param key
	 *            the key
	 * @return the string
	 */
	public static String getString(final String key) {
		try {
			return Messages.RESOURCE_BUNDLE.getString(key);
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
				Logger.warn(String.format("Couldn't find property file for locale '%s'. Falling back to default.",
				                          locale));
			}
			
			return ResourceBundle.getBundle(Messages.BUNDLE_NAME, Locale.US);
		}
	}
	
	/**
	 * Instantiates a new messages.
	 */
	private Messages() {
	}
}
