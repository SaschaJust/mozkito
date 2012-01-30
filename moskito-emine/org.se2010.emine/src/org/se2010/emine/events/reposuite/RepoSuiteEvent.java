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


package org.se2010.emine.events.reposuite;

import java.util.ArrayList;
import java.util.List;
import org.se2010.emine.artifacts.IArtifact;
import org.se2010.emine.events.IEMineEvent;

/**
 * Holds the artifacts which the corresponding view listen to and diplays accordingly
 * @author kaushikmukherjee
 *
 */

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
