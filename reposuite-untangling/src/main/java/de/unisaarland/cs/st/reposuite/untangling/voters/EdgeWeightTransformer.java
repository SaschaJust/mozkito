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
