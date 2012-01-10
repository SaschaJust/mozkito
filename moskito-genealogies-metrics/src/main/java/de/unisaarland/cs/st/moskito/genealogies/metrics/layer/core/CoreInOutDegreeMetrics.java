package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalInOutDegreeMetrics;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


/**
 * The Class CoreDependencyMetrics.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class CoreInOutDegreeMetrics extends GenealogyCoreMetric {
	
	UniversalInOutDegreeMetrics<JavaChangeOperation> universalMetric;
	
	public CoreInOutDegreeMetrics(AndamaGroup threadGroup, AndamaSettings settings, CoreChangeGenealogy genealogy) {
		super(threadGroup, settings, genealogy);
		universalMetric = new UniversalInOutDegreeMetrics<JavaChangeOperation>(genealogy);
	}
	
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalInOutDegreeMetrics.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyNode<JavaChangeOperation> item) {
		return universalMetric.handle(item.getNode());
	}
	
	
	
}
