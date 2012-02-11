/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.andama.threads;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CollectionCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Graph {
	
	private final HashSet<Class<?>>                                                                                   connectionTypes = new HashSet<Class<?>>();
	private final HashMap<Class<?>, HashMap<Class<? extends AndamaThread<?, ?>>, LinkedList<AndamaThreadable<?, ?>>>> inputTypes      = new HashMap<Class<?>, HashMap<Class<? extends AndamaThread<?, ?>>, LinkedList<AndamaThreadable<?, ?>>>>();
	
	private final HashMap<Class<?>, HashMap<Class<? extends AndamaThread<?, ?>>, LinkedList<AndamaThreadable<?, ?>>>> outputTypes     = new HashMap<Class<?>, HashMap<Class<? extends AndamaThread<?, ?>>, LinkedList<AndamaThreadable<?, ?>>>>();
	private final List<AndamaThreadable<?, ?>>                                                                        transformers    = new LinkedList<AndamaThreadable<?, ?>>();
	private AndamaGroup                                                                                               group;
	
	/**
	 * @param group
	 */
	public Graph(@NotNull final AndamaGroup group) {
		try {
			this.group = group;
			CollectionCondition.notEmpty(group.getThreads(), "Cannot build graph on empty thread group.");
			
			for (final AndamaThreadable<?, ?> thread : group.getThreads()) {
				if (thread.getInputType() != null) {
					this.connectionTypes.add(thread.getInputType());
					
					if (!this.inputTypes.containsKey(thread.getInputType())) {
						this.inputTypes.put(thread.getInputType(),
						                    new HashMap<Class<? extends AndamaThread<?, ?>>, LinkedList<AndamaThreadable<?, ?>>>());
					}
					
					final HashMap<Class<? extends AndamaThread<?, ?>>, LinkedList<AndamaThreadable<?, ?>>> map = this.inputTypes.get(thread.getInputType());
					if (!map.containsKey(thread.getBaseType())) {
						map.put(thread.getBaseType(), new LinkedList<AndamaThreadable<?, ?>>());
					}
					
					map.get(thread.getBaseType()).add(thread);
				}
				if (thread.getOutputType() != null) {
					this.connectionTypes.add(thread.getOutputType());
					
					if (!this.outputTypes.containsKey(thread.getOutputType())) {
						this.outputTypes.put(thread.getOutputType(),
						                     new HashMap<Class<? extends AndamaThread<?, ?>>, LinkedList<AndamaThreadable<?, ?>>>());
					}
					
					final HashMap<Class<? extends AndamaThread<?, ?>>, LinkedList<AndamaThreadable<?, ?>>> map = this.outputTypes.get(thread.getOutputType());
					if (!map.containsKey(thread.getBaseType())) {
						map.put(thread.getBaseType(), new LinkedList<AndamaThreadable<?, ?>>());
					}
					
					map.get(thread.getBaseType()).add(thread);
				}
				
				if (thread.getBaseType().equals(AndamaTransformer.class)) {
					this.transformers.add(thread);
				}
				
			}
		} finally {
			Condition.notNull(this.transformers,
			                  "inputTypeMap is assumed to be not null within the whole lifespan of this class.");
			Condition.notNull(this.inputTypes,
			                  "outputTypeMap is assumed to be not null within the whole lifespan of this class.");
			Condition.notNull(this.connectionTypes,
			                  "connectionTypes is assumed to be not null within the whole lifespan of this class.");
			// TODO add MapCondition.notEmpty();
			CollectionCondition.notEmpty(this.connectionTypes,
			                             "There has to be at least one connection type in the graph.");
			CollectionCondition.maxSize(this.connectionTypes, group.getThreads().size() - 1,
			                            "There can be at most as many types as nodes - 1.");
		}
	}
	
	/**
	 * 
	 */
	public void buildGraph() {
		final List<AndamaThreadable<?, ?>> headThreads = new LinkedList<AndamaThreadable<?, ?>>();
		final List<AndamaThreadable<?, ?>> tailThreads = new LinkedList<AndamaThreadable<?, ?>>();
		AndamaThreadable<?, ?> headThread = null;
		AndamaThreadable<?, ?> tailThread = null;
		
		for (final Class<?> inputType : this.connectionTypes) {
			final Map<Class<? extends AndamaThread<?, ?>>, LinkedList<AndamaThreadable<?, ?>>> inputMap = this.inputTypes.get(inputType);
			final Map<Class<? extends AndamaThread<?, ?>>, LinkedList<AndamaThreadable<?, ?>>> outputMap = this.outputTypes.get(inputType);
			
			// @formatter:off
			/*
			 * this pretty much determines the sub-graph layout there are 4 possible structures: 
			 * 1. flat:    --- 
			 * 2. y-close: >-- 
			 * 3. y-open:  --< 
			 * 4. cross:   >-<
			 */
			// @formatter:on
			if (inputMap.containsKey(AndamaMultiplexer.class)) {
				final LinkedList<AndamaThreadable<?, ?>> muxList = inputMap.get(AndamaMultiplexer.class);
				if (muxList.size() > 1) {
					// TODO error
				}
				
				if (inputMap.containsKey(AndamaDemultiplexer.class)) {
					// cross
					final LinkedList<AndamaThreadable<?, ?>> demuxList = inputMap.get(AndamaDemultiplexer.class);
					if (demuxList.size() > 1) {
						// TODO error
					} else {
						// look back for incoming threads (which should only be transformers and sources; since we don't
						// use transformers in subgraphs, sources is the only relevant thing here).
						final AndamaThreadable<?, ?> demux = demuxList.iterator().next();
						headThread = demux;
						tailThread = demux;
						final LinkedList<AndamaThreadable<?, ?>> sourceList = outputMap.get(AndamaSource.class);
						
						if ((sourceList != null) && !sourceList.isEmpty()) {
							// attach all available sources
							for (final AndamaThreadable<?, ?> source : sourceList) {
								connect(source, demux);
							}
						}
						
						headThreads.add(headThread);
						
						// next, look to the right for outgoing threads
						// we are currently in a demux which means we look out for filters next
						// after that, we take the multiplexer (which definitely exists-checked prior to this)
						// we then take the available sinks for that type
						
						final LinkedList<AndamaThreadable<?, ?>> filterList = inputMap.get(AndamaFilter.class);
						
						if ((filterList != null) && !filterList.isEmpty()) {
							// attach all available filters
							for (final AndamaThreadable<?, ?> filter : filterList) {
								connect(tailThread, filter);
								tailThread = filter;
							}
						}
						
						// now lookup multiplexers
						if ((muxList != null) && !muxList.isEmpty()) {
							if (tailThread.getBaseType().equals(AndamaDemultiplexer.class)) {
								// TODO: ERROR
							}
							
							for (final AndamaThreadable<?, ?> mux : muxList) {
								connect(tailThread, mux);
								tailThread = mux;
							}
						}
						
						final LinkedList<AndamaThreadable<?, ?>> sinkList = inputMap.get(AndamaSink.class);
						
						if ((sinkList != null) && !sinkList.isEmpty()) {
							for (final AndamaThreadable<?, ?> sink : sinkList) {
								connect(demux, sink);
							}
						}
						
						tailThreads.add(tailThread);
					}
				} else {
					// y-open
					final AndamaThreadable<?, ?> mux = muxList.iterator().next();
					headThread = mux;
					tailThread = mux;
					
					// look to the left (filters and sources)
					final LinkedList<AndamaThreadable<?, ?>> filterList = outputMap.get(AndamaFilter.class);
					
					if ((filterList != null) && !filterList.isEmpty()) {
						// attach all available filters
						for (final AndamaThreadable<?, ?> filter : filterList) {
							connect(filter, headThread);
							headThread = filter;
						}
					}
					
					final LinkedList<AndamaThreadable<?, ?>> sourceList = outputMap.get(AndamaSource.class);
					
					if ((sourceList != null) && !sourceList.isEmpty()) {
						// attach all available sources
						for (final AndamaThreadable<?, ?> source : sourceList) {
							connect(source, mux);
						}
					}
					
					headThreads.add(headThread);
					
					// look to the right
					final LinkedList<AndamaThreadable<?, ?>> sinkList = inputMap.get(AndamaSink.class);
					
					if ((sinkList != null) && !sinkList.isEmpty()) {
						for (final AndamaThreadable<?, ?> sink : sinkList) {
							connect(tailThread, sink);
						}
					}
					
					tailThreads.add(tailThread);
				}
			} else {
				if (inputMap.containsKey(AndamaDemultiplexer.class)) {
					// y-close
					final LinkedList<AndamaThreadable<?, ?>> demuxList = inputMap.get(AndamaDemultiplexer.class);
					if (demuxList.size() > 1) {
						// TODO error
					} else {
						// look back for incoming threads (which should only be transformers and sources; since we don't
						// use transformers in subgraphs, sources is the only relevant thing here).
						final AndamaThreadable<?, ?> demux = demuxList.iterator().next();
						headThread = demux;
						tailThread = demux;
						final LinkedList<AndamaThreadable<?, ?>> sourceList = outputMap.get(AndamaSource.class);
						
						if ((sourceList != null) && !sourceList.isEmpty()) {
							// attach all available sources
							for (final AndamaThreadable<?, ?> source : sourceList) {
								connect(source, demux);
							}
						}
						
						headThreads.add(headThread);
						
						// next, look to the right for outgoing threads
						// we are currently in a demux which means we look out for filters next
						// we then take the available sinks for that type
						
						final LinkedList<AndamaThreadable<?, ?>> filterList = inputMap.get(AndamaFilter.class);
						
						if ((filterList != null) && !filterList.isEmpty()) {
							// attach all available filters
							for (final AndamaThreadable<?, ?> filter : filterList) {
								connect(tailThread, filter);
								tailThread = filter;
							}
						}
						
						final LinkedList<AndamaThreadable<?, ?>> sinkList = inputMap.get(AndamaSink.class);
						
						if ((sinkList != null) && !sinkList.isEmpty()) {
							for (final AndamaThreadable<?, ?> sink : sinkList) {
								connect(demux, sink);
							}
						}
						
						tailThreads.add(tailThread);
					}
				} else {
					// flat
					final LinkedList<AndamaThreadable<?, ?>> sinkList = inputMap.get(AndamaSink.class);
					if ((sinkList != null) && (sinkList.size() > 1)) {
						// TODO error
					}
					
					final LinkedList<AndamaThreadable<?, ?>> sourceList = outputMap.get(AndamaSource.class);
					if ((sourceList != null) && (sourceList.size() > 1)) {
						// TODO error
					}
					
					final LinkedList<AndamaThreadable<?, ?>> filterList = inputMap.get(AndamaFilter.class);
					if ((filterList != null) && !filterList.isEmpty()) {
						for (final AndamaThreadable<?, ?> filter : filterList) {
							if (headThread == null) {
								headThread = filter;
								tailThread = filter;
							} else {
								connect(tailThread, filter);
								tailThread = filter;
							}
						}
					}
					
					if ((sourceList != null) && !sourceList.isEmpty()) {
						if (headThread != null) {
							final AndamaThreadable<?, ?> source = sourceList.iterator().next();
							connect(source, headThread);
							headThread = null;
						}
					}
					
					if ((sinkList != null) && !sinkList.isEmpty()) {
						if (tailThread != null) {
							final AndamaThreadable<?, ?> sink = sinkList.iterator().next();
							connect(tailThread, sink);
							tailThread = null;
						}
					}
				}
			}
		}
		
		for (final AndamaThreadable<?, ?> transformer : this.transformers) {
			boolean inputConnected = false;
			boolean outputConnected = false;
			for (final AndamaThreadable<?, ?> thread : tailThreads) {
				if (thread.getOutputType().equals(transformer.getInputType())) {
					connect(thread, transformer);
					// tailThreads.remove(thread);
					inputConnected = true;
					break;
				}
			}
			
			for (final AndamaThreadable<?, ?> thread : headThreads) {
				if (thread.getInputType().equals(transformer.getOutputType())) {
					connect(transformer, thread);
					// headThreads.remove(thread);
					outputConnected = true;
					break;
				}
			}
			
			if (!inputConnected || !outputConnected) {
				// TODO error
			}
		}
		
		for (final AndamaThreadable<?, ?> thread : this.group.getThreads()) {
			if (!thread.checkConnections()) {
				// TODO error
				System.err.println("ERROR");
			}
		}
	}
	
	/**
	 * @param from
	 * @param to
	 */
	@SuppressWarnings ("unchecked")
	private <T> void connect(final AndamaThreadable<?, ?> from,
	                         final AndamaThreadable<?, ?> to) {
		final AndamaThreadable<?, T> typedFrom = (AndamaThreadable<?, T>) from;
		final AndamaThreadable<T, ?> typedTo = (AndamaThreadable<T, ?>) to;
		
		typedFrom.connectOutput(typedTo);
	}
	
}
