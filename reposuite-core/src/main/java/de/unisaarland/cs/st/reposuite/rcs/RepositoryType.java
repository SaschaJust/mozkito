package de.unisaarland.cs.st.reposuite.rcs;

public enum RepositoryType {
	SUBVERSION, GIT, MERCURIAL, CVS;
	
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}
