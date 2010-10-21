package de.unisaarland.cs.st.reposuite.rcs;

public class AnnotationEntry {
	
	private String revision;
	private String alternativeFilePath;
	
	public AnnotationEntry(String revision) {
		this.revision = revision;
		alternativeFilePath = null;
	}
	
	public AnnotationEntry(String revision, String alternativeFilePath) {
		this.revision = revision;
		this.alternativeFilePath = alternativeFilePath;
	}
	
	public String getAlternativeFilePath() {
		return alternativeFilePath;
		
	}
	
	public String getRevision() {
		return revision;
	}
	
	public boolean hasAlternativePath() {
		return (alternativeFilePath != null);
	}
}
