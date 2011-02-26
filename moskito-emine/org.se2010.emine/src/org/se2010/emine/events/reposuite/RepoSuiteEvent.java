 package org.se2010.emine.events.reposuite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.se2010.emine.artifacts.IArtifact;
import org.se2010.emine.artifacts.ProblemArtifact;
import org.se2010.emine.artifacts.ProblemArtifactTypeList;
import org.se2010.emine.artifacts.SyntaxHighlightingArtifact;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.IEMineEvent;
import org.se2010.emine.views.SyntaxHighlightingView;

public class RepoSuiteEvent implements IEMineEvent{
	
	private List<String> changedMethod;
	
	SyntaxHighlightingArtifact syntax ;
	
	
	public RepoSuiteEvent(List<String> changedMethods){
		this.changedMethod = changedMethods;
		
	}
	
	private  ArrayList<ProblemArtifactTypeList> artifactTypeList;
	
	public ArrayList<ProblemArtifactTypeList> getArtifactTypeList() {
		return artifactTypeList;
	}

	public void setArtifactTypeList(
			ArrayList<ProblemArtifactTypeList> artifactTypeList) {
		this.artifactTypeList = artifactTypeList;
	}

	private ArrayList<IArtifact> artifacts;
	
	public ArrayList<IArtifact> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(ArrayList<IArtifact> artifacts) {
		this.artifacts = artifacts;
	}

	private ProblemArtifact artifact;
	
	public void createArtifacts(){
		
		artifacts = new ArrayList<IArtifact>();
		
		String methodName  = null;
	 
		 
		 for (int i = 0 ; i < changedMethod.size() ; i++){
			  methodName = changedMethod.get(i);
			  artifact = new ProblemArtifact("dummy title",  i , "dummy message", methodName);
			  artifacts.add(artifact);
		 }
			EMineEventBus.getInstance().fireEvent(this);	
	}
	
	
	
	
	public void createArtifacts2(){
		
	
		String methodName = null;
		 for (int i = 0 ; i < changedMethod.size() ; i++){
			 
			 HashMap<String, String> m1 = new HashMap<String, String>();
				HashMap<String, String> m2 = new HashMap<String, String>();

				 artifacts = new ArrayList<IArtifact>();

				 artifactTypeList = new ArrayList<ProblemArtifactTypeList>();

				
				m1.put("Type", "Bug");
				m1.put("Title", "myID");
				m1.put("Please Note", "highly dangerous");
				m1.put("ResourceName", changedMethod.get(i));
					
				m2.put("Type", "Mail");
				m2.put("Title", "notmyID");
				m2.put("Please Note", "not dangerous");
				m2.put("ResourceName", changedMethod.get(i));

				ProblemArtifact a1 = new ProblemArtifact("Important Bug", m1, "oho",
						null,changedMethod.get(i));
				ProblemArtifact a2 = new ProblemArtifact("Important Mail", m2, "ooo",
						null,changedMethod.get(i));
				ProblemArtifact a3 = new ProblemArtifact("Important Rice", m1, "oho",
						null,changedMethod.get(i));
				
				
				artifacts.add(a1);
				artifacts.add(a2);
				artifacts.add(a3);

				List<ProblemArtifact> l1 = new ArrayList<ProblemArtifact>();
				l1.add(a1);
				l1.add(a3);

				List<ProblemArtifact> l2 = new ArrayList<ProblemArtifact>();
				l2.add(a2);

				ProblemArtifactTypeList lt1 = new ProblemArtifactTypeList("Bug", l1);
				ProblemArtifactTypeList lt2 = new ProblemArtifactTypeList("Mail", l2);
				a1.setTypeList(lt1);
				a2.setTypeList(lt2);
				a3.setTypeList(lt1);
				artifactTypeList.add(lt1);
				artifactTypeList.add(lt2);
		 }
		
			EMineEventBus.getInstance().fireEvent(this);	

		
	}

	
	public void createSyntaxArtifacts (){
	}
	
	public ProblemArtifact getArtifact(){
		
		return this.artifact;
		
	}
	 
	

}
