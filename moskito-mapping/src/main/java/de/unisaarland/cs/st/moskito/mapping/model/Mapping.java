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
package de.unisaarland.cs.st.moskito.mapping.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;
import de.unisaarland.cs.st.moskito.mapping.elements.MapId;
import de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.persistence.Annotated;

/**
 * The Class Mapping.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Entity
@IdClass (MapId.class)
public class Mapping implements Annotated, IMapping {
	
	/** The Constant serialVersionUID. */
	private static final long           serialVersionUID = -8606759070008468513L;
	
	/** The features. */
	private Queue<MappingEngineFeature> features         = new LinkedBlockingQueue<MappingEngineFeature>();
	
	/** The strategies. */
	private Map<String, Boolean>        strategies       = new HashMap<String, Boolean>();
	
	/** The total confidence. */
	private double                      totalConfidence  = 0.0d;
	
	/** The element1. */
	private MappableEntity              element1;
	
	/** The element2. */
	private MappableEntity              element2;
	
	/** The from id. */
	private String                      fromId;
	
	/** The class1. */
	private String                      class1;
	
	/** The to id. */
	private String                      toId;
	
	/** The class2. */
	private String                      class2;
	
	/**
	 * Instantiates a new mapping.
	 * 
	 * @deprecated use {@link Mapping#Mapping(MappableEntity, MappableEntity)} used by persistence provider only
	 */
	@Deprecated
	public Mapping() {
		
	}
	
	/**
	 * Instantiates a new mapping.
	 * 
	 * @param element1
	 *            the element1
	 * @param element2
	 *            the element2
	 */
	public Mapping(final MappableEntity element1, final MappableEntity element2) {
		setElement1(element1);
		setElement2(element2);
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
		setTotalConfidence(getTotalConfidence() + confidence);
		getFeatures().add(new MappingEngineFeature(confidence, transactionFieldName, transactionSubstring,
		                                           reportFieldName, reportSubstring, mappingEngine));
	}
	
	/**
	 * Adds the strategy.
	 * 
	 * @param strategyName
	 *            the strategy name
	 * @param valid
	 *            the valid
	 */
	@Transient
	public void addStrategy(@NotNull @NotEmptyString final String strategyName,
	                        final Boolean valid) {
		getStrategies().put(strategyName, valid);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.model.IMapping#compareTo(de.unisaarland.cs.st.moskito.mapping.model.Mapping)
	 */
	@Override
	public int compareTo(final IMapping arg0) {
		return Double.compare(getTotalConfidence(), arg0.getTotalConfidence());
	}
	
	/**
	 * Fetch id.
	 * 
	 * @param o
	 *            the o
	 * @return the string
	 */
	private String fetchId(final Object o) {
		try {
			final Method method = o.getClass().getMethod("getId", new Class<?>[0]);
			return (String) method.invoke(o, new Object[0]);
		} catch (final SecurityException ignore) { // ignore
		} catch (final NoSuchMethodException ignore) { // ignore
		} catch (final IllegalArgumentException ignore) { // ignore
		} catch (final IllegalAccessException ignore) { // ignore
		} catch (final InvocationTargetException ignore) { // ignore
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.model.IMapping#getClass1()
	 */
	@Override
	public String getClass1() {
		return this.class1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.model.IMapping#getClass2()
	 */
	@Override
	public String getClass2() {
		return this.class2;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.model.IMapping#getElement1()
	 */
	@Override
	@ManyToOne (fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	public MappableEntity getElement1() {
		return this.element1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.model.IMapping#getElement2()
	 */
	@Override
	@ManyToOne (fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	public MappableEntity getElement2() {
		return this.element2;
	}
	
	/**
	 * Gets the features.
	 * 
	 * @return the features
	 */
	@ElementCollection (fetch = FetchType.EAGER)
	public Queue<MappingEngineFeature> getFeatures() {
		return this.features;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.model.IMapping#getFromId()
	 */
	@Override
	@Id
	public String getFromId() {
		return this.fromId;
	}
	
	/**
	 * Gets the scoring engines.
	 * 
	 * @return a set of {@link MappingEngine} classes that were used for this scoring
	 */
	@Transient
	public Set<Class<? extends MappingEngine>> getScoringEngines() {
		final HashSet<Class<? extends MappingEngine>> engines = new HashSet<Class<? extends MappingEngine>>();
		
		for (final MappingEngineFeature feature : getFeatures()) {
			engines.add(feature.getEngine());
		}
		
		return engines;
	}
	
	/**
	 * Gets the strategies.
	 * 
	 * @return the strategies
	 */
	@ElementCollection
	public Map<String, Boolean> getStrategies() {
		return this.strategies;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.model.IMapping#getToId()
	 */
	@Override
	@Id
	public String getToId() {
		return this.toId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.model.IMapping#getTotalConfidence()
	 */
	@Override
	@Basic
	public double getTotalConfidence() {
		return this.totalConfidence;
	}
	
	/**
	 * Sets the class1.
	 * 
	 * @param class1
	 *            the new class1
	 */
	public void setClass1(final String class1) {
		this.class1 = class1;
	}
	
	/**
	 * Sets the class2.
	 * 
	 * @param class2
	 *            the new class2
	 */
	public void setClass2(final String class2) {
		this.class2 = class2;
	}
	
	/**
	 * Sets the element1.
	 * 
	 * @param element1
	 *            the new element1
	 */
	public void setElement1(final MappableEntity element1) {
		this.element1 = element1;
		if (element1 != null) {
			setClass1(element1.getClass().getSimpleName());
			setFromId(fetchId(element1));
		}
	}
	
	/**
	 * Sets the element2.
	 * 
	 * @param element2
	 *            the new element2
	 */
	public void setElement2(final MappableEntity element2) {
		this.element2 = element2;
		
		if (element2 != null) {
			setClass2(element2.getClass().getSimpleName());
			setToId(fetchId(element2));
		}
	}
	
	/**
	 * Sets the features.
	 * 
	 * @param features
	 *            the features to set
	 */
	public void setFeatures(final Queue<MappingEngineFeature> features) {
		this.features = features;
	}
	
	/**
	 * Sets the from id.
	 * 
	 * @param id1
	 *            the new from id
	 */
	public void setFromId(final String id1) {
		this.fromId = id1;
	}
	
	/**
	 * Sets the strategies.
	 * 
	 * @param strategies
	 *            the strategies
	 */
	public void setStrategies(final Map<String, Boolean> strategies) {
		this.strategies = strategies;
	}
	
	/**
	 * Sets the to id.
	 * 
	 * @param id2
	 *            the new to id
	 */
	public void setToId(final String id2) {
		this.toId = id2;
	}
	
	/**
	 * Sets the total confidence.
	 * 
	 * @param totalConfidence
	 *            the totalConfidence to set
	 */
	public void setTotalConfidence(final double totalConfidence) {
		this.totalConfidence = totalConfidence;
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
		builder.append(getElement1());
		builder.append(", element2=");
		builder.append(getElement2());
		builder.append(", features=");
		builder.append(JavaUtils.collectionToString(getFeatures()));
		builder.append("]");
		return builder.toString();
	}
	
}
