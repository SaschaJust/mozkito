/*******************************************************************************
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
 ******************************************************************************/
package org.mozkito.mappings.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.mozkito.mappings.engines.Engine;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.persistence.Annotated;

/**
 * The Class Mapping.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class Relation implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8606759070008468513L;
	
	/** The candidate. */
	private Candidate         candidate;
	
	/** The features. */
	private Queue<Feature>    features         = new LinkedBlockingQueue<Feature>();
	
	/**
	 * Instantiates a new mapping.
	 * 
	 * @deprecated use {@link Relation#Mapping(MappableEntity, MappableEntity)} used by persistence provider only
	 */
	@Deprecated
	public Relation() {
		// for OpenJPA only
	}
	
	/**
	 * Instantiates a new mapping.
	 * 
	 * @param candidate
	 *            the candidate
	 */
	public Relation(final Candidate candidate) {
		this.candidate = candidate;
	}
	
	/**
	 * Adds the feature.
	 * 
	 * @param confidence
	 *            the confidence
	 * @param transactionFieldName
	 *            the transaction field name
	 * @param transactionFieldContent
	 *            the transaction field content
	 * @param transactionSubstring
	 *            the transaction substring
	 * @param reportFieldName
	 *            the report field name
	 * @param reportFieldContent
	 *            the report field content
	 * @param reportSubstring
	 *            the report substring
	 * @param mappingEngine
	 *            the mapping engine
	 */
	@Transient
	public void addFeature(final double confidence,
	                       @NotNull @NotEmpty final String transactionFieldName,
	                       @NotNull @NotEmpty final String transactionFieldContent,
	                       @NotNull @NotEmpty final String transactionSubstring,
	                       @NotNull @NotEmpty final String reportFieldName,
	                       @NotNull @NotEmpty final String reportFieldContent,
	                       @NotNull @NotEmpty final String reportSubstring,
	                       @NotNull final Class<? extends Engine> mappingEngine) {
		getFeatures().add(new Feature(confidence, transactionFieldName, transactionSubstring, reportFieldName,
		                              reportSubstring, mappingEngine));
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	/**
	 * Compare to.
	 * 
	 * @param arg0
	 *            the arg0
	 * @return the int
	 */
	public int compareTo(final Relation arg0) {
		return Double.compare(getTotalConfidence(), arg0.getTotalConfidence());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
		final Relation other = (Relation) obj;
		if (getCandidate() == null) {
			if (other.getCandidate() != null) {
				return false;
			}
		} else if (!getCandidate().equals(other.getCandidate())) {
			return false;
		}
		return true;
	}
	
	// /**
	// * Fetch id.
	// *
	// * @param o
	// * the o
	// * @return the string
	// */
	// private String fetchId(final Object o) {
	// try {
	// final Method method = o.getClass().getMethod("getId", new Class<?>[0]);
	// return (String) method.invoke(o, new Object[0]);
	// } catch (final SecurityException ignore) { // ignore
	// } catch (final NoSuchMethodException ignore) { // ignore
	// } catch (final IllegalArgumentException ignore) { // ignore
	// } catch (final IllegalAccessException ignore) { // ignore
	// } catch (final InvocationTargetException ignore) { // ignore
	// }
	// return null;
	// }
	//
	/**
	 * Gets the candidate.
	 * 
	 * @return the candidate
	 */
	@Id
	@OneToOne (fetch = FetchType.EAGER, cascade = {})
	public Candidate getCandidate() {
		return this.candidate;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IMapping#getClass1()
	 */
	/**
	 * Gets the class1.
	 * 
	 * @return the class1
	 */
	@Transient
	public String getClass1() {
		return getCandidate().getClass1();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IMapping#getClass2()
	 */
	/**
	 * Gets the class2.
	 * 
	 * @return the class2
	 */
	@Transient
	public String getClass2() {
		return getCandidate().getClass2();
	}
	
	/**
	 * Gets the scoring engines.
	 * 
	 * @return a set of {@link Engine} classes that were used for this scoring
	 */
	@Transient
	public Set<Class<? extends Engine>> getEngines() {
		final HashSet<Class<? extends Engine>> engines = new HashSet<Class<? extends Engine>>();
		
		for (final Feature feature : getFeatures()) {
			engines.add(feature.getEngine());
		}
		
		return engines;
	}
	
	/**
	 * Gets the features.
	 * 
	 * @return the features
	 */
	@ElementCollection (fetch = FetchType.EAGER)
	public Queue<Feature> getFeatures() {
		return this.features;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IMapping#getElement1()
	 */
	/**
	 * Gets the from.
	 * 
	 * @return the from
	 */
	@Transient
	public MappableEntity getFrom() {
		return getCandidate().getFrom();
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	@Transient
	public final String getHandle() {
		// PRECONDITIONS
		
		final StringBuilder builder = new StringBuilder();
		
		try {
			final LinkedList<Class<?>> list = new LinkedList<Class<?>>();
			Class<?> clazz = getClass();
			list.add(clazz);
			
			while ((clazz = clazz.getEnclosingClass()) != null) {
				list.addFirst(clazz);
			}
			
			for (final Class<?> c : list) {
				if (builder.length() > 0) {
					builder.append('.');
				}
				
				builder.append(c.getSimpleName());
			}
			
			return builder.toString();
		} finally {
			// POSTCONDITIONS
			Condition.notNull(builder,
			                  "Local variable '%s' in '%s:%s'.", "builder", getClass().getSimpleName(), "getHandle()"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IMapping#getElement2()
	 */
	/**
	 * Gets the to.
	 * 
	 * @return the to
	 */
	@Transient
	public MappableEntity getTo() {
		return getCandidate().getTo();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IMapping#getTotalConfidence()
	 */
	/**
	 * Gets the total confidence.
	 * 
	 * @return the total confidence
	 */
	@Transient
	public double getTotalConfidence() {
		// PRECONDITIONS
		Condition.notNull(getFeatures(), "Field '%s' in '%s'", "features", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		
		// METHOD BODY
		double confidence = 0d;
		
		try {
			for (final Feature feature : getFeatures()) {
				confidence += feature.getConfidence();
			}
			
			return confidence;
		} finally {
			// POSTCONDITIONS
			CompareCondition.notNegative(confidence, "local variable '%s' in '%s:%s'", "confidence", //$NON-NLS-1$ //$NON-NLS-2$
			                             getClass().getSimpleName(), "getTotalConfidence"); //$NON-NLS-1$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((getCandidate() == null)
		                                                     ? 0
		                                                     : getCandidate().hashCode());
		return result;
	}
	
	/**
	 * Sets the candidate.
	 * 
	 * @param candidate
	 *            the candidate to set
	 */
	public void setCandidate(final Candidate candidate) {
		this.candidate = candidate;
		
	}
	
	/**
	 * Sets the features.
	 * 
	 * @param features
	 *            the features to set
	 */
	public void setFeatures(final Queue<Feature> features) {
		this.features = features;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(getHandle());
		builder.append(" [totalConfidence="); //$NON-NLS-1$
		builder.append(getTotalConfidence());
		builder.append(", element1="); //$NON-NLS-1$
		builder.append(getFrom());
		builder.append(", element2="); //$NON-NLS-1$
		builder.append(getTo());
		builder.append(", features="); //$NON-NLS-1$
		builder.append(JavaUtils.collectionToString(getFeatures()));
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}
	
}
