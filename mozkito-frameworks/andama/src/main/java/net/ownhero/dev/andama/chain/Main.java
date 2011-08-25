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
		
		AndamaGraphBuilder builder = new AndamaGraphBuilder();
		AndamaGraph graph = builder.buildGraph(threads);
		
		if (graph != null) {
			builder.displayGraph(graph);
		} else {
			System.err.println("Could not build graph.");
		}
		
		for (AndamaGraph andamaGraph : builder.getGraphs()) {
			builder.displayGraph(andamaGraph);
			System.err.println();
		}
	}
	
}
