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
package de.unisaarland.cs.st.reposuite.persistence.model;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;

@Embeddable
public class StringTuple implements Annotated {
	
	private String            oldValue;
	private String            newValue;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -268313983915960230L;
	
	/**
	 * 
	 */
	protected StringTuple() {
		
	}
	
	/**
	 * @param oldValue
	 * @param newValue
	 */
	@NoneNull
	public StringTuple(final String oldValue, final String newValue) {
		setOldValue(oldValue);
		setNewValue(newValue);
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
		if (!(obj instanceof StringTuple)) {
			return false;
		}
		StringTuple other = (StringTuple) obj;
		if (getNewValue() == null) {
			if (other.getNewValue() != null) {
				return false;
			}
		} else if (!getNewValue().equals(other.getNewValue())) {
			return false;
		}
		if (getOldValue() == null) {
			if (other.getOldValue() != null) {
				return false;
			}
		} else if (!getOldValue().equals(other.getOldValue())) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the newValue
	 */
	@Basic
	@Lob
	public String getNewValue() {
		return this.newValue;
	}
	
	/**
	 * @return the oldValue
	 */
	@Basic
	@Lob
	public String getOldValue() {
		return this.oldValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getNewValue() == null)
		                                                  ? 0
		                                                  : getNewValue().hashCode());
		result = prime * result + ((getOldValue() == null)
		                                                  ? 0
		                                                  : getOldValue().hashCode());
		return result;
	}
	
	/**
	 * @param newValue
	 *            the newValue to set
	 */
	public void setNewValue(final String newValue) {
		this.newValue = newValue;
	}
	
	/**
	 * @param oldValue
	 *            the oldValue to set
	 */
	public void setOldValue(final String oldValue) {
		this.oldValue = oldValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StringTuple [old=");
		builder.append(getOldValue());
		builder.append(", new=");
		builder.append(getNewValue());
		builder.append("]");
		return builder.toString();
	}
	
}
