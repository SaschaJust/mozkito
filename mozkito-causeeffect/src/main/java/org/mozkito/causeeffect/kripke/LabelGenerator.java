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

package org.mozkito.causeeffect.kripke;

import java.util.Collection;

/**
 * The Interface LabelGenerator.
 * 
 * @param <T>
 *            the generic type
 */
public interface LabelGenerator<T> {
	
	/**
	 * Gets the labels.
	 * 
	 * @param t
	 *            the t
	 * @return the labels
	 */
	public Collection<Label> getLabels(T t);
	
}
