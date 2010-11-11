package de.unisaarland.cs.st.reposuite.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.exceptions.ExternalExecutableException;
import de.unisaarland.cs.st.reposuite.exceptions.FilePermissionException;

/**
 * The Class FileUtils.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class FileUtils {
	
	public static final String fileSeparator     = System.getProperty("file.separator");
	public static final String lineSeparator     = System.getProperty("line.separator");
	public static final String pathSeparator     = System.getProperty("path.separator");
	public static final File   tmpDir            = org.apache.commons.io.FileUtils.getTempDirectory();
	
	private static int         MAX_PERM          = 0;
	
	public static final int    EXECUTABLE        = (int) Math.pow(2, MAX_PERM++);
	public static final int    WRITABLE          = (int) Math.pow(2, MAX_PERM++);
	public static final int    READABLE          = (int) Math.pow(2, MAX_PERM++);
	public static final int    FILE              = (int) Math.pow(2, MAX_PERM++);
	public static final int    DIRECTORY         = (int) Math.pow(2, MAX_PERM++);
	public static final int    EXISTING          = (int) Math.pow(2, MAX_PERM++);
	public static final int    ACCESSIBLE_DIR    = EXISTING | DIRECTORY | READABLE | EXECUTABLE;
	public static final int    WRITABLE_DIR      = ACCESSIBLE_DIR | WRITABLE;
	public static final int    READABLE_FILE     = EXISTING | FILE | READABLE;
	public static final int    WRITABLE_FILE     = FILE | WRITABLE;
	public static final int    OVERWRITABLE_FILE = FILE | EXISTING | WRITABLE;
	public static final int    EXECUTABLE_FILE   = EXISTING | FILE | EXECUTABLE;
	
	/**
	 * Checks if the command maps to a valid accessible, executable file. If the
	 * command is not absolute, a PATH traversal search for the command is done.
	 * 
	 * @param command
	 *            the command string that shall be analyzed
	 * @return returns the absolute path to the command, if found
	 * @throws ExternalExecutableException
	 */
	public static String checkExecutable(final String command) throws ExternalExecutableException {
		Condition.notNull(command);
		if (command.startsWith(FileUtils.fileSeparator)
		        || ((command.length() > 2 /* device char + ':' */+ FileUtils.fileSeparator.length())
		                && (command.charAt(1) == ':') && command.substring(2).startsWith(FileUtils.fileSeparator))) {
			// We got an absolut path here
			File executable = new File(command);
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
			String pathVariable = System.getenv("PATH");
			String[] paths = pathVariable.split(":");
			File executable;
			
			for (String path : paths) {
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
	public static File createDir(final File parentDir, final String name) {
		try {
			ensureFilePermissions(parentDir, WRITABLE_DIR);
		} catch (FilePermissionException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return null;
		}
		
		File newDir = new File(parentDir.getAbsolutePath() + System.getProperty("file.separator") + name);
		if (newDir.exists()) {
			if (newDir.isDirectory()) {
				
				if (Logger.logWarn()) {
					Logger.warn("Did not create directory `" + name + "` in parent directory `"
					        + parentDir.getAbsolutePath()
					        + "`. Reason: directory exists already. Returning existing directory.");
				}
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
	public static File createRandomDir(final File parentDir, final String prefix, final String suffix) {
		try {
			File file = File.createTempFile(prefix, suffix, parentDir);
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
			return file;
		} catch (IOException e) {
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
	public static File createRandomDir(final String prefix, final String suffix) {
		return createRandomDir(tmpDir, prefix, suffix);
	}
	
	/**
	 * Creates a new temporary file
	 * 
	 * @return
	 */
	public static File createRandomFile() {
		try {
			return File.createTempFile("reposuite", String.valueOf(new DateTime().getMillis()), tmpDir);
		} catch (IOException e) {
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
	}
	
	/**
	 * @param file
	 * @param permissions
	 * @throws FilePermissionException
	 */
	public static void ensureFilePermissions(final File file, int permissions) throws FilePermissionException {
		Condition.notNull(file);
		Condition.less(permissions, getMAX_PERM());
		
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
		} catch (IOException e) {
			return null;
		}
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
	}
	
	/**
	 * Force delete on exit.
	 * 
	 * @param file
	 *            the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see {@link org.apache.commons.io.FileUtils#forceDeleteOnExit(File)}
	 */
	public static void forceDeleteOnExit(final File file) throws IOException {
		org.apache.commons.io.FileUtils.forceDeleteOnExit(file);
	}
	
	/**
	 * @return 2 to the power of MAX_PERM
	 */
	public static final int getMAX_PERM() {
		return (int) Math.pow(2, MAX_PERM);
	}
	
	/**
	 * @param baseDirectory
	 * @return
	 */
	public static List<File> getRecursiveDirectories(final File baseDirectory) {
		List<File> list = new LinkedList<File>();
		for (String subDirectoryPath : baseDirectory.list()) {
			File subDirectory = new File(baseDirectory.getAbsolutePath() + FileUtils.fileSeparator + subDirectoryPath);
			if (subDirectory.isDirectory() && subDirectory.canExecute() && subDirectory.canRead()) {
				list.add(subDirectory);
				list.addAll(getRecursiveDirectories(subDirectory));
			}
		}
		return list;
	}
	
	/**
	 * List files. @see
	 * {@link org.apache.commons.io.FileUtils#listFiles(File, String[], boolean)}
	 * 
	 * @param directory
	 *            the directory
	 * @param extensions
	 *            the extensions
	 * @param recursive
	 *            the recursive
	 * @return the collection
	 */
	public static Collection<File> listFiles(final File directory, final String[] extensions, final boolean recursive) {
		return org.apache.commons.io.FileUtils.listFiles(directory, extensions, recursive);
	}
	
	/**
	 * Unzips a given file to the specified directory
	 * 
	 * @param zipFile
	 *            the zip compressed file, not null
	 * @param directory
	 *            the target directory, not null
	 * @return true on success, false otherwise
	 */
	public static boolean unzip(final File zipFile, final File directory) {
		Condition.notNull(zipFile);
		Condition.notNull(directory);
		
		try {
			ensureFilePermissions(zipFile, READABLE_FILE);
			ensureFilePermissions(directory, WRITABLE_DIR);
			
			int BUFFER = 2048;
			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
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
				byte data[] = new byte[BUFFER];
				// write the files to the disk
				FileOutputStream fos = new FileOutputStream(new File(directory.getAbsolutePath()
				        + FileUtils.fileSeparator + entry.getName()));
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
			zis.close();
		} catch (IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		} catch (FilePermissionException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return false;
		}
		return true;
	}
	
	/**
	 * @return the simple class name
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}
