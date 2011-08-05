package de.unisaarland.cs.st.reposuite.genealogies;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.genealogies.model.GraphDBChangeOperation;
import de.unisaarland.cs.st.reposuite.genealogies.model.GraphDBVertex;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;

public class GenealogyVertex {
	
	public static enum GenealogyEdgeType {
		DefinitionOnDefinition,
		DefinitionOnDeletedDefinition,
		CallOnDefinition,
		DeletedDefinitionOnDefinition,
		DeletedCallOnCall,
		DeletedCallOnDeletedDefinition,
		UNKNOWN
	}
	
	private final GraphDBVertex node;
	private final ChangeGenealogy genealogy;
	
	protected GenealogyVertex(final ChangeGenealogy genealogy, final GraphDBVertex node) {
		this.node = node;
		this.genealogy = genealogy;
	}
	
	public GenealogyChangeOperation addChangeOperation(final JavaChangeOperation op) {
		return this.genealogy.addChangeOperationToVertex(op, this);
	}
	
	/**
	 * Adds a dependency between this --> v. Which means that this vertex
	 * requires vertex v to be applied first.
	 * 
	 * @param v
	 *            the GenealogyVertex this vertex shall depend upon.
	 * @param type
	 *            the type of the dependency to be added
	 */
	public void addDependency(final GenealogyVertex v, final GenealogyEdgeType type) {
		switch (type) {
			case DefinitionOnDefinition:
				this.node.addDoDDependency(v.getNode());
				break;
			case DefinitionOnDeletedDefinition:
				this.node.addDoDDDependency(v.getNode());
				break;
			case CallOnDefinition:
				this.node.addCoDDependency(v.getNode());
				break;
			case DeletedDefinitionOnDefinition:
				this.node.addDDoDDependency(v.getNode());
				break;
			case DeletedCallOnCall:
				this.node.addDCoCDependency(v.getNode());
				break;
			case DeletedCallOnDeletedDefinition:
				this.node.addDCoDDDependency(v.getNode());
				break;
			default:
				this.node.addUnknownDependency(v.getNode());
				break;
		}
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GenealogyVertex other = (GenealogyVertex) obj;
		return node.getId().equals(other.node.getId());
	}
	
	public Collection<GenealogyVertex> getAllIncomingDependencies() {
		Set<GenealogyVertex> result = new HashSet<GenealogyVertex>();
		for (GenealogyEdgeType t : GenealogyEdgeType.values()) {
			result.addAll(getIncomingDependencies(t));
		}
		return result;
	}
	
	public Collection<GenealogyVertex> getAllOutgoingDependencies(){
		Set<GenealogyVertex> result = new HashSet<GenealogyVertex>();
		for (GenealogyEdgeType t : GenealogyEdgeType.values()) {
			result.addAll(getOutgoingDependencies(t));
		}
		return result;
	}
	
	public Collection<JavaChangeOperation> getChangeOperations(){
		Collection<GraphDBChangeOperation> dbOps = this.node.getChangeOperations();
		Set<Long> opIds = new HashSet<Long>();
		for (GraphDBChangeOperation dbOp : dbOps) {
			opIds.add(dbOp.getChangeOperationId());
		}
		
		//get the JavaChangeOperation instances out of the relational database
		PersistenceUtil persistenceUtil = this.genealogy.getPersistenceUtil();
		List<JavaChangeOperation> result = new LinkedList<JavaChangeOperation>();
		
		Criteria<JavaChangeOperation> criteria = persistenceUtil.createCriteria(JavaChangeOperation.class);
		criteria.in("id", opIds);
		result.addAll(persistenceUtil.load(criteria));
		
		return result;
	}
	
	public Collection<GenealogyVertex> getIncomingDependencies(final GenealogyEdgeType type) {
		Collection<GraphDBVertex> dependencies;
		switch (type) {
			case DefinitionOnDefinition:
				dependencies = this.node.getIncomingDoDDependencies();
				break;
			case DefinitionOnDeletedDefinition:
				dependencies = this.node.getIncomingDoDDDependencies();
				break;
			case CallOnDefinition:
				dependencies = this.node.getIncomingCoDDependencies();
				break;
			case DeletedDefinitionOnDefinition:
				dependencies = this.node.getIncomingDDoDDependencies();
				break;
			case DeletedCallOnCall:
				dependencies = this.node.getIncomingDCoCDependencies();
				break;
			case DeletedCallOnDeletedDefinition:
				dependencies = this.node.getIncomingDCoDDDependencies();
				break;
			default:
				dependencies = this.node.getIncomingUnknownDependencies();
				break;
		}
		Set<GenealogyVertex> result = new HashSet<GenealogyVertex>();
		for (GraphDBVertex v : dependencies) {
			result.add(new GenealogyVertex(this.genealogy, v));
		}
		return result;
	}
	
	protected GraphDBVertex getNode() {
		return this.node;
	}
	
	public Collection<GenealogyVertex> getOutgoingDependencies(final GenealogyEdgeType type) {
		Collection<GraphDBVertex> dependencies;
		switch (type) {
			case DefinitionOnDefinition:
				dependencies = this.node.getDoDDependencies();
				break;
			case DefinitionOnDeletedDefinition:
				dependencies = this.node.getDoDDDependencies();
				break;
			case CallOnDefinition:
				dependencies = this.node.getCoDDependencies();
				break;
			case DeletedDefinitionOnDefinition:
				dependencies = this.node.getDDoDDependencies();
				break;
			case DeletedCallOnCall:
				dependencies = this.node.getDCoCDependencies();
				break;
			case DeletedCallOnDeletedDefinition:
				dependencies = this.node.getDCoDDDependencies();
				break;
			default:
				dependencies = this.node.getUnknownDependencies();
				break;
		}
		Set<GenealogyVertex> result = new HashSet<GenealogyVertex>();
		for (GraphDBVertex v : dependencies) {
			result.add(new GenealogyVertex(this.genealogy, v));
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		return this.node.getId().hashCode();
	}
	
}
