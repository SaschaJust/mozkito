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
package org.mozkito.settings;

import static org.junit.Assert.fail;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.junit.Ignore;
import org.junit.Test;
import org.mozkito.persistence.PersistenceUtil;

/**
 * The Class DatabaseArguments_NetTest.
 */
public class DatabaseArguments_NetTest {
	
	/**
	 * Test.
	 * 
	 * @throws SettingsParseError
	 *             the settings parse error
	 */
	@Test
	@Ignore
	public void test() throws SettingsParseError {
		
		System.setProperty("database.name", "mozkito_junit"); //$NON-NLS-1$ //$NON-NLS-2$
		System.setProperty("database.host", "grid1.st.cs.uni-saarland.de"); //$NON-NLS-1$ //$NON-NLS-2$
		System.setProperty("database.user", "miner"); //$NON-NLS-1$ //$NON-NLS-2$
		System.setProperty("database.password", "miner"); //$NON-NLS-1$ //$NON-NLS-2$
		System.setProperty("database.options", "CREATE"); //$NON-NLS-1$ //$NON-NLS-2$
		
		final Settings settings = new Settings();
		
		try {
			final DatabaseOptions dbArgs = new DatabaseOptions(settings.getRoot(), Requirement.required, "persistence"); //$NON-NLS-1$
			dbArgs.requirements(settings.getRoot());
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
