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
package org.mozkito.mappings.model;

import java.util.Map;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;

import org.mozkito.mappings.mappable.model.MappableEntity;

/**
 * The Interface IComposite.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public interface IComposite {
	
	/**
	 * Adds the strategy.
	 * 
	 * @param strategyName
	 *            the strategy name
	 * @param valid
	 *            the valid
	 */
	void addStrategy(@NotNull @NotEmptyString final String strategyName,
	                 final Boolean valid);
	
	/**
	 * Gets the class1.
	 * 
	 * @return the class1
	 */
	String getClass1();
	
	/**
	 * Gets the class2.
	 * 
	 * @return the class2
	 */
	String getClass2();
	
	/**
	 * Gets the from.
	 * 
	 * @return the from
	 */
	public abstract MappableEntity getFrom();
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public abstract String getHandle();
	
	/**
	 * Gets the relation.
	 * 
	 * @return the relation
	 */
	public abstract Relation getRelation();
	
	/**
	 * Gets the strategies.
	 * 
	 * @return the strategies
	 */
	public abstract Map<String, Boolean> getStrategies();
	
	/**
	 * Gets the to.
	 * 
	 * @return the to
	 */
	public abstract MappableEntity getTo();
	
}
