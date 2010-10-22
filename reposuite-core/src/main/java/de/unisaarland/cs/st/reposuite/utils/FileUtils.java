package de.unisaarland.cs.st.reposuite.utils;

import java.io.File;
import java.io.IOException;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

/**
 * The Class FileUtils.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class FileUtils {
	
	public static final File   tmpDir        = org.apache.commons.io.FileUtils.getTempDirectory();
	public static final String fileSeparator = System.getProperty("file.separator");
	public static final String lineSeparator = System.getProperty("line.separator");
	
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
	public static File createDir(File parentDir, String name) {
		if (!parentDir.isDirectory()) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Could not create directory `" + name + "` in parent directory `"
				        + parentDir.getAbsolutePath() + "`. Reason: parent directory is not a directory.");
			}
			return null;
		}
		if ((!parentDir.canExecute()) || (!parentDir.canWrite())) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Could not create directory `" + name + "` in parent directory `"
				        + parentDir.getAbsolutePath() + "`. Reason: permission denied.");
			}
			return null;
		}
		File newDir = new File(parentDir.getAbsolutePath() + System.getProperty("file.separator") + name);
		if (newDir.exists()) {
			if (newDir.isDirectory()) {
				
				if (RepoSuiteSettings.logWarn()) {
					Logger.warn("Did not create directory `" + name + "` in parent directory `"
					        + parentDir.getAbsolutePath()
					        + "`. Reason: directory exists already. Returning existing directory.");
				}
				return newDir;
			} else {
				if (RepoSuiteSettings.logError()) {
					Logger.error("Could not create directory `" + name + "` in parent directory `"
					        + parentDir.getAbsolutePath() + "`. Reason: path exists already as files.");
				}
				return null;
			}
		}
		if (!newDir.mkdirs()) {
			if (RepoSuiteSettings.logError()) {
				Logger.error("Could not create directory `" + name + "` in parent directory `"
				        + parentDir.getAbsolutePath() + "`. Reason: permission denied.");
			}
			return null;
		} else {
			if (RepoSuiteSettings.logInfo()) {
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
	public static File createRandomDir(File parentDir, String prefix, String suffix) {
		try {
			File file = File.createTempFile(prefix, suffix, parentDir);
			if (!file.delete()) {
				if (RepoSuiteSettings.logError()) {
					Logger.error("Could not delete random file `" + file.getAbsolutePath() + "`");
				}
				return null;
			}
			if (!file.mkdirs()) {
				if (RepoSuiteSettings.logError()) {
					Logger.error("Could not create random directory `" + file.getAbsolutePath() + "`");
				}
				return null;
			}
			return file;
		} catch (IOException e) {
			if (RepoSuiteSettings.logError()) {
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
	public static File createRandomDir(String prefix, String suffix) {
		return createRandomDir(tmpDir, prefix, suffix);
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
	public static void deleteDirectory(File directory) throws IOException {
		org.apache.commons.io.FileUtils.deleteDirectory(directory);
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
	public static void forceDelete(File file) throws IOException {
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
	public static void forceDeleteOnExit(File file) throws IOException {
		org.apache.commons.io.FileUtils.forceDeleteOnExit(file);
	}
}
