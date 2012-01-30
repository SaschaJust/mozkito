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
