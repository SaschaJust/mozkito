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

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.mozkito.mappings.engines.MappingEngine;
import org.mozkito.persistence.Annotated;

import net.ownhero.dev.andama.exceptions.ClassLoadingError;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class MappingEngineFeature.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Embeddable
public class Feature implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long                                        serialVersionUID = 4097360257338824107L;
	
	/** The confidence. */
	private double                                                   confidence;
	
	/** The from field name. */
	private String                                                   fromFieldName;
	
	/** The to field name. */
	private String                                                   toFieldName;
	
	/** The to substring. */
	private String                                                   toSubstring;
	
	/** The from substring. */
	private String                                                   fromSubstring;
	
	/** The fq class name. */
	private String                                                   fqClassName;
	
	/** The Constant cache. */
	private static final Map<String, Class<? extends MappingEngine>> cache            = new HashMap<String, Class<? extends MappingEngine>>();
	
	/**
	 * used by persistence provider only.
	 */
	Feature() {
	}
	
	/**
	 * Instantiates a new mapping engine feature.
	 * 
	 * @param confidence
	 *            the confidence
	 * @param fromFieldName
	 *            the from field name
	 * @param fromSubstring
	 *            the from substring
	 * @param toFieldName
	 *            the to field name
	 * @param toSubstring
	 *            the to substring
	 * @param mappingEngine
	 *            the mapping engine
	 */
	Feature(final double confidence, final String fromFieldName, final String fromSubstring, final String toFieldName,
	        final String toSubstring, final Class<? extends MappingEngine> mappingEngine) {
		setConfidence(confidence);
		setFromFieldName(fromFieldName);
		setFromSubstring(fromSubstring);
		setToFieldName(toFieldName);
		setToSubstring(toSubstring);
		setFqClassName(mappingEngine.getSimpleName());
		
		if (!cache.containsKey(getFqClassName())) {
			cache.put(getFqClassName(), mappingEngine);
		}
	}
	
	/**
	 * Gets the confidence.
	 * 
	 * @return the confidence
	 */
	public double getConfidence() {
		return this.confidence;
	}
	
	/**
	 * Gets the engine.
	 * 
	 * @return the engine
	 */
	@Transient
	public Class<? extends MappingEngine> getEngine() {
		try {
			if (cache.containsKey(getFqClassName())) {
				return cache.get(getFqClassName());
			} else {
				@SuppressWarnings ("unchecked")
				final Class<MappingEngine> engineClass = (Class<MappingEngine>) Class.forName(getFqClassName());
				cache.put(getFqClassName(), engineClass);
				return engineClass;
			}
		} catch (final ClassNotFoundException e) {
			
			if (Logger.logError()) {
				Logger.error(e, "Cannot find MappingEngine '%s'.", getFqClassName());
			}
			throw new ClassLoadingError(e, getFqClassName());
		} catch (final ClassCastException e) {
			if (Logger.logError()) {
				Logger.error(e, "Found corresponding class, but not of type MappingEngine '%s'.", getFqClassName());
			}
			return null;
		}
	}
	
	/**
	 * Gets the fq class name.
	 * 
	 * @return the fqClassName
	 */
	public String getFqClassName() {
		return this.fqClassName;
	}
	
	/**
	 * Gets the report field name.
	 * 
	 * @return the reportFieldName
	 */
	public String getReportFieldName() {
		return this.toFieldName;
	}
	
	/**
	 * Gets the report substring.
	 * 
	 * @return the reportSubstring
	 */
	public String getReportSubstring() {
		return this.toSubstring;
	}
	
	/**
	 * Gets the transaction field name.
	 * 
	 * @return the transactionFieldName
	 */
	public String getTransactionFieldName() {
		return this.fromFieldName;
	}
	
	/**
	 * Gets the transaction substring.
	 * 
	 * @return the transactionSubstring
	 */
	public String getTransactionSubstring() {
		return this.fromSubstring;
	}
	
	/**
	 * Sets the confidence.
	 * 
	 * @param confidence
	 *            the confidence to set
	 */
	public void setConfidence(final double confidence) {
		this.confidence = confidence;
	}
	
	/**
	 * Sets the fq class name.
	 * 
	 * @param fqClassName
	 *            the fqClassName to set
	 */
	public void setFqClassName(final String fqClassName) {
		this.fqClassName = fqClassName;
	}
	
	/**
	 * Sets the from field name.
	 * 
	 * @param transactionFieldName
	 *            the transactionFieldName to set
	 */
	public void setFromFieldName(final String transactionFieldName) {
		this.fromFieldName = transactionFieldName;
	}
	
	/**
	 * Sets the from substring.
	 * 
	 * @param transactionSubstring
	 *            the transactionSubstring to set
	 */
	public void setFromSubstring(final String transactionSubstring) {
		this.fromSubstring = transactionSubstring;
	}
	
	/**
	 * Sets the to field name.
	 * 
	 * @param reportFieldName
	 *            the reportFieldName to set
	 */
	public void setToFieldName(final String reportFieldName) {
		this.toFieldName = reportFieldName;
	}
	
	/**
	 * Sets the to substring.
	 * 
	 * @param reportSubstring
	 *            the reportSubstring to set
	 */
	public void setToSubstring(final String reportSubstring) {
		this.toSubstring = reportSubstring;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("MappingEngineFeature [confidence=");
		builder.append(this.confidence);
		builder.append(", fromFieldName=");
		builder.append(this.fromFieldName);
		builder.append(", toFieldName=");
		builder.append(this.toFieldName);
		builder.append(", toSubstring=");
		builder.append(this.toSubstring);
		builder.append(", fromSubstring=");
		builder.append(this.fromSubstring);
		builder.append(", fqClassName=");
		builder.append(this.fqClassName);
		builder.append("]");
		return builder.toString();
	}
	
}
