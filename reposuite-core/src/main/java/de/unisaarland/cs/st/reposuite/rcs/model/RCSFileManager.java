package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RCSFileManager {
	
	private final Map<String, RCSFile> currentFiles = new HashMap<String, RCSFile>();
	
	/**
	 * @param file
	 */
	public void addFile(final RCSFile file) {
		this.currentFiles.put(file.getLatestPath(), file);
	}
	
	/**
	 * @param path
	 * @param transaction
	 * @return
	 */
	public RCSFile createFile(final String path, final RCSTransaction transaction) {
		RCSFile file = new RCSFile(path, transaction);
		this.currentFiles.put(path, file);
		return file;
	}
	
	/**
	 * @param path
	 * @return
	 */
	public RCSFile getFile(final String path) {
		return this.currentFiles.get(path);
	}
}
