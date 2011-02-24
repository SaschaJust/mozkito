package org.se2010.emine.artifacts;

import java.util.Map;

import org.eclipse.core.resources.IFile;

public class SyntaxHighlightingArtifact implements IArtifact{

	private String title;
	private Map<Integer,HighlightIconType> map;	// map of Int lines->String color (at the moment red,yellow,green)
	private String message;
	private String file;
	
	/**
	 * 
	 * @param title of the artifact
	 * @param file path of the file in which the lines should be highlighted
	 * @param map a hashmap from lines to HighlightIconType which is the marker of the specified line
	 * @param message is displayed by hover at a marker
	 */
	public SyntaxHighlightingArtifact(String title,String file,Map<Integer,HighlightIconType> map, String message){
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
