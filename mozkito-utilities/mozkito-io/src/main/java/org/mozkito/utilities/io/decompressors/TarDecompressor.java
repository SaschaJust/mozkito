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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import org.mozkito.utilities.io.CompressionUtils.ArchiveType;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class TarDecompressor extends ArchiveDecompressor {
	
	@Override
	public File decompress(final File archive,
	                       final File targetDirectory) throws IOException {
		
		final File outputDirectory = prepareOutputDirectory(archive, targetDirectory, ArchiveType.TAR);
		
		SANITY: {
			assert outputDirectory != null;
		}
		
		try (TarArchiveInputStream tarIn = new TarArchiveInputStream(new FileInputStream(archive))) {
			TarArchiveEntry entry = null;
			while ((entry = tarIn.getNextTarEntry()) != null) {
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
					
					final long size = entry.getSize();
					byte[] content = null;
					try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetFile))) {
						
						while (size > Integer.MAX_VALUE) {
							content = new byte[Integer.MAX_VALUE];
							tarIn.read(content);
							outputStream.write(content);
						}
						
						SANITY: {
							assert size < Integer.MAX_VALUE;
						}
						
						if (size > 0) {
							content = new byte[(int) size];
							tarIn.read(content);
							outputStream.write(content);
						}
					}
				}
			}
		}
		
		// return the output file after successful write
		return outputDirectory;
		
	}
}
