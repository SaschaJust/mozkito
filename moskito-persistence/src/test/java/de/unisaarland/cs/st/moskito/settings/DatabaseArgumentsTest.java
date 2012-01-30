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

package de.unisaarland.cs.st.moskito.settings;

import static org.junit.Assert.fail;
import net.ownhero.dev.andama.settings.AndamaSettings;

import org.junit.Test;

public class DatabaseArgumentsTest {
	
	@Test
	public void test() {
		final AndamaSettings settings = new AndamaSettings();
		final DatabaseArguments dbArgs = new DatabaseArguments(settings, true, "persistence");
		
		System.setProperty("database.name", "moskito_junit");
		System.setProperty("database.host", "grid1.st.cs.uni-saarland.de");
		System.setProperty("database.user", "miner");
		System.setProperty("database.password", "miner");
		System.setProperty("database.options", "DB_DROP_CREATE");
		settings.parseArguments();
		
		if (dbArgs.getValue() == null) {
			fail();
		}
	}
	
}
