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

package net.ownhero.dev.andama.threads;

import static org.junit.Assert.fail;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.settings.Settings;
import net.ownhero.dev.andama.threads.kimtest.KimDemux;
import net.ownhero.dev.andama.threads.kimtest.KimMux;
import net.ownhero.dev.andama.threads.kimtest.KimSink;
import net.ownhero.dev.andama.threads.kimtest.KimSource;
import net.ownhero.dev.andama.threads.kimtest.KimTransformer;

import org.junit.Test;

public class KimTest {
	
	@Test
	public void test() {
		final Settings settings = new Settings();
		final Group group = new Group("me", new Chain(settings) {
			
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
			new Graph(group).buildGraph();
		} catch (final Throwable e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
}
