package org.se2010.emine.events.reposuite;

import java.util.HashMap;
import java.util.List;
import org.se2010.emine.artifacts.HighlightIconType;
import org.se2010.emine.artifacts.SyntaxhighlightingArtifact;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.EditorEvent;
import org.se2010.emine.events.IEMineEvent;
import org.se2010.emine.events.IEMineEventListener;
import org.se2010.emine.events.ModificationEvent;
import org.se2010.emine.views.SyntaxhighlightingView;

public class RepoSuiteListener implements IEMineEventListener {
	
	public RepoSuiteListener()
	{
		EMineEventBus.getInstance().registerEventListener(ModificationEvent.ClassChangedEvent.class, this);
		EMineEventBus.getInstance().registerEventListener(EditorEvent.EditorOpenedEvent.class, this);
	}	
	
	
	
	
	public void onEvent(IEMineEvent event) 
	{
		if(event instanceof EditorEvent.EditorOpenedEvent)
		{
			final String file = ((EditorEvent.EditorOpenedEvent)event).getFilePath();
			
			HashMap<Integer, HighlightIconType> map = new HashMap<Integer, HighlightIconType>() ;
			
			map.put(3, HighlightIconType.GREEN);
			map.put(1, HighlightIconType.YELLOW);
			
			
			SyntaxhighlightingArtifact syn = new SyntaxhighlightingArtifact("hopeitworks", file, map, "hello");
			SyntaxhighlightingView view = new SyntaxhighlightingView();
	        view.updateSyntaxhighlightingView(syn);    
		}
		else if (event instanceof ModificationEvent.ClassChangedEvent)
		{
			final List<String> changedMethodNames = ((ModificationEvent.ClassChangedEvent)event).getChangedMethods();
			
			if (changedMethodNames != null)
			{
				  RepoSuiteEvent mCoreEvent = new RepoSuiteEvent(changedMethodNames);
				  mCoreEvent.createArtifacts();
			}
		}
    
    
	}
	
	
	
}
