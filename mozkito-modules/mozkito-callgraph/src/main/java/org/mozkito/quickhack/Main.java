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

package org.mozkito.quickhack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.mozkito.callgraph.model.CallGraph;
import org.mozkito.callgraph.model.CallGraphEdge;
import org.mozkito.callgraph.model.MethodVertex;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		PRECONDITIONS: {
			assert args != null;
			assert args.length == 1;
			assert new File(args[0]).exists();
		}
		
		final File callgraphOutput = new File(args[0]);
		final CallGraph cg = CallGraph.unserialize(callgraphOutput);
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File("results.csv")))) {
			for (final MethodVertex vertex : cg.getVertices()) {
				
				final Collection<CallGraphEdge> edges = cg.getOutEdges(vertex);
				for (final CallGraphEdge cgEdge : edges) {
					writer.append(vertex.getFilename()).append(',').append(cg.getDest(cgEdge).getFilename())
					      .append(',').append(cgEdge.getOccurrence() + "");
					writer.newLine();
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
