package de.unisaarland.cs.st.reposuite.rcs.elements;

public enum ChangeType {
	Modified, Added, Deleted, Replaced;
	
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}
