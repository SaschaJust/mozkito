package de.unisaarland.cs.st.moskito.genealogies.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.neo4j.graphdb.GraphDatabaseService;

import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TestCoreChangeGenealogy extends CoreChangeGenealogy {
	
	public static enum TestEnvironmentOperation {
		T1F1, T1F2, T2F3, T3F1D, T3F1A, T3F2, T4F3D, T4F3A, T4F4, T5F4, T6F2, T7F2, T8F2, T9F1, T10F3, T10F4, T3F2M;
	}
	
	public Map<Long, JavaChangeOperation>                     staticOperations        = new HashMap<Long, JavaChangeOperation>();

	public Map<RCSTransaction, Set<JavaChangeOperation>>      transactionMap          = new HashMap<RCSTransaction, Set<JavaChangeOperation>>();
	
	public Map<Integer, RCSTransaction>                       environmentTransactions = new HashMap<Integer, RCSTransaction>();
	
	public Map<TestEnvironmentOperation, JavaChangeOperation> environmentOperations   = new HashMap<TestEnvironmentOperation, JavaChangeOperation>();
	
	public TestCoreChangeGenealogy(GraphDatabaseService graph, File dbFile) {
		super(graph, dbFile, null);
		//TODO setup the static elements for lookup
		//TODO move code from TestEnvironment here
	}
	
	@Override
	public JavaChangeOperation loadById(final long id, Class<? extends JavaChangeOperation> clazz) {
		return staticOperations.get(id);
	}
	
}
