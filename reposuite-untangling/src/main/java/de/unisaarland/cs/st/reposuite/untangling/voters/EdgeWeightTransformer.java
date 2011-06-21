/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.untangling.voters;

import org.apache.commons.collections15.Transformer;

import de.unisaarland.cs.st.reposuite.callgraph.model.CallGraphEdge;

/**
 * The Class EdgeWeightTransformer.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class EdgeWeightTransformer implements Transformer<CallGraphEdge, Double> {
	
	/*
	 * (non-Javadoc)
	 * @see
	 * org.apache.commons.collections15.Transformer#transform(java.lang.Object)
	 */
	@Override
	public Double transform(final CallGraphEdge edge) {
		return edge.getWeight();
	}
	
}
