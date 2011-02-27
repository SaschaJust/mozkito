package org.se2010.emine.views;

import java.util.ArrayList;
import java.util.List;

import org.se2010.emine.artifacts.IArtifact;

public abstract class ArtifactView {
	
	protected abstract void checkViewProperties();
	public abstract void clear();

	private final List<IArtifact> artifacts;
	
	public ArtifactView()
	{
		this.artifacts = new ArrayList<IArtifact>();
	}
	
	public void addArtifact(IArtifact artifact){
		artifacts.add(artifact);
	}
}
