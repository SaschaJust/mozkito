package de.unisaarland.cs.st.reposuite.rcs;

public enum ChangeType {
	Modified, Added, Deleted, Replaced;
	
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}
