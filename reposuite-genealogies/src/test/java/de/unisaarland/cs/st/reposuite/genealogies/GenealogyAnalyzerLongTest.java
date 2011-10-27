package de.unisaarland.cs.st.reposuite.genealogies;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.genealogies.TestEnvironment.TestEnvironmentOperation;
import de.unisaarland.cs.st.reposuite.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.reposuite.genealogies.utils.GenealogyAnalyzer;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;


public class GenealogyAnalyzerLongTest {
	
	@BeforeClass
	public static void beforeClass() {
		TestEnvironment.setup();
	}
	
	private GenealogyAnalyzer analyzer;
	
	@Before
	public void setup(){
		analyzer = new GenealogyAnalyzer();
	}
	
	@Test
	public void testGetDependencies(){
		
		
		Collection<JavaChangeOperation> deps = analyzer.getDependencies(
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T2F3),
				TestEnvironment.getPersistenceUtil());
		
		assert (deps != null);
		assertEquals(1, deps.size());
		assertEquals(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T1F2), deps.iterator().next());
		
		deps = analyzer.getDependencies(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F1D),
				TestEnvironment.getPersistenceUtil());
		assert (deps != null);
		assertEquals(1, deps.size());
		assertEquals(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T1F1), deps.iterator().next());
		
		deps = analyzer.getDependencies(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F1A),
				TestEnvironment.getPersistenceUtil());
		assert (deps != null);
		assertEquals(0, deps.size());
		
		deps = analyzer.getDependencies(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F2),
				TestEnvironment.getPersistenceUtil());
		assert (deps != null);
		assertEquals(1, deps.size());
		assertEquals(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F1A), deps.iterator().next());
		
		deps = analyzer.getDependencies(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T4F3D),
				TestEnvironment.getPersistenceUtil());
		assert (deps != null);
		assertEquals(1, deps.size());
		assertEquals(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T2F3), deps.iterator().next());
		
		deps = analyzer.getDependencies(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T4F3A),
				TestEnvironment.getPersistenceUtil());
		assert (deps != null);
		assertEquals(1, deps.size());
		assertEquals(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F1A), deps.iterator().next());
		
		deps = analyzer.getDependencies(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T4F4),
				TestEnvironment.getPersistenceUtil());
		assert (deps != null);
		assertEquals(1, deps.size());
		assertEquals(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F1A), deps.iterator().next());
		
		deps = analyzer.getDependencies(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T5F4),
				TestEnvironment.getPersistenceUtil());
		assert (deps != null);
		assertEquals(1, deps.size());
		assertEquals(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F1A), deps.iterator().next());
		
		deps = analyzer.getDependencies(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T6F2),
				TestEnvironment.getPersistenceUtil());
		assert (deps != null);
		assertEquals(1, deps.size());
		assertEquals(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F2M), deps.iterator().next());
		
		deps = analyzer.getDependencies(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T7F2),
				TestEnvironment.getPersistenceUtil());
		assert (deps != null);
		assertEquals(1, deps.size());
		assertEquals(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T6F2), deps.iterator().next());
		
		deps = analyzer.getDependencies(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T8F2),
				TestEnvironment.getPersistenceUtil());
		assert (deps != null);
		assertEquals(1, deps.size());
		assertEquals(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T7F2), deps.iterator().next());
		
		deps = analyzer.getDependencies(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T9F1),
				TestEnvironment.getPersistenceUtil());
		assert (deps != null);
		assertEquals(1, deps.size());
		assertEquals(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F1A), deps.iterator().next());
		
		deps = analyzer.getDependencies(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T10F3),
				TestEnvironment.getPersistenceUtil());
		assert (deps != null);
		assertEquals(0, deps.size());
		
		deps = analyzer.getDependencies(TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T10F4),
				TestEnvironment.getPersistenceUtil());
		assert (deps != null);
		assertEquals(0, deps.size());
		
	}
	
	@Test
	public void testGetEdgeTypeForDependency() {
		GenealogyEdgeType edgeType = GenealogyAnalyzer.getEdgeTypeForDependency(
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T2F3),
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T1F1));
		assertEquals(GenealogyEdgeType.CallOnDefinition, edgeType);
		
		edgeType = GenealogyAnalyzer.getEdgeTypeForDependency(
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F1D),
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T1F1));
		assertEquals(GenealogyEdgeType.DeletedDefinitionOnDefinition, edgeType);
		
		edgeType = GenealogyAnalyzer.getEdgeTypeForDependency(
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F2),
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F1A));
		assertEquals(GenealogyEdgeType.CallOnDefinition, edgeType);
		
		edgeType = GenealogyAnalyzer.getEdgeTypeForDependency(
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T4F3D),
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T2F3));
		assertEquals(GenealogyEdgeType.DeletedCallOnCall, edgeType);
		
		edgeType = GenealogyAnalyzer.getEdgeTypeForDependency(
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T4F3A),
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F1A));
		assertEquals(GenealogyEdgeType.CallOnDefinition, edgeType);
		
		edgeType = GenealogyAnalyzer.getEdgeTypeForDependency(
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T4F4),
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F1A));
		assertEquals(GenealogyEdgeType.CallOnDefinition, edgeType);
		
		edgeType = GenealogyAnalyzer.getEdgeTypeForDependency(
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T5F4),
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F1A));
		assertEquals(GenealogyEdgeType.CallOnDefinition, edgeType);
		
		edgeType = GenealogyAnalyzer.getEdgeTypeForDependency(
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T6F2),
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F2M));
		assertEquals(GenealogyEdgeType.DeletedDefinitionOnDefinition, edgeType);
		
		edgeType = GenealogyAnalyzer.getEdgeTypeForDependency(
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T7F2),
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T6F2));
		assertEquals(GenealogyEdgeType.DefinitionOnDeletedDefinition, edgeType);
		
		edgeType = GenealogyAnalyzer.getEdgeTypeForDependency(
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T8F2),
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T7F2));
		assertEquals(GenealogyEdgeType.DefinitionOnDefinition, edgeType);
		
		edgeType = GenealogyAnalyzer.getEdgeTypeForDependency(
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T9F1),
				TestEnvironment.environmentOperations.get(TestEnvironmentOperation.T3F1A));
		assertEquals(GenealogyEdgeType.DeletedDefinitionOnDefinition, edgeType);
		
	}
	
}
