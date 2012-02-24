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

import net.ownhero.dev.andama.exceptions.InvalidGraphLayoutException;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.CollectionCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kanuni.conditions.MapCondition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@SuppressWarnings ("rawtypes")
public class Graph {
	
	private final HashSet<Class<?>>                                                    connectionTypes = new HashSet<Class<?>>();
	private final HashMap<Class<?>, HashMap<Class<? extends Node>, LinkedList<INode>>> inputTypes      = new HashMap<Class<?>, HashMap<Class<? extends Node>, LinkedList<INode>>>();
	
	private final HashMap<Class<?>, HashMap<Class<? extends Node>, LinkedList<INode>>> outputTypes     = new HashMap<Class<?>, HashMap<Class<? extends Node>, LinkedList<INode>>>();
	private final List<INode>                                                          transformers    = new LinkedList<INode>();
	private Group                                                                      group;
	
	/**
	 * @param group
	 */
	@SuppressWarnings ("unchecked")
	public Graph(@NotNull final Group group) {
		try {
			this.group = group;
			CollectionCondition.notEmpty(group.getThreads(), "Cannot build graph on empty thread group.");
			
			for (final INode thread : group.getThreads()) {
				if (thread.getInputType() != null) {
					this.connectionTypes.add(thread.getInputType());
					
					if (!this.inputTypes.containsKey(thread.getInputType())) {
						this.inputTypes.put(thread.getInputType(),
						                    new HashMap<Class<? extends Node>, LinkedList<INode>>());
					}
					
					final HashMap<Class<? extends Node>, LinkedList<INode>> map = this.inputTypes.get(thread.getInputType());
					if (!map.containsKey(thread.getBaseType())) {
						map.put(thread.getBaseType(), new LinkedList<INode>());
					}
					
					map.get(thread.getBaseType()).add(thread);
				}
				
				if (thread.getOutputType() != null) {
					this.connectionTypes.add(thread.getOutputType());
					
					if (!this.outputTypes.containsKey(thread.getOutputType())) {
						this.outputTypes.put(thread.getOutputType(),
						                     new HashMap<Class<? extends Node>, LinkedList<INode>>());
					}
					
					final HashMap<Class<? extends Node>, LinkedList<INode>> map = this.outputTypes.get(thread.getOutputType());
					if (!map.containsKey(thread.getBaseType())) {
						map.put(thread.getBaseType(), new LinkedList<INode>());
					}
					
					map.get(thread.getBaseType()).add(thread);
				}
				
				if (thread.getBaseType().equals(Transformer.class)) {
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
			MapCondition.notEmpty(this.inputTypes, "There has to be at least one connection type in the graph.");
			MapCondition.notEmpty(this.outputTypes, "There has to be at least one connection type in the graph.");
			CollectionCondition.notEmpty(this.connectionTypes,
			                             "There has to be at least one connection type in the graph.");
			CollectionCondition.maxSize(this.connectionTypes, group.getThreads().size() - 1,
			                            "There can be at most as many types as nodes - 1.");
		}
	}
	
	/**
	 * @throws InvalidGraphLayoutException
	 * 
	 */
	public void buildGraph() throws InvalidGraphLayoutException {
		final List<INode> headThreads = new LinkedList<INode>();
		final List<INode> tailThreads = new LinkedList<INode>();
		final Map<Class<?>, Source<?>> orphanedSources = new HashMap<Class<?>, Source<?>>();
		final Map<Class<?>, Sink<?>> orphanedSinks = new HashMap<Class<?>, Sink<?>>();
		INode headThread = null;
		INode tailThread = null;
		
		for (final Class<?> inputType : this.connectionTypes) {
			headThread = null;
			tailThread = null;
			final Map<Class<? extends Node>, LinkedList<INode>> inputMap = this.inputTypes.get(inputType);
			final Map<Class<? extends Node>, LinkedList<INode>> outputMap = this.outputTypes.get(inputType);
			
			// @formatter:off
			/*
			 * this pretty much determines the sub-graph layout there are 4 possible structures: 
			 * 1. flat:    --- 
			 * 2. y-close: >-- 
			 * 3. y-open:  --< 
			 * 4. cross:   >-<
			 */
			// @formatter:on
			if (inputMap.containsKey(Multiplexer.class)) {
				final LinkedList<INode> muxList = inputMap.get(Multiplexer.class);
				if (muxList.size() > 1) {
					throw new InvalidGraphLayoutException(
					                                      String.format("Found %s %ss with the same input type: %s",
					                                                    muxList.size(),
					                                                    Multiplexer.class.getSimpleName().toLowerCase(),
					                                                    inputType));
				}
				
				if (inputMap.containsKey(Demultiplexer.class)) {
					// cross
					final LinkedList<INode> demuxList = inputMap.get(Demultiplexer.class);
					if (demuxList.size() > 1) {
						throw new InvalidGraphLayoutException(
						                                      String.format("Found %s %ss with the same input type: %s",
						                                                    muxList.size(),
						                                                    Demultiplexer.class.getSimpleName()
						                                                                       .toLowerCase(),
						                                                    inputType));
					} else {
						// look back for incoming threads (which should only be transformers and sources; since we don't
						// use transformers in subgraphs, sources is the only relevant thing here).
						final INode demux = demuxList.iterator().next();
						headThread = demux;
						tailThread = demux;
						final LinkedList<INode> sourceList = outputMap.get(Source.class);
						
						if ((sourceList != null) && !sourceList.isEmpty()) {
							// attach all available sources
							for (final INode source : sourceList) {
								connect(source, demux);
							}
						}
						
						headThreads.add(headThread);
						
						/*
						 * next, look to the right for outgoing threads we are currently in a demultiplexer which means
						 * we look out for filters next after that, we take the multiplexer (which definitely
						 * exists-checked prior to this) we then take the available sinks for that type
						 */
						
						final LinkedList<INode> filterList = inputMap.get(Filter.class);
						
						if ((filterList != null) && !filterList.isEmpty()) {
							// attach all available filters
							for (final INode filter : filterList) {
								connect(tailThread, filter);
								tailThread = filter;
							}
						}
						
						// now lookup multiplexers
						if ((muxList != null) && !muxList.isEmpty()) {
							if (tailThread.getBaseType().equals(Demultiplexer.class)) {
								throw new InvalidGraphLayoutException(
								                                      String.format("Layout would require to connect a %s to a %s. This is invalid.",
								                                                    Multiplexer.class.getSimpleName()
								                                                                     .toLowerCase(),
								                                                    Demultiplexer.class.getSimpleName()
								                                                                       .toLowerCase()));
							}
							
							for (final INode mux : muxList) {
								connect(tailThread, mux);
								tailThread = mux;
							}
						}
						
						final LinkedList<INode> sinkList = inputMap.get(Sink.class);
						
						if ((sinkList != null) && !sinkList.isEmpty()) {
							for (final INode sink : sinkList) {
								connect(demux, sink);
							}
						}
						
						tailThreads.add(tailThread);
					}
				} else {
					// y-open
					final INode mux = muxList.iterator().next();
					headThread = mux;
					tailThread = mux;
					
					// look to the left (filters and sources)
					final LinkedList<INode> filterList = outputMap.get(Filter.class);
					
					if ((filterList != null) && !filterList.isEmpty()) {
						// attach all available filters
						for (final INode filter : filterList) {
							connect(filter, headThread);
							headThread = filter;
						}
					}
					
					final LinkedList<INode> sourceList = outputMap.get(Source.class);
					
					if ((sourceList != null) && !sourceList.isEmpty()) {
						// attach all available sources
						for (final INode source : sourceList) {
							connect(source, mux);
						}
					}
					
					headThreads.add(headThread);
					
					// look to the right
					final LinkedList<INode> sinkList = inputMap.get(Sink.class);
					
					if ((sinkList != null) && !sinkList.isEmpty()) {
						for (final INode sink : sinkList) {
							connect(tailThread, sink);
						}
					}
					
					tailThreads.add(tailThread);
				}
			} else {
				if (inputMap.containsKey(Demultiplexer.class)) {
					// y-close
					final LinkedList<INode> demuxList = inputMap.get(Demultiplexer.class);
					if (demuxList.size() > 1) {
						throw new InvalidGraphLayoutException(
						                                      String.format("Found %s %ss with the same input type: %s",
						                                                    demuxList.size(),
						                                                    Demultiplexer.class.getSimpleName()
						                                                                       .toLowerCase(),
						                                                    inputType));
					} else {
						// look back for incoming threads (which should only be transformers and sources; since we don't
						// use transformers in subgraphs, sources is the only relevant thing here).
						final INode demux = demuxList.iterator().next();
						headThread = demux;
						tailThread = demux;
						final LinkedList<INode> sourceList = outputMap.get(Source.class);
						
						if ((sourceList != null) && !sourceList.isEmpty()) {
							// attach all available sources
							for (final INode source : sourceList) {
								connect(source, demux);
							}
						}
						
						headThreads.add(headThread);
						
						// next, look to the right for outgoing threads
						// we are currently in a demux which means we look out for filters next
						// we then take the available sinks for that type
						
						final LinkedList<INode> filterList = inputMap.get(Filter.class);
						
						if ((filterList != null) && !filterList.isEmpty()) {
							// attach all available filters
							for (final INode filter : filterList) {
								connect(tailThread, filter);
								tailThread = filter;
							}
						}
						
						final LinkedList<INode> sinkList = inputMap.get(Sink.class);
						
						if ((sinkList != null) && !sinkList.isEmpty()) {
							for (final INode sink : sinkList) {
								connect(demux, sink);
							}
						}
						
						tailThreads.add(tailThread);
					}
				} else {
					// flat
					final LinkedList<INode> sinkList = inputMap.get(Sink.class);
					if ((sinkList != null) && (sinkList.size() > 1)) {
						throw new InvalidGraphLayoutException(
						                                      String.format("Found %s %ss but found no corresponding %s with the same input type: %s",
						                                                    sinkList.size(), Sink.class.getSimpleName()
						                                                                               .toLowerCase(),
						                                                    Multiplexer.class.getSimpleName()
						                                                                     .toLowerCase(), inputType));
					}
					
					final LinkedList<INode> sourceList = outputMap.get(Source.class);
					if ((sourceList != null) && (sourceList.size() > 1)) {
						throw new InvalidGraphLayoutException(
						                                      String.format("Found %s %ss but found no corresponding %s with the same input type: %s",
						                                                    sinkList.size(),
						                                                    Source.class.getSimpleName().toLowerCase(),
						                                                    Demultiplexer.class.getSimpleName()
						                                                                       .toLowerCase(),
						                                                    inputType));
					}
					
					final LinkedList<INode> filterList = inputMap.get(Filter.class);
					if ((filterList != null) && !filterList.isEmpty()) {
						for (final INode filter : filterList) {
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
						final INode source = sourceList.iterator().next();
						if (headThread != null) {
							connect(source, headThread);
							headThread = null;
						} else {
							orphanedSources.put(source.getOutputType(), (Source<?>) source);
						}
					}
					
					if ((sinkList != null) && !sinkList.isEmpty()) {
						final INode sink = sinkList.iterator().next();
						if (tailThread != null) {
							connect(tailThread, sink);
							tailThread = null;
						} else {
							orphanedSinks.put(sink.getInputType(), (Sink<?>) sink);
							
						}
					}
				}
			}
		}
		
		for (final INode transformer : this.transformers) {
			boolean inputConnected = false;
			boolean outputConnected = false;
			final Class<?> outputType = transformer.getOutputType();
			final Class<?> inputType = transformer.getInputType();
			
			for (final INode thread : tailThreads) {
				if (thread.getOutputType().equals(transformer.getInputType())) {
					connect(thread, transformer);
					inputConnected = true;
					break;
				}
			}
			
			if (!inputConnected && orphanedSources.containsKey(inputType)) {
				final Source<?> source = orphanedSources.get(inputType);
				orphanedSources.remove(source);
				connect(source, transformer);
				inputConnected = true;
			}
			
			for (final INode thread : headThreads) {
				if (thread.getInputType().equals(transformer.getOutputType())) {
					connect(transformer, thread);
					outputConnected = true;
					break;
				}
			}
			
			if (!outputConnected && orphanedSinks.containsKey(outputType)) {
				final Sink<?> sink = orphanedSinks.get(outputType);
				orphanedSinks.remove(sink);
				connect(transformer, sink);
				outputConnected = true;
			}
			
			if (!inputConnected || !outputConnected) {
				throw new InvalidGraphLayoutException(String.format("The node %d:%s could not be %s connected.",
				                                                    transformer.getThreadID(), transformer.getName(),
				                                                    inputConnected
				                                                                  ? "output"
				                                                                  : "input"));
			}
		}
		
		if ((orphanedSources.size() == 1) && (orphanedSinks.size() == 1)) {
			final Source<?> source = orphanedSources.values().iterator().next();
			final Sink<?> sink = orphanedSinks.values().iterator().next();
			
			if (source.getOutputType().equals(sink.getInputType())) {
				connect(source, sink);
			}
		}
		
		for (final INode thread : this.group.getThreads()) {
			if (!thread.checkConnections()) {
				throw new InvalidGraphLayoutException("Generated graph layout is incomplete.");
			}
		}
	}
	
	/**
	 * @param from
	 * @param to
	 */
	@SuppressWarnings ("unchecked")
	private <T> void connect(final INode from,
	                         final INode to) {
		final INode<?, T> typedFrom = from;
		final INode<T, ?> typedTo = to;
		
		typedFrom.connectOutput(typedTo);
	}
	
}
