package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.util.Collection;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public abstract class ChangeGenealogyVertexMetric {
	
	public ChangeGenealogyVertexMetric(ChangeGenealogyMetricSet metricSet){
		if (!metricSet.register(this, this.getClass().getCanonicalName())) {
			if (Logger.logError()) {
				Logger.error("Could not register change genealogy metric " + this.getClass().getCanonicalName()
						+ ". Most likely it had been registered before.");
			}
		}
	}
	
	public final String getVertexLabel(Collection<JavaChangeOperation> vertices) {
		if (vertices.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (JavaChangeOperation vertex : vertices) {
			sb.append(vertex.getId());
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(vertices.iterator().next().getRevision().getTransaction().getId());
		return sb.toString();
	}

	public final String getVertexLabel(JavaChangeOperation vertex) {
		StringBuilder sb = new StringBuilder();
		sb.append(vertex.getId());
		sb.append("@");
		sb.append(vertex.getRevision().getTransaction().getId());
		return sb.toString();
	}
	
	public final String getVertexLabel(Object vertex) {
		if (Logger.logError()) {
			Logger.error("Unsupported vertex type: " + vertex.getClass().getCanonicalName());
		}
		return "";
	}
	
	public final String getVertexLabel(RCSTransaction vertex) {
		return vertex.getId();
	}
	
	public abstract double visit(Collection<JavaChangeOperation> vertex);
	
	public abstract double visit(JavaChangeOperation vertex);
	
	public final double visit(Object vertex) {
		if (Logger.logError()) {
			Logger.error("Unsupported vertex type: " + vertex.getClass().getCanonicalName());
		}
		return 0;
	}
	
	public abstract double visit(RCSTransaction vertex);
	
}
