package org.emine.tutorial;

import java.util.HashMap;

import org.se2010.emine.artifacts.HighlightIconType;
import org.se2010.emine.artifacts.SyntaxHighlightingArtifact;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.EditorEvent;
import org.se2010.emine.events.IEMineEvent;
import org.se2010.emine.events.IEMineEventListener;
import org.se2010.emine.events.reposuite.RepoSuiteEvent;

public final class DebugListener implements IEMineEventListener 
{
	@Override
	public void onEvent(IEMineEvent event) 
	{
		System.out.println("==== DebugListener:Event ====> " + event);

		if(event instanceof EditorEvent.EditorOpenedEvent)
		{
            final String                              file 			   = ((EditorEvent.EditorOpenedEvent)event).getFilePath();
            final HashMap<Integer, HighlightIconType> highlightingMap  = new HashMap<Integer, HighlightIconType>() ;
            highlightingMap.put(1, HighlightIconType.YELLOW);
            highlightingMap.put(3, HighlightIconType.GREEN);
            
            final SyntaxHighlightingArtifact syn    = new SyntaxHighlightingArtifact("My SyntaxHighlightingArtifct", 
            																		 file, 
            																		 highlightingMap,
            																		 "Hello World");
            final RepoSuiteEvent 			 rEvent = new RepoSuiteEvent();
            
            rEvent.addArtifact(syn);
            EMineEventBus.getInstance().fireEvent(rEvent);
		}
	}
}
