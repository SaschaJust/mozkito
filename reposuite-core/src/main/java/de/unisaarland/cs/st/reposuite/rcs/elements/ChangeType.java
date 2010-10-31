package de.unisaarland.cs.st.reposuite.rcs.elements;

public enum ChangeType {
	Modified, Added, Deleted, Renamed;
	
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}
