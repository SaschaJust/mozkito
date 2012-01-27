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

import org.junit.Test;

public class OutputFileArgumentTest {
	
	private static String name = "outputFile";
	
	@Test
	public void testNotRequiredExistsNoOverwrite() {
		File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		AndamaSettings settings = new AndamaSettings();
		OutputFileArgument arg = new OutputFileArgument(settings, name, "test argument", file.getAbsolutePath(), false,
				false);
		settings.parseArguments();
		File value = arg.getValue();
		assertTrue(value == null);
	}
	
	@Test
	public void testNotRequiredExistsOverwrite() {
		File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		AndamaSettings settings = new AndamaSettings();
		OutputFileArgument arg = new OutputFileArgument(settings, name, "test argument", file.getAbsolutePath(), false,
				true);
		settings.parseArguments();
		File value = arg.getValue();
		assertTrue(value != null);
		assertEquals(file.getAbsolutePath(), value.getAbsolutePath());
	}
	
	@Test
	public void testRequiredExistsNoOverwrite() {
		File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		AndamaSettings settings = new AndamaSettings();
		OutputFileArgument arg = new OutputFileArgument(settings, name, "test argument", file.getAbsolutePath(), true,
				false);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testRequiredExistsOverwrite() {
		File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		AndamaSettings settings = new AndamaSettings();
		OutputFileArgument arg = new OutputFileArgument(settings, name, "test argument", file.getAbsolutePath(), true,
				true);
		settings.parseArguments();
		File value = arg.getValue();
		assertTrue(value != null);
		assertEquals(file.getAbsolutePath(), value.getAbsolutePath());
	}
	
	//TODO write remaining test cases
	
}
