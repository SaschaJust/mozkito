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

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.BooleanArgument;
import net.ownhero.dev.hiari.settings.BooleanArgument.Options;
import net.ownhero.dev.hiari.settings.DirectoryArgument;
import net.ownhero.dev.hiari.settings.HostArgument;
import net.ownhero.dev.hiari.settings.PortArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.StringArgument;
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
import org.mozkito.issues.tracker.settings.Messages;


import scala.actors.threadpool.Arrays;

/**
 * @author Kim Herzig <herzig@mozkito.org>
 * 
 */
public class ProxyOptionsTest {
	
	@SuppressWarnings ("unchecked")
	private static List<String> keys = Arrays.asList(new String[] { "proxy.cacheDir", "proxy.host", "proxy.internal",
	        "proxy.password", "proxy.port", "useProxy", "tracker.proxy.username", "useProxy" });
	
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
	public void impossibleNPETest() {
		try {
			System.setProperty("useProxy", "true");
			final Settings settings = new Settings();
			
			final Options useProxyOptions = new BooleanArgument.Options(settings.getRoot(), "useProxy",
			                                                            "Activates proxy features.", null,
			                                                            Requirement.required);
			final Options internalOptions = new BooleanArgument.Options(settings.getRoot(), "internal",
			                                                            "Use internal proxy (recommended).", true,
			                                                            Requirement.iff(useProxyOptions));
			new HostArgument.Options(settings.getRoot(), "host", //$NON-NLS-1$
			                         Messages.getString("ProxyOptions.proxyHost_description"), //$NON-NLS-1$
			                         null, Requirement.and(Requirement.iff(useProxyOptions),
			                                               Requirement.equals(internalOptions, false)));
			new DirectoryArgument.Options(settings.getRoot(), "cacheDir", "Cache directory for the internal proxy.",
			                              null, Requirement.and(Requirement.iff(useProxyOptions),
			                                                    Requirement.equals(internalOptions, true)), true);
			new PortArgument.Options(settings.getRoot(),
			                         "port", Messages.getString("ProxyOptions.proxyPort_description"), 8584, //$NON-NLS-1$ //$NON-NLS-2$
			                         Requirement.iff(useProxyOptions), true);
			final net.ownhero.dev.hiari.settings.StringArgument.Options usernameOptions = new StringArgument.Options(
			                                                                                                         settings.getRoot(),
			                                                                                                         "username", //$NON-NLS-1$
			                                                                                                         Messages.getString("ProxyOptions.proxyUser_description"), null, //$NON-NLS-1$
			                                                                                                         Requirement.optional);
			new StringArgument.Options(settings.getRoot(), "password", //$NON-NLS-1$
			                           Messages.getString("ProxyOptions.proxyPassword_description"), //$NON-NLS-1$
			                           null, Requirement.iff(usernameOptions), true);
			ArgumentFactory.create(useProxyOptions);
			final BooleanArgument useProxyArgument = settings.getArgument(useProxyOptions);
			assert (useProxyArgument.getValue() != null);
			
		} catch (final SettingsParseError e) {
			e.printStackTrace();
			fail();
		} catch (final ArgumentRegistrationException e) {
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
