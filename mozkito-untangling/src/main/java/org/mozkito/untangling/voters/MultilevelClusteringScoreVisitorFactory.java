/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.mozkito.untangling.voters;

import org.mozkito.clustering.MultilevelClusteringScoreVisitor;
import org.mozkito.versions.model.RCSTransaction;


/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public abstract class MultilevelClusteringScoreVisitorFactory<T extends MultilevelClusteringScoreVisitor<?>> {
	
	public abstract T createVoter(final RCSTransaction transaction);
	
	public abstract String getVoterName();
}
