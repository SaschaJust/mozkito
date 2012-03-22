/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
 *******************************************************************************/

package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.util.Collection;

/**
 * The Interface GenealogyMetric.
 *
 * @param <T> the generic type
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public interface GenealogyMetric<T> {
	
	/**
	 * Gets the metric names.
	 *
	 * @return the metric names
	 */
	public abstract Collection<String> getMetricNames();
	
	/**
	 * Handle.
	 *
	 * @param item the item
	 * @return the collection
	 */
	public abstract Collection<GenealogyMetricValue> handle(T item);
}
