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

package org.mozkito.utilities.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FilenameUtils;

import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.utilities.io.decompressors.Bzip2Decompressor;
import org.mozkito.utilities.io.decompressors.GzipDecompressor;
import org.mozkito.utilities.io.decompressors.IDecompressor;
import org.mozkito.utilities.io.decompressors.JarDecompressor;
import org.mozkito.utilities.io.decompressors.Pack200Decompressor;
import org.mozkito.utilities.io.decompressors.RarDecompressor;
import org.mozkito.utilities.io.decompressors.SevenZipDecompressor;
import org.mozkito.utilities.io.decompressors.TarDecompressor;
import org.mozkito.utilities.io.decompressors.XzipDecompressor;
import org.mozkito.utilities.io.decompressors.ZipDecompressor;
import org.mozkito.utilities.io.exceptions.UnsupportedExtensionException;

/**
 * The Class CompressionUtils.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class CompressionUtils {
	
	/**
	 * The Enum ArchiveType.
	 */
	public static enum ArchiveType {
		
		/** The tar. */
		TAR (new TarDecompressor(), "tar"),
		/** The zip. */
		ZIP (new ZipDecompressor(), "zip"),
		/** The jar. */
		JAR (new JarDecompressor(), "jar"),
		/** The BZI p2. */
		BZIP2 (new Bzip2Decompressor(), "bz", "bz2", "bzip2"),
		/** The gzip. */
		GZIP (new GzipDecompressor(), "gz", "gzip"),
		/** The xz. */
		XZ (new XzipDecompressor(), "xz", "xzip"),
		/** The sevenz. */
		SEVENZ (new SevenZipDecompressor(), "7z"),
		/** The rar. */
		RAR (new RarDecompressor(), "rar"),
		/** The PAC k200. */
		PACK200 (new Pack200Decompressor(), "pack");
		
		/**
		 * For extension.
		 * 
		 * @param extension
		 *            the extension
		 * @return the archive type
		 * @throws UnsupportedExtensionException
		 *             the unsupported extension exception
		 */
		public static ArchiveType forExtension(final String extension) throws UnsupportedExtensionException {
			PRECONDITIONS: {
				if (extension == null) {
					throw new NullPointerException();
				}
			}
			
			for (final ArchiveType type : values()) {
				SANITY: {
					assert type.extensions != null;
				}
				for (final String archiveExtension : type.extensions) {
					if (extension.equalsIgnoreCase(archiveExtension)) {
						return type;
					}
				}
			}
			throw new UnsupportedExtensionException("Extension '" + extension
			        + "' not supported. Current supported archive types: " + JavaUtils.arrayToString(values()));
		}
		
		/** The decompressor. */
		private IDecompressor iDecompressor;
		
		/** The extensions. */
		private String[]      extensions;
		
		/**
		 * Instantiates a new archive type.
		 * 
		 * @param iDecompressor
		 *            the decompressor
		 * @param extensions
		 *            the extensions
		 */
		private ArchiveType(final IDecompressor iDecompressor, final String... extensions) {
			PRECONDITIONS: {
				if (iDecompressor == null) {
					throw new NullPointerException();
				}
				if (extensions == null) {
					throw new NullPointerException();
				}
				if (extensions.length == 0) {
					throw new IllegalArgumentException();
				}
			}
			
			this.iDecompressor = iDecompressor;
			this.extensions = extensions;
		}
		
		/**
		 * Gets the decompressor.
		 * 
		 * @return the decompressor
		 */
		public final IDecompressor getDecompressor() {
			PRECONDITIONS: {
				assert this.iDecompressor != null;
			}
			
			return this.iDecompressor;
		}
	}
	
	/** The Constant X_COMPRESS. */
	private static final String X_COMPRESS = "x-compress";
	
	/**
	 * Decompress.
	 * 
	 * @param archive
	 *            the archive
	 * @param targetDirectory
	 *            the target directory
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File decompress(final File archive,
	                              final File targetDirectory) throws IOException {
		final String extension = FilenameUtils.getExtension(archive.getName());
		final ArchiveType type = ArchiveType.forExtension(extension);
		final IDecompressor decompressor = type.getDecompressor();
		return decompressor.decompress(archive, targetDirectory);
	}
	
	/**
	 * Checks if is archive.
	 * 
	 * @param file
	 *            the file
	 * @return true, if is archive
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static boolean isArchive(final File file) throws IOException {
		final String mimeType = Files.probeContentType(file.toPath());
		return X_COMPRESS.equals(mimeType.toUpperCase());
	}
	
	/**
	 * Checks if is supported type.
	 * 
	 * @param archive
	 *            the archive
	 * @return true, if is supported type
	 */
	public static boolean isSupportedType(final File archive) {
		try {
			ArchiveType.forExtension(FilenameUtils.getExtension(archive.getName()));
			return true;
		} catch (final UnsupportedExtensionException e) {
			return false;
		}
		
	}
	
	/**
	 * Instantiates a new compression utils.
	 * 
	 * @param ignore
	 *            the ignore
	 */
	private CompressionUtils(final Void ignore) {
		// avoid instantion
	}
}
