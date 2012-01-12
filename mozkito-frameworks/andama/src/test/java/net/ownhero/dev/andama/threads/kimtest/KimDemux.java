package net.ownhero.dev.andama.threads.kimtest;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaDemultiplexer;
import net.ownhero.dev.andama.threads.AndamaGroup;

public class KimDemux extends AndamaDemultiplexer<Double> {
	
	public KimDemux(final AndamaGroup threadGroup, final AndamaSettings settings, final boolean parallelizable) {
		super(threadGroup, settings, parallelizable);
	}
	
}
