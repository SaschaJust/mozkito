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
package de.unisaarland.cs.st.moskito.mapping.engines;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.settings.MappingOptions;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SettingsTest {
	
	@Test
	public void test() {
		try {
			final Settings settings = new Settings();
			final ArgumentSet<MappingFinder, MappingOptions> argumentSet = ArgumentSetFactory.create(new MappingOptions(
			                                                                                                            settings.getRoot(),
			                                                                                                            Requirement.required));
			final MappingFinder finder = argumentSet.getValue();
			
			for (final MappingEngine engine : finder.getEngines().values()) {
				System.err.println(String.format("%s\t%s", engine.getHandle(), engine.getDescription()));
			}
		} catch (final SettingsParseError | ArgumentSetRegistrationException | ArgumentRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			
		}
		
	}
}
