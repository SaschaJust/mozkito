/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package org.mozkito.mappings.engines;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;

import org.junit.Assert;
import org.mozkito.mappings.finder.Finder;
import org.mozkito.mappings.settings.MappingOptions;

/**
 * The Class SettingsTest.
 *
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class SettingsTest {
	
	/**
	 * No test.
	 */
	public void noTest() {
		Settings settings = null;
		try {
			settings = new Settings();
		} catch (final SettingsParseError e1) {
			if (Logger.logError()) {
				Logger.error(e1);
			}
			Assert.fail();
		}
		
		if (settings != null) {
			try {
				final ArgumentSet<Finder, MappingOptions> argumentSet = ArgumentSetFactory.create(new MappingOptions(
				                                                                                                     settings.getRoot(),
				                                                                                                     Requirement.required));
				System.err.println(settings.getHelpString());
				System.err.println(settings.toString());
				final Finder finder = argumentSet.getValue();
				
				for (final Engine engine : finder.getEngines().values()) {
					System.err.println(String.format("%s\t%s", engine.getHandle(), engine.getDescription()));
				}
			} catch (final SettingsParseError | ArgumentSetRegistrationException | ArgumentRegistrationException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				System.err.println(settings.toString());
				System.err.println(settings.getHelpString());
				Assert.fail();
			}
		}
		
	}
}
