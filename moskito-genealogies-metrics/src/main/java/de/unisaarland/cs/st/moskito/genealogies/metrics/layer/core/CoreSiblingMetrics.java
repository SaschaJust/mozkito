package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyCoreNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalSiblingMetrics;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


/**
 * The Class CoreDependencyMetrics.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class CoreSiblingMetrics extends GenealogyCoreMetric {
	
	UniversalSiblingMetrics<JavaChangeOperation> universalMetric;
	
	public CoreSiblingMetrics(CoreChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalSiblingMetrics<JavaChangeOperation>(genealogy);
	}
	
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalSiblingMetrics.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyCoreNode item) {
		return universalMetric.handle(item.getNode());
	}
	
	
	
}
