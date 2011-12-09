package de.unisaarland.cs.st.moskito.genealogies.layer.universal;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.lang.StringUtils;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

/**
 * The Class UniversalDependencyMetrics. Returns a set of metric values
 * indicating the number of
 * 
 * @param <T>
 *            the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class UniversalDependantsMetrics<T> {
	
	public static String allDependants        = "NumDependants";
	
	public static String definitionDependants = "NumDefinitionDependants";
	public static String callDependants       = "NumCallDependants";
	
	public static String composeMetricName(GenealogyEdgeType eType) {
		return "Num" + eType.toString() + "Dependencies";
	}
	
	private List<String>       metricNames;
	
	private ChangeGenealogy<T> genealogy;
	
	public UniversalDependantsMetrics(ChangeGenealogy<T> genealogy){
		this.genealogy = genealogy;
	}
	
	public Collection<String> getMetricNames() {
		if ((metricNames != null) && (!metricNames.isEmpty())) {
			return metricNames;
		}
		metricNames = new LinkedList<String>();
		metricNames.add(allDependants);
		metricNames.add(definitionDependants);
		metricNames.add(callDependants);
		for (GenealogyEdgeType eType : GenealogyEdgeType.values()) {
			metricNames.add(composeMetricName(eType));
		}
		return metricNames;
	}
	
	public Collection<GenealogyMetricValue> handle(T node) {
		List<GenealogyMetricValue> result = new LinkedList<GenealogyMetricValue>();
		
		//NumDependencies
		int numAllDependants = genealogy.getAllDependants(node).size();
		result.add(new GenealogyMetricValue(allDependants, genealogy.getNodeId(node), numAllDependants));
		
		//for each GenealogyEdgeType
		for (GenealogyEdgeType eType : GenealogyEdgeType.values()) {
			int numDependants = genealogy.getDependants(node, eType).size();
			result.add(new GenealogyMetricValue("NumDependencies", genealogy.getNodeId(node), numDependants));
		}
		
		//Definition dependants
		int numDefinitionDependants = genealogy.getDependants(node, GenealogyEdgeType.DefinitionOnDefinition,
				GenealogyEdgeType.DefinitionOnDeletedDefinition, GenealogyEdgeType.DeletedDefinitionOnDefinition)
				.size();
		result.add(new GenealogyMetricValue(allDependants, genealogy.getNodeId(node), numDefinitionDependants));
		
		//Call dependants
		int numCallDependants = genealogy.getDependants(node, GenealogyEdgeType.CallOnDefinition,
				GenealogyEdgeType.DeletedCallOnCall, GenealogyEdgeType.DeletedCallOnDeletedDefinition).size();
		result.add(new GenealogyMetricValue(allDependants, genealogy.getNodeId(node), numCallDependants));
		
		//check for data integrity
		Condition.check(result.size() == getMetricNames().size(), "The number of "
				+ "generated dependency metrics differs from the number of metric names. "
				+ "Num of metric names: %s. Num of metric values: %s. "
				+ "Please check for miss matches. MetricNames=%s. Generated MetricValue=%s", getMetricNames().size(),
				result.size(), StringUtils.join(getMetricNames(), ","), StringUtils.join(result, ","));
		
		return result;
	}
	
}
