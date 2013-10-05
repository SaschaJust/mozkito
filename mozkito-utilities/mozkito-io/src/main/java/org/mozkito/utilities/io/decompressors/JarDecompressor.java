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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;

import org.mozkito.utilities.io.CompressionUtils.ArchiveType;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class JarDecompressor extends ArchiveDecompressor {
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.io.decompressors.IDecompressor#decompress(java.io.File, java.io.File)
	 */
	@Override
	public File decompress(final File archive,
	                       final File target) throws IOException {
		final File outputDirectory = prepareOutputDirectory(archive, target, ArchiveType.JAR);
		
		SANITY: {
			assert outputDirectory != null;
		}
		
		try (final JarFile jarFile = new JarFile(archive)) {
			final Enumeration<JarEntry> enumeration = jarFile.entries();
			
			while (enumeration.hasMoreElements()) {
				final JarEntry entry = enumeration.nextElement();
				final InputStream content = jarFile.getInputStream(entry);
				final File targetFile = new File(outputDirectory, entry.getName());
				
				if (entry.isDirectory()) {
					if ((!targetFile.exists() && !targetFile.mkdirs()) || !targetFile.isDirectory()) {
						throw new IOException("Failed creating directory: "
						        + targetFile.getParentFile().getAbsolutePath());
					}
				} else {
					if ((targetFile.getParentFile() != null) && !targetFile.getParentFile().exists()) {
						if (!targetFile.getParentFile().mkdirs()) {
							throw new IOException("Failed creating directory: "
							        + targetFile.getParentFile().getAbsolutePath());
						}
					}
					
					try (FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
						IOUtils.copy(content, fileOutputStream);
					}
				}
			}
		}
		
		return outputDirectory;
	}
	
}
