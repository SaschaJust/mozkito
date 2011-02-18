package org.se2010.emine.events.reposuite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.se2010.emine.artifacts.HighlightIconType;
import org.se2010.emine.artifacts.IArtifact;
import org.se2010.emine.artifacts.ProblemArtifact;
import org.se2010.emine.artifacts.SyntaxhighlightingArtifact;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.EditorEvent;
import org.se2010.emine.events.IEMineEvent;
import org.se2010.emine.events.IEMineEventListener;
import org.se2010.emine.events.ModificationEvent;
import org.se2010.emine.views.SyntaxhighlightingView;


/**
 * Dummy Implementation which is only intended for testing purposes:
 * Simulates information provision from the MSA Core
 */
public final class RepoSuiteListener implements IEMineEventListener 
{
	
	public RepoSuiteListener()
	{
		EMineEventBus.getInstance().registerEventListener(ModificationEvent.ClassChangedEvent.class, this);
		EMineEventBus.getInstance().registerEventListener(EditorEvent.EditorOpenedEvent.class, this);
	}	
	
	public void createArtifacts(final List<String> changedMethods)
	{
		final ArrayList<IArtifact> artifacts = new ArrayList<IArtifact>();
		
		int i = 0;
		for(final String methodName : changedMethods)
		{
			  final IArtifact artifact = new ProblemArtifact("dummy title", i , "dummy message", methodName);
			  artifacts.add(artifact);
			  i++;
		}
		
		final RepoSuiteEvent event = new RepoSuiteEvent();
		event.addAllArtifacts(artifacts);
		EMineEventBus.getInstance().fireEvent(event);
	}
	
	
	public void onEvent(final IEMineEvent event) 
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
				this.createArtifacts(changedMethodNames);
			}
		}
    
    
	}
	
	
	
}
