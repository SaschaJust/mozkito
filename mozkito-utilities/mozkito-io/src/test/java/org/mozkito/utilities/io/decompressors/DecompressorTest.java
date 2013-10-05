/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.utilities.io.decompressors;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.Test;

import org.mozkito.utilities.io.CompressionUtils;
import org.mozkito.utilities.io.FileUtils;
import org.mozkito.utilities.io.IOUtils;

/**
 * The Class DecompressorTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DecompressorTest {
	
	/** The Constant ARCHIVE_PREFIX. */
	private static final String ARCHIVE_PREFIX = "decompression_test";
	
	/**
	 * After class.
	 */
	@AfterClass
	public static void afterClass() {
		// delete ARCHIVE_PREFIX + "_" +"*";
	}
	
	/**
	 * Test7z.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	@Test
	public final void test7z() throws IOException, URISyntaxException {
		testExtraction("tar", "7z");
	}
	
	/**
	 * Test bzip2.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	@Test
	public final void testBzip2() throws IOException, URISyntaxException {
		testExtraction("tar", "bz2");
	}
	
	/**
	 * Test extraction.
	 * 
	 * @param extensions
	 *            the extensions
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	private void testExtraction(final String... extensions) throws IOException, URISyntaxException {
		PRECONDITIONS: {
			assert extensions != null;
			assert extensions.length > 0;
		}
		
		try {
			File dir = null;
			final LinkedList<String> extensionList = new LinkedList<>(Arrays.asList(extensions));
			final String allExtensions = StringUtils.join(extensionList, '.');
			File archiveFile = IOUtils.getTemporaryCopyOfFile(ARCHIVE_PREFIX + "_",
			                                                  "." + allExtensions,
			                                                  getClass().getResource("/" + ARCHIVE_PREFIX + "."
			                                                                                 + allExtensions).toURI());
			
			String extension = null;
			while (!(extension = FilenameUtils.getExtension(archiveFile.getName())).isEmpty()) {
				// final File tmpDir = org.apache.commons.io.FileUtils.getTempDirectory();
				final File tmpDir = FileUtils.createRandomDir(ARCHIVE_PREFIX + "_" + extension, "",
				                                              FileUtils.FileShutdownAction.DELETE);
				dir = CompressionUtils.decompress(archiveFile, tmpDir);
				if (!FilenameUtils.getExtension(dir.getName()).isEmpty()) {
					assert dir.isFile();
				} else {
					assert dir.isDirectory();
				}
				
				archiveFile = dir;
			}
			
			final File emptyDir = new File(dir, "empty_dir");
			assertTrue(emptyDir.exists());
			assertTrue(emptyDir.isDirectory());
			
			final File test1Txt = new File(dir, "test1.txt");
			assertTrue(test1Txt.exists());
			assertTrue(test1Txt.isFile());
			
			try {
				final String test1TxtString = FileUtils.readFileToString(test1Txt);
				assertTrue(test1TxtString.startsWith("Lorem "));
			} catch (final IOException e) {
				fail(e.getMessage());
			}
			
			final File test2Log = new File(dir, "test2.log");
			assertTrue(test2Log.exists());
			assertTrue(test2Log.isFile());
			
			try {
				final String test2LogString = FileUtils.readFileToString(test2Log);
				assertTrue(test2LogString.startsWith("TODO"));
			} catch (final IOException e) {
				fail(e.getMessage());
			}
			
			final File empty_multi = new File(dir, "empty_multi");
			assertTrue(empty_multi.exists());
			assertTrue(empty_multi.isDirectory());
			
			final File empty_multi2 = new File(empty_multi, "level");
			assertTrue(empty_multi2.exists());
			assertTrue(empty_multi2.isDirectory());
			
			final File empty_multi3 = new File(empty_multi2, "directoroes");
			assertTrue(empty_multi3.exists());
			assertTrue(empty_multi3.isDirectory());
			
			final File multi = new File(dir, "multi");
			assertTrue(multi.exists());
			assertTrue(multi.isDirectory());
			assertTrue(new File(multi, "first").exists());
			
			final File multi2 = new File(multi, "level");
			assertTrue(multi2.exists());
			assertTrue(multi2.isDirectory());
			assertTrue(new File(multi2, "second").exists());
			
			final File multi3 = new File(multi2, "directories");
			assertTrue(multi3.exists());
			assertTrue(multi3.isDirectory());
			assertTrue(new File(multi3, "third").exists());
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Test gzip.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	@Test
	public final void testGzip() throws IOException, URISyntaxException {
		testExtraction("tar", "gz");
	}
	
	/**
	 * Test jar.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	@Test
	public final void testJar() throws IOException, URISyntaxException {
		testExtraction("jar");
	}
	
	/**
	 * Test pack200.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	@Test
	public final void testPack200() throws IOException, URISyntaxException {
		testExtraction("pack", "gz");
	}
	
	/**
	 * Test rar.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	@Test
	public final void testRar() throws IOException, URISyntaxException {
		testExtraction("rar");
	}
	
	/**
	 * Test tar.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	@Test
	public final void testTar() throws IOException, URISyntaxException {
		testExtraction("tar");
	}
	
	/**
	 * Test zip.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 */
	@Test
	public final void testZip() throws IOException, URISyntaxException {
		testExtraction("zip");
	}
}
