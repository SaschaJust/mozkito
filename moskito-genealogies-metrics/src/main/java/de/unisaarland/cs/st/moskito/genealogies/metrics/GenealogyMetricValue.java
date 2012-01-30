/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


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
