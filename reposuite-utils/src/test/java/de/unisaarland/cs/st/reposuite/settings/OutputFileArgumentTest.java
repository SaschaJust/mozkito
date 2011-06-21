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



public class OutputFileArgumentTest {
	
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
	public void testDefaultNotRequiredNoOverwriteNotSetExists() {
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), false,
		                                                false);
		try {
			settings.parseArguments();
			assertEquals(null, arg.getValue());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultNotRequiredNoOverwriteNotSetNotExists() {
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", noFilePath, false, false);
		try {
			settings.parseArguments();
			File value = arg.getValue();
			assertEquals(null, value);
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultNotRequiredNoOverwriteSetExists() {
		sysProps.put("test", file.getAbsolutePath());
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), false,
		                                                false);
		try {
			settings.parseArguments();
			assertEquals(null, arg.getValue());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultNotRequiredNoOverwriteSetNotExists() {
		sysProps.put("test", file.getAbsolutePath());
		file.delete();
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), false,
		                                                false);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
			
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultNotRequiredOverwriteNotSet() {
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), false,
		                                                true);
		try {
			settings.parseArguments();
			assertEquals(defaultFile.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultNotRequiredOverwriteSetExists() {
		sysProps.put("test", file.getAbsolutePath());
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), false,
		                                                true);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultNotRequiredOverwriteSetNotExists() {
		sysProps.put("test", file.getAbsolutePath());
		file.delete();
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), false,
		                                                true);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
			
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultRequiredNoOverwriteNotSetExists() {
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), true,
		                                                false);
		try {
			settings.parseArguments();
			arg.getValue();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testDefaultRequiredNoOverwriteNotSetNotExists() {
		defaultFile.delete();
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), true,
		                                                false);
		try {
			settings.parseArguments();
			assertEquals(defaultFile.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultRequiredNoOverwriteSetExists() {
		sysProps.put("test", file.getAbsolutePath());
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), true,
		                                                false);
		try {
			settings.parseArguments();
			arg.getValue();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testDefaultRequiredNoOverwriteSetNotExists() {
		sysProps.put("test", file.getAbsolutePath());
		file.delete();
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), true,
		                                                false);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultRequiredOverwriteNotSetExists() {
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), true, true);
		try {
			settings.parseArguments();
			assertEquals(defaultFile.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultRequiredOverwriteNotSetNotExists() {
		defaultFile.delete();
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), true, true);
		try {
			settings.parseArguments();
			assertEquals(defaultFile.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultRequiredOverwriteSetExists() {
		sysProps.put("test", file.getAbsolutePath());
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), true, true);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testDefaultRequiredOverwriteSetNotExists() {
		sysProps.put("test", file.getAbsolutePath());
		file.delete();
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", defaultFile.getAbsolutePath(), true, true);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefaultNotRequiredNoOverwriteNotSet() {
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", null, false, false);
		try {
			settings.parseArguments();
			assertEquals(null, arg.getValue());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefaultNotRequiredNoOverwriteSetExists() {
		sysProps.put("test", file.getAbsolutePath());
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", null, false, false);
		try {
			settings.parseArguments();
			assertEquals(null, arg.getValue());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefaultNotRequiredNoOverwriteSetNotExists() {
		sysProps.put("test", file.getAbsolutePath());
		file.delete();
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", null, false, false);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefaultNotRequiredOverwriteNotSet() {
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", null, false, true);
		try {
			settings.parseArguments();
			assertEquals(null, arg.getValue());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefaultNotRequiredOverwriteSetExists() {
		sysProps.put("test", file.getAbsolutePath());
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", null, false, true);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefaultNotRequiredOverwriteSetNotExists() {
		sysProps.put("test", file.getAbsolutePath());
		file.delete();
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", null, false, true);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefaultRequiredNoOverwriteNotSet() {
		new OutputFileArgument(settings, "test", "", null, true, false);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testNoDefaultRequiredNoOverwriteSetExists() {
		sysProps.put("test", file.getAbsolutePath());
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", null, true, false);
		try {
			settings.parseArguments();
			arg.getValue();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testNoDefaultRequiredNoOverwriteSetNotExists() {
		sysProps.put("test", file.getAbsolutePath());
		file.delete();
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", null, true, false);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefaultRequiredOverwriteNotSet() {
		new OutputFileArgument(settings, "test", "", null, true, true);
		try {
			settings.parseArguments();
			fail();
		} catch (Shutdown e) {
			
		}
	}
	
	@Test
	public void testNoDefaultRequiredOverwriteSetExists() {
		sysProps.put("test", file.getAbsolutePath());
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", null, true, true);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
	@Test
	public void testNoDefaultRequiredOverwriteSetNotExists() {
		sysProps.put("test", file.getAbsolutePath());
		file.delete();
		OutputFileArgument arg = new OutputFileArgument(settings, "test", "", null, true, true);
		try {
			settings.parseArguments();
			assertEquals(file.getAbsolutePath(), arg.getValue().getAbsolutePath());
		} catch (Shutdown e) {
			fail();
		}
	}
	
}
