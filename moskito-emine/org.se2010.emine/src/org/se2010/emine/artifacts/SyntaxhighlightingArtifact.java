package org.se2010.emine.artifacts;

import java.util.Map;

import org.eclipse.core.resources.IFile;

public class SyntaxhighlightingArtifact implements Artifact{

	private String title;
	private Map<Integer,HighlightIconType> map;	// map of Int lines->String color (at the moment red,yellow,green)
	private String message;
//	private IFile file;
	private String file;
	
	public SyntaxhighlightingArtifact(String title,String file,Map<Integer,HighlightIconType> map, String message){
		this.title=title;
		this.map=map;
		this.message=message;
		this.file=file;
	}
	
	public String getFile(){
		return file;
	}
	
	public Map<Integer,HighlightIconType> getMap(){
		return map;
	}
	
	public String getMessage(){
		return message;
	}
	
	@Override
	public String getTitle() {
		return title;
	}

	
}
