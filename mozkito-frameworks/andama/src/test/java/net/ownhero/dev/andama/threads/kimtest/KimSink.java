package net.ownhero.dev.andama.threads.kimtest;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSink;

public class KimSink extends AndamaSink<Double> {
	
	public KimSink(final AndamaGroup threadGroup, final AndamaSettings settings, final boolean parallelizable) {
		super(threadGroup, settings, parallelizable);
	}
	
}
