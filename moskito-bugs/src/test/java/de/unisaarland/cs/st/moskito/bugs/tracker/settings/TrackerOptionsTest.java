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
package de.unisaarland.cs.st.moskito.bugs.tracker.settings;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import scala.actors.threadpool.Arrays;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class TrackerOptionsTest {
	
	@SuppressWarnings ("unchecked")
	private static List<String> keys = Arrays.asList(new String[] { "tracker.password", "tracker.proxy.cacheDir",
	        "tracker.proxy.host", "tracker.proxy.internal", "tracker.proxy.password", "tracker.proxy.port",
	        "tracker.proxy.useProxy", "tracker.proxy.username", "tracker.type", "tracker.uri", "tracker.user" });
	
	@BeforeClass
	public static void beforeClass() {
		for (final String key : keys) {
			assert (System.getProperty(key) == null);
		}
	}
	
	@After
	public void after() {
		for (final String key : TrackerOptionsTest.keys) {
			if (System.getProperty(key) != null) {
				System.clearProperty(key);
			}
		}
	}
	
	@Before
	public void before() {
		for (final String key : TrackerOptionsTest.keys) {
			if (System.getProperty(key) != null) {
				System.clearProperty(key);
			}
		}
	}
	
	@Test
	public void test() {
		System.setProperty("tracker.proxy.useProxy", "true");
		System.setProperty("tracker.proxy.internal", "true");
	}
	
}
