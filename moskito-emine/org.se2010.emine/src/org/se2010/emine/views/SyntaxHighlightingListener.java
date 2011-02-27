package org.se2010.emine.views;

import org.se2010.emine.artifacts.IArtifact;
import org.se2010.emine.artifacts.SyntaxHighlightingArtifact;
import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.IEMineEvent;
import org.se2010.emine.events.IEMineEventListener;
import org.se2010.emine.events.reposuite.RepoSuiteEvent;

public class SyntaxHighlightingListener implements IEMineEventListener
{
	public SyntaxHighlightingListener() { }
	
	
	public void registerOnEventBus()
	{
		EMineEventBus.getInstance().registerEventListener(RepoSuiteEvent.class, this);	
	}
	
	@Override
	public void onEvent(final IEMineEvent event) 
	{
		if(event instanceof RepoSuiteEvent)
		{
			final RepoSuiteEvent rEvent = (RepoSuiteEvent) event;
			
			for(final IArtifact artifact : rEvent.getArtifacts())
			{
				if(artifact instanceof SyntaxHighlightingArtifact)
				{
	                final SyntaxHighlightingArtifact syn  = (SyntaxHighlightingArtifact) artifact;
	                final SyntaxHighlightingView     view = new SyntaxHighlightingView();
	                view.updateSyntaxhighlightingView(syn); 
				}
			}
		}
	}
}
