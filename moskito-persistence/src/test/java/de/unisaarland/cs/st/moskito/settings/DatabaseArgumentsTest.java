package de.unisaarland.cs.st.moskito.settings;

import static org.junit.Assert.fail;
import net.ownhero.dev.andama.settings.AndamaSettings;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.persistence.OpenJPAUtil;


public class DatabaseArgumentsTest {
	
	@Test
	public void test() {
		AndamaSettings settings = new AndamaSettings();
		DatabaseArguments dbArgs = new DatabaseArguments(settings, true, "persistence");
		
		System.setProperty("database.name", "rcs_branch_test");
		System.setProperty("database.host", "grid1.st.cs.uni-saarland.de");
		System.setProperty("database.user", "miner");
		System.setProperty("database.password", "miner");
		settings.parseArguments();
		
		OpenJPAUtil.createTestSessionFactory("ppa");
		
		if (!dbArgs.getValue()) {
			fail();
		}
	}
	
}
