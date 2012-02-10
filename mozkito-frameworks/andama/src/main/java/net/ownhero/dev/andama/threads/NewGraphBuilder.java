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
import java.util.ListIterator;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class NewGraphBuilder {
	
	private final HashMap<Class<? extends AndamaThread<?, ?>>, HashMap<Class<?>, LinkedList<AndamaThreadable<?, ?>>>> inputTypeMap  = new HashMap<Class<? extends AndamaThread<?, ?>>, HashMap<Class<?>, LinkedList<AndamaThreadable<?, ?>>>>();
	private final HashMap<Class<? extends AndamaThread<?, ?>>, HashMap<Class<?>, LinkedList<AndamaThreadable<?, ?>>>> outputTypeMap = new HashMap<Class<? extends AndamaThread<?, ?>>, HashMap<Class<?>, LinkedList<AndamaThreadable<?, ?>>>>();
	private  final HashSet<AndamaThreadable<?, ?>> processedNodes = new HashSet<AndamaThreadable<?,?>>();
	private  final HashSet<AndamaThreadable<?, ?>> openNodes = new HashSet<AndamaThreadable<?,?>>();
	
	}
	
	public NewGraphBuilder(final AndamaGroup group) {
		for (final AndamaThreadable<?, ?> thread : group.getThreads()) {
			fillMap(thread, thread.getInputType(), thread.getBaseType(), this.inputTypeMap);
			fillMap(thread, thread.getOutputType(), thread.getBaseType(), this.outputTypeMap);
		}
		this.openNodes.addAll(group.getThreads());
	}
	public void buildGraph() {
		AndamaThreadable<?, ?> thread = null;
		if (this.inputTypeMap.containsKey(AndamaMultiplexer.class)) {
			for (final List<AndamaThreadable<?, ?>> list : this.inputTypeMap.get(AndamaMultiplexer.class).values()) {
				final ListIterator<AndamaThreadable<?, ?>> iterator = list.listIterator();
				while (iterator.hasNext()) {
				thread = iterator.next();
					if (this.outputTypeMap.containsKey(AndamaFilter.class)) {
						final HashMap<Class<?>, LinkedList<AndamaThreadable<?, ?>>> map = this.outputTypeMap.get(AndamaFilter.class);
						if (map.containsKey(thread.getInputType())) {
							for (final AndamaThreadable<?, ?> thread2 : map.get(thread.getInputType())) {
								thread.connectInput(thread2);
								thread = thread2;
							}
						}
					}
				}
			}
			}
		}
	
	private void fillMap(final AndamaThreadable<?, ?> thread,
	                     final Class<?> dataType,
	                     final Class<? extends AndamaThread<?, ?>> nodeType,
	                     final HashMap<Class<? extends AndamaThread<?, ?>>, HashMap<Class<?>, LinkedList<AndamaThreadable<?, ?>>>> map) {
		if (!map.containsKey(thread.getBaseType())) {
			map.put(thread.getBaseType(), new HashMap<Class<?>, LinkedList<AndamaThreadable<?, ?>>>());
		}
		
		final HashMap<Class<?>, LinkedList<AndamaThreadable<?, ?>>> innerMap = map.get(thread.getBaseType());
		
		if (!innerMap.containsKey(thread.getInputType())) {
			innerMap.put(thread.getInputType(), new LinkedList<AndamaThreadable<?, ?>>());
		}
		
		innerMap.get(thread.getInputType()).add(thread);
	}
	
}
