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
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.mozkito.mappings.engines.MappingEngine;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.persistence.Annotated;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * The Class Mapping.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
public class Relation implements Annotated, IRelation {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8606759070008468513L;
	
	/** The features. */
	private Queue<Feature>    features         = new LinkedBlockingQueue<Feature>();
	
	private Candidate         candidate;
	
	/**
	 * Instantiates a new mapping.
	 * 
	 * @deprecated use {@link Relation#Mapping(MappableEntity, MappableEntity)} used by persistence provider only
	 */
	@Deprecated
	public Relation() {
		
	}
	
	/**
	 * Instantiates a new mapping.
	 * 
	 * @param element1
	 *            the element1
	 * @param element2
	 *            the element2
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
	                       @NotNull final Class<? extends MappingEngine> mappingEngine) {
		getFeatures().add(new Feature(confidence, transactionFieldName, transactionSubstring, reportFieldName,
		                              reportSubstring, mappingEngine));
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * org.mozkito.mapping.model.IMapping#compareTo(org.mozkito.mapping.model.Mapping)
	 */
	@Override
	public int compareTo(final IRelation arg0) {
		return Double.compare(getTotalConfidence(), arg0.getTotalConfidence());
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
	@Override
	@Id
	@ManyToOne (fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	public Candidate getCandidate() {
		return this.candidate;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IMapping#getClass1()
	 */
	@Override
	public String getClass1() {
		return getCandidate().getFrom().getBaseType().getCanonicalName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IMapping#getClass2()
	 */
	@Override
	public String getClass2() {
		return getCandidate().getTo().getBaseType().getCanonicalName();
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
	@Override
	public MappableEntity getFrom() {
		return getCandidate().getFrom();
	}
	
	/**
	 * Gets the scoring engines.
	 * 
	 * @return a set of {@link MappingEngine} classes that were used for this scoring
	 */
	@Transient
	public Set<Class<? extends MappingEngine>> getScoringEngines() {
		final HashSet<Class<? extends MappingEngine>> engines = new HashSet<Class<? extends MappingEngine>>();
		
		for (final Feature feature : getFeatures()) {
			engines.add(feature.getEngine());
		}
		
		return engines;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IMapping#getElement2()
	 */
	@Override
	public MappableEntity getTo() {
		return getCandidate().getTo();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mapping.model.IMapping#getTotalConfidence()
	 */
	@Override
	@Transient
	public double getTotalConfidence() {
		// PRECONDITIONS
		Condition.notNull(this.features, "Field '%s' in '%s'", "features", getClass().getSimpleName());
		
		// METHOD BODY
		double confidence = 0d;
		
		try {
			for (final Feature feature : getFeatures()) {
				confidence += feature.getConfidence();
			}
			
			return confidence;
		} finally {
			// POSTCONDITIONS
			CompareCondition.notNegative(confidence, "local variable '%s' in '%s:%s'", "confidence",
			                             getClass().getSimpleName(), "getTotalConfidence");
		}
	}
	
	/**
	 * @param candidate
	 *            the candidate to set
	 */
	public final void setCandidate(final Candidate candidate) {
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
		builder.append("Mapping [totalConfidence=");
		builder.append(getTotalConfidence());
		builder.append(", element1=");
		builder.append(getFrom());
		builder.append(", element2=");
		builder.append(getTo());
		builder.append(", features=");
		builder.append(JavaUtils.collectionToString(getFeatures()));
		builder.append("]");
		return builder.toString();
	}
	
}
