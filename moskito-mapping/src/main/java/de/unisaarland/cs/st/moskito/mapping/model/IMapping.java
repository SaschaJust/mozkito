/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.mapping.model;

import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public interface IMapping extends Comparable<IMapping> {
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public abstract int compareTo(final IMapping arg0);
	
	/**
	 * @return
	 */
	public abstract String getClass1();
	
	/**
	 * @return
	 */
	public abstract String getClass2();
	
	/**
	 * @return
	 */
	@ManyToOne (fetch = FetchType.EAGER)
	public abstract MappableEntity getElement1();
	
	/**
	 * @return
	 */
	@ManyToOne (fetch = FetchType.EAGER)
	public abstract MappableEntity getElement2();
	
	/**
	 * @return
	 */
	@Id
	public abstract String getFromId();
	
	/**
	 * @return
	 */
	@Id
	public abstract String getToId();
	
	/**
	 * @return the totalConfidence
	 */
	@Basic
	public abstract double getTotalConfidence();
	
}
