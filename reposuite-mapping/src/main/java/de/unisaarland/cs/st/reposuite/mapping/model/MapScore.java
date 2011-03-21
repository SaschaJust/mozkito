/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
@Entity
public class MapScore implements Annotated {
	
	private static final long  serialVersionUID = -8606759070008468513L;
	
	RCSTransaction             transaction;
	
	Report                     report;
	
	double                     totalConfidence  = 0.0d;
	
	List<MappingEngineFeature> features         = new LinkedList<MappingEngineFeature>();
	
	/**
	 * @param transaction
	 * @param report
	 */
	public MapScore(final RCSTransaction transaction, final Report report) {
		this.transaction = transaction;
		this.report = report;
	}
	
	/**
	 * @param confidence
	 * @param fieldName
	 * @param relevantString
	 * @param mappingEngine
	 */
	@Transient
	public void addFeature(final double confidence,
	                       @NotNull @NotEmpty final String fieldName,
	                       @NotNull @NotEmpty final String relevantString,
	                       @NotNull final Class<MappingEngine> mappingEngine) {
		this.totalConfidence += confidence;
		this.features.add(new MappingEngineFeature(confidence, fieldName, relevantString, mappingEngine));
	}
	
	/**
	 * @return the features
	 */
	@ElementCollection
	// FIXME
	public List<MappingEngineFeature> getFeatures() {
		return this.features;
	}
	
	/**
	 * @return the report
	 */
	public Report getReport() {
		return this.report;
	}
	
	/**
	 * @return a set of {@link MappingEngine} classes that were used for this scoring
	 */
	@Transient
	public Set<Class<MappingEngine>> getScoringEngines() {
		HashSet<Class<MappingEngine>> engines = new HashSet<Class<MappingEngine>>();
		
		for (MappingEngineFeature feature : this.features) {
			engines.add(feature.getEngine());
		}
		
		return engines;
	}
	
	/**
	 * @return the totalConfidence
	 */
	@Basic
	public double getTotalConfidence() {
		return this.totalConfidence;
	}
	
	/**
	 * @return the transaction
	 */
	public RCSTransaction getTransaction() {
		return this.transaction;
	}
	
	/**
	 * @return
	 */
	@Override
	@Transient
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	/**
	 * @param features the features to set
	 */
	public void setFeatures(final List<MappingEngineFeature> features) {
		this.features = features;
	}
	
	/**
	 * @param report the report to set
	 */
	public void setReport(final Report report) {
		this.report = report;
	}
	
	/**
	 * @param totalConfidence the totalConfidence to set
	 */
	public void setTotalConfidence(final double totalConfidence) {
		this.totalConfidence = totalConfidence;
	}
	
	/**
	 * @param transaction the transaction to set
	 */
	public void setTransaction(final RCSTransaction transaction) {
		this.transaction = transaction;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MapScore [totalConfidence=");
		builder.append(this.totalConfidence);
		builder.append(", features=");
		builder.append(JavaUtils.collectionToString(this.features));
		builder.append("]");
		return builder.toString();
	}
	
}
