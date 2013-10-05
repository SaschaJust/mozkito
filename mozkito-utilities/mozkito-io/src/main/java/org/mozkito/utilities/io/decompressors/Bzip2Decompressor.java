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

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.IOUtils;

import org.mozkito.utilities.io.CompressionUtils.ArchiveType;
import org.mozkito.utilities.io.exceptions.FilePermissionException;
import org.mozkito.utilities.io.exceptions.UnsupportedExtensionException;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class Bzip2Decompressor extends CompressionDecompressor {
	
	/**
	 * Bunzip2 provides bzip2 decompression. Please note that this won't transitively call the appropriate decompressor
	 * on the inner archive, if there is any.
	 * 
	 * @param archive
	 *            the archive file object
	 * @param targetDirectory
	 *            the target directory the file is extracted to
	 * @throws NullPointerException
	 *             if the archive is null
	 * @throws FileNotFoundException
	 *             if the archive file cannot be found.
	 * @throws UnsupportedExtensionException
	 *             if extension is not any of the valid extensions for this decompressor.
	 * @throws FileAlreadyExistsException
	 *             if the targetFile already exists.
	 * @throws FilePermissionException
	 *             if the we do not have sufficient rights to read from the archive.
	 * @throws IOException
	 *             Signals that an I/O exception has occurred (e.g. creating the result file, creating the target
	 *             directory (if not present), reading from the archive or writing to the file).
	 */
	@Override
	public File decompress(final File archive,
	                       final File targetDirectory) throws IOException {
		final File outputFile = prepareOutputFile(archive, targetDirectory, ArchiveType.BZIP2);
		
		// open compressor input stream and a file output stream and copy data between the two
		try (BZip2CompressorInputStream bz2InputStream = new BZip2CompressorInputStream(
		                                                                                new BufferedInputStream(
		                                                                                                        new FileInputStream(
		                                                                                                                            archive)))) {
			try (final FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
				IOUtils.copy(bz2InputStream, fileOutputStream);
			}
		}
		
		// return the output file after successful write
		return outputFile;
		
	}
	
}
