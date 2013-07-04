/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.mappings.utils.graph;

import org.mozkito.mappings.mappable.model.MappableEntity;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class MappingsGraph {
	
	public static final String TYPE_KEY    = "type";
	
	public static final String FIXES_LABEL = "fixes";
	
	/**
	 * Gets the graph id.
	 * 
	 * @param entity
	 *            the entity
	 * @return the graph id
	 */
	public static final String getGraphID(final MappableEntity entity) {
		return entity.getBaseType().getCanonicalName() + "##" + entity.getId();
	}
	
	public static final String getVertexType(final MappableEntity entity) {
		return entity.getClassName();
	}
}
