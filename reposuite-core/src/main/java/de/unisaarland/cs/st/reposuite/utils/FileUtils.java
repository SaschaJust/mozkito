package de.unisaarland.cs.st.reposuite.utils;

import java.io.File;

/**
 * The Class FileUtils.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class FileUtils {
	
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
			Logger.error("Could not create directory `" + name + "` in parent directory `"
			        + parentDir.getAbsolutePath() + "`. Reason: parent directory is not a directory.");
			return null;
		}
		if ((!parentDir.canExecute()) || (!parentDir.canWrite())) {
			Logger.error("Could not create directory `" + name + "` in parent directory `"
			        + parentDir.getAbsolutePath() + "`. Reason: permission denied.");
			return null;
		}
		File newDir = new File(parentDir.getAbsolutePath() + System.getProperty("file.separator") + name);
		if (newDir.exists()) {
			if (newDir.isDirectory()) {
				
				Logger.warn("Did not create directory `" + name + "` in parent directory `"
				        + parentDir.getAbsolutePath()
				        + "`. Reason: directory exists already. Returning existing directory.");
				return newDir;
			} else {
				Logger.error("Could not create directory `" + name + "` in parent directory `"
				        + parentDir.getAbsolutePath() + "`. Reason: path exists already as files.");
				return null;
			}
		}
		if (!newDir.mkdirs()) {
			Logger.error("Could not create directory `" + name + "` in parent directory `"
			        + parentDir.getAbsolutePath() + "`. Reason: permission denied.");
			return null;
		}
		return newDir;
	}
	
}
