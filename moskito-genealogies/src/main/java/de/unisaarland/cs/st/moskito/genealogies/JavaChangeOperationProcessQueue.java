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
	
	private static enum IteratorMode {
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
			if (Logger.logWarn()) {
				Logger.warn("Cannot hadle JavaElements that are neither JavaMethodDefinitions nor JavaMethodCalls. Got: "
						+ element.getClass().getCanonicalName());
			}
		}
	}
	
	@Override
	public boolean hasNext() {
		if (iterator == null) {
			iterator = deletedDefinitions.iterator();
			return hasNext();
		}
		
		if (iterator.hasNext()) {
			return true;
		} else {
			switch (iteratorMode) {
				case DD:
					iterator = modifiedDefinitions.iterator();
					break;
				case MD:
					iterator = addedDefinitions.iterator();
					break;
				case AD:
					iterator = deletedCalls.iterator();
					break;
				case DC:
					iterator = modifiedCalls.iterator();
					break;
				case MC:
					iterator = addedCalls.iterator();
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
