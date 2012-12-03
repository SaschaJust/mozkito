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

package org.mozkito.genealogies;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.codeanalysis.model.JavaMethodCall;
import org.mozkito.codeanalysis.model.JavaMethodDefinition;

/**
 * The Class JavaChangeOperationProcessQueue.
 */
public class JavaChangeOperationProcessQueue implements Iterator<JavaChangeOperation> {
	
	/**
	 * The Enum IteratorMode.
	 */
	protected static enum IteratorMode {
		
		/** The dd. */
		DD,
		/** The md. */
		MD,
		/** The ad. */
		AD,
		/** The dc. */
		DC,
		/** The mc. */
		MC,
		/** The ac. */
		AC;
	}
	
	/** The deleted definitions. */
	private final List<JavaChangeOperation> deletedDefinitions  = new LinkedList<JavaChangeOperation>();
	
	/** The modified definitions. */
	private final List<JavaChangeOperation> modifiedDefinitions = new LinkedList<JavaChangeOperation>();
	
	/** The added definitions. */
	private final List<JavaChangeOperation> addedDefinitions    = new LinkedList<JavaChangeOperation>();
	
	/** The deleted calls. */
	private final List<JavaChangeOperation> deletedCalls        = new LinkedList<JavaChangeOperation>();
	
	/** The modified calls. */
	private final List<JavaChangeOperation> modifiedCalls       = new LinkedList<JavaChangeOperation>();
	
	/** The added calls. */
	private final List<JavaChangeOperation> addedCalls          = new LinkedList<JavaChangeOperation>();
	
	/** The iterator. */
	private Iterator<JavaChangeOperation>   iterator            = null;
	
	/** The iterator mode. */
	private IteratorMode                    iteratorMode        = IteratorMode.DD;
	
	/**
	 * Instantiates a new java change operation process queue.
	 */
	public JavaChangeOperationProcessQueue() {
		
	}
	
	/**
	 * Adds the.
	 * 
	 * @param operation
	 *            the operation
	 * @return true, if successful
	 */
	@NoneNull
	public boolean add(final JavaChangeOperation operation) {
		if (operation.getChangedElementLocation() == null) {
			if (Logger.logDebug()) {
				Logger.debug("Skipping JavaChangeOperation: " + operation.getId() + ". ChangedElementLocation == null.");
			}
			return false;
		}
		if (operation.getChangedElementLocation().getElement() == null) {
			if (Logger.logDebug()) {
				Logger.debug("Skipping JavaChangeOperation: " + operation.getId() + " with location "
				        + operation.getChangedElementLocation().getId() + ". ChangedElement == null.");
			}
			return false;
		}
		final JavaElement element = operation.getChangedElementLocation().getElement();
		if (element instanceof JavaMethodDefinition) {
			switch (operation.getChangeType()) {
				case Added:
					this.addedDefinitions.add(operation);
					return true;
				case Modified:
				case Renamed:
					this.modifiedDefinitions.add(operation);
					return true;
				case Deleted:
					this.deletedDefinitions.add(operation);
					return true;
				default:
					return false;
			}
		} else if (element instanceof JavaMethodCall) {
			switch (operation.getChangeType()) {
				case Added:
					this.addedCalls.add(operation);
					return true;
				case Modified:
				case Renamed:
					this.modifiedCalls.add(operation);
					return true;
				case Deleted:
					this.deletedCalls.add(operation);
					return true;
				default:
					return false;
			}
		} else {
			if (Logger.logDebug()) {
				Logger.debug("Cannot handle JavaElements that are neither JavaMethodDefinitions nor JavaMethodCalls. Got: "
				        + element.getClass().getCanonicalName());
			}
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (this.iterator == null) {
			this.iterator = this.deletedDefinitions.iterator();
			this.iteratorMode = IteratorMode.DD;
		}
		
		if (this.iterator.hasNext()) {
			return true;
		}
		switch (this.iteratorMode) {
			case DD:
				this.iterator = this.modifiedDefinitions.iterator();
				this.iteratorMode = IteratorMode.MD;
				break;
			case MD:
				this.iterator = this.addedDefinitions.iterator();
				this.iteratorMode = IteratorMode.AD;
				break;
			case AD:
				this.iterator = this.deletedCalls.iterator();
				this.iteratorMode = IteratorMode.DC;
				break;
			case DC:
				this.iterator = this.modifiedCalls.iterator();
				this.iteratorMode = IteratorMode.MC;
				break;
			case MC:
				this.iterator = this.addedCalls.iterator();
				this.iteratorMode = IteratorMode.AC;
				break;
			case AC:
				return false;
		}
		return hasNext();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public JavaChangeOperation next() {
		return this.iterator.next();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		this.iterator.remove();
	}
	
}
