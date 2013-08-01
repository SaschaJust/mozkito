/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.graphs;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Element;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class GraphIndex.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class GraphIndex {
	
	/**
	 * Make.
	 * 
	 * @return the graph index
	 */
	public static GraphIndex make() {
		return new GraphIndex();
	}
	
	/** The field name. */
	private String                   fieldName  = null;
	
	/** The data type. */
	private Class<?>                 dataType   = null;
	
	/** The target type. */
	private Class<? extends Element> targetType = null;
	
	/** The signature. */
	private GraphIndex[]             signature  = null;
	
	/** The unique. */
	private Direction                unique     = null;
	
	/** The primary key. */
	private GraphIndex[]             primaryKey = null;
	
	/** The index name. */
	private String                   indexName  = null;
	
	/**
	 * Data type.
	 * 
	 * @param dataType
	 *            the data type
	 * @return the graph index
	 */
	public GraphIndex dataType(@NotNull final Class<?> dataType) {
		this.dataType = dataType;
		return this;
	}
	
	/**
	 * Gets the data type.
	 * 
	 * @return the data type
	 */
	public Class<?> getDataType() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.dataType;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the field name.
	 * 
	 * @return the field name
	 */
	public String getFieldName() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.fieldName;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the index name.
	 * 
	 * @return the indexName
	 */
	public final String getIndexName() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.indexName;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.indexName, "Field '%s' in '%s'.", "indexName", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the primary key.
	 * 
	 * @return the primaryKey
	 */
	public final GraphIndex[] getPrimaryKey() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.primaryKey;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.primaryKey, "Field '%s' in '%s'.", "primaryKey", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the signature.
	 * 
	 * @return the signature
	 */
	public final GraphIndex[] getSignature() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.signature;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.signature, "Field '%s' in '%s'.", "signature", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the target type.
	 * 
	 * @return the targetType
	 */
	public final Class<? extends Element> getTargetType() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.targetType;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.targetType, "Field '%s' in '%s'.", "targetType", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the unique.
	 * 
	 * @return the unique
	 */
	public final Direction getUnique() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.unique;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.unique, "Field '%s' in '%s'.", "unique", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Index name.
	 * 
	 * @param indexName
	 *            the index name
	 * @return the graph index
	 */
	public GraphIndex indexName(final String indexName) {
		this.indexName = indexName;
		return this;
	}
	
	/**
	 * Name.
	 * 
	 * @param fieldName
	 *            the field name
	 * @return the graph index
	 */
	public GraphIndex name(@NotNull final String fieldName) {
		this.fieldName = fieldName;
		return this;
	}
	
	/**
	 * Primary key.
	 * 
	 * @param graphIndex
	 *            the graph index
	 * @return the graph index
	 */
	public GraphIndex primaryKey(final GraphIndex... graphIndex) {
		this.primaryKey = graphIndex;
		return this;
	}
	
	/**
	 * Signature.
	 * 
	 * @param graphIndex
	 *            the graph index
	 * @return the graph index
	 */
	public GraphIndex signature(final GraphIndex... graphIndex) {
		this.signature = graphIndex;
		return this;
	}
	
	/**
	 * Target type.
	 * 
	 * @param targetType
	 *            the target type
	 * @return the graph index
	 */
	public GraphIndex targetType(@NotNull final Class<? extends Element> targetType) {
		this.targetType = targetType;
		return this;
	}
	
	/**
	 * Unique.
	 * 
	 * @param direction
	 *            the direction
	 * @return the graph index
	 */
	public GraphIndex unique(final Direction direction) {
		this.unique = direction;
		return this;
	}
	
	/**
	 * Gets the direction.
	 * 
	 * @return the direction
	 */
	public Direction uniqueness() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.unique;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
