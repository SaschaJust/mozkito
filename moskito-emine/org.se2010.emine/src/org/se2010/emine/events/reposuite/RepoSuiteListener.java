package org.se2010.emine.events.reposuite;

import java.util.List;

import org.se2010.emine.events.EMineEventBus;
import org.se2010.emine.events.IEMineEvent;
import org.se2010.emine.events.IEMineEventListener;
import org.se2010.emine.events.ModificationEvent;
import org.se2010.emine.events.ModificationEvent.ClassChangedEvent;

public class RepoSuiteListener implements IEMineEventListener {
	
	public RepoSuiteListener(){

	EMineEventBus.getInstance().registerEventListener(ModificationEvent.ClassChangedEvent.class, this);
	}
	
	public void onEvent(IEMineEvent event) {
		// TODO Auto-generated method stub
	List<String> changedMethodNames = ((ModificationEvent.ClassChangedEvent)event).getChangedMethods();
	
	
    RepoSuiteEvent mCoreEvent = new RepoSuiteEvent(changedMethodNames);
    
    mCoreEvent.createArtifacts();
    
	
	}
	
}
