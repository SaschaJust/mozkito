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

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.exceptions.SettingsParseError;
import net.ownhero.dev.andama.settings.arguments.OutputFileArgument;
import net.ownhero.dev.andama.settings.requirements.Optional;
import net.ownhero.dev.andama.settings.requirements.Required;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OutputFileArgumentTest {
	
	private static String      name = "outputFile";
	private OutputFileArgument arg  = null;
	private File               file = null;
	
	@Test
	public void blaTest() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		this.arg = new OutputFileArgument(
		                                  settings.getRootArgumentSet(),
		                                  "output.xml",
		                                  "Instead of writing the source code change operations to the DB, output them as XML into this file.",
		                                  null, new Optional(), true);
		settings.parse();
		final File value = this.arg.getValue();
		assertEquals(null, value);
		
	}
	
	@Before
	public void setup() {
		this.file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
	}
	
	@After
	public void tearDown() {
		if ((this.arg != null) && (this.arg.getValue() != null)) {
			this.arg.getValue().delete();
		}
	}
	
	@Test
	public void testNotRequiredExistsNoOverwrite() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		this.arg = new OutputFileArgument(settings.getRootArgumentSet(), name, "test argument",
		                                  this.file.getAbsolutePath(), new Optional(), false);
		settings.parse();
		final File value = this.arg.getValue();
		assertTrue(value == null);
	}
	
	@Test
	public void testNotRequiredExistsOverwrite() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		this.arg = new OutputFileArgument(settings.getRootArgumentSet(), name, "test argument",
		                                  this.file.getAbsolutePath(), new Optional(), true);
		settings.parse();
		final File value = this.arg.getValue();
		assertTrue(value != null);
		assertEquals(this.file.getAbsolutePath(), value.getAbsolutePath());
	}
	
	@Test
	public void testNotRequiredNotExistsNoOverwrite() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		this.file.delete();
		this.arg = new OutputFileArgument(settings.getRootArgumentSet(), name, "test argument",
		                                  this.file.getAbsolutePath(), new Optional(), false);
		settings.parse();
		final File value = this.arg.getValue();
		assertEquals(this.file.getAbsolutePath(), value.getAbsolutePath());
	}
	
	@Test
	public void testNotRequiredNotExistsOverwrite() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		this.arg = new OutputFileArgument(settings.getRootArgumentSet(), name, "test argument",
		                                  "/tmp/fhdjkshfjksdhfjk.kim", new Optional(), true);
		settings.parse();
		final File value = this.arg.getValue();
		assertTrue(value != null);
	}
	
	@Test
	public void testRequiredExistsNoOverwrite() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		new OutputFileArgument(settings.getRootArgumentSet(), name, "test argument", this.file.getAbsolutePath(),
		                       new Required(), false);
		try {
			settings.parse();
			fail();
		} catch (final SettingsParseError e) {
			
		}
	}
	
	@Test
	public void testRequiredExistsOverwrite() throws ArgumentRegistrationException, SettingsParseError {
		final Settings settings = new Settings();
		this.arg = new OutputFileArgument(settings.getRootArgumentSet(), name, "test argument",
		                                  this.file.getAbsolutePath(), new Required(), true);
		settings.parse();
		final File value = this.arg.getValue();
		assertTrue(value != null);
		assertEquals(this.file.getAbsolutePath(), value.getAbsolutePath());
	}
	
}
