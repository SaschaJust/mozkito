package de.unisaarland.cs.st.reposuite.rcs;

public enum RepositoryType {
	CVS, GIT, MERCURIAL, SUBVERSION;
	
	public static String getHandle() {
		return RepositoryType.class.getSimpleName();
	}
}
