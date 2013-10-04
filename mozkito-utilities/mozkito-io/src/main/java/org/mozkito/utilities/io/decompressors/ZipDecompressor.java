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

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;

import org.mozkito.utilities.io.CompressionUtils.ArchiveType;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class ZipDecompressor extends ArchiveDecompressor {
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.io.decompressors.IDecompressor#decompress(java.io.File, java.io.File)
	 */
	@Override
	public File decompress(final File archive,
	                       final File target) throws IOException {
		final File outputDirectory = prepareOutputDirectory(archive, target, ArchiveType.ZIP);
		
		SANITY: {
			assert outputDirectory != null;
		}
		
		final ZipFile zipFile = new ZipFile(archive);
		final Enumeration<ZipArchiveEntry> enumeration = zipFile.getEntries();
		
		while (enumeration.hasMoreElements()) {
			final ZipArchiveEntry entry = enumeration.nextElement();
			final InputStream content = zipFile.getInputStream(entry);
			final File entryFile = new File(outputDirectory, entry.getName());
			
			if (!entryFile.getParentFile().exists()) {
				if (!entryFile.getParentFile().mkdirs()) {
					throw new IOException();
				}
			}
			
			try (FileOutputStream fileOutputStream = new FileOutputStream(entryFile)) {
				IOUtils.copy(content, fileOutputStream);
			}
		}
		
		return outputDirectory;
	}
	
}
