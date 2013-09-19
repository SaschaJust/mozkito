/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.mappings.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.mozkito.issues.model.Report;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class BugFixCandidate.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
@Access (AccessType.PROPERTY)
@DiscriminatorValue ("BUGFIX")
public class BugFixCandidate extends Candidate<Report, ChangeSet> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1988830209799551449L;
	
	/** The report. */
	private Report            report;
	
	/** The change set. */
	private ChangeSet         changeSet;
	
	/**
	 * Instantiates a new bug fix candidate.
	 * 
	 * @param report
	 *            the report
	 * @param changeSet
	 *            the change set
	 */
	public BugFixCandidate(final Report report, final ChangeSet changeSet) {
		super(Type.FIX);
		this.report = report;
		this.changeSet = changeSet;
	}
	
	/**
	 * Gets the change set.
	 * 
	 * @return the changeSet
	 */
	@ManyToOne (cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	public ChangeSet getChangeSet() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.changeSet;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.model.Candidate#getFrom()
	 */
	@Override
	@Transient
	public Report getFrom() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getReport();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the report.
	 * 
	 * @return the report
	 */
	@ManyToOne (cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	public Report getReport() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.report;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.model.Candidate#getTo()
	 */
	@Override
	@Transient
	public ChangeSet getTo() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getChangeSet();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the change set.
	 * 
	 * @param changeSet
	 *            the changeSet to set
	 */
	public void setChangeSet(final ChangeSet changeSet) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.changeSet = changeSet;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the report.
	 * 
	 * @param report
	 *            the report to set
	 */
	public void setReport(final Report report) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.report = report;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
