package net.ownhero.dev.ioda;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import net.ownhero.dev.ioda.exceptions.ExternalExecutableException;
import net.ownhero.dev.ioda.exceptions.FilePermissionException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.joda.time.DateTime;

import ucar.unidata.io.bzip2.CBZip2InputStream;

/**
 * The Class FileUtils.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class FileUtils {
	
	public static enum FileShutdownAction {
		KEEP, DELETE
	}
	
	public static enum SupportedPackers {
		ZIP, JAR, LZMA, GZ, GZIP, BZIP, BZIP2, BZ, BZ2, XZ, XZIP;
	}
	
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
										Logger.warn("Could not delete directory: " + f.getAbsolutePath(), e);
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
											Logger.warn("Could not delete file: " + f.getAbsolutePath(), e);
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
	
	public static final String                        fileSeparator     = System.getProperty("file.separator");
	public static final String                        lineSeparator     = System.getProperty("line.separator");
	public static final String                        pathSeparator     = System.getProperty("path.separator");
	public static final File                          tmpDir            = org.apache.commons.io.FileUtils.getTempDirectory();
	
	private static int                                MAX_PERM          = 0;
	
	public static final int                           EXECUTABLE        = (int) Math.pow(2, MAX_PERM++);
	public static final int                           WRITABLE          = (int) Math.pow(2, MAX_PERM++);
	public static final int                           READABLE          = (int) Math.pow(2, MAX_PERM++);
	public static final int                           FILE              = (int) Math.pow(2, MAX_PERM++);
	public static final int                           DIRECTORY         = (int) Math.pow(2, MAX_PERM++);
	public static final int                           EXISTING          = (int) Math.pow(2, MAX_PERM++);
	public static final int                           ACCESSIBLE_DIR    = EXISTING | DIRECTORY | READABLE | EXECUTABLE;
	public static final int                           WRITABLE_DIR      = ACCESSIBLE_DIR | WRITABLE;
	public static final int                           READABLE_FILE     = EXISTING | FILE | READABLE;
	public static final int                           WRITABLE_FILE     = FILE | WRITABLE;
	public static final int                           OVERWRITABLE_FILE = FILE | EXISTING | WRITABLE;
	public static final int                           EXECUTABLE_FILE   = EXISTING | FILE | EXECUTABLE;
	
	private static Map<FileShutdownAction, Set<File>> fileManager       = new HashMap<FileShutdownAction, Set<File>>();
	
	public static void addToFileManager(final File file,
	                                    final FileShutdownAction shutdownAction) {
		if (!fileManager.containsKey(shutdownAction)) {
			fileManager.put(shutdownAction, new HashSet<File>());
		}
		fileManager.get(shutdownAction).add(file);
	}
	
	/**
	 * @param bzip2File
	 * @param directory
	 * @return
	 */
	public static boolean bunzip2(final File bzip2File,
	                              final File directory) {
		try {
			ensureFilePermissions(bzip2File, READABLE_FILE);
			ensureFilePermissions(directory, WRITABLE_DIR);
			
			final FileInputStream fis = new FileInputStream(bzip2File);
			final CBZip2InputStream zis = new CBZip2InputStream(fis);
			
			final int BUFFER = 2048;
			final byte[] buffer = new byte[BUFFER];
			String path = bzip2File.getName();
			final int i = path.lastIndexOf(".");
			if (i > 0) {
				path = directory.getAbsolutePath() + FileUtils.fileSeparator + path.substring(0, i - 1);
			} else {
				// TODO error
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
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		} catch (final FilePermissionException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if the command maps to a valid accessible, executable file. If the
	 * command is not absolute, a PATH traversal search for the command is done.
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
				} else {
					throw new ExternalExecutableException("Command `" + command + "` is not a file.");
				}
			} else if (!executable.canExecute()) {
				throw new ExternalExecutableException("File `" + command + "` is not executable.");
			}
			return command;
		} else {
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
			
			throw new ExternalExecutableException("Command `" + command + "` could not be found in PATH="
			        + pathVariable);
		}
	}
	
	/**
	 * @param srcFile
	 * @param destDir
	 * @throws IOException
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
	 * @param directory
	 * @param shutdownAction
	 * @return
	 */
	public static File createDir(final File directory,
	                             final FileShutdownAction shutdownAction) {
		final String dirName = directory.getAbsolutePath();
		final int index = dirName.lastIndexOf(FileUtils.fileSeparator);
		return createDir(new File(dirName.substring(0, index)), dirName.substring(index), shutdownAction);
	}
	
	/**
	 * Created a directory in parent directory with given name. If the directory
	 * exists before, the file handle to the existing directory will be returned
	 * and a warning message logged.
	 * 
	 * @param parentDir
	 *            the parent directory
	 * @param name
	 *            the name of the new directory to be created
	 * @return the file handle corresponding to the requested new directory if
	 *         existed or created. <code>null</code> otherwise.
	 */
	public static File createDir(final File parentDir,
	                             final String name,
	                             final FileShutdownAction shutdownAction) {
		
		try {
			ensureFilePermissions(parentDir, WRITABLE_DIR);
		} catch (final FilePermissionException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return null;
		}
		
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
			} else {
				if (Logger.logError()) {
					Logger.error("Could not create directory `" + name + "` in parent directory `"
					        + parentDir.getAbsolutePath() + "`. Reason: path exists already as files.");
				}
				return null;
			}
		}
		if (!newDir.mkdirs()) {
			if (Logger.logError()) {
				Logger.error("Could not create directory `" + name + "` in parent directory `"
				        + parentDir.getAbsolutePath() + "`. Reason: permission denied.");
			}
			return null;
		} else {
			if (Logger.logInfo()) {
				Logger.info("Created temp directory `" + name + "` in parent directory `" + parentDir.getAbsolutePath());
			}
			addToFileManager(newDir, shutdownAction);
			return newDir;
		}
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
	 * @return the file
	 */
	public static File createRandomDir(final File parentDir,
	                                   final String prefix,
	                                   final String suffix,
	                                   final FileShutdownAction shutdownAction) {
		
		try {
			final File file = File.createTempFile(prefix, suffix, parentDir);
			if (!file.delete()) {
				if (Logger.logError()) {
					Logger.error("Could not delete random file `" + file.getAbsolutePath() + "`");
				}
				return null;
			}
			if (!file.mkdirs()) {
				if (Logger.logError()) {
					Logger.error("Could not create random directory `" + file.getAbsolutePath() + "`");
				}
				return null;
			}
			addToFileManager(file, shutdownAction);
			return file;
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error("Could not create random file in `" + tmpDir.getAbsolutePath() + "`");
			}
			return null;
		}
	}
	
	/**
	 * Creates the random dir.
	 * 
	 * @param prefix
	 *            the prefix
	 * @param suffix
	 *            the suffix
	 * @return the file
	 */
	public static File createRandomDir(final String prefix,
	                                   
	                                   final String suffix,
	                                   final FileShutdownAction shutdownAction) {
		return createRandomDir(tmpDir, prefix, suffix, shutdownAction);
		
	}
	
	/**
	 * Creates a new temporary file.
	 * 
	 * @return the file
	 */
	public static File createRandomFile(final FileShutdownAction shutdownAction) {
		try {
			final File file = File.createTempFile("reposuite", String.valueOf(new DateTime().getMillis()), tmpDir);
			addToFileManager(file, shutdownAction);
			return file;
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return null;
		}
	}
	
	/**
	 * @param prefix
	 * @param suffix
	 * @param shutdownAction
	 * @return
	 */
	public static File createRandomFile(final String prefix,
	                                    final String suffix,
	                                    final FileShutdownAction shutdownAction) {
		try {
			final File file = File.createTempFile(prefix, suffix);
			addToFileManager(file, shutdownAction);
			return file;
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return null;
		}
	}
	
	/**
	 * Delete directory.
	 * 
	 * @param directory
	 *            the directory
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see {@link org.apache.commons.io.FileUtils#deleteDirectory(File)}
	 */
	public static void deleteDirectory(final File directory) throws IOException {
		org.apache.commons.io.FileUtils.deleteDirectory(directory);
		removeFromFileManager(directory);
	}
	
	/**
	 * @param data
	 * @param file
	 * @throws IOException
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
	                                         int permissions) throws FilePermissionException {
		CompareCondition.less(permissions, getMAX_PERM(),
		                      "Filepermission alias must be less then the maximum known alias bitmask.");
		
		if (((permissions &= EXISTING) != 0) && !file.exists()) {
			throw new FilePermissionException("`" + file.getAbsolutePath() + "` is not a directory.");
		}
		
		if (((permissions &= READABLE) != 0) && !file.canRead()) {
			throw new FilePermissionException("File `" + file.getAbsolutePath() + "` is not readable.");
		}
		
		if (((permissions &= EXECUTABLE) != 0) && !file.canExecute()) {
			throw new FilePermissionException("File `" + file.getAbsolutePath() + "` is not executable.");
		}
		
		if (((permissions &= WRITABLE) != 0) && !file.canWrite()) {
			throw new FilePermissionException("File `" + file.getAbsolutePath() + "` is not writable.");
		}
		
		if (((permissions &= FILE) != 0) && !file.isFile()) {
			throw new FilePermissionException("`" + file.getAbsolutePath() + "` is not a file.");
		}
		
		if (((permissions &= DIRECTORY) != 0) && !file.isDirectory()) {
			throw new FilePermissionException("`" + file.getAbsolutePath() + "` is not a directory.");
		}
	}
	
	/**
	 * File to lines.
	 * 
	 * @param file
	 *            the file
	 * @return the list
	 */
	public static List<String> fileToLines(final File file) {
		try {
			return org.apache.commons.io.FileUtils.readLines(file);
		} catch (final IOException e) {
			return null;
		}
	}
	
	/**
	 * @param topLevelDir
	 * @param filter
	 * @return
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
		} else {
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
	}
	
	/**
	 * @param topLevelDir
	 * @param name
	 * @return
	 */
	public static Iterator<File> findFiles(final File topLevelDir,
	                                       final String name) {
		return findFiles(topLevelDir, new IOFileFilter() {
			
			@Override
			public boolean accept(final File file) {
				if (file.getName().equals(name)) {
					return true;
				} else {
					return false;
				}
			}
			
			@Override
			public boolean accept(final File dir,
			                      final String name) {
				return false;
			}
		});
	}
	
	/**
	 * Force delete.
	 * 
	 * @param file
	 *            the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see {@link org.apache.commons.io.FileUtils#forceDelete(File)}
	 */
	public static void forceDelete(final File file) throws IOException {
		org.apache.commons.io.FileUtils.forceDelete(file);
		fileManager.remove(file);
	}
	
	/**
	 * @deprecated Does not work anyway. Reposuite now uses a file manager that
	 *             deleted open file handles (marked to be deleted) at
	 *             termination anyway. Usage is implicit when using ToolChain.
	 * 
	 *             Force delete on exit.
	 * 
	 * @param file
	 *            the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see {@link org.apache.commons.io.FileUtils#forceDeleteOnExit(File)}
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
	 * @return
	 * @throws IOException
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
	 * 
	 * This method returns an file iterator the iterates over sub-directories
	 * only.
	 * 
	 * @param topLevelDir
	 * @return An valid file iterator if given top level directory exists and is
	 *         a directory. Null otherwise.
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
	 * @param gzipFile
	 * @param directory
	 * @return
	 */
	public static boolean gunzip(final File gzipFile,
	                             final File directory) {
		try {
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
				// TODO error
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
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		} catch (final FilePermissionException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		}
		return true;
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
	 * @return the collection
	 *         {@link org.apache.commons.io.FileUtils#listFiles(File, String[], boolean)}
	 */
	public static Collection<File> listFiles(final File directory,
	                                         final String[] extensions,
	                                         final boolean recursive) {
		return org.apache.commons.io.FileUtils.listFiles(directory, extensions, recursive);
	}
	
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
	
	public static String readFileToString(final File file) throws IOException {
		return org.apache.commons.io.FileUtils.readFileToString(file);
	}
	
	private static void removeFromFileManager(final File file) {
		for (final FileShutdownAction key : fileManager.keySet()) {
			fileManager.get(key).remove(file);
		}
	}
	
	/**
	 * @param lzmaFile
	 * @param directory
	 * @return
	 */
	public static boolean unlzma(final File lzmaFile,
	                             final File directory) {
		try {
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
				// TODO error
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
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		} catch (final FilePermissionException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		}
		return true;
	}
	
	public static boolean unpack(final File packedFile,
	                             final File directory) {
		boolean success = false;
		String[] split;
		try {
			final String path = packedFile.getCanonicalPath();
			split = path.split("\\.");
			String format = null;
			if (split.length > 0) {
				format = split[split.length - 1];
			} else {
				return false;
			}
			if (format.equalsIgnoreCase("ZIP") || format.equalsIgnoreCase("JAR")) {
				return unzip(packedFile, directory);
			} else if (format.equalsIgnoreCase("XZ") || format.equalsIgnoreCase("LZMA")
			        || format.equalsIgnoreCase("XZIP")) {
				success = unlzma(packedFile, directory);
				if (success) {
					if (split.length > 1) {
						format = split[split.length - 2];
						if (format.equalsIgnoreCase("TAR")) {
							return untar(new File(path.substring(0, path.length() - split[split.length - 1].length()
							                     - 1)), directory);
						}
					}
				} else {
					return false;
				}
			} else if (format.equalsIgnoreCase("GZ") || format.equalsIgnoreCase("GZIP")) {
				success = gunzip(packedFile, directory);
				if (success) {
					if (split.length > 1) {
						format = split[split.length - 2];
						if (format.equalsIgnoreCase("TAR")) {
							return untar(new File(path.substring(0, path.length() - split[split.length - 1].length()
							                     - 1)), directory);
						}
					}
				} else {
					return false;
				}
			} else if (format.equalsIgnoreCase("BZ") || format.equalsIgnoreCase("BZIP")
			        || format.equalsIgnoreCase("BZIP2") || format.equalsIgnoreCase("BZ2")) {
				success = bunzip2(packedFile, directory);
				if (success) {
					if (split.length > 1) {
						format = split[split.length - 2];
						if (format.equalsIgnoreCase("TAR")) {
							return untar(new File(path.substring(0, path.length() - split[split.length - 1].length()
							                     - 1)), directory);
						}
					}
				} else {
					return false;
				}
			}
		} catch (final IOException e) {
			return false;
		}
		return success;
		
	}
	
	/**
	 * @param tarFile
	 * @param directory
	 * @return
	 */
	public static boolean untar(final File tarFile,
	                            final File directory) {
		try {
			ensureFilePermissions(tarFile, READABLE_FILE);
			ensureFilePermissions(directory, WRITABLE_DIR);
			
			final int BUFFER = 2048;
			BufferedOutputStream dest = null;
			final FileInputStream fis = new FileInputStream(tarFile);
			final TarArchiveInputStream zis = new TarArchiveInputStream(new BufferedInputStream(fis));
			ArchiveEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					(new File(directory.getAbsolutePath() + FileUtils.fileSeparator + entry.getName())).mkdir();
					continue;
				}
				if (Logger.logDebug()) {
					Logger.debug("Extracting: " + entry);
				}
				int count;
				final byte data[] = new byte[BUFFER];
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
			zis.close();
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		} catch (final FilePermissionException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Unzips a given file to the specified directory.
	 * 
	 * @param zipFile
	 *            the zip compressed file, not null
	 * @param directory
	 *            the target directory, not null
	 * @return true on success, false otherwise
	 */
	@NoneNull
	public static boolean unzip(final File zipFile,
	                            final File directory) {
		try {
			ensureFilePermissions(zipFile, READABLE_FILE);
			ensureFilePermissions(directory, WRITABLE_DIR);
			
			final int BUFFER = 2048;
			BufferedOutputStream dest = null;
			final FileInputStream fis = new FileInputStream(zipFile);
			final ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory()) {
					(new File(directory.getAbsolutePath() + FileUtils.fileSeparator + entry.getName())).mkdir();
					continue;
				}
				if (Logger.logDebug()) {
					Logger.debug("Extracting: " + entry);
				}
				int count;
				final byte data[] = new byte[BUFFER];
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
			zis.close();
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		} catch (final FilePermissionException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Gets the handle.
	 * 
	 * @return the simple class name
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
	
}
