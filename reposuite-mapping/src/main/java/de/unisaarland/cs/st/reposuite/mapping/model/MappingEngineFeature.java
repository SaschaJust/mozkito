/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
@Embeddable
public class MappingEngineFeature implements Annotated {
	
	private static final long                        serialVersionUID = 4097360257338824107L;
	double                                           confidence;
	String                                           fieldName;
	String                                           substring;
	String                                           fqClassName;
	private static Map<String, Class<MappingEngine>> cache            = new HashMap<String, Class<MappingEngine>>();
	
	/**
	 * 
	 */
	@SuppressWarnings ("unused")
	private MappingEngineFeature() {
	}
	
	/**
	 * @param confidence
	 * @param fieldName
	 * @param substring
	 * @param mappingEngine
	 */
	public MappingEngineFeature(final double confidence, final String fieldName, final String substring,
	        final Class<MappingEngine> mappingEngine) {
		this.confidence = confidence;
		this.fieldName = fieldName;
		this.substring = substring;
		this.fqClassName = mappingEngine.getCanonicalName();
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
	public Class<MappingEngine> getEngine() {
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
	 * @return the fieldName
	 */
	public String getFieldName() {
		return this.fieldName;
	}
	
	/**
	 * @return the fqClassName
	 */
	public String getFqClassName() {
		return this.fqClassName;
	}
	
	/**
	 * @return the substring
	 */
	public String getSubstring() {
		return this.substring;
	}
	
	@Override
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(final double confidence) {
		this.confidence = confidence;
	}
	
	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(final String fieldName) {
		this.fieldName = fieldName;
	}
	
	/**
	 * @param fqClassName the fqClassName to set
	 */
	public void setFqClassName(final String fqClassName) {
		this.fqClassName = fqClassName;
	}
	
	/**
	 * @param substring the substring to set
	 */
	public void setSubstring(final String substring) {
		this.substring = substring;
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
		builder.append(", fieldName=");
		builder.append(this.fieldName);
		builder.append(", substring=");
		builder.append(this.substring);
		builder.append(", fqClassName=");
		builder.append(this.fqClassName);
		builder.append("]");
		return builder.toString();
	}
	
}
