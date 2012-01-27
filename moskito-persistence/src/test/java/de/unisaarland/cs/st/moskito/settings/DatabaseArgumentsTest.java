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
		System.setProperty("database.options", "DROPIFEXISTS");
		settings.parseArguments();
		
		if (dbArgs.getValue() == null) {
			fail();
		}
	}
	
}
