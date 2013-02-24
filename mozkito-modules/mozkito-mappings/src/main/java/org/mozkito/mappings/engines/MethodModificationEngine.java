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
package org.mozkito.mappings.engines;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.codeanalysis.model.JavaMethodDefinition;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.mappable.model.MappableChangeSet;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.mappings.storages.PersistenceStorage;
import org.mozkito.mappings.storages.Storage;
import org.mozkito.persistence.PPAPersistenceUtil;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.model.ChangeSet;

/**
 * This engine scores according to the equality of the authors of both entities. If the confidence value isn't set
 * explicitly, the default value is used.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class MethodModificationEngine extends Engine {
	
	/** The constant defaultConfidence. */
	public static final Double DEFAULT_CONFIDENCE = 1d;
	
	/** The constant description. */
	public static final String DESCRIPTION        = Messages.getString("MethodModificationEngine.description"); //$NON-NLS-1$
	                                                                                                            
	/** The Constant TAG. */
	public static final String TAG                = "methodmodification";
	
	/** The confidence. */
	private Double             confidence         = MethodModificationEngine.DEFAULT_CONFIDENCE;
	
	/**
	 * Instantiates a new author equality engine.
	 * 
	 * @param confidence
	 *            the confidence
	 */
	public MethodModificationEngine(@NotNull final double confidence) {
		// PRECONDITIONS
		
		try {
			this.confidence = confidence;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the confidence.
	 * 
	 * @return the confidence
	 */
	public final Double getConfidence() {
		// PRECONDITIONS
		
		try {
			return this.confidence;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.confidence, "Field '%s' in '%s'.", "confidence", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Override
	public final String getDescription() {
		return MethodModificationEngine.DESCRIPTION;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.engines.Engine#score(org.mozkito.mappings.model.Relation)
	 */
	@Override
	public void score(final @NotNull Relation relation) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final MappableEntity from = relation.getFrom();
			final MappableEntity to = relation.getTo();
			final PersistenceStorage persistenceStorage = getStorage(PersistenceStorage.class);
			
			SANITY: {
				assert from != null;
				assert to != null;
				assert persistenceStorage != null;
				assert persistenceStorage.getUtil() != null;
			}
			
			int matches = 0;
			final StringBuilder builder = new StringBuilder();
			double localConfidence = 0d;
			
			final PersistenceUtil persistenceUtil = persistenceStorage.getUtil();
			final Set<String> subjects = new HashSet<>();
			
			final Collection<JavaChangeOperation> changeOperations = PPAPersistenceUtil.getChangeOperation(persistenceUtil,
			                                                                                               ((MappableChangeSet) from).getChangeSet());
			
			for (final JavaChangeOperation operation : changeOperations) {
				if (operation.getChangeType().equals(ChangeType.Modified)) {
					
					final JavaElement javaElement = operation.getChangedElementLocation().getElement();
					if (javaElement instanceof JavaMethodDefinition) {
						final String fullQualifiedName = javaElement.getFullQualifiedName();
						subjects.add(fullQualifiedName);
					}
				}
			}
			
			for (final String subject : subjects) {
				final String bodyText = (String) to.get(FieldKey.BODY);
				
				if (StringUtils.containsIgnoreCase(bodyText, subject)) {
					++matches;
					if (builder.length() > 0) {
						builder.append(',');
					}
					builder.append(subject.toUpperCase());
				}
			}
			
			localConfidence = ((double) matches) / ((double) subjects.size());
			
			addFeature(relation, localConfidence, "JAVA_CHANGE_OPERATION", "", //$NON-NLS-1$ //$NON-NLS-2$
			           JavaUtils.collectionToString(subjects), FieldKey.BODY.name(), "", builder.toString()); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				assert CollectionUtils.exists(relation.getFeatures(), new Predicate() {
					
					/**
					 * {@inheritDoc}
					 * 
					 * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
					 */
					@Override
					public boolean evaluate(final Object object) {
						return ((Feature) object).getEngine().equals(getClass());
					}
				});
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.register.Node#storageDependency()
	 */
	@Override
	public Set<Class<? extends Storage>> storageDependency() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return new HashSet<Class<? extends Storage>>() {
				
				/**
                 * 
                 */
				private static final long serialVersionUID = 5345541441559660378L;
				
				{
					add(PersistenceStorage.class);
				}
			};
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Supported.
	 * 
	 * @return the expression
	 */
	@Override
	public final Expression supported() {
		return new And(new Atom(Index.FROM, ChangeSet.class), new Atom(Index.TO, FieldKey.BODY));
	}
	
}
