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

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;

import org.mozkito.utilities.io.CompressionUtils.ArchiveType;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class RarDecompressor extends ArchiveDecompressor {
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.io.decompressors.IDecompressor#decompress(java.io.File, java.io.File)
	 */
	@Override
	public File decompress(final File archive,
	                       final File target) throws IOException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final File outputDirectory = prepareOutputDirectory(archive, target, ArchiveType.RAR);
			
			Archive a = null;
			try {
				a = new Archive(new FileVolumeManager(archive));
				FileHeader entry = null;
				
				while ((entry = a.nextFileHeader()) != null) {
					final File targetFile = new File(outputDirectory,
					                                 entry.getFileNameString()
					                                      .replace("\\",
					                                               org.mozkito.utilities.io.FileUtils.fileSeparator));
					
					if (entry.isDirectory()) {
						if ((!targetFile.exists() && !targetFile.mkdirs()) || !targetFile.isDirectory()) {
							throw new IOException("Failed creating directory: " + targetFile.getAbsolutePath());
						}
					} else {
						// junrar does not explicitly extract parent directories...
						if ((!targetFile.getParentFile().exists() && !targetFile.getParentFile().mkdirs())
						        || !targetFile.getParentFile().isDirectory()) {
							throw new IOException("Failed creating directory: " + targetFile.getAbsolutePath());
						}
						
						final FileOutputStream os = new FileOutputStream(targetFile);
						a.extractFile(entry, os);
						os.close();
					}
				}
				
			} catch (final RarException e) {
				throw new IOException(e);
			} finally {
				if (a != null) {
					a.close();
				}
			}
			
			return outputDirectory;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
