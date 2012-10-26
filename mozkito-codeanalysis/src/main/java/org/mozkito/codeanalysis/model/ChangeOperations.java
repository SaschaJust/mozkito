/*******************************************************************************
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
 ******************************************************************************/
package org.mozkito.codeanalysis.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mozkito.versions.elements.ChangeType;


/**
 * The Class ChangeOperations.
 * 
 * @author Kim Herzig <kim@mozkito.org>
 */
public class ChangeOperations {
	
	/** The def operations. */
	private final Map<ChangeType, Map<JavaElement, JavaChangeOperation>> defOperations  = new HashMap<ChangeType, Map<JavaElement, JavaChangeOperation>>();
	
	/** The call operations. */
	private final Set<JavaChangeOperation>                               callOperations = new HashSet<JavaChangeOperation>();
	
	/**
	 * Instantiates a new change operations.
	 */
	public ChangeOperations() {
		this.defOperations.put(ChangeType.Added, new HashMap<JavaElement, JavaChangeOperation>());
		this.defOperations.put(ChangeType.Deleted, new HashMap<JavaElement, JavaChangeOperation>());
		this.defOperations.put(ChangeType.Modified, new HashMap<JavaElement, JavaChangeOperation>());
	}
	
	/**
	 * Adds the.
	 * 
	 * @param op
	 *            the op
	 * @return true, if successful
	 */
	public boolean add(final JavaChangeOperation _op) {
		
		JavaChangeOperation op = _op;
		// check if a definition was added and deleted
		final JavaElement element = op.getChangedElementLocation().getElement();
		if ((element instanceof JavaTypeDefinition) || (element instanceof JavaMethodDefinition)) {
			if (op.getChangeType().equals(ChangeType.Added)) {
				if (this.defOperations.get(ChangeType.Deleted).containsKey(op.getChangedElementLocation().getElement())) {
					this.defOperations.get(ChangeType.Deleted).remove(op.getChangedElementLocation().getElement());
					op = new JavaChangeOperation(ChangeType.Modified, op.getChangedElementLocation(), op.getRevision());
				}
			} else if (op.getChangeType().equals(ChangeType.Deleted)) {
				if (this.defOperations.get(ChangeType.Added).containsKey(op.getChangedElementLocation().getElement())) {
					this.defOperations.get(ChangeType.Added).remove(op.getChangedElementLocation().getElement());
					op = new JavaChangeOperation(ChangeType.Modified, op.getChangedElementLocation(), op.getRevision());
				}
			}
			this.defOperations.get(op.getChangeType()).put(op.getChangedElementLocation().getElement(), op);
			return true;
		}
		return this.callOperations.add(op);
	}
	
	/**
	 * Gets the operations.
	 * 
	 * @return the operations
	 */
	public Collection<JavaChangeOperation> getOperations() {
		final Collection<JavaChangeOperation> result = new HashSet<JavaChangeOperation>();
		for (final Map<JavaElement, JavaChangeOperation> map : this.defOperations.values()) {
			for (final JavaChangeOperation op : map.values()) {
				result.add(op);
			}
		}
		for (final JavaChangeOperation op : this.callOperations) {
			result.add(op);
		}
		return result;
	}
	
}
