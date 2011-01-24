package org.se2010.emine.views;

import java.util.List;

import org.se2010.emine.artifacts.Artifact;

public abstract class ArtifactView {
	
	//TODO should this wrapper class control the view classes?
	//for this has it to hold an object of the respective classes?
	//then TODO add the respective update methodes

	private List<Artifact> artifacts;
	
	public void addArtifact(Artifact artifact){
		artifacts.add(artifact);
	}
	
	
	public String extracteffectedname(){
		//TODO need some argument to compute necessary stuff, what was this method for?
		return null;
	}
	
	
	
	protected abstract void checkViewProperties();
	public abstract void clear();
	//TODO method vizualizeBackendConfiguration necessary??
	
}
