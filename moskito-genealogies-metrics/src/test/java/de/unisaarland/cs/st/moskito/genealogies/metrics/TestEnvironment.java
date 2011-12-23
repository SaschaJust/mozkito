package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.io.File;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.kisa.LogLevel;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.TestChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

public class TestEnvironment {
	
	
	private static PersistenceUtil                                   persistenceUtil         = null;
	
	protected static CoreChangeGenealogy                             changeGenealogy;
	
	protected static File                                            tmpGraphDBFile;
	
	protected static TestChangeGenealogy genealogy;
	
	public static PersistenceUtil getPersistenceUtil() {
		return persistenceUtil;
	}
	
	public static void setup() {
		
		tmpGraphDBFile = FileUtils.createRandomDir("moskito", "test_change_genealogy", FileShutdownAction.DELETE);
		genealogy = TestChangeGenealogy.readFromDB(tmpGraphDBFile);
		
		//TODO add vertices and edges
		
		genealogy.addVertex("1");
		genealogy.addVertex("2");
		genealogy.addVertex("3");
		genealogy.addVertex("4");
		genealogy.addVertex("5");
		genealogy.addVertex("6");
		genealogy.addVertex("7");
		genealogy.addVertex("8");
		genealogy.addVertex("9");
		genealogy.addVertex("10");
		genealogy.addVertex("11");
		genealogy.addVertex("12");
		genealogy.addVertex("13");
		genealogy.addVertex("14");
		
		genealogy.addEdge("1", "2", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("1", "3", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("1", "4", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("1", "7", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("2", "4", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("2", "5", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("3", "6", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("3", "7", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("4", "5", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("4", "8", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("5", "6", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("5", "8", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("6", "4", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("6", "9", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("6", "10", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("8", "11", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("9", "11", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("9", "12", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("10", "13", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("11", "14", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("12", "14", GenealogyEdgeType.DefinitionOnDefinition);
		genealogy.addEdge("13", "14", GenealogyEdgeType.DefinitionOnDefinition);
		
		Logger.setLogLevel(LogLevel.INFO);
	}
}
