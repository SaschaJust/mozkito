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

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.moskito.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Embeddable
public class MappingEngineFeature implements Annotated {
	
	private static final long                                  serialVersionUID = 4097360257338824107L;
	private double                                             confidence;
	private String                                             fromFieldName;
	private String                                             toFieldName;
	private String                                             toSubstring;
	private String                                             fromSubstring;
	private String                                             fqClassName;
	private static Map<String, Class<? extends MappingEngine>> cache            = new HashMap<String, Class<? extends MappingEngine>>();
	
	/**
	 * used by persistence provider only
	 */
	public MappingEngineFeature() {
	}
	
	/**
	 * @param confidence
	 * @param fromFieldName
	 * @param fromSubstring
	 * @param mappingEngine
	 */
	public MappingEngineFeature(final double confidence, final String fromFieldName, final String fromSubstring,
	        final String toFieldName, final String toSubstring, final Class<? extends MappingEngine> mappingEngine) {
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
				final Class<MappingEngine> engineClass = (Class<MappingEngine>) Class.forName(this.fqClassName);
				cache.put(this.fqClassName, engineClass);
				return engineClass;
			}
		} catch (final ClassNotFoundException e) {
			
			if (Logger.logError()) {
				Logger.error("Cannot find MappingEngine: " + this.fqClassName, e);
			}
			return null;
		} catch (final ClassCastException e) {
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
		return this.toFieldName;
	}
	
	/**
	 * @return the reportSubstring
	 */
	public String getReportSubstring() {
		return this.toSubstring;
	}
	
	/**
	 * @return the transactionFieldName
	 */
	public String getTransactionFieldName() {
		return this.fromFieldName;
	}
	
	/**
	 * @return the transactionSubstring
	 */
	public String getTransactionSubstring() {
		return this.fromSubstring;
	}
	
	/**
	 * @param confidence
	 *            the confidence to set
	 */
	public void setConfidence(final double confidence) {
		this.confidence = confidence;
	}
	
	/**
	 * @param fqClassName
	 *            the fqClassName to set
	 */
	public void setFqClassName(final String fqClassName) {
		this.fqClassName = fqClassName;
	}
	
	/**
	 * @param transactionFieldName
	 *            the transactionFieldName to set
	 */
	public void setFromFieldName(final String transactionFieldName) {
		this.fromFieldName = transactionFieldName;
	}
	
	/**
	 * @param transactionSubstring
	 *            the transactionSubstring to set
	 */
	public void setFromSubstring(final String transactionSubstring) {
		this.fromSubstring = transactionSubstring;
	}
	
	/**
	 * @param reportFieldName
	 *            the reportFieldName to set
	 */
	public void setToFieldName(final String reportFieldName) {
		this.toFieldName = reportFieldName;
	}
	
	/**
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
