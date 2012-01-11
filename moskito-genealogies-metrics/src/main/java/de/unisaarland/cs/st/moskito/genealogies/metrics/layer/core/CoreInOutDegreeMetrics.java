package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalInOutDegreeMetrics;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyCoreNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


/**
 * The Class CoreDependencyMetrics.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class CoreInOutDegreeMetrics extends GenealogyCoreMetric {
	
	UniversalInOutDegreeMetrics<JavaChangeOperation> universalMetric;
	
	public CoreInOutDegreeMetrics(CoreChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalInOutDegreeMetrics<JavaChangeOperation>(genealogy);
	}
	
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalInOutDegreeMetrics.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyCoreNode item) {
		return universalMetric.handle(item.getNode());
	}
	
	
	
}
