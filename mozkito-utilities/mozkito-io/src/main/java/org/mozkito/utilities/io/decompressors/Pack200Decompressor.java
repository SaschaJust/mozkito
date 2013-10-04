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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.IOUtils;

import org.mozkito.utilities.io.CompressionUtils.ArchiveType;
import org.mozkito.utilities.io.exceptions.FilePermissionException;
import org.mozkito.utilities.io.exceptions.UnsupportedExtensionException;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class Pack200Decompressor extends CompressionDecompressor {
	
	/**
	 * 
	 */
	public Pack200Decompressor() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.io.decompressors.CompressionDecompressor#decompress(java.io.File, java.io.File)
	 */
	@Override
	public File decompress(final File archive,
	                       final File targetDirectory) throws NullPointerException,
	                                                  FileNotFoundException,
	                                                  UnsupportedExtensionException,
	                                                  FileAlreadyExistsException,
	                                                  FilePermissionException,
	                                                  IOException {
		
		final File outputFile = prepareOutputFile(archive, targetDirectory, ArchiveType.PACK200);
		
		// open compressor input stream and a file output stream and copy data between the two
		try (XZCompressorInputStream xzInputStream = new XZCompressorInputStream(
		                                                                         new BufferedInputStream(
		                                                                                                 new FileInputStream(
		                                                                                                                     archive)))) {
			try (final FileOutputStream fileOutputStream = new FileOutputStream(outputFile.getParentFile())) {
				IOUtils.copy(xzInputStream, fileOutputStream);
			}
		}
		
		// return the output file after successful write
		return outputFile;
	}
	
}
