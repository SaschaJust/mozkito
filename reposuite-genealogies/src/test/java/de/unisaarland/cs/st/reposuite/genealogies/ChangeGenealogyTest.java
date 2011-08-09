package de.unisaarland.cs.st.reposuite.genealogies;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;

import org.junit.Test;

public class ChangeGenealogyTest {
	
	@Test
	public void testAddVertex() {
		File testDir = FileUtils.createRandomDir("generalogy", "test", FileShutdownAction.DELETE);
		System.out.println("test directory: " + testDir.getAbsolutePath());
		ChangeGenealogy cg = ChangeGenealogy.readFromDB(testDir, null);
		
		Long[] l1 = new Long[] { 0l, 1l, 2l, 3l, 4l, 5l };
		Long[] l2 = new Long[] { 0l, 6l, 7l };
		cg.addVertex("1", Arrays.asList(l1));
		cg.addVertex("2", Arrays.asList(l2));
		
		int vertexCount = 0;
		
		for (GenealogyVertex vertex : cg.vertexSet()) {
			++vertexCount;
		}
		assertEquals("The graph DB contains too less or too many vertices!", 2, vertexCount);
	}
	
}
