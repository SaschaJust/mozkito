package org.se2010.emine.events.reposuite;

import java.util.ArrayList;
import java.util.List;
import org.se2010.emine.artifacts.IArtifact;
import org.se2010.emine.events.IEMineEvent;

public final class RepoSuiteEvent implements IEMineEvent
{
        private final ArrayList<IArtifact> artifacts;
        
        public RepoSuiteEvent()
        {
                this.artifacts = new ArrayList<IArtifact>();
        }
        
        public List<IArtifact> getArtifacts() 
        {
                return new ArrayList<IArtifact>(this.artifacts);
        }

        public void addArtifact(final IArtifact artifact)
        {
                this.artifacts.add(artifact);
        }
        
        public void addAllArtifacts(final List<IArtifact> artifacts) 
        {
                this.artifacts.addAll(artifacts);
        }

        @Override
        public String toString() 
        {
                return "RepoSuiteEvent [artifacts=" + artifacts + "]";
        }
}
