package de.unisaarland.cs.st.moskito.rcs.git;

import org.neo4j.graphdb.RelationshipType;

public enum GitRevDependencyType implements RelationshipType {
	BRANCH_EDGE, MERGE_EDGE;
}
