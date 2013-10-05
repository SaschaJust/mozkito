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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;

import org.mozkito.utilities.io.CompressionUtils.ArchiveType;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class GzipDecompressor extends CompressionDecompressor {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public File decompress(final File archive,
	                       final File targetDirectory) throws IOException {
		
		final File outputFile = prepareOutputFile(archive, targetDirectory, ArchiveType.GZIP);
		
		// open compressor input stream and a file output stream and copy data between the two
		try (GzipCompressorInputStream gzInputStream = new GzipCompressorInputStream(
		                                                                             new BufferedInputStream(
		                                                                                                     new FileInputStream(
		                                                                                                                         archive)))) {
			try (final FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
				IOUtils.copy(gzInputStream, fileOutputStream);
			}
		}
		
		// return the output file after successful write
		return outputFile;
		
	}
	
}
