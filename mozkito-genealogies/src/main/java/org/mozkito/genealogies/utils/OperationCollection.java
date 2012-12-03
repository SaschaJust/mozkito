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
 *******************************************************************************/

package org.mozkito.genealogies.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.mozkito.codeanalysis.model.JavaChangeOperation;

/**
 * The Class OperationCollection.
 */
public class OperationCollection {
	
	/** The collection. */
	private final List<JavaChangeOperation> collection = new LinkedList<JavaChangeOperation>();
	
	/**
	 * Instantiates a new operation collection.
	 * 
	 * @param collection
	 *            the collection
	 */
	public OperationCollection(final Collection<JavaChangeOperation> collection) {
		this.collection.addAll(collection);
	}
	
	/**
	 * Unpack.
	 * 
	 * @return the collection
	 */
	public Collection<JavaChangeOperation> unpack() {
		return this.collection;
	}
	
}
