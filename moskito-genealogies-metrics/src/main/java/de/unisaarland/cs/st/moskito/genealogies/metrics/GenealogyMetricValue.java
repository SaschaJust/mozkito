package de.unisaarland.cs.st.moskito.genealogies.metrics;


public class GenealogyMetricValue {
	
	private String nodeId;
	private double value;
	private String metricId;
	
	public GenealogyMetricValue(String metricId, String nodeId, double value) {
		this.nodeId = nodeId;
		this.value = value;
		this.metricId = metricId;
	}
	
	public String getMetricId() {
		return this.metricId;
	}
	
	public String getNodeId() {
		return this.nodeId;
	}
	
	public double getValue() {
		return this.value;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("MetricValue[");
		
		sb.append("metricId = ");
		sb.append(getMetricId());
		
		sb.append(", nodeId = ");
		sb.append(getNodeId());
		
		sb.append(", value = ");
		sb.append(getValue());
		
		sb.append("]");
		
		return sb.toString();
	}

}
