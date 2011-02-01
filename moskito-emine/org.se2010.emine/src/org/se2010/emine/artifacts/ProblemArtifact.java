 package org.se2010.emine.artifacts;

import java.util.Map;

import org.eclipse.core.resources.IFile;

public class ProblemArtifact implements Artifact{
	private String title;			
	private int id;
	private String message;			 	
	private Map<String,String> map;	// map of String categories->String properties 
	private String resource;
	
	public ProblemArtifact(String t,  int id , String me, String resource ){
		this.title = t;
		this.message = me;
		this.setId(id);
		this.setResource(resource);
 	}
	
	 


	public String getMessage() {
		return message;
	}

 
	
	public String getTitle() {
		return title;
	}




	public void setId(int id) {
		this.id = id;
	}




	public int getId() {
		return id;
	}




	public void setResource(String resource) {
		this.resource = resource;
	}




	public String getResource() {
		return resource;
	}




	public void setMap(Map<String,String> map) {
		this.map = map;
	}




	public Map<String,String> getMap() {
		return map;
	}

}
