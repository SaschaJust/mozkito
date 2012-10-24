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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.Test;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.genealogies.JavaChangeOperationProcessQueue;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.utils.ChangeGenealogyUtils;
import org.mozkito.genealogies.utils.GenealogyTestEnvironment;
import org.mozkito.genealogies.utils.GenealogyTestEnvironment.TestEnvironmentOperation;
import org.mozkito.persistence.ConnectOptions;
import org.mozkito.testing.MozkitoTest;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.versions.BranchFactory;


public class JavaChangeOperationProcessQueue_MozkitoTest extends MozkitoTest {
	
	@Test
	@DatabaseSettings (unit = "codeanalysis", database = "moskito_genealogies_test_environment", options = ConnectOptions.CREATE)
	public void test() {
		final File tmpGraphDBFile = FileUtils.createRandomDir(this.getClass().getSimpleName(), "",
		                                                      FileShutdownAction.DELETE);
		final BranchFactory branchFactory = new BranchFactory(getPersistenceUtil());
		
		final GenealogyTestEnvironment testEnvironment = ChangeGenealogyUtils.getGenealogyTestEnvironment(tmpGraphDBFile,
		                                                                                                  branchFactory);
		final CoreChangeGenealogy changeGenealogy = testEnvironment.getChangeGenealogy();
		final Map<TestEnvironmentOperation, JavaChangeOperation> environmentOperations = testEnvironment.getEnvironmentOperations();
		
		JavaChangeOperationProcessQueue queue = new JavaChangeOperationProcessQueue();
		
		for (final JavaChangeOperation op : environmentOperations.values()) {
			queue.add(op);
		}
		
		int counter = 0;
		
		final Set<JavaChangeOperation> deletedDefinitions = new HashSet<JavaChangeOperation>();
		deletedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T3F1D));
		deletedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T9F1));
		deletedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T6F2));
		
		final Set<JavaChangeOperation> modifiedDefinitions = new HashSet<JavaChangeOperation>();
		modifiedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T3F2M));
		modifiedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T8F2));
		
		final Set<JavaChangeOperation> addedDefinitions = new HashSet<JavaChangeOperation>();
		addedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T1F1));
		addedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T1F2));
		addedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T3F1A));
		addedDefinitions.add(environmentOperations.get(TestEnvironmentOperation.T7F2));
		
		final Set<JavaChangeOperation> deletedCalls = new HashSet<JavaChangeOperation>();
		deletedCalls.add(environmentOperations.get(TestEnvironmentOperation.T4F3D));
		deletedCalls.add(environmentOperations.get(TestEnvironmentOperation.T10F3));
		deletedCalls.add(environmentOperations.get(TestEnvironmentOperation.T10F4));
		
		final Set<JavaChangeOperation> modifiedCalls = new HashSet<JavaChangeOperation>();
		
		final Set<JavaChangeOperation> addedCalls = new HashSet<JavaChangeOperation>();
		addedCalls.add(environmentOperations.get(TestEnvironmentOperation.T2F3));
		addedCalls.add(environmentOperations.get(TestEnvironmentOperation.T3F2));
		addedCalls.add(environmentOperations.get(TestEnvironmentOperation.T4F3A));
		addedCalls.add(environmentOperations.get(TestEnvironmentOperation.T4F4));
		addedCalls.add(environmentOperations.get(TestEnvironmentOperation.T5F4));
		
		while (queue.hasNext()) {
			final JavaChangeOperation next = queue.next();
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
		for (final JavaChangeOperation op : addedCalls) {
			queue.add(op);
		}
		counter = 0;
		while (queue.hasNext()) {
			final JavaChangeOperation next = queue.next();
			assert (next != null);
			assert (addedCalls.contains(next));
			++counter;
		}
		assertEquals(addedCalls.size(), counter);
		changeGenealogy.close();
		try {
			FileUtils.deleteDirectory(tmpGraphDBFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
}
