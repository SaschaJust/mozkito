package de.unisaarland.cs.st.reposuite.ppa.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;

// TODO: Auto-generated Javadoc
/**
 * The Class ChangeOperations.
 * 
 * @author Kim Herzig <kim@cs.uni-saarland.de>
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
		defOperations.put(ChangeType.Added, new HashMap<JavaElement, JavaChangeOperation>());
		defOperations.put(ChangeType.Deleted, new HashMap<JavaElement, JavaChangeOperation>());
		defOperations.put(ChangeType.Modified, new HashMap<JavaElement, JavaChangeOperation>());
	}
	
	/**
	 * Adds the.
	 * 
	 * @param op
	 *            the op
	 * @return true, if successful
	 */
	public boolean add(JavaChangeOperation op) {
		
		// check if a definition was added and deleted
		JavaElement element = op.getChangedElementLocation().getElement();
		if ((element instanceof JavaClassDefinition) || (element instanceof JavaMethodDefinition)) {
			if (op.getChangeType().equals(ChangeType.Added)) {
				if (defOperations.get(ChangeType.Deleted).containsKey(op.getChangedElementLocation().getElement())) {
					defOperations.get(ChangeType.Deleted).remove(op.getChangedElementLocation().getElement());
					op = new JavaChangeOperation(ChangeType.Modified, op.getChangedElementLocation(), op.getRevision());
				}
			} else if (op.getChangeType().equals(ChangeType.Deleted)) {
				if (defOperations.get(ChangeType.Added).containsKey(op.getChangedElementLocation().getElement())) {
					defOperations.get(ChangeType.Added).remove(op.getChangedElementLocation().getElement());
					op = new JavaChangeOperation(ChangeType.Modified, op.getChangedElementLocation(), op.getRevision());
				}
			}
			defOperations.get(op.getChangeType()).put(op.getChangedElementLocation().getElement(), op);
			return true;
		} else {
			return callOperations.add(op);
		}
		
	}
	
	/**
	 * Gets the operations.
	 * 
	 * @return the operations
	 */
	public Collection<JavaChangeOperation> getOperations() {
		Collection<JavaChangeOperation> result = new HashSet<JavaChangeOperation>();
		for (Map<JavaElement, JavaChangeOperation> map : defOperations.values()) {
			for (JavaChangeOperation op : map.values()) {
				result.add(op);
			}
		}
		for (JavaChangeOperation op : callOperations) {
			result.add(op);
		}
		return result;
	}
	
}
