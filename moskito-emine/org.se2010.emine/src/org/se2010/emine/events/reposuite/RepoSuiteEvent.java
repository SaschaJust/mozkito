package org.se2010.emine.events.reposuite;

import java.util.List;

import org.se2010.emine.artifacts.ProblemArtifact;
import org.se2010.emine.events.IEMineEvent;

public class RepoSuiteEvent implements IEMineEvent{
	
	private List<String> changedMethod;
	
	
	public RepoSuiteEvent(List<String> changedMethods){
		this.changedMethod = changedMethods;
		
	}
	
	
	private ProblemArtifact artifact;
	
	public void createArtifact(){
		String methodName  = null;
	 
		 
		 for (int i = 0 ; i < changedMethod.size() ; i++){
			  methodName = changedMethod.get(i);
		 }
		 
		 artifact =  new ProblemArtifact("bugs reports", null,"this is a bug report", null,methodName);

		
	}
	
	public ProblemArtifact getArtifact(){
		
		return this.artifact;
		
	}
	 

}
