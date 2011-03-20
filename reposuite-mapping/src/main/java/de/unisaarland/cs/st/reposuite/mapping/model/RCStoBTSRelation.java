/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
@Entity
public class RCStoBTSRelation implements Annotated {
	
	private static final long                 serialVersionUID = 4915635311512829521L;
	private double                            confidence;
	private RCSTransaction                    transaction;
	private Report                            report;
	Map<MappingEngine, Tuple<String, Double>> reasons          = new HashMap<MappingEngine, Tuple<String, Double>>();
	
	/**
	 * @return
	 */
	@Transient
	public DateTime getCloseTime() {
		return this.getReport().getResolutionTimestamp();
	}
	
	/**
	 * @return the confidence
	 */
	@Basic
	public double getConfidence() {
		return this.confidence;
	}
	
	/**
	 * @return
	 */
	@Transient
	public DateTime getFixTime() {
		return this.getTransaction().getTimestamp();
	}
	
	/**
	 * @return the report
	 */
	public Report getReport() {
		return this.report;
	}
	
	/**
	 * @return the transaction
	 */
	public RCSTransaction getTransaction() {
		return this.transaction;
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
	 * @param report the report to set
	 */
	public void setReport(final Report report) {
		this.report = report;
	}
	
	/**
	 * @param transaction the transaction to set
	 */
	public void setTransaction(final RCSTransaction transaction) {
		this.transaction = transaction;
	}
}
