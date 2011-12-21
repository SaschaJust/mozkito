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
	
	public void add(JavaChangeOperation operation) {
		JavaElement element = operation.getChangedElementLocation().getElement();
		if (element instanceof JavaMethodDefinition) {
			switch (operation.getChangeType()) {
				case Added:
					addedDefinitions.add(operation);
					break;
				case Modified:
				case Renamed:
					modifiedDefinitions.add(operation);
					break;
				case Deleted:
					deletedDefinitions.add(operation);
					break;
			}
		} else if (element instanceof JavaMethodCall) {
			switch (operation.getChangeType()) {
				case Added:
					addedCalls.add(operation);
					break;
				case Modified:
				case Renamed:
					modifiedCalls.add(operation);
					break;
				case Deleted:
					deletedCalls.add(operation);
					break;
			}
		} else {
			if (Logger.logDebug()) {
				Logger.debug("Cannot hadle JavaElements that are neither JavaMethodDefinitions nor JavaMethodCalls. Got: "
						+ element.getClass().getCanonicalName());
			}
		}
	}
	
	public void clear() {
		deletedDefinitions.clear();
		modifiedDefinitions.clear();
		addedDefinitions.clear();
		deletedCalls.clear();
		modifiedCalls.clear();
		addedCalls.clear();
		iteratorMode = IteratorMode.DD;
		iterator = null;
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
