package de.unisaarland.cs.st.reposuite.genealogies.model;

import java.util.Collection;

import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.Direction;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Relation;

public interface GraphDBChangeOperation extends Vertex {
	
	public static String keyName = "javachangeoperationId";
	
	@Property(keyName)
	public long getChangeOperationId();
	
	@Relation(label=GraphDBVertex.toOperationLabel, direction=Direction.INVERSE)
	Collection<GraphDBVertex> getContainingVertices();
	
	@Property(keyName)
	void setChangeOperationId(Long id);
	
}
