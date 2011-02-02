 package org.se2010.emine.events.reposuite;

import java.util.ArrayList;
import java.util.List;

import org.se2010.emine.artifacts.ProblemArtifact;
import org.se2010.emine.artifacts.SyntaxhighlightingArtifact;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.IEMineEvent;
import org.se2010.emine.views.SyntaxhighlightingView;

public class RepoSuiteEvent implements IEMineEvent{
	
	private List<String> changedMethod;
	
	SyntaxhighlightingArtifact syntax ;
	
	
	public RepoSuiteEvent(List<String> changedMethods){
		this.changedMethod = changedMethods;
		
	}
	
	private  ArrayList<ProblemArtifact> artifacts;
	
	
	public ArrayList<ProblemArtifact> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(ArrayList<ProblemArtifact> artifacts) {
		this.artifacts = artifacts;
	}

	private ProblemArtifact artifact;
	
	public void createArtifacts(){
		
		artifacts = new ArrayList<ProblemArtifact>();
		
		String methodName  = null;
	 
		 
		 for (int i = 0 ; i < changedMethod.size() ; i++){
			  methodName = changedMethod.get(i);
			  artifact = new ProblemArtifact("dummy title", i , "dummy message", methodName);
				 artifacts.add(artifact);

		 }
		 
			EMineEventBus.getInstance().fireEvent(this);


		
	}
	
	public void createSyntaxArtifacts (){
		
		
		
		
		
	}
	
	public ProblemArtifact getArtifact(){
		
		return this.artifact;
		
	}
	 

}
