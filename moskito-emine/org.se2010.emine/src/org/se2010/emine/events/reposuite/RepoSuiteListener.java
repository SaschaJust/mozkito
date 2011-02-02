package org.se2010.emine.events.reposuite;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.se2010.emine.artifacts.HighlightIconType;
import org.se2010.emine.artifacts.SyntaxhighlightingArtifact;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.EditorEvent;
import org.se2010.emine.events.IEMineEvent;
import org.se2010.emine.events.IEMineEventListener;
import org.se2010.emine.events.ModificationEvent;
import org.se2010.emine.events.ModificationEvent.ClassChangedEvent;
import org.se2010.emine.views.SyntaxhighlightingView;

public class RepoSuiteListener implements IEMineEventListener {
	
	public RepoSuiteListener(){

	EMineEventBus.getInstance().registerEventListener(ModificationEvent.ClassChangedEvent.class, this);
	
	EMineEventBus.getInstance().registerEventListener(EditorEvent.EditorOpenedEvent.class, this);
	


	}	
	
	public void onEvent(IEMineEvent event) {
		
		// TODO Auto-generated method stub
		
	
		
	List<String> changedMethodNames = ((ModificationEvent.ClassChangedEvent)event).getChangedMethods();
	
		if (changedMethodNames !=null ){
			  RepoSuiteEvent mCoreEvent = new RepoSuiteEvent(changedMethodNames);
			    
			    mCoreEvent.createArtifacts();
			
		}
		
		IFile file = ((EditorEvent.EditorOpenedEvent)event).getFile();
		
		HashMap<Integer, HighlightIconType> map = new HashMap<Integer, HighlightIconType>() ;
		
		map.put(3, HighlightIconType.GREEN);
		
		
		SyntaxhighlightingArtifact syn = new SyntaxhighlightingArtifact("hopeitworks", file, map, "hello");
		
		
		SyntaxhighlightingView view = new SyntaxhighlightingView();
		
         view.updateSyntaxhighlightingView(syn);    
    
    
	}
	
	
	
}
