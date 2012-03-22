/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.untangling.voters;

import org.apache.commons.collections15.Transformer;

import de.unisaarland.cs.st.moskito.callgraph.model.CallGraphEdge;

/**
 * The Class EdgeWeightTransformer.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class EdgeWeightTransformer implements Transformer<CallGraphEdge, Double> {
	
	/*
	 * (non-Javadoc)
	 * @see org.apache.commons.collections15.Transformer#transform(java.lang.Object)
	 */
	@Override
	public Double transform(final CallGraphEdge edge) {
		return edge.getWeight();
	}
	
}
