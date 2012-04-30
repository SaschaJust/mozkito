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

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import scala.actors.threadpool.Arrays;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class ProxyOptionsTest {
	
	@SuppressWarnings ("unchecked")
	private static List<String> keys = Arrays.asList(new String[] { "proxy.cacheDir", "proxy.host", "proxy.internal",
	        "proxy.password", "proxy.port", "proxy.useProxy", "tracker.proxy.username" });
	
	private static File         cacheDir;
	
	@BeforeClass
	public static void beforeClass() {
		for (final String key : keys) {
			assert (System.getProperty(key) == null);
		}
		cacheDir = FileUtils.createRandomDir(ProxyOptionsTest.class.getSimpleName(),
		                                     ProxyOptionsTest.class.getSimpleName(), FileShutdownAction.DELETE);
	}
	
	@After
	public void after() {
		for (final String key : keys) {
			if (System.getProperty(key) != null) {
				System.clearProperty(key);
			}
		}
		try {
			FileUtils.deleteDirectory(cacheDir);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	@Before
	public void before() {
		for (final String key : keys) {
			if (System.getProperty(key) != null) {
				System.clearProperty(key);
			}
		}
	}
	
	@Test
	public void test() {
		try {
			System.setProperty("proxy.useProxy", "true");
			System.setProperty("proxy.internal", "true");
			System.setProperty("proxy.cacheDir", cacheDir.getAbsolutePath());
			final Settings settings = new Settings();
			ArgumentSetFactory.create(new ProxyOptions(settings.getRoot(), Requirement.required));
		} catch (final SettingsParseError e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentSetRegistrationException e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentRegistrationException e) {
			e.printStackTrace();
			fail();
		} finally {
			//
		}
	}
	
}
