package de.unisaarland.cs.st.moskito.genealogies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.genealogies.core.ChangeGenealogyUtils;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class CoreChangeGenealogyTest extends TestEnvironment {
	
	@BeforeClass
	public static void beforeClass() {
		TestEnvironment.setup();
	}
	
	
	@Test
	public void testChangeGenealogy() {
		
		Map<String, JavaChangeOperation> transactions2Vertices = new HashMap<String, JavaChangeOperation>();
		
		File tmpGraphDBFile = FileUtils
				.createRandomDir("reposuite", "change_genealogy_test", FileShutdownAction.DELETE);
		
		CoreChangeGenealogy changeGenealogy = ChangeGenealogyUtils.readFromDB(tmpGraphDBFile);
		assertTrue(changeGenealogy != null);
		
		for (Entry<RCSTransaction, Set<JavaChangeOperation>> transactionEntry : transactionMap.entrySet()) {
			for (JavaChangeOperation operation : transactionEntry.getValue()) {
				changeGenealogy.addVertex(operation);
			}
		}
		
		assertEquals(41, changeGenealogy.vertexSize());
		
		for (Entry<RCSTransaction, Set<JavaChangeOperation>> transactionEntry : transactionMap.entrySet()) {
			for (JavaChangeOperation op : transactionEntry.getValue()) {
				assertTrue(changeGenealogy.hasVertex(op));
			}
		}
		
		//TODO test adding edges
		
		changeGenealogy.close();
	}
}
