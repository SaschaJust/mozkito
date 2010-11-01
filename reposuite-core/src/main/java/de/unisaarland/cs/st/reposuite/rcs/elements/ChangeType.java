package de.unisaarland.cs.st.reposuite.rcs.elements;

public enum ChangeType {
	Added, Deleted, Modified, Renamed;
	
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}
