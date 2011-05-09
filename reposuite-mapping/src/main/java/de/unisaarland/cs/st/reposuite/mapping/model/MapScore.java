/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.model;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.MapId;
import de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import net.ownhero.dev.ioda.JavaUtils;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
@Entity
@IdClass (MapId.class)
public class MapScore implements Annotated, Comparable<MapScore> {
	
	private static final long  serialVersionUID = -8606759070008468513L;
	
	List<MappingEngineFeature> features         = new LinkedList<MappingEngineFeature>();
	Report                     report;
	double                     totalConfidence  = 0.0d;
	RCSTransaction             transaction;
	
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
	 * @param transactionFieldName
	 * @param relevantString
	 * @param mappingEngine
	 */
	@Transient
	public void addFeature(final double confidence,
	                       @NotNull @NotEmpty final String transactionFieldName,
	                       @NotNull @NotEmpty final String transactionSubstring,
	                       @NotNull @NotEmpty final String reportFieldName,
	                       @NotNull @NotEmpty final String reportSubstring,
	                       @NotNull final Class<? extends MappingEngine> mappingEngine) {
		this.totalConfidence += confidence;
		this.features.add(new MappingEngineFeature(confidence, transactionFieldName, transactionSubstring,
		                                           reportFieldName, reportSubstring, mappingEngine));
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final MapScore arg0) {
		return Double.compare(this.totalConfidence, arg0.totalConfidence);
	}
	
	/**
	 * @return the features
	 */
	@ElementCollection (fetch = FetchType.EAGER)
	public List<MappingEngineFeature> getFeatures() {
		return this.features;
	}
	
	/**
	 * @return the report
	 */
	@ManyToOne (fetch = FetchType.LAZY, cascade = {}, optional = false)
	@JoinColumn (nullable = false, name = "reportid")
	@Id
	public Report getReport() {
		return this.report;
	}
	
	/**
	 * @return a set of {@link MappingEngine} classes that were used for this scoring
	 */
	@Transient
	public Set<Class<? extends MappingEngine>> getScoringEngines() {
		HashSet<Class<? extends MappingEngine>> engines = new HashSet<Class<? extends MappingEngine>>();
		
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
	@Id
	@ManyToOne (fetch = FetchType.LAZY, cascade = {}, optional = false)
	@JoinColumn (nullable = false, name = "transactionid")
	public RCSTransaction getTransaction() {
		return this.transaction;
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
		builder.append(getTotalConfidence());
		builder.append(", transaction=");
		builder.append(getTransaction().getId());
		builder.append(", report=");
		builder.append(getReport().getId());
		builder.append(", features=");
		builder.append(JavaUtils.collectionToString(getFeatures()));
		builder.append("]");
		return builder.toString();
	}
	
}
