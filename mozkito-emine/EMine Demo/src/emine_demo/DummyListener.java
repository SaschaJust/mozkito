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


package emine_demo;

import java.util.*;

import org.se2010.emine.artifacts.*;
import org.se2010.emine.events.*;
import org.se2010.emine.events.reposuite.RepoSuiteEvent;

public final class DummyListener implements IEMineEventListener {
	
	public DummyListener() { }	
	
	public void registerOnEventBus()
	{
		final IEMineEventBus bus = EMineEventBus.getInstance();
		bus.registerEventListener(ModificationEvent.ClassChangedEvent.class, this);
		bus.registerEventListener(EditorEvent.EditorOpenedEvent.class, this);
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
    
    
	public void createArtifacts2(final List<String> changedMethods)
	{
		final RepoSuiteEvent event = new RepoSuiteEvent();
		
		for (int i = 0 ; i < changedMethods.size() ; i++)
		{
		    HashMap<String, String> m1 = new HashMap<String, String>();
			HashMap<String, String> m2 = new HashMap<String, String>();
	
//			ArrayList<IArtifact>               artifacts        = new ArrayList<IArtifact>();
//			ArrayList<ProblemArtifactTypeList> artifactTypeList = new ArrayList<ProblemArtifactTypeList>();
	
			
			m1.put("Type", "Bug");
			m1.put("Title", "myID");
			m1.put("Please Note", "highly dangerous");
			m1.put("ResourceName", changedMethods.get(i));
				
			m2.put("Type", "Mail");
			m2.put("Title", "notmyID");
			m2.put("Please Note", "not dangerous");
			m2.put("ResourceName", changedMethods.get(i));
	
			
			final ProblemArtifact a1 = new ProblemArtifact("Important Bug", m1, "oho",  changedMethods.get(i));
			final ProblemArtifact a2 = new ProblemArtifact("Important Mail", m2, "ooo", changedMethods.get(i));
			final ProblemArtifact a3 = new ProblemArtifact("Important Rice", m1, "oho", changedMethods.get(i));
			
			a1.setGroupName("Bug");
			a2.setGroupName("Mail");
			a3.setGroupName("Bug");
			
			event.addArtifact(a1);
			event.addArtifact(a2);
			event.addArtifact(a3);
		}
		
		EMineEventBus.getInstance().fireEvent(event);
	}    
    
    
    public void onEvent(final IEMineEvent event) 
    {
            if(event instanceof EditorEvent.EditorOpenedEvent)
            {
                    final String                              file = ((EditorEvent.EditorOpenedEvent)event).getFilePath();
                    final HashMap<Integer, HighlightIconType> map  = new HashMap<Integer, HighlightIconType>() ;
                    map.put(3, HighlightIconType.GREEN);
                    map.put(1, HighlightIconType.YELLOW);
                    
                    
                    final SyntaxHighlightingArtifact syn    = new SyntaxHighlightingArtifact("hopeitworks", file, map, "hello");
                    final RepoSuiteEvent 			 rEvent = new RepoSuiteEvent();
                    
                    rEvent.addArtifact(syn);
                    EMineEventBus.getInstance().fireEvent(rEvent);
            }
            else if (event instanceof ModificationEvent.ClassChangedEvent)
            {
                    final List<String> changedMethodNames = ((ModificationEvent.ClassChangedEvent)event).getChangedMethods();
                    
                    if (changedMethodNames != null)
                    {
                            this.createArtifacts2(changedMethodNames);
                    }
            }
    }
}
