/**
 * 
 */
package de.unisaarland.cs.st.reposuite.toolchain;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import de.unisaarland.cs.st.reposuite.exceptions.Shutdown;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * The thread pool manages all threads of the tool chain. Since all
 * {@link RepoSuiteThread}s have to register themselves in the
 * {@link RepoSuiteThreadGroup} which is owned by the
 * {@link RepoSuiteThreadPool}, the thread pool has control over all threads of
 * the tool chain. Additionally, this class automatically generates a connected
 * graph for the registered threads. See the corresponding methods for details.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteThreadPool {
	
	private final RepoSuiteThreadGroup threads;
	
	/**
	 * {@link RepoSuiteThreadPool} constructor. Initializes the
	 * {@link RepoSuiteThreadGroup};
	 * 
	 * @param name
	 *            the name of the {@link RepoSuiteThreadGroup}
	 */
	public RepoSuiteThreadPool(final String name, final RepoSuiteToolchain toolchain) {
		this.threads = new RepoSuiteThreadGroup(name, toolchain);
	}
	
	/**
	 * This method builds a graph consisting of nodes mapping a
	 * {@link RepoSuiteThread} class, e.g. {@link RepoSuiteSourceThread}, to a
	 * type determining the input output connectors of the corresponding
	 * threads.
	 * 
	 * @param source
	 * @param filterSet
	 * @param transformerSet
	 * @param sinkSet
	 * @return a {@link List} of {@link Tuple} containing a
	 *         {@link RepoSuiteThread} and a {@link Type} which suffices to
	 *         classify the corresponding {@link RepoSuiteThread}.
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	private LinkedList<Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>>> buildGraph(final Type source,
	                                                                                          Set<Tuple<Type, Type>> filterSet,
	                                                                                          Set<Tuple<Type, Type>> transformerSet,
	                                                                                          Set<Tuple<Type, Type>> sinkSet) {
		LinkedList<Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>>> list = new LinkedList<Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>>>();
		
		HashSet<Tuple<Type, Type>> validFilter = new HashSet<Tuple<Type, Type>>(CollectionUtils.select(filterSet,
		                                                                                               new Predicate() {
			                                                                                               
			                                                                                               @Override
			                                                                                               public boolean evaluate(final Object object) {
				                                                                                               Tuple<Type, Type> element = (Tuple<Type, Type>) object;
				                                                                                               return (element.getFirst().equals(source));
			                                                                                               }
		                                                                                               }));
		if (!validFilter.isEmpty()) {
			for (Tuple<Type, Type> tuple : validFilter) {
				Type newSource = tuple.getSecond();
				filterSet = new HashSet<Tuple<Type, Type>>(filterSet);
				filterSet.remove(tuple);
				
				LinkedList<Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>>> graph = buildGraph(newSource,
				                                                                                          filterSet,
				                                                                                          transformerSet,
				                                                                                          sinkSet);
				
				if (graph != null) {
					list.add(new Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>>(
					                                                                        RepoSuiteFilterThread.class,
					                                                                        tuple));
					list.addAll(graph);
					return list;
				} else {
					filterSet.add(tuple);
				}
			}
		}
		
		HashSet<Tuple<Type, Type>> validTransformer = new HashSet<Tuple<Type, Type>>(
		                                                                             CollectionUtils.select(transformerSet,
		                                                                                                    new Predicate() {
			                                                                                                    
			                                                                                                    @Override
			                                                                                                    public boolean evaluate(final Object object) {
				                                                                                                    Tuple<Type, Type> element = (Tuple<Type, Type>) object;
				                                                                                                    return (element.getFirst().equals(source));
			                                                                                                    }
		                                                                                                    }));
		if (!validTransformer.isEmpty()) {
			for (Tuple<Type, Type> tuple : validTransformer) {
				Type newSource = tuple.getSecond();
				transformerSet = new HashSet<Tuple<Type, Type>>(transformerSet);
				transformerSet.remove(tuple);
				
				LinkedList<Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>>> graph = buildGraph(newSource,
				                                                                                          filterSet,
				                                                                                          transformerSet,
				                                                                                          sinkSet);
				
				if (graph != null) {
					list.add(new Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>>(
					                                                                        RepoSuiteTransformerThread.class,
					                                                                        tuple));
					list.addAll(graph);
					return list;
				} else {
					transformerSet.add(tuple);
				}
			}
		}
		
		HashSet<Tuple<Type, Type>> validSink = new HashSet<Tuple<Type, Type>>(CollectionUtils.select(sinkSet,
		                                                                                             new Predicate() {
			                                                                                             
			                                                                                             @Override
			                                                                                             public boolean evaluate(final Object object) {
				                                                                                             Tuple<Type, Type> element = (Tuple<Type, Type>) object;
				                                                                                             return (element.getFirst().equals(source));
			                                                                                             }
		                                                                                             }));
		if (!validSink.isEmpty()) {
			for (Tuple<Type, Type> tuple : validSink) {
				Type newSource = tuple.getSecond();
				sinkSet = new HashSet<Tuple<Type, Type>>(sinkSet);
				sinkSet.remove(tuple);
				
				LinkedList<Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>>> graph = buildGraph(newSource,
				                                                                                          filterSet,
				                                                                                          transformerSet,
				                                                                                          sinkSet);
				
				if (graph != null) {
					list.add(new Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>>(RepoSuiteSinkThread.class,
					                                                                        tuple));
					list.addAll(graph);
					return list;
				} else {
					sinkSet.add(tuple);
				}
			}
		}
		
		if (filterSet.isEmpty() && transformerSet.isEmpty() && sinkSet.isEmpty()) {
			return new LinkedList<Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>>>();
		} else {
			return null;
		}
		
	}
	
	/**
	 * this method is responsible for connecting the threads according to the
	 * computed graph.
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	private void connectThreads() {
		Map<Tuple<Type, Type>, List<RepoSuiteSourceThread<?>>> sourceThreads = new HashMap<Tuple<Type, Type>, List<RepoSuiteSourceThread<?>>>();
		Map<Tuple<Type, Type>, List<RepoSuiteFilterThread<?>>> filterThreads = new HashMap<Tuple<Type, Type>, List<RepoSuiteFilterThread<?>>>();
		Map<Tuple<Type, Type>, List<RepoSuiteTransformerThread<?, ?>>> transformerThreads = new HashMap<Tuple<Type, Type>, List<RepoSuiteTransformerThread<?, ?>>>();
		Map<Tuple<Type, Type>, List<RepoSuiteSinkThread<?>>> sinkThreads = new HashMap<Tuple<Type, Type>, List<RepoSuiteSinkThread<?>>>();
		Tuple<Type, Type> tuple;
		
		for (RepoSuiteThread thread : this.threads.getThreads()) {
			if (thread instanceof RepoSuiteSourceThread) {
				RepoSuiteSourceThread<?> sourceThread = (RepoSuiteSourceThread<?>) thread;
				Type c = getOutputClassType(sourceThread);
				tuple = new Tuple<Type, Type>(c, c);
				if (!sourceThreads.containsKey(tuple)) {
					sourceThreads.put(tuple, new LinkedList<RepoSuiteSourceThread<?>>());
				}
				sourceThreads.get(tuple).add(sourceThread);
			} else if (thread instanceof RepoSuiteFilterThread) {
				RepoSuiteFilterThread<?> filterThread = (RepoSuiteFilterThread<?>) thread;
				Type c = getInputClassType(filterThread);
				tuple = new Tuple<Type, Type>(c, c);
				if (!filterThreads.containsKey(tuple)) {
					filterThreads.put(tuple, new LinkedList<RepoSuiteFilterThread<?>>());
				}
				filterThreads.get(tuple).add(filterThread);
			} else if (thread instanceof RepoSuiteTransformerThread) {
				RepoSuiteTransformerThread<?, ?> transformerThread = (RepoSuiteTransformerThread<?, ?>) thread;
				tuple = new Tuple<Type, Type>(getInputClassType(transformerThread),
				                              getOutputClassType(transformerThread));
				if (!transformerThreads.containsKey(tuple)) {
					transformerThreads.put(tuple, new LinkedList<RepoSuiteTransformerThread<?, ?>>());
				}
				transformerThreads.get(tuple).add(transformerThread);
			} else if (thread instanceof RepoSuiteSinkThread) {
				RepoSuiteSinkThread<?> sinkThread = (RepoSuiteSinkThread<?>) thread;
				Type c = getInputClassType(sinkThread);
				tuple = new Tuple<Type, Type>(c, c);
				if (!sinkThreads.containsKey(tuple)) {
					sinkThreads.put(tuple, new LinkedList<RepoSuiteSinkThread<?>>());
				}
				sinkThreads.get(tuple).add(sinkThread);
			}
		}
		
		if (Logger.logInfo()) {
			Logger.info("Invoking graph builder.");
		}
		
		if (sourceThreads.keySet().size() != 1) {
			
			if (Logger.logError()) {
				Logger.error("Currently, only simple graphs are supported (using only 1 class of source). Given: ");
				for (List<RepoSuiteSourceThread<?>> list : sourceThreads.values()) {
					for (RepoSuiteSourceThread<?> t : list) {
						Logger.error(t.getName());
					}
				}
				
			}
			throw new Shutdown();
		}
		
		if (sinkThreads.keySet().size() != 1) {
			
			if (Logger.logError()) {
				Logger.error("Currently, only simple graphs are supported (using only 1 class of sink). Given: ");
				for (List<RepoSuiteSinkThread<?>> list : sinkThreads.values()) {
					for (RepoSuiteSinkThread<?> t : list) {
						Logger.error(t.getName());
					}
				}
			}
			throw new Shutdown();
		}
		
		// BUILD GRAPH
		Tuple<Type, Type> source = sourceThreads.keySet().iterator().next();
		LinkedList<Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>>> graph = new LinkedList<Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>>>();
		Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>> sourceTuple = new Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>>(
		                                                                                                                                        RepoSuiteSourceThread.class,
		                                                                                                                                        source);
		graph.add(sourceTuple);
		LinkedList<Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>>> build = buildGraph(source.getSecond(),
		                                                                                          filterThreads.keySet(),
		                                                                                          transformerThreads.keySet(),
		                                                                                          sinkThreads.keySet());
		if (build != null) {
			graph.addAll(build);
			
			LinkedList<? extends RepoSuiteThread> previousList = null;
			LinkedList<? extends RepoSuiteThread> currentList = null;
			for (Tuple<Class<? extends RepoSuiteThread>, Tuple<Type, Type>> element : graph) {
				if (element.getFirst().equals(RepoSuiteSourceThread.class)) {
					currentList = (LinkedList<? extends RepoSuiteThread>) sourceThreads.get(element.getSecond());
				} else if (element.getFirst().equals(RepoSuiteFilterThread.class)) {
					currentList = (LinkedList<? extends RepoSuiteThread>) filterThreads.get(element.getSecond());
				} else if (element.getFirst().equals(RepoSuiteTransformerThread.class)) {
					currentList = (LinkedList<? extends RepoSuiteThread>) transformerThreads.get(element.getSecond());
				} else if (element.getFirst().equals(RepoSuiteSinkThread.class)) {
					currentList = (LinkedList<? extends RepoSuiteThread>) sinkThreads.get(element.getSecond());
				}
				if (previousList != null) {
					for (RepoSuiteThread current : currentList) {
						for (RepoSuiteThread previous : previousList) {
							current.connectInput(previous);
						}
					}
				}
				
				previousList = currentList;
			}
		} else {
			if (Logger.logError()) {
				Logger.error("Could not connect threads: ");
				Logger.error("Sources: " + JavaUtils.mapToString(sourceThreads));
				Logger.error("Filters: " + JavaUtils.mapToString(filterThreads));
				Logger.error("Transformers: " + JavaUtils.mapToString(transformerThreads));
				Logger.error("Sinks: " + JavaUtils.mapToString(sinkThreads));
			}
			throw new Shutdown();
		}
	}
	
	/**
	 * this method invokes the graph builder (which builds the graph and
	 * connects the threads) and starts all threads afterwards.
	 */
	public void execute() {
		connectThreads();
		
		for (Thread thread : this.threads.getThreads()) {
			thread.start();
		}
		
		for (Thread thread : this.threads.getThreads()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				throw new Shutdown();
			}
		}
	}
	
	/**
	 * @param thread
	 * @return the type of the input chunks of the given thread
	 */
	@SuppressWarnings ("rawtypes")
	private Type getInputClassType(final RepoSuiteThread thread) {
		ParameterizedType type = (ParameterizedType) thread.getClass().getGenericSuperclass();
		return type.getActualTypeArguments()[0];
	}
	
	/**
	 * @param thread
	 * @return the type of the output chunks of the given thread
	 */
	@SuppressWarnings ("rawtypes")
	private Type getOutputClassType(final RepoSuiteThread thread) {
		ParameterizedType type = (ParameterizedType) thread.getClass().getGenericSuperclass();
		return (type.getActualTypeArguments().length > 1
		                                                ? type.getActualTypeArguments()[1]
		                                                : type.getActualTypeArguments()[0]);
	}
	
	/**
	 * @return the inner thread group
	 */
	public RepoSuiteThreadGroup getThreadGroup() {
		return this.threads;
	}
	
	/**
	 * shuts down all threads
	 */
	public void shutdown() {
		if (Logger.logError()) {
			Logger.error("Terminating " + this.threads.activeCount() + " threads.");
		}
		
		this.threads.shutdown();
		
		FileUtils.shutdown();
		System.exit(0);
	}
}
