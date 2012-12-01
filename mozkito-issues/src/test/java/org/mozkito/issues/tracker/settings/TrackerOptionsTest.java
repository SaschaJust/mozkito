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
package org.mozkito.issues.tracker.settings;

import static org.junit.Assert.fail;

import java.util.List;

import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mozkito.issues.tracker.settings.TrackerOptions;


import scala.actors.threadpool.Arrays;

// TODO: Auto-generated Javadoc
/**
 * The Class TrackerOptionsTest.
 *
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class TrackerOptionsTest {
	
	/** The keys. */
	@SuppressWarnings ("unchecked")
	private static List<String> keys = Arrays.asList(new String[] { "tracker.password", "tracker.proxy.cacheDir",
	        "tracker.proxy.host", "tracker.proxy.internal", "tracker.proxy.password", "tracker.proxy.port",
	        "tracker.useProxy", "tracker.proxy.username", "tracker.type", "tracker.uri", "tracker.user" });
	
	/**
	 * Before class.
	 */
	@BeforeClass
	public static void beforeClass() {
		for (final String key : keys) {
			assert (System.getProperty(key) == null);
		}
	}
	
	/**
	 * After.
	 */
	@After
	public void after() {
		for (final String key : TrackerOptionsTest.keys) {
			if (System.getProperty(key) != null) {
				System.clearProperty(key);
			}
		}
	}
	
	/**
	 * Before.
	 */
	@Before
	public void before() {
		for (final String key : TrackerOptionsTest.keys) {
			if (System.getProperty(key) != null) {
				System.clearProperty(key);
			}
		}
	}
	
	/**
	 * Test.
	 *
	 * @throws ArgumentRegistrationException the argument registration exception
	 */
	@Test (expected = ArgumentRegistrationException.class)
	public void test() throws ArgumentRegistrationException {
		try {
			System.setProperty("tracker.useProxy", "true");
			final Settings settings = new Settings();
			ArgumentSetFactory.create(new TrackerOptions(settings.getRoot(), Requirement.required));
			System.err.println(settings);
			fail();
		} catch (final SettingsParseError e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
}
