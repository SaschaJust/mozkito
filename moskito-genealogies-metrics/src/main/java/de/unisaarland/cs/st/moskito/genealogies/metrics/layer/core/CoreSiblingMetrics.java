package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalSiblingMetrics;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyCoreNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


/**
 * The Class CoreDependencyMetrics.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class CoreSiblingMetrics extends GenealogyCoreMetric {
	
	UniversalSiblingMetrics<JavaChangeOperation> universalMetric;
	
	public CoreSiblingMetrics(AndamaGroup threadGroup, AndamaSettings settings, CoreChangeGenealogy genealogy) {
		super(threadGroup, settings, genealogy);
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
