package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;

import org.joda.time.Days;

import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalResponseTimeMetrics;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


/**
 * The Class CoreDependencyMetrics.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class CoreResponseTimeMetrics extends GenealogyCoreMetric implements DayTimeDiff<JavaChangeOperation> {
	
	UniversalResponseTimeMetrics<JavaChangeOperation> universalMetric;
	
	public CoreResponseTimeMetrics(AndamaGroup threadGroup, AndamaSettings settings, CoreChangeGenealogy genealogy) {
		super(threadGroup, settings, genealogy);
		universalMetric = new UniversalResponseTimeMetrics<JavaChangeOperation>(genealogy, this);
	}
	
	
	@Override
	public int daysDiff(JavaChangeOperation t1, JavaChangeOperation t2) {
		return Days.daysBetween(t1.getRevision().getTransaction().getTimestamp(),
		        t2.getRevision().getTransaction().getTimestamp()).getDays();
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return universalMetric.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyNode<JavaChangeOperation> item) {
		return universalMetric.handle(item.getNode());
	}
	
	
	
}
