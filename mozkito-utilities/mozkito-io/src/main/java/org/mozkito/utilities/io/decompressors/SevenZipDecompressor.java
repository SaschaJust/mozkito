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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import org.mozkito.utilities.io.CompressionUtils.ArchiveType;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class SevenZipDecompressor extends ArchiveDecompressor {
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.io.decompressors.CompressionDecompressor#decompress(java.io.File, java.io.File)
	 */
	@Override
	public File decompress(final File archive,
	                       final File target) throws IOException {
		final File outputDirectory = prepareOutputDirectory(archive, target, ArchiveType.SEVENZ);
		
		SANITY: {
			assert outputDirectory != null;
		}
		
		SevenZFile sevenZFile = null;
		try {
			sevenZFile = new SevenZFile(archive);
			
			SevenZArchiveEntry entry = null;
			while ((entry = sevenZFile.getNextEntry()) != null) {
				final File targetFile = new File(outputDirectory, entry.getName());
				
				if (entry.isDirectory()) {
					if ((!targetFile.exists() && !targetFile.mkdirs()) || !targetFile.isDirectory()) {
						throw new IOException("Failed creating directory: "
						        + targetFile.getParentFile().getAbsolutePath());
					}
				} else {
					final long size = entry.getSize();
					byte[] content = null;
					try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetFile))) {
						
						while (size > Integer.MAX_VALUE) {
							content = new byte[Integer.MAX_VALUE];
							sevenZFile.read(content);
							outputStream.write(content);
						}
						
						SANITY: {
							assert size < Integer.MAX_VALUE;
						}
						
						if (size > 0) {
							content = new byte[(int) size];
							sevenZFile.read(content);
							outputStream.write(content);
						}
					}
				}
			}
		} finally {
			if (sevenZFile != null) {
				sevenZFile.close();
			}
		}
		return outputDirectory;
	}
	
}
