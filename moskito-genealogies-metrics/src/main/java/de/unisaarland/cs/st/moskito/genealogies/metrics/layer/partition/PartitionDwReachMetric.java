package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;
import java.util.Comparator;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;

import org.joda.time.DateTime;
import org.joda.time.Days;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalDwReachMetric;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public class PartitionDwReachMetric extends GenealogyPartitionMetric {
	
	private UniversalDwReachMetric<Collection<JavaChangeOperation>> universalMetric;
	private static int                                              dayDiffSize = 14;
	
	public PartitionDwReachMetric(AndamaGroup threadGroup, AndamaSettings settings,
			ChangeGenealogy<Collection<JavaChangeOperation>> genealogy) {
		super(threadGroup, settings, genealogy);
		universalMetric = new UniversalDwReachMetric<Collection<JavaChangeOperation>>(
				genealogy,
				new Comparator<Collection<JavaChangeOperation>>() {
					
					@Override
					public int compare(Collection<JavaChangeOperation> original, Collection<JavaChangeOperation> t) {
						
						DateTime latestOriginal = null;
						for (JavaChangeOperation op : original) {
							if (latestOriginal == null) {
								latestOriginal = op.getRevision().getTransaction().getTimestamp();
							}else{
								DateTime tmp = op.getRevision().getTransaction().getTimestamp();
								if (tmp.isAfter(latestOriginal)) {
									latestOriginal = tmp;
								}
							}
						}
						
						DateTime earliestT = null;
						for (JavaChangeOperation op : t) {
							if (earliestT == null) {
								earliestT = op.getRevision().getTransaction().getTimestamp();
							} else {
								DateTime tmp = op.getRevision().getTransaction().getTimestamp();
								if (tmp.isBefore(earliestT)) {
									earliestT = tmp;
								}
							}
						}
						
						Days daysBetween = Days.daysBetween(latestOriginal, earliestT);
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
	public Collection<GenealogyMetricValue> handle(GenealogyPartitionNode item) {
		return universalMetric.handle(item.getNode());
	}
	
	
}
