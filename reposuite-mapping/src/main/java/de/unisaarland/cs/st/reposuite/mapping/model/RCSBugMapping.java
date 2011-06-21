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

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.elements.MapId;
import de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import net.ownhero.dev.ioda.JavaUtils;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
@Entity
@IdClass (MapId.class)
public class RCSBugMapping implements Annotated {
	
	private static final long     serialVersionUID = -6423537467677757941L;
	
	private MapScore              score;
	private List<MappingStrategy> strategies       = new LinkedList<MappingStrategy>();
	private Boolean               valid            = null;
	
	/**
	 * @param score
	 */
	public RCSBugMapping(final MapScore score) {
		setScore(score);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RCSBugMapping)) {
			return false;
		}
		RCSBugMapping other = (RCSBugMapping) obj;
		if (getScore() == null) {
			if (other.getScore() != null) {
				return false;
			}
		} else if (!getScore().equals(other.getScore())) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the report
	 */
	@Transient
	public Report getReport() {
		return getScore().getReport();
	}
	
	/**
	 * @return the score
	 */
	@Id
	@ManyToOne (fetch = FetchType.LAZY, cascade = {}, optional = false)
	@JoinColumns ({ @JoinColumn (nullable = false, name = "reportid", referencedColumnName = "reportid"),
	        @JoinColumn (nullable = false, name = "transactionid", referencedColumnName = "transactionid") })
	public MapScore getScore() {
		return this.score;
	}
	
	/**
	 * @return the strategies
	 */
	public List<MappingStrategy> getStrategies() {
		return this.strategies;
	}
	
	/**
	 * @return the transaction
	 */
	@Transient
	public RCSTransaction getTransaction() {
		return getScore().getTransaction();
	}
	
	/**
	 * @return the valid
	 */
	public Boolean getValid() {
		return this.valid;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getScore() == null)
		                                               ? 0
		                                               : getScore().hashCode());
		return result;
	}
	
	/**
	 * @param score the score to set
	 */
	public void setScore(final MapScore score) {
		this.score = score;
	}
	
	/**
	 * @param strategies the strategies to set
	 */
	public void setStrategies(final List<MappingStrategy> strategies) {
		this.strategies = strategies;
	}
	
	/**
	 * @param valid the valid to set
	 */
	public void setValid(final Boolean valid) {
		this.valid = valid;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RCSBugMapping [report=");
		builder.append(getReport().getId());
		builder.append(", transaction=");
		builder.append(getTransaction().getId());
		builder.append(", score=");
		builder.append(getScore());
		builder.append(", strategies=");
		builder.append(JavaUtils.collectionToString(getStrategies()));
		builder.append(", valid=");
		builder.append(getValid());
		builder.append("]");
		return builder.toString();
	}
}
