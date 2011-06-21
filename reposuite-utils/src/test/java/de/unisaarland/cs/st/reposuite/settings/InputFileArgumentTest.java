/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

public class InputFileArgumentTest {
	
	private File file;
	private File defaultFile;
	private RepoSuiteSettings settings;
	private Properties        sysProps;
	private final String      noFilePath = "/fhdsjfdjskhf/fdsfdsfdsa/fdsa/fdas/fdsa/fds/fsd/afds";
	
	@Before
	public void setUp() {
		sysProps = System.getProperties();
		settings = new RepoSuiteSettings();
		file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
		defaultFile = FileUtils.createRandomFile(FileShutdownAction.DELETE);
	}
	
	@After
	public void tearDown() {
		file.delete();
		defaultFile.delete();
		sysProps.remove("test");
	}
	
	@Test
	public void testDefaultNotRequiredNotSet() {
		new InputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), false);
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultNotRequiredSetExists() {
		sysProps.put("test", file.getAbsolutePath());
		InputFileArgument arg = new InputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), false);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultNotRequiredSetNotExist() {
		sysProps.put("test", noFilePath);
		new InputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), false);
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultRequiredNotSet() {
		new InputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), true);
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultRequiredSetExists() {
		sysProps.put("test", file.getAbsolutePath());
		InputFileArgument arg = new InputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), true);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultRequiredSetNotExist() {
		sysProps.put("test", noFilePath);
		new InputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), true);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
		}
	}
	
	@Test
	public void testNoDefaultNotRequiredNotSet() {
		new InputFileArgument(settings, "test", "", null, false);
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefaultNotRequiredSetExists() {
		sysProps.put("test", file.getAbsolutePath());
		InputFileArgument arg = new InputFileArgument(settings, "test", "", null, false);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefaultNotRequiredSetNotExist() {
		sysProps.put("test", noFilePath);
		new InputFileArgument(settings, "test", "", null, false);
		try {
			settings.parseArguments();
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefaultRequiredNotSet() {
		new InputFileArgument(settings, "test", "", null, true);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testNoDefaultRequiredSetExists() {
		sysProps.put("test", file.getAbsolutePath());
		InputFileArgument arg = new InputFileArgument(settings, "test", "", null, true);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefaultRequiredSetNotExist() {
		sysProps.put("test", noFilePath);
		new InputFileArgument(settings, "test", "", null, true);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
}
