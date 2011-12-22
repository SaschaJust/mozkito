package de.unisaarland.cs.st.moskito.genealogies.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.BooleanArgument;
import net.ownhero.dev.andama.settings.DirectoryArgument;
import net.ownhero.dev.andama.settings.OutputFileArgument;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.pgm.util.graphml.GraphMLWriter;

import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.moskito.genealogies.core.ChangeGenealogyUtils;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.GenealogyEdgeType;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;
import de.unisaarland.cs.st.moskito.settings.RepositorySettings;

public class GenealogyUtils {
	
	private static void exportToDOT(CoreChangeGenealogy genealogy, File dotFile) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(dotFile));
		
		out.write("digraph g {");
		out.write(FileUtils.lineSeparator);
		Iterator<JavaChangeOperation> vertexIterator = genealogy.vertexIterator();
		while (vertexIterator.hasNext()) {
			JavaChangeOperation op = vertexIterator.next();
			String opId = genealogy.getNodeId(op);
			for (JavaChangeOperation parent : genealogy.getAllParents(op)) {
				String parentId = genealogy.getNodeId(parent);
				out.write(opId);
				out.write(" -> ");
				out.write(parentId);
				out.write(FileUtils.lineSeparator);
			}
		}
		out.write("}");
		out.close();
	}
	
	public static void exportToGraphML(final CoreChangeGenealogy genealogy,
			final File outFile) {
		try {
			FileOutputStream out = new FileOutputStream(outFile);
			Graph g = new Neo4jGraph(genealogy.getGraphDBService());
			GraphMLWriter.outputGraph(g, out);
		} catch (FileNotFoundException e) {
			if (Logger.logError()) {
				Logger.error(e.getLocalizedMessage(), e);
			}
		} catch (XMLStreamException e) {
			if (Logger.logError()) {
				Logger.error(e.getLocalizedMessage(), e);
			}
		}
	}
	
	public static String getGenealogyStats(final CoreChangeGenealogy genealogy) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("#Vertices: ");
		sb.append(genealogy.vertexSize());
		sb.append(FileUtils.lineSeparator);
		sb.append("#Edges: ");
		
		sb.append(genealogy.edgeSize());
		sb.append(FileUtils.lineSeparator);
		sb.append("Edge types used: ");
		
		for (GenealogyEdgeType t : genealogy.getExistingEdgeTypes()) {
			sb.append(t.toString());
			sb.append(" ");
		}
		sb.append(FileUtils.lineSeparator);
		return sb.toString();
	}
	
	public static void run() {
		
		RepositorySettings settings = new RepositorySettings();
		
		DatabaseArguments persistenceArgs = new DatabaseArguments(settings, true, "ppa");
		
		DirectoryArgument graphDBArg = new DirectoryArgument(settings, "genealogy.graphdb",
				"Directory in which to load the GraphDB from.", null,
				true, true);
		
		BooleanArgument statsArg = new BooleanArgument(settings, "stats",
				"Print vertex/edge statistic for ChangeGenealogy", "false",
				false);
		
		OutputFileArgument graphmlArg = new OutputFileArgument(settings, "graphml.out",
				"Export the graph as GraphML file into this file.",
				null, false, false);
		
		OutputFileArgument dotArg = new OutputFileArgument(settings, "dot.out",
				"Export the graph as DOT file (must be processed using graphviz).", null, false, true);
		
		settings.parseArguments();
		
		persistenceArgs.getValue();
		
		PersistenceUtil persistenceUtil;
		try {
			persistenceUtil = PersistenceManager.getUtil();
		} catch (UninitializedDatabaseException e1) {
			throw new UnrecoverableError(e1);
		}
		
		CoreChangeGenealogy genealogy = ChangeGenealogyUtils.readFromDB(graphDBArg.getValue(), persistenceUtil);
		
		if (statsArg.getValue()) {
			System.out.println(getGenealogyStats(genealogy));
		}
		
		File graphmlFile = graphmlArg.getValue();
		if (graphmlFile != null) {
			exportToGraphML(genealogy, graphmlFile);
		}
		
		File dotFile = dotArg.getValue();
		if (dotFile != null) {
			try {
				exportToDOT(genealogy, dotFile);
			} catch (IOException e) {
				throw new UnrecoverableError(e);
			}
		}
		
		genealogy.close();
	}
	
}
