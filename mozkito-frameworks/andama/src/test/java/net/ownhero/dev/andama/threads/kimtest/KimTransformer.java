package net.ownhero.dev.andama.threads.kimtest;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;

public class KimTransformer extends AndamaTransformer<Integer, Double> {
	
	public KimTransformer(final AndamaGroup threadGroup, final AndamaSettings settings, final boolean parallelizable) {
		super(threadGroup, settings, parallelizable);
		
	}
	
}
