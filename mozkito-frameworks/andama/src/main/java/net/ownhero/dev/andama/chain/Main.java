/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.ownhero.dev.andama.chain;

import java.util.LinkedList;

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
		
		net.ownhero.dev.andama.graph.AndamaGraph.buildGraph(group);
		// for (AndamaThread<?, ?> thread : group.getThreads()) {
		// System.err.println("Thread: " + thread.getHandle());
		// if (thread.hasInputConnector()) {
		// System.err.println("Input connections: ");
		// for (AndamaThreadable<?, ?> t : thread.getInputThreads()) {
		// System.err.println("- " + t.getHandle());
		// }
		// }
		//
		// if (thread.hasOutputConnector()) {
		// System.err.println("Output connections: ");
		// for (AndamaThreadable<?, ?> t : thread.getOutputThreads()) {
		// System.err.println("- " + t.getHandle());
		// }
		// }
		// }
		
		System.out.println("Done");
	}
	
}
