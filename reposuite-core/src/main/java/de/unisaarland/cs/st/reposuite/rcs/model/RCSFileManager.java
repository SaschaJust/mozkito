package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.HashMap;
import java.util.Map;

public class RCSFileManager {
	
	private final Map<String, RCSFile> currentFiles = new HashMap<String, RCSFile>();
	
	public RCSFile getFile(final String path) {
		return this.currentFiles.get(path);
	}
}
