package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyCoreNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalStructuralHolesMetrics;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


/**
 * The Class CoreDependencyMetrics.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class CoreStructuralHolesMetrics extends GenealogyCoreMetric {
	
	UniversalStructuralHolesMetrics<JavaChangeOperation> universalMetric;
	
	public CoreStructuralHolesMetrics(CoreChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalStructuralHolesMetrics<JavaChangeOperation>(genealogy);
	}
	
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalStructuralHolesMetrics.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyCoreNode item) {
		return universalMetric.handle(item.getNode());
	}
	
	
	
}
