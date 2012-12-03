/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.genealogies.metrics;

import java.io.File;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.kisa.LogLevel;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.core.GenealogyEdgeType;
import org.mozkito.persistence.PersistenceUtil;

/**
 * The Class TestEnvironment.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class TestEnvironment {
	
	/** The persistence util. */
	private static PersistenceUtil       persistenceUtil = null;
	
	/** The change genealogy. */
	protected static CoreChangeGenealogy changeGenealogy;
	
	/** The tmp graph db file. */
	protected static File                tmpGraphDBFile;
	
	/** The genealogy. */
	protected static TestChangeGenealogy genealogy;
	
	/**
	 * Gets the persistence util.
	 * 
	 * @return the persistence util
	 */
	public static PersistenceUtil getPersistenceUtil() {
		return TestEnvironment.persistenceUtil;
	}
	
	/**
	 * Setup.
	 */
	public static void setup() {
		
		TestEnvironment.tmpGraphDBFile = FileUtils.createRandomDir("moskito", "test_change_genealogy",
		                                                           FileShutdownAction.DELETE);
		TestEnvironment.genealogy = TestChangeGenealogy.readFromDB(TestEnvironment.tmpGraphDBFile);
		
		TestEnvironment.genealogy.addVertex("1");
		TestEnvironment.genealogy.addVertex("2");
		TestEnvironment.genealogy.addVertex("3");
		TestEnvironment.genealogy.addVertex("4");
		TestEnvironment.genealogy.addVertex("5");
		TestEnvironment.genealogy.addVertex("6");
		TestEnvironment.genealogy.addVertex("7");
		TestEnvironment.genealogy.addVertex("8");
		TestEnvironment.genealogy.addVertex("9");
		TestEnvironment.genealogy.addVertex("10");
		TestEnvironment.genealogy.addVertex("11");
		TestEnvironment.genealogy.addVertex("12");
		TestEnvironment.genealogy.addVertex("13");
		TestEnvironment.genealogy.addVertex("14");
		
		TestEnvironment.genealogy.addEdge("1", "2", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("1", "3", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("1", "4", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("1", "7", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("2", "4", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("2", "5", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("3", "6", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("3", "7", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("4", "5", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("4", "8", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("5", "6", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("5", "8", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("6", "4", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("6", "9", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("6", "10", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("8", "11", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("9", "11", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("9", "12", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("10", "13", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("11", "14", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("12", "14", GenealogyEdgeType.DefinitionOnDefinition);
		TestEnvironment.genealogy.addEdge("13", "14", GenealogyEdgeType.DefinitionOnDefinition);
		
		Logger.setLogLevel(LogLevel.INFO);
	}
}
