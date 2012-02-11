/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
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
package net.ownhero.dev.andama.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.settings.arguments.InputFileArgument;
import net.ownhero.dev.andama.settings.dependencies.Optional;
import net.ownhero.dev.andama.settings.dependencies.Required;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InputFileArgumentTest {
	
	private File          file;
	private static String name = "inputFile";
	
	@Before
	public void setUp() throws Exception {
		this.file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
	}
	
	@After
	public void tearDown() throws Exception {
		if (this.file.exists()) {
			this.file.delete();
		}
	}
	
	@Test
	public void testNotRequiredExists() {
		final Settings settings = new Settings();
		final InputFileArgument arg = new InputFileArgument(settings.getRootArgumentSet(), name, "test argument",
		                                                    this.file.getAbsolutePath(), new Optional());
		settings.parseArguments();
		final File value = arg.getValue();
		assertTrue(value != null);
		assertEquals(this.file.getAbsolutePath(), value.getAbsolutePath());
	}
	
	@Test
	public void testNotRequiredNotExists() {
		final Settings settings = new Settings();
		new InputFileArgument(settings.getRootArgumentSet(), name, "test argument", this.file.getAbsolutePath(),
		                      new Optional());
		this.file.delete();
		try {
			settings.parseArguments();
			fail();
		} catch (final Shutdown s) {
			
		}
	}
	
	@Test
	public void testNotRequiredNotGiven() {
		final Settings settings = new Settings();
		final InputFileArgument arg = new InputFileArgument(settings.getRootArgumentSet(), name, "test argument", null,
		                                                    new Optional());
		settings.parseArguments();
		assertEquals(null, arg.getValue());
	}
	
	@Test
	public void testRequiredExists() {
		final Settings settings = new Settings();
		final InputFileArgument arg = new InputFileArgument(settings.getRootArgumentSet(), name, "test argument",
		                                                    this.file.getAbsolutePath(), new Required());
		assertEquals(name, arg.getName());
		settings.parseArguments();
		final File value = arg.getValue();
		assertTrue(value != null);
		assertEquals(this.file.getAbsolutePath(), value.getAbsolutePath());
	}
	
	@Test
	public void testRequiredNotExists() {
		final Settings settings = new Settings();
		new InputFileArgument(settings.getRootArgumentSet(), name, "test argument", this.file.getAbsolutePath(),
		                      new Required());
		this.file.delete();
		try {
			settings.parseArguments();
			fail();
		} catch (final Shutdown e) {
			
		}
	}
	
	@Test
	public void testRequiredNotGiven() {
		final Settings settings = new Settings();
		new InputFileArgument(settings.getRootArgumentSet(), name, "test argument", null, new Required());
		try {
			settings.parseArguments();
			fail();
		} catch (final Shutdown e) {
			
		}
	}
	
}
