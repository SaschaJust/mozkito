package de.unisaarland.cs.st.reposuite.infozilla.model.patch;

public class PatchHunk {
	
	private String text;
	
	public PatchHunk() {
		text = "";
	}
	
	public PatchHunk(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
}
