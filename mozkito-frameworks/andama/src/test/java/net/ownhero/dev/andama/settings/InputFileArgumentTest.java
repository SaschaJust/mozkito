/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.ownhero.dev.andama.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InputFileArgumentTest {
	
	private File        file;
	private static String name = "inputFile";
	
	@Before
	public void setUp() throws Exception {
		file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
	}
	
	@After
	public void tearDown() throws Exception {
		if(file.exists()){
			file.delete();
		}
	}
	
	@Test
	public void testNotRequiredExists() {
		AndamaSettings settings = new AndamaSettings();
		InputFileArgument arg = new InputFileArgument(settings, name, "test argument", file.getAbsolutePath(), false);
		settings.parseArguments();
		File value = arg.getValue();
		assertTrue(value != null);
		assertEquals(file.getAbsolutePath(), value.getAbsolutePath());
	}
	
	@Test
	public void testNotRequiredNotExists() {
		AndamaSettings settings = new AndamaSettings();
		new InputFileArgument(settings, name, "test argument", file.getAbsolutePath(), false);
		file.delete();
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown s) {
			
		}
	}
	
	@Test
	public void testNotRequiredNotGiven() {
		AndamaSettings settings = new AndamaSettings();
		InputFileArgument arg = new InputFileArgument(settings, name, "test argument", null, false);
		settings.parseArguments();
		assertEquals(null, arg.getValue());
	}
	
	@Test
	public void testRequiredExists() {
		AndamaSettings settings = new AndamaSettings();
		InputFileArgument arg = new InputFileArgument(settings, name, "test argument", file.getAbsolutePath(), true);
		assertEquals(name, arg.getName());
		settings.parseArguments();
		File value = arg.getValue();
		assertTrue(value != null);
		assertEquals(file.getAbsolutePath(), value.getAbsolutePath());
	}
	
	@Test
	public void testRequiredNotExists() {
		AndamaSettings settings = new AndamaSettings();
		new InputFileArgument(settings, name, "test argument", file.getAbsolutePath(), true);
		file.delete();
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testRequiredNotGiven() {
		AndamaSettings settings = new AndamaSettings();
		new InputFileArgument(settings, name, "test argument", null, true);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
}
