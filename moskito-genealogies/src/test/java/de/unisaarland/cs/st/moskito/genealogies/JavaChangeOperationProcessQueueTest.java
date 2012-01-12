package de.unisaarland.cs.st.moskito.genealogies;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public class JavaChangeOperationProcessQueueTest extends TestEnvironment {
	
	@Test
	public void test() {
		setup();
		JavaChangeOperationProcessQueue queue = new JavaChangeOperationProcessQueue();
		
		for (JavaChangeOperation op : environmentOperations.values()) {
			queue.add(op);
		}
		
		int counter = 0;
		
		
		Set<JavaChangeOperation> deletedDefinitions = new HashSet<JavaChangeOperation>();
		deletedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T3F1D));
		deletedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T9F1));
		deletedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T6F2));
		
		Set<JavaChangeOperation> modifiedDefinitions = new HashSet<JavaChangeOperation>();
		modifiedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T3F2M));
		modifiedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T8F2));
		
		Set<JavaChangeOperation> addedDefinitions = new HashSet<JavaChangeOperation>();
		addedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T1F1));
		addedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T1F2));
		addedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T3F1A));
		addedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T7F2));
		
		Set<JavaChangeOperation> deletedCalls = new HashSet<JavaChangeOperation>();
		deletedCalls.add(environmentOperations.get(TestEnvironmentOperation.T4F3D));
		deletedCalls.add(environmentOperations.get(TestEnvironmentOperation.T10F3));
		deletedCalls.add(environmentOperations.get(TestEnvironmentOperation.T10F4));
		
		Set<JavaChangeOperation> modifiedCalls = new HashSet<JavaChangeOperation>();
		
		Set<JavaChangeOperation> addedCalls = new HashSet<JavaChangeOperation>();
		addedCalls.add(environmentOperations.get(TestEnvironmentOperation.T2F3));
		addedCalls.add(environmentOperations.get(TestEnvironmentOperation.T3F2));
		addedCalls.add(environmentOperations.get(TestEnvironmentOperation.T4F3A));
		addedCalls.add(environmentOperations.get(TestEnvironmentOperation.T4F4));
		addedCalls.add(environmentOperations.get(TestEnvironmentOperation.T5F4));
		
		while (queue.hasNext()) {
			JavaChangeOperation next = queue.next();
			assert (next != null);
			assert (environmentOperations.values().contains(next));
			++counter;
			
			if (!deletedDefinitions.isEmpty()) {
				assert (!modifiedDefinitions.contains(next));
				assert (!addedDefinitions.contains(next));
				assert (!deletedCalls.contains(next));
				assert (!modifiedCalls.contains(next));
				assert (!addedCalls.contains(next));
				deletedDefinitions.remove(next);
			} else if (!modifiedDefinitions.isEmpty()) {
				assert (!addedDefinitions.contains(next));
				assert (!deletedCalls.contains(next));
				assert (!modifiedCalls.contains(next));
				assert (!addedCalls.contains(next));
				modifiedDefinitions.remove(next);
			} else if (!addedDefinitions.isEmpty()) {
				assert (!deletedCalls.contains(next));
				assert (!modifiedCalls.contains(next));
				assert (!addedCalls.contains(next));
				addedDefinitions.remove(next);
			} else if (!deletedCalls.isEmpty()) {
				assert (!modifiedCalls.contains(next));
				assert (!addedCalls.contains(next));
				deletedCalls.remove(next);
			} else if (!modifiedCalls.isEmpty()) {
				assert (!addedCalls.contains(next));
				modifiedCalls.remove(next);
			} else {
				addedCalls.remove(next);
			}
		}
		assertEquals(environmentOperations.values().size(), counter);
		assert (deletedDefinitions.isEmpty());
		assert (modifiedDefinitions.isEmpty());
		assert (addedDefinitions.isEmpty());
		assert (deletedCalls.isEmpty());
		assert (modifiedCalls.isEmpty());
		assert (addedCalls.isEmpty());
		changeGenealogy.close();
		
		queue = new JavaChangeOperationProcessQueue();
		for (JavaChangeOperation op : addedCalls) {
			queue.add(op);
		}
		counter = 0;
		while (queue.hasNext()) {
			JavaChangeOperation next = queue.next();
			assert (next != null);
			assert (addedCalls.contains(next));
			++counter;
		}
		assertEquals(addedCalls.size(), counter);
	}
	
}
