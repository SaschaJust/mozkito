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
package org.mozkito.codechanges.lightweightparser.structure;

/**
 * The Class Parameter.
 */
public class Parameter {
	
	/** The name. */
	private final String name;
	
	/** The type. */
	private final String type;
	
	/**
	 * Instantiates a new parameter.
	 * 
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 */
	public Parameter(final String name, final String type) {
		super();
		this.name = name;
		this.type = type;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.type + " " + this.name;
	}
	
}
