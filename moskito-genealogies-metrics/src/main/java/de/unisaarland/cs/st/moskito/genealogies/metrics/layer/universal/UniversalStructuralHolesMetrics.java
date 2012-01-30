package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.collections15.Transformer;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.utils.JungGenealogyGraph;
import de.unisaarland.cs.st.moskito.genealogies.utils.JungGenealogyGraph.Edge;
import edu.uci.ics.jung.algorithms.metrics.StructuralHoles;


public class UniversalStructuralHolesMetrics<T> {


	public static Collection<String> getMetricNames() {
		Collection<String> result = new LinkedList<String>();
		result.add(ConstraintName);
		result.add(aggregateConstraintName);
		result.add(EffectiveSizeName);
		result.add(EfficiencyName);
		result.add(HierarchyName);
		return result;
	}

	private final ChangeGenealogy<T> genealogy;
	private final StructuralHoles<T, JungGenealogyGraph.Edge<T>> structuralHoles;

	private static String aggregateConstraintName = "AggregateConstraint";
	private static String ConstraintName = "Constraint";
	private static String EffectiveSizeName = "EffectiveSize";
	private static String EfficiencyName = "Efficiency";
	private static String HierarchyName = "Hierarchy";

	public UniversalStructuralHolesMetrics(ChangeGenealogy<T> genealogy) {
		this.genealogy = genealogy;
		JungGenealogyGraph<T> jungGraph = new JungGenealogyGraph<T>(genealogy);
		structuralHoles = new StructuralHoles<T, JungGenealogyGraph.Edge<T>>(
				jungGraph,
				new Transformer<JungGenealogyGraph.Edge<T>, Integer>() {

					@Override
					public Integer transform(Edge<T> input) {
						return 1;
					}

				});
	}

	public Collection<GenealogyMetricValue> handle(T node) {
		Collection<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();

		String nodeId = genealogy.getNodeId(node);

		result.add(new GenealogyMetricValue(aggregateConstraintName, nodeId,
				structuralHoles.aggregateConstraint(node)));
		result.add(new GenealogyMetricValue(ConstraintName, nodeId,
				structuralHoles.constraint(node)));
		result.add(new GenealogyMetricValue(EffectiveSizeName, nodeId,
				structuralHoles.effectiveSize(node)));
		result.add(new GenealogyMetricValue(EfficiencyName, nodeId,
				structuralHoles.efficiency(node)));
		result.add(new GenealogyMetricValue(HierarchyName, nodeId,
				structuralHoles.hierarchy(node)));

		return result;
	}

}
