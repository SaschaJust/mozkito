package de.unisaarland.cs.st.reposuite.rcs;

public enum RepositoryType {
	SUBVERSION, GIT, MERCURIAL, CVS;
	
	public static String getHandle() {
		return RepositoryType.class.getSimpleName();
	}
}
