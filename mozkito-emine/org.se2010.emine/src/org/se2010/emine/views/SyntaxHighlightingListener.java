/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


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
