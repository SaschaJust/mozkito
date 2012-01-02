package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;

import org.joda.time.DateTime;
import org.joda.time.Days;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalResponseTimeMetrics;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public class PartitionResponseTimeMetrics extends GenealogyPartitionMetric implements
DayTimeDiff<Collection<JavaChangeOperation>> {
	
	
	private UniversalResponseTimeMetrics<Collection<JavaChangeOperation>> universalMetric;
	
	public PartitionResponseTimeMetrics(AndamaGroup threadGroup, AndamaSettings settings,
			ChangeGenealogy<Collection<JavaChangeOperation>> genealogy) {
		super(threadGroup, settings, genealogy);
		universalMetric = new UniversalResponseTimeMetrics<Collection<JavaChangeOperation>>(genealogy, this);
	}
	
	@Override
	public int daysDiff(Collection<JavaChangeOperation> p1, Collection<JavaChangeOperation> p2) {
		DateTime p1EarlyTime = null;
		DateTime p1LateTime = null;
		
		DateTime p2EarlyTime = null;
		DateTime p2LateTime = null;
		
		for (JavaChangeOperation p1Op : p1) {
			DateTime tmpTime = p1Op.getRevision().getTransaction().getTimestamp();
			if (p1EarlyTime == null) {
				p1EarlyTime = tmpTime;
			} else if (tmpTime.isBefore(p1EarlyTime)) {
				p1EarlyTime = tmpTime;
			}
			if (p1LateTime == null) {
				p1LateTime = tmpTime;
			} else if (tmpTime.isAfter(p1LateTime)) {
				p1LateTime = tmpTime;
			}
		}
		
		for (JavaChangeOperation p2Op : p2) {
			DateTime tmpTime = p2Op.getRevision().getTransaction().getTimestamp();
			if (p2EarlyTime == null) {
				p2EarlyTime = tmpTime;
			} else if (tmpTime.isBefore(p2EarlyTime)) {
				p2EarlyTime = tmpTime;
			}
			if (p2LateTime == null) {
				p2LateTime = tmpTime;
			} else if (tmpTime.isAfter(p2LateTime)) {
				p2LateTime = tmpTime;
			}
		}
		
		int diff = Days.daysBetween(p1LateTime, p2EarlyTime).getDays();
		int diff2 = Days.daysBetween(p2LateTime, p1EarlyTime).getDays();
		return Math.min(diff, diff2);
		
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return universalMetric.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyNode<Collection<JavaChangeOperation>> item) {
		return universalMetric.handle(item.getNode());
	}
	
}