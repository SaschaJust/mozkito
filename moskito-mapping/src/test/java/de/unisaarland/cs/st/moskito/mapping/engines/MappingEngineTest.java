package de.unisaarland.cs.st.moskito.mapping.engines;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.mapping.settings.MappingSettings;

public class MappingEngineTest {
	
	@Test
	public void test() {
		final BackrefEngine engine = new BackrefEngine();
		engine.setSettings(new MappingSettings());
		final String name = engine.getOptionName("test");
		System.err.println(name);
	}
	
}
