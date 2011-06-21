/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.mapping.elements;

public class MapId {
	
	private String transaction;
	private long   report;
	
	/**
	 * 
	 */
	public MapId() {
	}
	
	/**
	 * @param transaction
	 * @param report
	 */
	public MapId(final String transactionId, final long reportId) {
		setTransaction(transactionId);
		setReport(reportId);
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
		if (!(obj instanceof MapId)) {
			return false;
		}
		MapId other = (MapId) obj;
		if (getReport() != other.getReport()) {
			return false;
		}
		if (getTransaction() == null) {
			if (other.getTransaction() != null) {
				return false;
			}
		} else if (!getTransaction().equals(other.getTransaction())) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the report
	 */
	public long getReport() {
		return this.report;
	}
	
	/**
	 * @return the transaction
	 */
	public String getTransaction() {
		return this.transaction;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (getReport() ^ (getReport() >>> 32));
		result = prime * result + ((getTransaction() == null)
		                                                     ? 0
		                                                     : getTransaction().hashCode());
		return result;
	}
	
	/**
	 * @param report the report to set
	 */
	public void setReport(final long reportId) {
		this.report = reportId;
	}
	
	/**
	 * @param transaction the transaction to set
	 */
	public void setTransaction(final String transactionId) {
		this.transaction = transactionId;
	}
}
