/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
@Embeddable
public class MappingEngineFeature implements Annotated {
	
	private static final long                                  serialVersionUID = 4097360257338824107L;
	double                                                     confidence;
	String                                                     transactionFieldName;
	String                                                     reportFieldName;
	String                                                     reportSubstring;
	String                                                     transactionSubstring;
	String                                                     fqClassName;
	private static Map<String, Class<? extends MappingEngine>> cache            = new HashMap<String, Class<? extends MappingEngine>>();
	
	/**
	 * 
	 */
	public MappingEngineFeature() {
	}
	
	/**
	 * @param confidence
	 * @param transactionFieldName
	 * @param transactionSubstring
	 * @param mappingEngine
	 */
	public MappingEngineFeature(final double confidence, final String transactionFieldName,
	        final String transactionSubstring, final String reportFieldName, final String reportSubstring,
	        final Class<? extends MappingEngine> mappingEngine) {
		setConfidence(confidence);
		setTransactionFieldName(transactionFieldName);
		setTransactionSubstring(transactionSubstring);
		setReportFieldName(reportFieldName);
		setReportSubstring(reportSubstring);
		setFqClassName(mappingEngine.getCanonicalName());
		
		if (!cache.containsKey(getFqClassName())) {
			cache.put(getFqClassName(), mappingEngine);
		}
	}
	
	/**
	 * @return the confidence
	 */
	public double getConfidence() {
		return this.confidence;
	}
	
	/**
	 * @return
	 */
	@Transient
	public Class<? extends MappingEngine> getEngine() {
		try {
			if (cache.containsKey(this.fqClassName)) {
				return cache.get(this.fqClassName);
			} else {
				@SuppressWarnings ("unchecked")
				Class<MappingEngine> engineClass = (Class<MappingEngine>) Class.forName(this.fqClassName);
				cache.put(this.fqClassName, engineClass);
				return engineClass;
			}
		} catch (ClassNotFoundException e) {
			
			if (Logger.logError()) {
				Logger.error("Cannot find MappingEngine: " + this.fqClassName, e);
			}
			return null;
		} catch (ClassCastException e) {
			if (Logger.logError()) {
				Logger.error("Found corresponding class, but not of type MappingEngine: " + this.fqClassName, e);
			}
			return null;
		}
	}
	
	/**
	 * @return the fqClassName
	 */
	public String getFqClassName() {
		return this.fqClassName;
	}
	
	/**
	 * @return the reportFieldName
	 */
	public String getReportFieldName() {
		return this.reportFieldName;
	}
	
	/**
	 * @return the reportSubstring
	 */
	public String getReportSubstring() {
		return this.reportSubstring;
	}
	
	/**
	 * @return the transactionFieldName
	 */
	public String getTransactionFieldName() {
		return this.transactionFieldName;
	}
	
	/**
	 * @return the transactionSubstring
	 */
	public String getTransactionSubstring() {
		return this.transactionSubstring;
	}
	
	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(final double confidence) {
		this.confidence = confidence;
	}
	
	/**
	 * @param fqClassName the fqClassName to set
	 */
	public void setFqClassName(final String fqClassName) {
		this.fqClassName = fqClassName;
	}
	
	/**
	 * @param reportFieldName the reportFieldName to set
	 */
	public void setReportFieldName(final String reportFieldName) {
		this.reportFieldName = reportFieldName;
	}
	
	/**
	 * @param reportSubstring the reportSubstring to set
	 */
	public void setReportSubstring(final String reportSubstring) {
		this.reportSubstring = reportSubstring;
	}
	
	/**
	 * @param transactionFieldName the transactionFieldName to set
	 */
	public void setTransactionFieldName(final String transactionFieldName) {
		this.transactionFieldName = transactionFieldName;
	}
	
	/**
	 * @param transactionSubstring the transactionSubstring to set
	 */
	public void setTransactionSubstring(final String transactionSubstring) {
		this.transactionSubstring = transactionSubstring;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappingEngineFeature [confidence=");
		builder.append(this.confidence);
		builder.append(", transactionFieldName=");
		builder.append(this.transactionFieldName);
		builder.append(", transactionSubstring=");
		builder.append(this.transactionSubstring);
		builder.append(", fqClassName=");
		builder.append(this.fqClassName);
		builder.append("]");
		return builder.toString();
	}
	
}
