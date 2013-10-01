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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import lzma.sdk.lzma.Decoder;
import lzma.streams.LzmaInputStream;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import org.mozkito.utilities.io.exceptions.ExternalExecutableException;
import org.mozkito.utilities.io.exceptions.FilePermissionException;

// import ucar.unidata.io.bzip2.CBZip2InputStream;

/**
 * The Class FileUtils.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class FileUtils {
	
	/**
	 * The Enum FileShutdownAction.
	 */
	public static enum FileShutdownAction {
		
		/** The keep. */
		KEEP,
		/** The delete. */
		DELETE
	}
	
	/**
	 * The Enum SupportedPackers.
	 */
	public static enum SupportedPackers {
		
		/** The zip. */
		ZIP,
		/** The jar. */
		JAR,
		/** The lzma. */
		LZMA,
		/** The gz. */
		GZ,
		/** The gzip. */
		GZIP,
		/** The bzip. */
		BZIP,
		/** The BZI p2. */
		BZIP2,
		/** The bz. */
		BZ,
		/** The B z2. */
		BZ2,
		/** The xz. */
		XZ,
		/** The xzip. */
		XZIP,
		/** The tar. */
		TAR;
		
		/**
		 * Gets the string values.
		 * 
		 * @return the string values
		 */
		public static Set<String> getStringValues() {
			final Set<String> result = new HashSet<String>();
			for (final SupportedPackers value : SupportedPackers.values()) {
				result.add(value.name());
			}
			return result;
		}
	}
	
	/** The Constant supportedPackers. */
	public static final Set<String>                   supportedPackers  = SupportedPackers.getStringValues();
	
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				if ((fileManager.get(FileShutdownAction.DELETE) != null)
				        && !fileManager.get(FileShutdownAction.DELETE).isEmpty()) {
					if (Logger.logInfo()) {
						Logger.info("Deleting " + fileManager.get(FileShutdownAction.DELETE).size()
						        + " temporary files using shutdown hook.");
					}
				}
				final Set<File> filesToDelete = new HashSet<File>();
				if (fileManager.containsKey(FileShutdownAction.DELETE)) {
					filesToDelete.addAll(fileManager.get(FileShutdownAction.DELETE));
					for (final File f : filesToDelete) {
						if (f.exists()) {
							if (f.isDirectory()) {
								try {
									if (Logger.logDebug()) {
										Logger.debug("Deleting directory: " + f.getAbsolutePath());
									}
									deleteDirectory(f);
								} catch (final IOException e) {
									if (Logger.logWarn()) {
										Logger.warn(e, "Could not delete directory: '%s'", f.getAbsolutePath());
									}
								}
							} else {
								if (Logger.logDebug()) {
									Logger.debug("Deleting file: " + f.getAbsolutePath());
								}
								if (!f.delete()) {
									try {
										FileUtils.forceDelete(f);
									} catch (final IOException e) {
										if (Logger.logWarn()) {
											Logger.warn(e, "Could not delete file: '%s'", f.getAbsolutePath());
										}
									}
								}
							}
						}
					}
				}
			}
		});
	}
	
	/** The Constant fileSeparator. */
	public static final String                        fileSeparator     = System.getProperty("file.separator");
	
	/** The Constant lineSeparator. */
	public static final String                        lineSeparator     = System.getProperty("line.separator");
	
	/** The Constant pathSeparator. */
	public static final String                        pathSeparator     = System.getProperty("path.separator");
	
	/** The Constant tmpDir. */
	public static final File                          tmpDir            = org.apache.commons.io.FileUtils.getTempDirectory();
	
	/** The max perm. */
	private static int                                MAX_PERM          = 0;
	
	/** The Constant EXECUTABLE. */
	public static final int                           EXECUTABLE        = (int) Math.pow(2, MAX_PERM++);
	
	/** The Constant WRITABLE. */
	public static final int                           WRITABLE          = (int) Math.pow(2, MAX_PERM++);
	
	/** The Constant READABLE. */
	public static final int                           READABLE          = (int) Math.pow(2, MAX_PERM++);
	
	/** The Constant FILE. */
	public static final int                           FILE              = (int) Math.pow(2, MAX_PERM++);
	
	/** The Constant DIRECTORY. */
	public static final int                           DIRECTORY         = (int) Math.pow(2, MAX_PERM++);
	
	/** The Constant EXISTING. */
	public static final int                           EXISTING          = (int) Math.pow(2, MAX_PERM++);
	
	/** The Constant ACCESSIBLE_DIR. */
	public static final int                           ACCESSIBLE_DIR    = EXISTING | DIRECTORY | READABLE | EXECUTABLE;
	
	/** The Constant WRITABLE_DIR. */
	public static final int                           WRITABLE_DIR      = ACCESSIBLE_DIR | WRITABLE;
	
	/** The Constant READABLE_FILE. */
	public static final int                           READABLE_FILE     = EXISTING | FILE | READABLE;
	
	/** The Constant WRITABLE_FILE. */
	public static final int                           WRITABLE_FILE     = FILE | WRITABLE;
	
	/** The Constant OVERWRITABLE_FILE. */
	public static final int                           OVERWRITABLE_FILE = FILE | EXISTING | WRITABLE;
	
	/** The Constant EXECUTABLE_FILE. */
	public static final int                           EXECUTABLE_FILE   = EXISTING | FILE | EXECUTABLE;
	
	/** The file manager. */
	private static Map<FileShutdownAction, Set<File>> fileManager       = new HashMap<FileShutdownAction, Set<File>>();
	
	/**
	 * Adds the to file manager.
	 * 
	 * @param file
	 *            the file
	 * @param shutdownAction
	 *            the shutdown action
	 */
	public static void addToFileManager(final File file,
	                                    final FileShutdownAction shutdownAction) {
		if (!fileManager.containsKey(shutdownAction)) {
			fileManager.put(shutdownAction, new HashSet<File>());
		}
		fileManager.get(shutdownAction).add(file);
	}
	
	// /**
	// * Bunzip2.
	// *
	// * @param bzip2File
	// * the bzip2 file
	// * @param directory
	// * the directory
	// * @throws IOException
	// * Signals that an I/O exception has occurred.
	// * @throws FilePermissionException
	// * the file permission exception
	// */
	// public static void bunzip2(final File bzip2File,
	// final File directory) throws IOException, FilePermissionException {
	// ensureFilePermissions(bzip2File, READABLE_FILE);
	// ensureFilePermissions(directory, WRITABLE_DIR);
	// FileInputStream fis = null;
	// CBZip2InputStream zis = null;
	// try {
	// fis = new FileInputStream(bzip2File);
	// zis = new CBZip2InputStream(fis);
	//
	// final int BUFFER = 2048;
	// final byte[] buffer = new byte[BUFFER];
	// String path = bzip2File.getName();
	//			final int i = path.lastIndexOf("."); //$NON-NLS-1$
	// if (i > 0) {
	// path = directory.getAbsolutePath() + FileUtils.fileSeparator + path.substring(0, i - 1);
	// } else {
	// throw new IOException("Compressed file does not contain a file extension like `.zip`.");
	//
	// }
	//
	// final File outputFile = new File(path);
	// ensureFilePermissions(outputFile, WRITABLE_FILE);
	// final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(outputFile));
	//
	// while ((zis.read(buffer)) != -1) {
	// stream.write(buffer);
	// }
	//
	// stream.flush();
	// stream.close();
	// zis.close();
	// } finally {
	// try {
	// if (fis != null) {
	// fis.close();
	// }
	// } catch (final IOException ignore) {
	// // ignore
	// }
	//
	// try {
	// if (zis != null) {
	// zis.close();
	// }
	// } catch (final IOException ignore) {
	// // ignore
	// }
	// }
	// }
	
	/**
	 * Checks if the command maps to a valid accessible, executable file. If the command is not absolute, a PATH
	 * traversal search for the command is done.
	 * 
	 * @param command
	 *            the command string that shall be analyzed
	 * @return returns the absolute path to the command, if found
	 * @throws ExternalExecutableException
	 *             the external executable exception
	 */
	@NoneNull
	public static String checkExecutable(final String command) throws ExternalExecutableException {
		if (command.startsWith(FileUtils.fileSeparator)
		        || ((command.length() > (2 /* device char + ':' */+ FileUtils.fileSeparator.length()))
		                && (command.charAt(1) == ':') && command.substring(2).startsWith(FileUtils.fileSeparator))) {
			// We got an absolute path here
			final File executable = new File(command);
			if (!executable.exists()) {
				throw new ExternalExecutableException("File `" + command + "` does not exist.");
			} else if (!executable.isFile()) {
				if (executable.isDirectory()) {
					throw new ExternalExecutableException("Command `" + command + "` is a directory.");
				}
				throw new ExternalExecutableException("Command `" + command + "` is not a file.");
			} else if (!executable.canExecute()) {
				throw new ExternalExecutableException("File `" + command + "` is not executable.");
			}
			return command;
		}
		// relative path
		final String pathVariable = System.getenv("PATH");
		final String[] paths = pathVariable.split(":");
		File executable;
		
		for (final String path : paths) {
			executable = new File(path + FileUtils.fileSeparator + command);
			if (executable.exists() && executable.isFile() && executable.canExecute()) {
				return executable.getAbsolutePath();
			}
		}
		
		throw new ExternalExecutableException("Command `" + command + "` could not be found in PATH=" + pathVariable);
	}
	
	/**
	 * Copy file to directory.
	 * 
	 * @param srcFile
	 *            the src file
	 * @param destDir
	 *            the dest dir
	 * @param shutdownAction
	 *            the shutdown action
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@NoneNull
	public static void copyFileToDirectory(final File srcFile,
	                                       final File destDir,
	                                       final FileShutdownAction shutdownAction) throws IOException {
		
		org.apache.commons.io.FileUtils.copyFileToDirectory(srcFile, destDir);
		
		final String filename = srcFile.getName();
		final File copiedFile = new File(destDir.getAbsolutePath() + FileUtils.fileSeparator + filename);
		if (!copiedFile.exists()) {
			if (Logger.logWarn()) {
				Logger.warn("Requested file copy operation (file " + srcFile.getAbsolutePath() + " into dir "
				        + destDir.getAbsolutePath() + " might be incomplete!");
			}
		} else {
			addToFileManager(copiedFile, shutdownAction);
		}
	}
	
	/**
	 * Creates the dir.
	 * 
	 * @param directory
	 *            the directory
	 * @param shutdownAction
	 *            the shutdown action
	 * @return the file
	 * @throws FilePermissionException
	 *             the file permission exception
	 */
	public static File createDir(final File directory,
	                             final FileShutdownAction shutdownAction) throws FilePermissionException {
		final String dirName = directory.getAbsolutePath();
		final int index = dirName.lastIndexOf(FileUtils.fileSeparator);
		return createDir(new File(dirName.substring(0, index)), dirName.substring(index), shutdownAction);
	}
	
	/**
	 * Created a directory in parent directory with given name. If the directory exists before, the file handle to the
	 * existing directory will be returned and a warning message logged.
	 * 
	 * @param parentDir
	 *            the parent directory
	 * @param name
	 *            the name of the new directory to be created
	 * @param shutdownAction
	 *            the shutdown action
	 * @return the file handle corresponding to the requested new directory if existed or created. <code>null</code>
	 *         otherwise.
	 * @throws FilePermissionException
	 *             the file permission exception
	 */
	public static File createDir(final File parentDir,
	                             final String name,
	                             final FileShutdownAction shutdownAction) throws FilePermissionException {
		
		ensureFilePermissions(parentDir, WRITABLE_DIR);
		
		final File newDir = new File(parentDir.getAbsolutePath() + FileUtils.fileSeparator + name);
		if (newDir.exists()) {
			if (newDir.isDirectory()) {
				
				if (Logger.logWarn()) {
					Logger.warn("Did not create directory `" + name + "` in parent directory `"
					        + parentDir.getAbsolutePath()
					        + "`. Reason: directory exists already. Returning existing directory.");
				}
				addToFileManager(newDir, shutdownAction);
				return newDir;
			}
			if (Logger.logError()) {
				Logger.error("Could not create directory `" + name + "` in parent directory `"
				        + parentDir.getAbsolutePath() + "`. Reason: path exists already as files.");
			}
			return null;
		}
		if (!newDir.mkdirs()) {
			if (Logger.logError()) {
				Logger.error("Could not create directory `" + name + "` in parent directory `"
				        + parentDir.getAbsolutePath() + "`. Reason: permission denied.");
			}
			return null;
		}
		if (Logger.logDebug()) {
			Logger.debug("Created temp directory `" + name + "` in parent directory `" + parentDir.getAbsolutePath());
		}
		addToFileManager(newDir, shutdownAction);
		return newDir;
	}
	
	/**
	 * Creates the random dir.
	 * 
	 * @param parentDir
	 *            the parent dir
	 * @param prefix
	 *            the prefix
	 * @param suffix
	 *            the suffix
	 * @param shutdownAction
	 *            the shutdown action
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File createRandomDir(final File parentDir,
	                                   final String prefix,
	                                   final String suffix,
	                                   final FileShutdownAction shutdownAction) throws IOException {
		
		final File file = File.createTempFile(prefix, suffix, parentDir);
		if (!file.delete()) {
			throw new IOException("Could not delete random file `" + file.getAbsolutePath() + "`");
		}
		if (!file.mkdirs()) {
			throw new IOException("Could not create random directory `" + file.getAbsolutePath() + "`");
		}
		addToFileManager(file, shutdownAction);
		return file;
	}
	
	/**
	 * Creates the random dir.
	 * 
	 * @param prefix
	 *            the prefix
	 * @param suffix
	 *            the suffix
	 * @param shutdownAction
	 *            the shutdown action
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File createRandomDir(final String prefix,
	                                   
	                                   final String suffix,
	                                   final FileShutdownAction shutdownAction) throws IOException {
		return createRandomDir(tmpDir, prefix, suffix, shutdownAction);
		
	}
	
	/**
	 * Creates a new temporary file.
	 * 
	 * @param shutdownAction
	 *            the shutdown action
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File createRandomFile(final FileShutdownAction shutdownAction) throws IOException {
		final File file = File.createTempFile("tempfile", String.valueOf(new DateTime().getMillis()), tmpDir); //$NON-NLS-1$
		addToFileManager(file, shutdownAction);
		return file;
		
	}
	
	/**
	 * Creates the random file.
	 * 
	 * @param prefix
	 *            the prefix
	 * @param suffix
	 *            the suffix
	 * @param shutdownAction
	 *            the shutdown action
	 * @return the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File createRandomFile(final String prefix,
	                                    final String suffix,
	                                    final FileShutdownAction shutdownAction) throws IOException {
		final File file = File.createTempFile(prefix, suffix);
		addToFileManager(file, shutdownAction);
		return file;
		
	}
	
	/**
	 * Delete directory. See also {@link org.apache.commons.io.FileUtils#deleteDirectory(File)}
	 * 
	 * @param directory
	 *            the directory
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * 
	 * 
	 */
	public static void deleteDirectory(final File directory) throws IOException {
		org.apache.commons.io.FileUtils.deleteDirectory(directory);
		removeFromFileManager(directory);
	}
	
	/**
	 * Dump.
	 * 
	 * @param data
	 *            the data
	 * @param file
	 *            the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void dump(final byte[] data,
	                        final File file) throws IOException {
		final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
		stream.write(data);
		stream.flush();
		stream.close();
	}
	
	/**
	 * Ensure file permissions.
	 * 
	 * @param file
	 *            the file
	 * @param permissions
	 *            the permissions
	 * @throws FilePermissionException
	 *             the file permission exception
	 */
	public static void ensureFilePermissions(@NotNull final File file,
	                                         final int permissions) throws FilePermissionException {
		CompareCondition.less(permissions, getMAX_PERM(),
		                      "Filepermission alias must be less then the maximum known alias bitmask.");
		
		int localPermissions = permissions;
		if (((localPermissions &= EXISTING) != 0) && !file.exists()) {
			throw new FilePermissionException("`" + file.getAbsolutePath() + "` is not a directory.");
		}
		
		if (((localPermissions &= READABLE) != 0) && !file.canRead()) {
			throw new FilePermissionException("File `" + file.getAbsolutePath() + "` is not readable.");
		}
		
		if (((localPermissions &= EXECUTABLE) != 0) && !file.canExecute()) {
			throw new FilePermissionException("File `" + file.getAbsolutePath() + "` is not executable.");
		}
		
		if (((localPermissions &= WRITABLE) != 0) && !file.canWrite()) {
			throw new FilePermissionException("File `" + file.getAbsolutePath() + "` is not writable.");
		}
		
		if (((localPermissions &= FILE) != 0) && !file.isFile()) {
			throw new FilePermissionException("`" + file.getAbsolutePath() + "` is not a file.");
		}
		
		if (((localPermissions &= DIRECTORY) != 0) && !file.isDirectory()) {
			throw new FilePermissionException("`" + file.getAbsolutePath() + "` is not a directory.");
		}
	}
	
	/**
	 * File to lines.
	 * 
	 * @param file
	 *            the file
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static List<String> fileToLines(final File file) throws IOException {
		return org.apache.commons.io.FileUtils.readLines(file);
	}
	
	/**
	 * Find files.
	 * 
	 * @param topLevelDir
	 *            the top level dir
	 * @param filter
	 *            the filter
	 * @return the iterator
	 */
	public static Iterator<File> findFiles(final File topLevelDir,
	                                       final IOFileFilter filter) {
		if (!topLevelDir.isDirectory()) {
			return null;
		}
		
		if (filter != null) {
			return org.apache.commons.io.FileUtils.iterateFiles(topLevelDir, filter, new IOFileFilter() {
				
				@Override
				public boolean accept(final File file) {
					return true;
				}
				
				@Override
				public boolean accept(final File dir,
				                      final String name) {
					return true;
				}
			});
		}
		return org.apache.commons.io.FileUtils.iterateFiles(topLevelDir, new IOFileFilter() {
			
			@Override
			public boolean accept(final File file) {
				return true;
			}
			
			@Override
			public boolean accept(final File dir,
			                      final String name) {
				return true;
			}
		}, new IOFileFilter() {
			
			@Override
			public boolean accept(final File file) {
				return true;
			}
			
			@Override
			public boolean accept(final File dir,
			                      final String name) {
				return true;
			}
		});
	}
	
	/**
	 * Find files.
	 * 
	 * @param topLevelDir
	 *            the top level dir
	 * @param name
	 *            the name
	 * @return the iterator
	 */
	public static Iterator<File> findFiles(final File topLevelDir,
	                                       final String name) {
		return findFiles(topLevelDir, new IOFileFilter() {
			
			@Override
			public boolean accept(final File file) {
				if (file.getName().equals(name)) {
					return true;
				}
				return false;
			}
			
			@Override
			public boolean accept(final File dir,
			                      final String name) {
				return false;
			}
		});
	}
	
	/**
	 * Force delete. See also {@link org.apache.commons.io.FileUtils#forceDelete(File)}.
	 * 
	 * @param file
	 *            the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void forceDelete(final File file) throws IOException {
		org.apache.commons.io.FileUtils.forceDelete(file);
		fileManager.remove(file);
	}
	
	/**
	 * Force delete on exit. See also {@link org.apache.commons.io.FileUtils#forceDeleteOnExit(File)}.
	 * 
	 * @param file
	 *            the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @deprecated Does not work anyway. Reposuite now uses a file manager that deleted open file handles (marked to be
	 *             deleted) at termination anyway. Usage is implicit when using ToolChain.
	 * 
	 *             Force delete on exit.
	 */
	@Deprecated
	public static void forceDeleteOnExit(final File file) throws IOException {
		org.apache.commons.io.FileUtils.forceDeleteOnExit(file);
	}
	
	/**
	 * Iterate files in a directory.
	 * 
	 * @param directory
	 *            Directory to start in
	 * @param extensions
	 *            List of file extensions to be matched
	 * @param recursive
	 *            Iterate recursively other the sub-directories
	 * @return the file iterator
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Iterator<File> getFileIterator(final File directory,
	                                             final String[] extensions,
	                                             final boolean recursive) throws IOException {
		return org.apache.commons.io.FileUtils.iterateFiles(directory, extensions, recursive);
	}
	
	/**
	 * Gets the line iterator.
	 * 
	 * @param file
	 *            the file
	 * @return the line iterator
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static LineIterator getLineIterator(final File file) throws IOException {
		return org.apache.commons.io.FileUtils.lineIterator(file);
	}
	
	/**
	 * Gets the managed open files.
	 * 
	 * @return the managed open files
	 */
	public static Map<FileShutdownAction, Set<File>> getManagedOpenFiles() {
		final Map<FileShutdownAction, Set<File>> openFiles = new HashMap<FileShutdownAction, Set<File>>();
		for (final FileShutdownAction key : fileManager.keySet()) {
			openFiles.put(key, new HashSet<File>(fileManager.get(key)));
		}
		return openFiles;
	}
	
	/**
	 * Gets the mA x_ perm.
	 * 
	 * @return 2 to the power of MAX_PERM
	 */
	public static final int getMAX_PERM() {
		return (int) Math.pow(2, MAX_PERM);
	}
	
	/**
	 * Gets the recursive directories.
	 * 
	 * @param baseDirectory
	 *            the base directory
	 * @return the recursive directories
	 */
	public static List<File> getRecursiveDirectories(final File baseDirectory) {
		final List<File> list = new LinkedList<File>();
		for (final String subDirectoryPath : baseDirectory.list()) {
			final File subDirectory = new File(baseDirectory.getAbsolutePath() + FileUtils.fileSeparator
			        + subDirectoryPath);
			if (subDirectory.isDirectory() && subDirectory.canExecute() && subDirectory.canRead()) {
				list.add(subDirectory);
				list.addAll(getRecursiveDirectories(subDirectory));
			}
		}
		return list;
	}
	
	/**
	 * This method returns an file iterator the iterates over sub-directories only.
	 * 
	 * @param topLevelDir
	 *            the top level dir
	 * @return An valid file iterator if given top level directory exists and is a directory. Null otherwise.
	 */
	@NoneNull
	public static Iterator<File> getSubDirectoryIterator(final File topLevelDir) {
		if ((!topLevelDir.exists()) || (!topLevelDir.isDirectory())) {
			return null;
		}
		return org.apache.commons.io.FileUtils.iterateFiles(topLevelDir, new IOFileFilter() {
			
			@Override
			public boolean accept(final File file) {
				return false;
			}
			
			@Override
			public boolean accept(final File dir,
			                      final String name) {
				return false;
			}
		}, new IOFileFilter() {
			
			@Override
			public boolean accept(final File file) {
				return true;
			}
			
			@Override
			public boolean accept(final File dir,
			                      final String name) {
				return true;
			}
			
		});
	}
	
	/**
	 * Gets the unpacked name.
	 * 
	 * @param file
	 *            the file
	 * @return the unpacked name
	 */
	public static String getUnpackedName(final File file) {
		final String[] split = file.getName().split("\\.");
		
		int i = split.length - 1;
		
		while (i > 0) {
			if (supportedPackers.contains(split[i].toUpperCase())) {
				--i;
			} else {
				break;
			}
		}
		
		return StringUtils.join(split, '.', 0, i + 1);
	}
	
	/**
	 * Gunzip.
	 * 
	 * @param gzipFile
	 *            the gzip file
	 * @param directory
	 *            the directory
	 * @throws FilePermissionException
	 *             the file permission exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void gunzip(final File gzipFile,
	                          final File directory) throws FilePermissionException, IOException {
		ensureFilePermissions(gzipFile, READABLE_FILE);
		ensureFilePermissions(directory, WRITABLE_DIR);
		
		final FileInputStream fis = new FileInputStream(gzipFile);
		final GZIPInputStream zis = new GZIPInputStream(fis);
		final int BUFFER = 2048;
		final byte[] buffer = new byte[BUFFER];
		String path = gzipFile.getName();
		final int i = path.lastIndexOf(".");
		if (i > 0) {
			path = directory.getAbsolutePath() + FileUtils.fileSeparator + path.substring(0, i - 1);
		} else {
			zis.close();
			throw new IOException("Compressed file does not contain a file extension like `.zip`.");
		}
		
		final File outputFile = new File(path);
		ensureFilePermissions(outputFile, WRITABLE_FILE);
		final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(outputFile));
		
		while ((zis.read(buffer)) != -1) {
			stream.write(buffer);
		}
		
		stream.flush();
		stream.close();
		zis.close();
	}
	
	/**
	 * List files. @see
	 * 
	 * @param directory
	 *            the directory
	 * @param extensions
	 *            the extensions
	 * @param recursive
	 *            the recursive
	 * @return the collection {@link org.apache.commons.io.FileUtils#listFiles(File, String[], boolean)}
	 */
	public static Collection<File> listFiles(final File directory,
	                                         final String[] extensions,
	                                         final boolean recursive) {
		return org.apache.commons.io.FileUtils.listFiles(directory, extensions, recursive);
	}
	
	/**
	 * Permissions to string.
	 * 
	 * @param file
	 *            the file
	 * @return the string
	 */
	public static String permissionsToString(final File file) {
		final StringBuilder builder = new StringBuilder();
		
		if (file.exists()) {
			
			if (file.isDirectory()) {
				builder.append('d');
			}
			
			if (file.canRead()) {
				builder.append('r');
			}
			
			if (file.canWrite()) {
				builder.append('w');
			}
			
			if (file.canExecute()) {
				builder.append('x');
			}
		} else {
			builder.append("not existing");
		}
		
		return builder.toString();
	}
	
	/**
	 * Read file to string.
	 * 
	 * @param file
	 *            the file
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String readFileToString(final File file) throws IOException {
		return org.apache.commons.io.FileUtils.readFileToString(file);
	}
	
	/**
	 * Removes the from file manager.
	 * 
	 * @param file
	 *            the file
	 */
	private static void removeFromFileManager(final File file) {
		for (final FileShutdownAction key : fileManager.keySet()) {
			fileManager.get(key).remove(file);
		}
	}
	
	/**
	 * Unlzma.
	 * 
	 * @param lzmaFile
	 *            the lzma file
	 * @param directory
	 *            the directory
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws FilePermissionException
	 *             the file permission exception
	 */
	public static void unlzma(final File lzmaFile,
	                          final File directory) throws IOException, FilePermissionException {
		ensureFilePermissions(lzmaFile, READABLE_FILE);
		ensureFilePermissions(directory, WRITABLE_DIR);
		
		final int BUFFER = 2048;
		final FileInputStream fis = new FileInputStream(lzmaFile);
		final LzmaInputStream zis = new LzmaInputStream(new BufferedInputStream(fis), new Decoder());
		final byte[] buffer = new byte[BUFFER];
		String path = lzmaFile.getName();
		final int i = path.lastIndexOf(".");
		if (i > 0) {
			path = directory.getAbsolutePath() + FileUtils.fileSeparator + path.substring(0, i - 1);
		} else {
			zis.close();
			throw new IOException("Compressed file does not contain a file extension like `.zip`.");
		}
		
		final File outputFile = new File(path);
		ensureFilePermissions(outputFile, WRITABLE_FILE);
		final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(outputFile));
		
		while ((zis.read(buffer)) != -1) {
			stream.write(buffer);
		}
		
		stream.flush();
		stream.close();
		zis.close();
		
	}
	
	/**
	 * Unpack.
	 * 
	 * @param packedFile
	 *            the packed file
	 * @param directory
	 *            the directory
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws FilePermissionException
	 *             the file permission exception
	 */
	public static void unpack(final File packedFile,
	                          final File directory) throws IOException, FilePermissionException {
		String[] split;
		
		final String path = packedFile.getCanonicalPath();
		split = path.split("\\.");
		String format = null;
		if (split.length > 0) {
			format = split[split.length - 1];
		} else {
			throw new IOException("File does not have an extension.");
		}
		
		if (format.equalsIgnoreCase("ZIP") || format.equalsIgnoreCase("JAR")) {
			unzip(packedFile, directory);
		} else if (format.equalsIgnoreCase("XZ") || format.equalsIgnoreCase("LZMA") || format.equalsIgnoreCase("XZIP")) {
			unlzma(packedFile, directory);
			if (split.length > 1) {
				format = split[split.length - 2];
				if (format.equalsIgnoreCase("TAR")) {
					untar(new File(path.substring(0, path.length() - split[split.length - 1].length() - 1)), directory);
				}
			}
		} else if (format.equalsIgnoreCase("GZ") || format.equalsIgnoreCase("GZIP")) {
			gunzip(packedFile, directory);
			if (split.length > 1) {
				format = split[split.length - 2];
				if (format.equalsIgnoreCase("TAR")) {
					untar(new File(path.substring(0, path.length() - split[split.length - 1].length() - 1)), directory);
				}
			}
		} else if (format.equalsIgnoreCase("BZ") || format.equalsIgnoreCase("BZIP") || format.equalsIgnoreCase("BZIP2")
		        || format.equalsIgnoreCase("BZ2")) {
			// bunzip2(packedFile, directory);
			if (split.length > 1) {
				format = split[split.length - 2];
				if (format.equalsIgnoreCase("TAR")) {
					untar(new File(path.substring(0, path.length() - split[split.length - 1].length() - 1)), directory);
				}
			}
		}
		
	}
	
	/**
	 * Untar.
	 * 
	 * @param tarFile
	 *            the tar file
	 * @param directory
	 *            the directory
	 * @throws FilePermissionException
	 *             the file permission exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void untar(final File tarFile,
	                         final File directory) throws FilePermissionException, IOException {
		ensureFilePermissions(tarFile, READABLE_FILE);
		ensureFilePermissions(directory, WRITABLE_DIR);
		
		final int BUFFER = 2048;
		BufferedOutputStream dest = null;
		final FileInputStream fis = new FileInputStream(tarFile);
		final TarArchiveInputStream zis = new TarArchiveInputStream(new BufferedInputStream(fis));
		
		try {
			ArchiveEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					final File newDir = new File(directory.getAbsolutePath() + FileUtils.fileSeparator
					        + entry.getName());
					final boolean success = newDir.exists() || newDir.mkdirs();
					if (!success) {
						throw new IOException("Creating directory failed: " + directory.getAbsolutePath()
						        + FileUtils.fileSeparator + entry.getName());
					}
					continue;
				}
				if (Logger.logDebug()) {
					Logger.debug("Extracting: " + entry);
				}
				int count;
				final byte[] data = new byte[BUFFER];
				// write the files to the disk
				final FileOutputStream fos = new FileOutputStream(new File(directory.getAbsolutePath()
				        + FileUtils.fileSeparator + entry.getName()));
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
		} finally {
			zis.close();
		}
		
	}
	
	/**
	 * Unzips a given file to the specified directory.
	 * 
	 * @param zipFile
	 *            the zip compressed file, not null
	 * @param directory
	 *            the target directory, not null
	 * @throws FilePermissionException
	 *             the file permission exception
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@NoneNull
	public static void unzip(final File zipFile,
	                         final File directory) throws FilePermissionException, FileNotFoundException, IOException {
		ensureFilePermissions(zipFile, READABLE_FILE);
		ensureFilePermissions(directory, WRITABLE_DIR);
		
		final int BUFFER = 2048;
		BufferedOutputStream dest = null;
		final FileInputStream fis = new FileInputStream(zipFile);
		final ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		
		try {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					final File newDir = new File(directory.getAbsolutePath() + FileUtils.fileSeparator
					        + entry.getName());
					final boolean success = newDir.exists() || newDir.mkdirs();
					if (!success) {
						throw new IOException("Creating directory failed: " + directory.getAbsolutePath()
						        + FileUtils.fileSeparator + entry.getName());
					}
					continue;
				}
				if (Logger.logDebug()) {
					Logger.debug("Extracting: " + entry);
				}
				int count;
				final byte[] data = new byte[BUFFER];
				// write the files to the disk
				final FileOutputStream fos = new FileOutputStream(new File(directory.getAbsolutePath()
				        + FileUtils.fileSeparator + entry.getName()));
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
		} finally {
			zis.close();
		}
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the simple class name
	 */
	public String getClassName() {
		return this.getClass().getSimpleName();
	}
	
}
