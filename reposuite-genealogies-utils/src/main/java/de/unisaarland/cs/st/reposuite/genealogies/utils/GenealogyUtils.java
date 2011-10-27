package de.unisaarland.cs.st.reposuite.genealogies.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.stream.XMLStreamException;

import net.ownhero.dev.andama.settings.BooleanArgument;
import net.ownhero.dev.andama.settings.DirectoryArgument;
import net.ownhero.dev.andama.settings.OutputFileArgument;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.pgm.util.graphml.GraphMLWriter;

import de.unisaarland.cs.st.reposuite.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.reposuite.genealogies.layer.ChangeGenealogy;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;

public class GenealogyUtils {
	
	public static void exportToGraphML(final ChangeGenealogy genealogy,
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
	
	public static String getGenealogyStats(final ChangeGenealogy genealogy) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("#Vertices: ");
		sb.append(genealogy.vertexSize());
		sb.append(FileUtils.lineSeparator);
		sb.append("#Edges: ");
		
		sb.append(genealogy.edgeSize());
		sb.append(FileUtils.lineSeparator);
		sb.append("Edge types used: ");
		
		for (String t : genealogy.getExistingEdgeTypes()) {
			sb.append(t);
			sb.append(" ");
		}
		sb.append(FileUtils.lineSeparator);
		return sb.toString();
	}
	
	public static void run() {
		
		RepositorySettings settings = new RepositorySettings();
		
		DirectoryArgument graphDBArg = new DirectoryArgument(settings, "genealogy.graphdb",
		                                                     "Directory in which to load the GraphDB from.", null,
		                                                     true, true);
		
		BooleanArgument statsArg = new BooleanArgument(settings, "stats",
		                                               "Print vertex/edge statistic for ChangeGenealogy", "false",
		                                               false);
		
		OutputFileArgument graphmlArg = new OutputFileArgument(settings, "graphml.out",
		                                                       "Export the graph as GraphML file into this file.",
		                                                       null, false, false);
		
		settings.parseArguments();
		
		ChangeGenealogy genealogy = CoreChangeGenealogy.readFromDB(graphDBArg.getValue());
		
		if (statsArg.getValue()) {
			System.out.println(getGenealogyStats(genealogy));
		}
		
		File graphmlFile = graphmlArg.getValue();
		if (graphmlFile != null) {
			exportToGraphML(genealogy, graphmlFile);
		}
		genealogy.close();
	}
	
}
