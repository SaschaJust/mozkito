package org.se2010.emine.views;

import java.util.ArrayList;
import java.util.List;

import org.se2010.emine.artifacts.Artifact;

public abstract class ArtifactView {
	
	//TODO should this wrapper class control the view classes?
	//for this has it to hold an object of the respective classes?
	//then TODO add the respective update methodes

	private final List<Artifact> artifacts;
	
	public ArtifactView()
	{
		this.artifacts = new ArrayList<Artifact>();
	}
	
	public void addArtifact(Artifact artifact){
		artifacts.add(artifact);
	}
	
	
	public String extracteffectedname(){
		//TODO need some argument to compute necessary stuff, what was this method for?
		return null;
	}
	
	
	
	protected abstract void checkViewProperties();
	// TODO open question if this is handled in the controller class
	public abstract void clear();
	//TODO method vizualizeBackendConfiguration necessary??
	
}
