package de.unisaarland.cs.st.reposuite.rcs;

public enum RepositoryType {
	SUBVERSION, GIT, MERCURIAL;
	
	public static String getHandle() {
		return RepositoryType.class.getSimpleName();
	}
}
