package de.unisaarland.cs.st.moskito.genealogies.core;

import org.neo4j.graphdb.RelationshipType;

public enum GenealogyEdgeType implements RelationshipType {
	DefinitionOnDefinition,
	DefinitionOnDeletedDefinition,
	CallOnDefinition,
	DeletedDefinitionOnDefinition,
	DeletedCallOnCall,
	DeletedCallOnDeletedDefinition,
	UNKNOWN
}
