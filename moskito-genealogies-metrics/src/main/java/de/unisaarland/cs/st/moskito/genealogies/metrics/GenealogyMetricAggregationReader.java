package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class GenealogyMetricAggregationReader extends AndamaSource<GenealogyMetricValue> {
	
	private Iterator<String>               nodeIditerator;
	private Iterator<GenealogyMetricValue> outputIter;
	
	public GenealogyMetricAggregationReader(final AndamaGroup threadGroup, final AndamaSettings settings,
	        final Map<String, Map<String, Double>> metricValues, final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<GenealogyMetricValue, GenealogyMetricValue>(this) {
			
			@Override
			public void preExecution() {
				GenealogyMetricAggregationReader.this.nodeIditerator = metricValues.keySet().iterator();
			}
		};
		
		new ProcessHook<GenealogyMetricValue, GenealogyMetricValue>(this) {
			
			@Override
			public void process() {
				
				while ((GenealogyMetricAggregationReader.this.outputIter == null)
				        || (!GenealogyMetricAggregationReader.this.outputIter.hasNext())) {
					
					if (!GenealogyMetricAggregationReader.this.nodeIditerator.hasNext()) {
						setCompleted();
						return;
					}
					
					final String transactionId = GenealogyMetricAggregationReader.this.nodeIditerator.next();
					final RCSTransaction transaction = persistenceUtil.loadById(transactionId, RCSTransaction.class);
					if (transaction != null) {
						final Collection<RCSFile> changedFiles = transaction.getChangedFiles();
						final Collection<GenealogyMetricValue> output = new HashSet<GenealogyMetricValue>();
						
						for (final Entry<String, Double> metricSet : metricValues.get(transactionId).entrySet()) {
							for (final RCSFile file : changedFiles) {
								output.add(new GenealogyMetricValue(metricSet.getKey(), file.getPath(transaction),
								                                    metricSet.getValue()));
							}
						}
						GenealogyMetricAggregationReader.this.outputIter = output.iterator();
					}
				}
				
				this.provideOutputData(GenealogyMetricAggregationReader.this.outputIter.next());
				
				if ((!GenealogyMetricAggregationReader.this.outputIter.hasNext())
				        && (!GenealogyMetricAggregationReader.this.nodeIditerator.hasNext())) {
					setCompleted();
				}
			}
		};
	}
}
