/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.andama.chain;

import java.util.LinkedList;

import net.ownhero.dev.andama.graph.AndamaGraph;
import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaThread;

public class Main {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		AndamaChain chain = new AndamaChain(null) {
			
			@Override
			public void setup() {
			}
			
			@Override
			public void shutdown() {
			}
		};
		
		AndamaGroup group = new AndamaGroup("test", chain);
		LinkedList<AndamaThread<?, ?>> threads = new LinkedList<AndamaThread<?, ?>>();
		threads.add(new DoubleSink(group, null, false));
		threads.add(new StringFilter(group, null, false));
		threads.add(new StringDoubleTransformer(group, null, false));
		threads.add(new IntegerFilter(group, null, false));
		threads.add(new IntegerDoubleTransformer(group, null, false));
		threads.add(new DoubleDemultiplexer(group, null, false));
		threads.add(new StringSource(group, null, false));
		threads.add(new IntegerSource(group, null, false));
		
		AndamaGraph graph = net.ownhero.dev.andama.graph.AndamaGraph.buildGraph(group);
		graph.connectThreads(0);
		System.out.println("Done");
		
		// if (graph != null) {
		// builder.displayGraph(graph);
		// } else {
		// System.err.println("Could not build graph.");
		// }
		//
		// for (AndamaGraph andamaGraph : builder.getGraphs()) {
		// builder.displayGraph(andamaGraph);
		// System.err.println();
		// }
	}
	
}
