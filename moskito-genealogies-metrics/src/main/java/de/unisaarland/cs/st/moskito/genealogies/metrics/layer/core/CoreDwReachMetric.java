package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

import java.util.Collection;
import java.util.Comparator;

import org.joda.time.DateTime;
import org.joda.time.Days;

import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyCoreNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalDwReachMetric;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

/**
 * The Class CoreDependencyMetrics.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class CoreDwReachMetric extends GenealogyCoreMetric {
	
	private static int                                  dayDiffSize = 14;
	
	private UniversalDwReachMetric<JavaChangeOperation> universalMetric;
	
	public CoreDwReachMetric(CoreChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalDwReachMetric<JavaChangeOperation>(genealogy,
				new Comparator<JavaChangeOperation>() {
			
			@Override
			public int compare(JavaChangeOperation original, JavaChangeOperation t) {
				
				DateTime oTime = original.getRevision().getTransaction().getTimestamp();
				DateTime tTime = t.getRevision().getTransaction().getTimestamp();
				
				Days daysBetween = Days.daysBetween(oTime, tTime);
				if (daysBetween.getDays() > dayDiffSize) {
					return 1;
				}
				return -1;
			}
		});
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalDwReachMetric.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyCoreNode item) {
		return universalMetric.handle(item.getNode());
	}
	
}
