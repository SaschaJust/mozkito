/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

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

import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepoSuiteThreadPool {
	
	private final RepoSuiteThreadGroup threads;
	
	public RepoSuiteThreadPool(final String name) {
		this.threads = new RepoSuiteThreadGroup(name);
	}
	
	@SuppressWarnings ("unchecked")
	private LinkedList<Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object>> buildGraph(final Type source,
	        Set<Type> filterSet, Set<Tuple<Type, Type>> transformerSet, Set<Type> sinkSet) {
		LinkedList<Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object>> list = new LinkedList<Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object>>();
		
		if (filterSet.contains(source)) {
			filterSet = new HashSet<Type>(filterSet);
			filterSet.remove(source);
			
			LinkedList<Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object>> graph = buildGraph(source, filterSet,
			        transformerSet, sinkSet);
			if (graph != null) {
				list.add(new Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object>(
				        (Class<? extends RepoSuiteThread<?, ?>>) RepoSuiteFilterThread.class, source));
				list.addAll(graph);
				return list;
			} else {
				filterSet.add(source);
			}
			
		}
		
		HashSet<Tuple<Type, Type>> validTransformer = new HashSet<Tuple<Type, Type>>(CollectionUtils.select(
		        transformerSet, new Predicate() {
			        
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
				
				LinkedList<Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object>> graph = buildGraph(newSource,
				        filterSet, transformerSet, sinkSet);
				
				if (graph != null) {
					list.add(new Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object>(
					        (Class<? extends RepoSuiteThread<?, ?>>) RepoSuiteTransformerThread.class, tuple));
					list.addAll(graph);
					return list;
				} else {
					transformerSet.add(tuple);
				}
			}
		}
		
		if (sinkSet.contains(source)) {
			sinkSet = new HashSet<Type>(sinkSet);
			sinkSet.remove(source);
			
			LinkedList<Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object>> graph = buildGraph(source, filterSet,
			        transformerSet, sinkSet);
			if (graph != null) {
				list.add(new Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object>(
				        (Class<? extends RepoSuiteThread<?, ?>>) RepoSuiteSinkThread.class, source));
				list.addAll(graph);
				return list;
			} else {
				sinkSet.add(source);
			}
			
		}
		
		if (filterSet.isEmpty() && transformerSet.isEmpty() && sinkSet.isEmpty()) {
			return new LinkedList<Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object>>();
		} else {
			return null;
		}
		
	}
	
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	private void connectThreads() {
		Map<Type, List<RepoSuiteSourceThread<?>>> sourceThreads = new HashMap<Type, List<RepoSuiteSourceThread<?>>>();
		Map<Type, List<RepoSuiteFilterThread<?>>> filterThreads = new HashMap<Type, List<RepoSuiteFilterThread<?>>>();
		Map<Tuple<Type, Type>, List<RepoSuiteTransformerThread<?, ?>>> transformerThreads = new HashMap<Tuple<Type, Type>, List<RepoSuiteTransformerThread<?, ?>>>();
		Map<Type, List<RepoSuiteSinkThread<?>>> sinkThreads = new HashMap<Type, List<RepoSuiteSinkThread<?>>>();
		Tuple<Type, Type> tuple;
		
		for (RepoSuiteThread<?, ?> thread : this.threads.getThreads()) {
			if (thread instanceof RepoSuiteSourceThread) {
				RepoSuiteSourceThread<?> sourceThread = (RepoSuiteSourceThread<?>) thread;
				Type c = getInputClassType(sourceThread);
				if (!sourceThreads.containsKey(c)) {
					sourceThreads.put(c, new LinkedList<RepoSuiteSourceThread<?>>());
				}
				sourceThreads.get(c).add(sourceThread);
			} else if (thread instanceof RepoSuiteFilterThread) {
				RepoSuiteFilterThread<?> filterThread = (RepoSuiteFilterThread<?>) thread;
				Type c = getInputClassType(filterThread);
				
				if (!filterThreads.containsKey(c)) {
					filterThreads.put(c, new LinkedList<RepoSuiteFilterThread<?>>());
				}
				filterThreads.get(c).add(filterThread);
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
				if (!sinkThreads.containsKey(c)) {
					sinkThreads.put(c, new LinkedList<RepoSuiteSinkThread<?>>());
				}
				sinkThreads.get(c).add(sinkThread);
			}
		}
		
		if (Logger.logInfo()) {
			Logger.info("Invoking graph builder.");
		}
		
		if (sourceThreads.keySet().size() != 1) {
			
			if (Logger.logError()) {
				Logger.error("Currently, only simple graphs are supported (using only 1 class of source). Given: "
				        + JavaUtils.collectionToString(sourceThreads.keySet()));
			}
			throw new RuntimeException();
		}
		
		if (sinkThreads.keySet().size() != 1) {
			
			if (Logger.logError()) {
				Logger.error("Currently, only simple graphs are supported (using only 1 class of source). Given: "
				        + JavaUtils.collectionToString(sinkThreads.keySet()));
			}
			throw new RuntimeException();
		}
		
		// BUILD GRAPH
		Type source = sourceThreads.keySet().iterator().next();
		LinkedList<Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object>> graph = new LinkedList<Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object>>();
		Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object> sourceTuple = new Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object>(
		        (Class<? extends RepoSuiteThread<?, ?>>) RepoSuiteSourceThread.class, source);
		graph.add(sourceTuple);
		LinkedList<Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object>> build = buildGraph(source,
		        filterThreads.keySet(), transformerThreads.keySet(), sinkThreads.keySet());
		graph.addAll(build);
		System.err.println(graph);
		
		LinkedList<? extends RepoSuiteThread<?, ?>> previousList = null;
		LinkedList<? extends RepoSuiteThread<?, ?>> currentList = null;
		for (Tuple<Class<? extends RepoSuiteThread<?, ?>>, Object> element : graph) {
			if (element.getFirst().equals(RepoSuiteSourceThread.class)) {
				currentList = (LinkedList<? extends RepoSuiteThread<?, ?>>) sourceThreads.get(element.getSecond());
			} else if (element.getFirst().equals(RepoSuiteFilterThread.class)) {
				currentList = (LinkedList<? extends RepoSuiteThread<?, ?>>) filterThreads.get(element.getSecond());
			} else if (element.getFirst().equals(RepoSuiteTransformerThread.class)) {
				currentList = (LinkedList<? extends RepoSuiteThread<?, ?>>) transformerThreads.get(element.getSecond());
			} else if (element.getFirst().equals(RepoSuiteSinkThread.class)) {
				currentList = (LinkedList<? extends RepoSuiteThread<?, ?>>) sinkThreads.get(element.getSecond());
			}
			if (previousList != null) {
				for (RepoSuiteThread<?, ?> current : currentList) {
					for (RepoSuiteThread<?, ?> previous : previousList) {
						current.connectInput((RepoSuiteGeneralThread) previous);
					}
				}
			}
			
			previousList = currentList;
		}
		
	}
	
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
				throw new RuntimeException();
			}
		}
	}
	
	private Type getInputClassType(final RepoSuiteThread<?, ?> thread) {
		ParameterizedType type = (ParameterizedType) thread.getClass().getGenericSuperclass();
		return type.getActualTypeArguments()[0];
	}
	
	private Type getOutputClassType(final RepoSuiteThread<?, ?> thread) {
		ParameterizedType type = (ParameterizedType) thread.getClass().getGenericSuperclass();
		return type.getActualTypeArguments()[1];
	}
	
	/**
	 * @return
	 */
	public RepoSuiteThreadGroup getThreadGroup() {
		return this.threads;
	}
	
	public void shutdown() {
		if (Logger.logError()) {
			Logger.error("Terminating " + this.threads.activeCount() + " threads.");
		}
		
		this.threads.shutdown();
	}
}
