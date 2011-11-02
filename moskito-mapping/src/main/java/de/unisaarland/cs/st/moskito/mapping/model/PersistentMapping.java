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

import java.util.LinkedList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.strategies.MappingStrategy;
import de.unisaarland.cs.st.moskito.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
public class PersistentMapping implements Annotated {
	
	private static final long serialVersionUID = -6423537467677757941L;
	
	private MapScore          score;
	private List<String>      strategies       = new LinkedList<String>();
	private Boolean           valid            = null;
	
	/**
	 * used by persistence provider only
	 */
	public PersistentMapping() {
	}
	
	/**
	 * @param score
	 */
	public PersistentMapping(final MapScore score) {
		setScore(score);
	}
	
	/**
	 * @param strategy
	 */
	@Transient
	public void addStrategy(final MappingStrategy strategy) {
		getStrategies().add(strategy.getHandle());
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
		if (!(obj instanceof PersistentMapping)) {
			return false;
		}
		PersistentMapping other = (PersistentMapping) obj;
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
	 * @return
	 */
	@Transient
	public MappableEntity getElement1() {
		return getScore().getElement1();
	}
	
	/**
	 * @return
	 */
	@Transient
	public MappableEntity getElement2() {
		return getScore().getElement2();
	}
	
	/**
	 * @return the score
	 */
	@Id
	@ManyToOne (fetch = FetchType.LAZY, cascade = {}, optional = false)
	@JoinColumns ({ @JoinColumn (nullable = false, name = "fromId", referencedColumnName = "fromId"),
	        @JoinColumn (nullable = false, name = "toId", referencedColumnName = "toId") })
	public MapScore getScore() {
		return this.score;
	}
	
	/**
	 * @return the strategies
	 */
	@ElementCollection
	public List<String> getStrategies() {
		return this.strategies;
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
		result = (prime * result) + ((getScore() == null)
		                                                 ? 0
		                                                 : getScore().hashCode());
		return result;
	}
	
	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(final MapScore score) {
		this.score = score;
	}
	
	/**
	 * @param strategies
	 *            the strategies to set
	 */
	public void setStrategies(final List<String> strategies) {
		this.strategies = strategies;
	}
	
	/**
	 * @param valid
	 *            the valid to set
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
		builder.append("PersistentMapping [from=");
		builder.append(getElement1());
		builder.append(", to=");
		builder.append(getElement2());
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
