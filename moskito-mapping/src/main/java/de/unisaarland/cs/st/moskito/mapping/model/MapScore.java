/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.mapping.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Transient;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.moskito.mapping.elements.MapId;
import de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.persistence.Annotated;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@IdClass (MapId.class)
public class MapScore implements Annotated, Comparable<MapScore> {
	
	private static final long          serialVersionUID = -8606759070008468513L;
	
	private List<MappingEngineFeature> features         = new LinkedList<MappingEngineFeature>();
	private double                     totalConfidence  = 0.0d;
	
	private MappableEntity             element1;
	private MappableEntity             element2;
	
	private String                     fromId;
	private String                     class1;
	
	private String                     toId;
	private String                     class2;
	
	/**
	 * used by persistence provider only
	 */
	public MapScore() {
		
	}
	
	/**
	 * @param transaction
	 * @param report
	 */
	public MapScore(final MappableEntity element1, final MappableEntity element2) {
		setElement1(element1);
		setElement2(element2);
	}
	
	/**
	 * @param confidence
	 * @param transactionFieldName
	 * @param relevantString
	 * @param mappingEngine
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final MapScore arg0) {
		return Double.compare(getTotalConfidence(), arg0.getTotalConfidence());
	}
	
	/**
	 * @param o
	 * @return
	 */
	private String fetchId(final Object o) {
		try {
			Method method = o.getClass().getMethod("getId", new Class<?>[0]);
			return (String) method.invoke(o, new Object[0]);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		return null;
	}
	
	/**
	 * @return
	 */
	public String getClass1() {
		return this.class1;
	}
	
	/**
	 * @return
	 */
	public String getClass2() {
		return this.class2;
	}
	
	/**
	 * @return
	 */
	@Transient
	public MappableEntity getElement1() {
		if (this.element1 != null) {
			return this.element1;
		} else {
			try {
				Class<?> clazz = Class.forName(getClass1());
				return (MappableEntity) PersistenceManager.getUtil().loadById(getFromId(), clazz);
			} catch (ClassNotFoundException e) {
				throw new UnrecoverableError(e);
			} catch (UninitializedDatabaseException e) {
				throw new UnrecoverableError(e);
			}
		}
	}
	
	/**
	 * @return
	 */
	@Transient
	public MappableEntity getElement2() {
		if (this.element2 != null) {
			return this.element2;
		} else {
			try {
				Class<?> clazz = Class.forName(getClass2());
				return (MappableEntity) PersistenceManager.getUtil().loadById(getToId(), clazz);
			} catch (ClassNotFoundException e) {
				throw new UnrecoverableError(e);
			} catch (UninitializedDatabaseException e) {
				throw new UnrecoverableError(e);
			}
		}
	}
	
	/**
	 * @return the features
	 */
	@ElementCollection (fetch = FetchType.EAGER)
	public List<MappingEngineFeature> getFeatures() {
		return this.features;
	}
	
	/**
	 * @return
	 */
	@Id
	public String getFromId() {
		return this.fromId;
	}
	
	/**
	 * @return a set of {@link MappingEngine} classes that were used for this
	 *         scoring
	 */
	@Transient
	public Set<Class<? extends MappingEngine>> getScoringEngines() {
		HashSet<Class<? extends MappingEngine>> engines = new HashSet<Class<? extends MappingEngine>>();
		
		for (MappingEngineFeature feature : getFeatures()) {
			engines.add(feature.getEngine());
		}
		
		return engines;
	}
	
	/**
	 * @return
	 */
	@Id
	public String getToId() {
		return this.toId;
	}
	
	/**
	 * @return the totalConfidence
	 */
	@Basic
	public double getTotalConfidence() {
		return this.totalConfidence;
	}
	
	/**
	 * @param class1
	 */
	public void setClass1(final String class1) {
		this.class1 = class1;
	}
	
	/**
	 * @param class2
	 */
	public void setClass2(final String class2) {
		this.class2 = class2;
	}
	
	/**
	 * @param element1
	 */
	public void setElement1(final MappableEntity element1) {
		this.element1 = element1;
		if (element1 != null) {
			setClass1(element1.getClass().getCanonicalName());
			setFromId(fetchId(element1));
		}
	}
	
	/**
	 * @param element2
	 */
	public void setElement2(final MappableEntity element2) {
		this.element2 = element2;
		
		if (element2 != null) {
			setClass2(element2.getClass().getCanonicalName());
			setToId(fetchId(element2));
		}
	}
	
	/**
	 * @param features
	 *            the features to set
	 */
	public void setFeatures(final List<MappingEngineFeature> features) {
		this.features = features;
	}
	
	/**
	 * @param id1
	 */
	public void setFromId(final String id1) {
		this.fromId = id1;
	}
	
	/**
	 * @param id2
	 */
	public void setToId(final String id2) {
		this.toId = id2;
	}
	
	/**
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
		StringBuilder builder = new StringBuilder();
		builder.append("MapScore [totalConfidence=");
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
