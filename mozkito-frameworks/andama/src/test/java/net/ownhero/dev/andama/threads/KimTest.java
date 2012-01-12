package net.ownhero.dev.andama.threads;

import static org.junit.Assert.fail;
import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.kimtest.KimDemux;
import net.ownhero.dev.andama.threads.kimtest.KimMux;
import net.ownhero.dev.andama.threads.kimtest.KimSink;
import net.ownhero.dev.andama.threads.kimtest.KimSource;
import net.ownhero.dev.andama.threads.kimtest.KimTransformer;

import org.junit.Test;

public class KimTest {
	
	@Test
	public void test() {
		final AndamaSettings settings = new AndamaSettings();
		final AndamaGroup group = new AndamaGroup("me", new AndamaChain(settings) {
			
			@Override
			public void setup() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void shutdown() {
				// TODO Auto-generated method stub
				
			}
		});
		
		new KimSource(group, settings, false);
		new KimMux(group, settings, false);
		new KimTransformer(group, settings, false);
		new KimTransformer(group, settings, false);
		new KimTransformer(group, settings, false);
		new KimDemux(group, settings, false);
		new KimSink(group, settings, false);
		
		try {
			AndamaGraph.buildGraph(group);
		} catch (final Throwable e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
}
