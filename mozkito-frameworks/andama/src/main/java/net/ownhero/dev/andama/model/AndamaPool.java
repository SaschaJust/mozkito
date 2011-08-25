/**
 * 
 */
package net.ownhero.dev.andama.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.threads.AndamaFilter;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSink;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.AndamaThread;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.andama.utils.AndamaUtils;
import net.ownhero.dev.ioda.Tuple;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The thread pool manages all threads of the tool chain. Since all
 * {@link AndamaThread}s have to register themselves in the
 * {@link AndamaGroup} which is owned by the
 * {@link AndamaPool}, the thread pool has control over all threads of
 * the tool chain. Additionally, this class automatically generates a connected
 * graph for the registered threads. See the corresponding methods for details.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class AndamaPool {
	
	Logger                    logger = LoggerFactory.getLogger(this.getClass());
	
	private final AndamaGroup threads;
	
	/**
	 * {@link AndamaPool} constructor. Initializes the
	 * {@link AndamaGroup};
	 * 
	 * @param name
	 *            the name of the {@link AndamaGroup}
	 */
	public AndamaPool(final String name, final AndamaChain toolchain) {
		this.threads = new AndamaGroup(name, toolchain);
	}
	
	/**
	 * This method builds a graph consisting of nodes mapping a
	 * {@link AndamaThread} class, e.g. {@link AndamaSource}, to a
	 * type determining the input output connectors of the corresponding
	 * threads.
	 * 
	 * @param source
	 * @param filterSet
	 * @param transformerSet
	 * @param sinkSet
	 * @return a {@link List} of {@link Tuple} containing a
	 *         {@link AndamaThread} and a {@link Type} which suffices to
	 *         classify the corresponding {@link AndamaThread}.
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	private LinkedList<Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>>> buildGraph(final Type source,
	                                                                                       Set<Tuple<Type, Type>> filterSet,
	                                                                                       Set<Tuple<Type, Type>> transformerSet,
	                                                                                       Set<Tuple<Type, Type>> sinkSet) {
		LinkedList<Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>>> list = new LinkedList<Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>>>();
		
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
				
				LinkedList<Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>>> graph = buildGraph(newSource,
				                                                                                       filterSet,
				                                                                                       transformerSet,
				                                                                                       sinkSet);
				
				if (graph != null) {
					list.add(new Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>>(AndamaFilter.class, tuple));
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
				
				LinkedList<Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>>> graph = buildGraph(newSource,
				                                                                                       filterSet,
				                                                                                       transformerSet,
				                                                                                       sinkSet);
				
				if (graph != null) {
					list.add(new Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>>(AndamaTransformer.class, tuple));
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
				
				LinkedList<Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>>> graph = buildGraph(newSource,
				                                                                                       filterSet,
				                                                                                       transformerSet,
				                                                                                       sinkSet);
				
				if (graph != null) {
					list.add(new Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>>(AndamaSink.class, tuple));
					list.addAll(graph);
					return list;
				} else {
					sinkSet.add(tuple);
				}
			}
		}
		
		if (filterSet.isEmpty() && transformerSet.isEmpty() && sinkSet.isEmpty()) {
			return new LinkedList<Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>>>();
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
		Map<Tuple<Type, Type>, List<AndamaSource<?>>> sourceThreads = new HashMap<Tuple<Type, Type>, List<AndamaSource<?>>>();
		Map<Tuple<Type, Type>, List<AndamaFilter<?>>> filterThreads = new HashMap<Tuple<Type, Type>, List<AndamaFilter<?>>>();
		Map<Tuple<Type, Type>, List<AndamaTransformer<?, ?>>> transformerThreads = new HashMap<Tuple<Type, Type>, List<AndamaTransformer<?, ?>>>();
		Map<Tuple<Type, Type>, List<AndamaSink<?>>> sinkThreads = new HashMap<Tuple<Type, Type>, List<AndamaSink<?>>>();
		Tuple<Type, Type> tuple;
		
		for (AndamaThread thread : this.threads.getThreads()) {
			if (thread instanceof AndamaSource) {
				AndamaSource<?> sourceThread = (AndamaSource<?>) thread;
				Type c = getOutputClassType(sourceThread);
				tuple = new Tuple<Type, Type>(c, c);
				if (!sourceThreads.containsKey(tuple)) {
					sourceThreads.put(tuple, new LinkedList<AndamaSource<?>>());
				}
				sourceThreads.get(tuple).add(sourceThread);
			} else if (thread instanceof AndamaFilter) {
				AndamaFilter<?> filterThread = (AndamaFilter<?>) thread;
				Type c = getInputClassType(filterThread);
				tuple = new Tuple<Type, Type>(c, c);
				if (!filterThreads.containsKey(tuple)) {
					filterThreads.put(tuple, new LinkedList<AndamaFilter<?>>());
				}
				filterThreads.get(tuple).add(filterThread);
			} else if (thread instanceof AndamaTransformer) {
				AndamaTransformer<?, ?> transformerThread = (AndamaTransformer<?, ?>) thread;
				tuple = new Tuple<Type, Type>(getInputClassType(transformerThread),
				                              getOutputClassType(transformerThread));
				if (!transformerThreads.containsKey(tuple)) {
					transformerThreads.put(tuple, new LinkedList<AndamaTransformer<?, ?>>());
				}
				transformerThreads.get(tuple).add(transformerThread);
			} else if (thread instanceof AndamaSink) {
				AndamaSink<?> sinkThread = (AndamaSink<?>) thread;
				Type c = getInputClassType(sinkThread);
				tuple = new Tuple<Type, Type>(c, c);
				if (!sinkThreads.containsKey(tuple)) {
					sinkThreads.put(tuple, new LinkedList<AndamaSink<?>>());
				}
				sinkThreads.get(tuple).add(sinkThread);
			}
		}
		
		if (this.logger.isInfoEnabled()) {
			this.logger.info("Invoking graph builder.");
		}
		
		if (sourceThreads.keySet().size() != 1) {
			
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Currently, only simple graphs are supported (using only 1 class of source). Given: ");
				for (List<AndamaSource<?>> list : sourceThreads.values()) {
					for (AndamaSource<?> t : list) {
						this.logger.error(t.getName());
					}
				}
				
			}
			throw new Shutdown();
		}
		
		if (sinkThreads.keySet().size() != 1) {
			
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Currently, only simple graphs are supported (using only 1 class of sink). Given: ");
				for (List<AndamaSink<?>> list : sinkThreads.values()) {
					for (AndamaSink<?> t : list) {
						this.logger.error(t.getName());
					}
				}
			}
			throw new Shutdown();
		}
		
		// BUILD GRAPH
		Tuple<Type, Type> source = sourceThreads.keySet().iterator().next();
		LinkedList<Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>>> graph = new LinkedList<Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>>>();
		Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>> sourceTuple = new Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>>(
		                                                                                                                                  AndamaSource.class,
		                                                                                                                                  source);
		graph.add(sourceTuple);
		LinkedList<Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>>> build = buildGraph(source.getSecond(),
		                                                                                       filterThreads.keySet(),
		                                                                                       transformerThreads.keySet(),
		                                                                                       sinkThreads.keySet());
		if (build != null) {
			graph.addAll(build);
			
			LinkedList<? extends AndamaThread> previousList = null;
			LinkedList<? extends AndamaThread> currentList = null;
			for (Tuple<Class<? extends AndamaThread>, Tuple<Type, Type>> element : graph) {
				if (element.getFirst().equals(AndamaSource.class)) {
					currentList = (LinkedList<? extends AndamaThread>) sourceThreads.get(element.getSecond());
				} else if (element.getFirst().equals(AndamaFilter.class)) {
					currentList = (LinkedList<? extends AndamaThread>) filterThreads.get(element.getSecond());
				} else if (element.getFirst().equals(AndamaTransformer.class)) {
					currentList = (LinkedList<? extends AndamaThread>) transformerThreads.get(element.getSecond());
				} else if (element.getFirst().equals(AndamaSink.class)) {
					currentList = (LinkedList<? extends AndamaThread>) sinkThreads.get(element.getSecond());
				}
				if (previousList != null) {
					for (AndamaThread current : currentList) {
						for (AndamaThread previous : previousList) {
							current.connectInput(previous);
						}
					}
				}
				
				previousList = currentList;
			}
		} else {
			if (this.logger.isErrorEnabled()) {
				this.logger.error("Could not connect threads: ");
				this.logger.error("Sources: " + AndamaUtils.mapToString(sourceThreads));
				this.logger.error("Filters: " + AndamaUtils.mapToString(filterThreads));
				this.logger.error("Transformers: " + AndamaUtils.mapToString(transformerThreads));
				this.logger.error("Sinks: " + AndamaUtils.mapToString(sinkThreads));
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
				
				if (this.logger.isErrorEnabled()) {
					this.logger.error(e.getMessage(), e);
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
	private Type getInputClassType(final AndamaThread thread) {
		ParameterizedType type = (ParameterizedType) thread.getClass().getGenericSuperclass();
		return type.getActualTypeArguments()[0];
	}
	
	/**
	 * @param thread
	 * @return the type of the output chunks of the given thread
	 */
	@SuppressWarnings ("rawtypes")
	private Type getOutputClassType(final AndamaThread thread) {
		ParameterizedType type = (ParameterizedType) thread.getClass().getGenericSuperclass();
		return (type.getActualTypeArguments().length > 1
		                                                ? type.getActualTypeArguments()[1]
		                                                : type.getActualTypeArguments()[0]);
	}
	
	/**
	 * @return the inner thread group
	 */
	public AndamaGroup getThreadGroup() {
		return this.threads;
	}
	
	/**
	 * shuts down all threads
	 */
	public void shutdown() {
		if (this.logger.isErrorEnabled()) {
			this.logger.error("Terminating " + this.threads.activeCount() + " threads.");
		}
		
		this.threads.shutdown();
		System.exit(0);
	}
}
