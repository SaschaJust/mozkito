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

package de.unisaarland.cs.st.mozkito.settings;

import static org.junit.Assert.fail;

import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.junit.Ignore;
import org.junit.Test;

import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.mozkito.settings.DatabaseOptions;

public class DatabaseArguments_NetTest {
	
	@Test
	@Ignore
	public void test() throws SettingsParseError {
		
		System.setProperty("database.name", "moskito_junit");
		System.setProperty("database.host", "grid1.st.cs.uni-saarland.de");
		System.setProperty("database.user", "miner");
		System.setProperty("database.password", "miner");
		System.setProperty("database.options", "CREATE");
		
		final Settings settings = new Settings();
		
		try {
			final DatabaseOptions dbArgs = new DatabaseOptions(settings.getRoot(), Requirement.required, "persistence");
			System.err.println(settings);
			System.err.println(settings.getHelpString());
			System.err.println(dbArgs);
			final Map<String, IOptions<?, ?>> requirements = dbArgs.requirements(settings.getRoot());
			for (final IOptions<?, ?> option : requirements.values()) {
				System.err.println(option);
			}
			final ArgumentSet<PersistenceUtil, DatabaseOptions> argumentSet = ArgumentSetFactory.create(dbArgs);
			
			if (argumentSet.getValue() == null) {
				fail();
			}
		} catch (final Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
}
