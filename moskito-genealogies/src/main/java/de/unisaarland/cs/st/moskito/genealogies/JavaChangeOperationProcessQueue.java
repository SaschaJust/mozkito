/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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


package de.unisaarland.cs.st.moskito.genealogies;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElement;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.moskito.ppa.model.JavaMethodDefinition;


public class JavaChangeOperationProcessQueue implements Iterator<JavaChangeOperation> {
	
	protected static enum IteratorMode {
		DD, MD, AD, DC, MC, AC;
	}
	
	private List<JavaChangeOperation> deletedDefinitions = new LinkedList<JavaChangeOperation>();
	private List<JavaChangeOperation> modifiedDefinitions = new LinkedList<JavaChangeOperation>();
	private List<JavaChangeOperation> addedDefinitions    = new LinkedList<JavaChangeOperation>();
	
	private List<JavaChangeOperation> deletedCalls        = new LinkedList<JavaChangeOperation>();
	private List<JavaChangeOperation> modifiedCalls       = new LinkedList<JavaChangeOperation>();
	private List<JavaChangeOperation> addedCalls          = new LinkedList<JavaChangeOperation>();
	
	private Iterator<JavaChangeOperation> iterator            = null;
	private IteratorMode                  iteratorMode        = IteratorMode.DD;
	
	public JavaChangeOperationProcessQueue() {
		
	}
	
	public boolean add(JavaChangeOperation operation) {
		JavaElement element = operation.getChangedElementLocation().getElement();
		if (element instanceof JavaMethodDefinition) {
			switch (operation.getChangeType()) {
				case Added:
					addedDefinitions.add(operation);
					return true;
				case Modified:
				case Renamed:
					modifiedDefinitions.add(operation);
					return true;
				case Deleted:
					deletedDefinitions.add(operation);
					return true;
			}
		} else if (element instanceof JavaMethodCall) {
			switch (operation.getChangeType()) {
				case Added:
					addedCalls.add(operation);
					return true;
				case Modified:
				case Renamed:
					modifiedCalls.add(operation);
					return true;
				case Deleted:
					deletedCalls.add(operation);
					return true;
			}
		} else {
			if (Logger.logDebug()) {
				Logger.debug("Cannot handle JavaElements that are neither JavaMethodDefinitions nor JavaMethodCalls. Got: "
						+ element.getClass().getCanonicalName());
			}
		}
		return false;
	}
	
	
	@Override
	public boolean hasNext() {
		if (iterator == null) {
			iterator = deletedDefinitions.iterator();
			iteratorMode = IteratorMode.DD;
		}
		
		if (iterator.hasNext()) {
			return true;
		} else {
			switch (iteratorMode) {
				case DD:
					iterator = modifiedDefinitions.iterator();
					iteratorMode = IteratorMode.MD;
					break;
				case MD:
					iterator = addedDefinitions.iterator();
					iteratorMode = IteratorMode.AD;
					break;
				case AD:
					iterator = deletedCalls.iterator();
					iteratorMode = IteratorMode.DC;
					break;
				case DC:
					iterator = modifiedCalls.iterator();
					iteratorMode = IteratorMode.MC;
					break;
				case MC:
					iterator = addedCalls.iterator();
					iteratorMode = IteratorMode.AC;
					break;
				case AC:
					return false;
			}
			return hasNext();
		}
	}
	
	@Override
	public JavaChangeOperation next() {
		return iterator.next();
	}
	
	@Override
	public void remove() {
		iterator.remove();
	}
	
}
