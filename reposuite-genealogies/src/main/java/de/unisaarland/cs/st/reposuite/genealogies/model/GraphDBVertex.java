package de.unisaarland.cs.st.reposuite.genealogies.model;

import java.util.Collection;

import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.Direction;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Relation;

public interface GraphDBVertex extends Vertex {
	
	public static String keyName = "transactionId";
	public static String toOperationLabel = "contains";
	
	@Relation(label = toOperationLabel)
	public Collection<GraphDBChangeOperation> addChangeOperations(GraphDBChangeOperation op);
	
	@Relation(label = "CoD")
	void addCoDDependency(GraphDBVertex v);
	
	@Relation(label = "DCoC")
	void addDCoCDependency(GraphDBVertex v);
	
	@Relation(label = "DCoDD")
	void addDCoDDDependency(GraphDBVertex v);
	
	@Relation(label = "DDoD")
	void addDDoDDependency(GraphDBVertex v);
	
	@Relation(label = "DoDD")
	void addDoDDDependency(GraphDBVertex v);
	
	@Relation(label = "DoD")
	void addDoDDependency(GraphDBVertex v);
	
	@Relation(label = "UNKNOWN")
	void addUnknownDependency(GraphDBVertex v);
	
	@Relation(label = toOperationLabel)
	public Collection<GraphDBChangeOperation> getChangeOperations();
	
	@Relation(label = "CoD")
	public Collection<GraphDBVertex> getCoDDependencies();
	
	@Relation(label = "DCoC")
	public Collection<GraphDBVertex> getDCoCDependencies();
	
	@Relation(label = "DCoDD")
	public Collection<GraphDBVertex> getDCoDDDependencies();
	
	@Relation(label = "DDoD")
	public Collection<GraphDBVertex> getDDoDDependencies();
	
	@Relation(label = "DoDD")
	public Collection<GraphDBVertex> getDoDDDependencies();
	
	@Relation(label = "DoD")
	public Collection<GraphDBVertex> getDoDDependencies();
	
	@Relation(label = "CoD", direction=Direction.INVERSE)
	public Collection<GraphDBVertex> getIncomingCoDDependencies();
	
	@Relation(label = "DCoC", direction=Direction.INVERSE)
	public Collection<GraphDBVertex> getIncomingDCoCDependencies();
	
	@Relation(label = "DCoDD", direction=Direction.INVERSE)
	public Collection<GraphDBVertex> getIncomingDCoDDDependencies();
	
	@Relation(label = "DDoD", direction=Direction.INVERSE)
	public Collection<GraphDBVertex> getIncomingDDoDDependencies();
	
	@Relation(label = "DoDD", direction=Direction.INVERSE)
	public Collection<GraphDBVertex> getIncomingDoDDDependencies();
	
	@Relation(label = "DoD", direction=Direction.INVERSE)
	public Collection<GraphDBVertex> getIncomingDoDDependencies();
	
	@Relation(label = "UNKNOWN", direction=Direction.INVERSE)
	public Collection<GraphDBVertex> getIncomingUnknownDependencies();
	
	@Property(keyName)
	public String getTransactionId();
	
	@Relation(label = "UNKNOWN")
	public Collection<GraphDBVertex> getUnknownDependencies();
	
	@Property(keyName)
	void setTransactionId(String tId);
}


