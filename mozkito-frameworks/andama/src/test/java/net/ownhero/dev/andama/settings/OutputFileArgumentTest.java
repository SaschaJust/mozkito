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
import net.ownhero.dev.andama.settings.dependencies.Optional;
import net.ownhero.dev.andama.settings.dependencies.Required;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.After;
import org.junit.Test;

public class OutputFileArgumentTest {
	
	private static String      name = "outputFile";
	private OutputFileArgument arg  = null;
	
	@Test
	public void blaTest() {
		final AndamaSettings settings = new AndamaSettings();
		this.arg = new OutputFileArgument(
		                                  settings.getRootArgumentSet(),
		                                  "output.xml",
		                                  "Instead of writing the source code change operations to the DB, output them as XML into this file.",
		                                  null, new Optional(), true);
		settings.parseArguments();
		final File value = this.arg.getValue();
		assertEquals(null, value);
		
	}
	
	@After
	public void tearDown() {
		if ((this.arg != null) && (this.arg.getValue() != null)) {
			this.arg.getValue().delete();
		}
	}
	
	@Test
	public void testNotRequiredExistsNoOverwrite() {
		final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		final AndamaSettings settings = new AndamaSettings();
		this.arg = new OutputFileArgument(settings.getRootArgumentSet(), name, "test argument", file.getAbsolutePath(),
		                                  new Optional(), false);
		settings.parseArguments();
		final File value = this.arg.getValue();
		assertTrue(value == null);
	}
	
	@Test
	public void testNotRequiredExistsOverwrite() {
		final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		final AndamaSettings settings = new AndamaSettings();
		this.arg = new OutputFileArgument(settings.getRootArgumentSet(), name, "test argument", file.getAbsolutePath(),
		                                  new Optional(), true);
		settings.parseArguments();
		final File value = this.arg.getValue();
		assertTrue(value != null);
		assertEquals(file.getAbsolutePath(), value.getAbsolutePath());
	}
	
	@Test
	public void testNotRequiredNotExistsNoOverwrite() {
		final AndamaSettings settings = new AndamaSettings();
		this.arg = new OutputFileArgument(settings.getRootArgumentSet(), name, "test argument",
		                                  "/tmp/fhdjkshfjksdhfjk.kim", new Optional(), false);
		settings.parseArguments();
		final File value = this.arg.getValue();
		assertEquals("/tmp/fhdjkshfjksdhfjk.kim", value.getAbsolutePath());
	}
	
	@Test
	public void testNotRequiredNotExistsOverwrite() {
		final AndamaSettings settings = new AndamaSettings();
		this.arg = new OutputFileArgument(settings.getRootArgumentSet(), name, "test argument",
		                                  "/tmp/fhdjkshfjksdhfjk.kim", new Optional(), true);
		settings.parseArguments();
		final File value = this.arg.getValue();
		assertTrue(value != null);
	}
	
	@Test
	public void testRequiredExistsNoOverwrite() {
		final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		final AndamaSettings settings = new AndamaSettings();
		new OutputFileArgument(settings.getRootArgumentSet(), name, "test argument", file.getAbsolutePath(),
		                       new Required(), false);
		try {
			settings.parseArguments();
			fail();
		} catch (final Shutdown e) {
			
		}
	}
	
	@Test
	public void testRequiredExistsOverwrite() {
		final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		final AndamaSettings settings = new AndamaSettings();
		this.arg = new OutputFileArgument(settings.getRootArgumentSet(), name, "test argument", file.getAbsolutePath(),
		                                  new Required(), true);
		settings.parseArguments();
		final File value = this.arg.getValue();
		assertTrue(value != null);
		assertEquals(file.getAbsolutePath(), value.getAbsolutePath());
	}
	
}
