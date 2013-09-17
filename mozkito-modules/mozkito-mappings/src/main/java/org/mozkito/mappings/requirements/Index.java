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
package org.mozkito.mappings.requirements;


/**
 * This enum represents the index that is used in {@link Expression}s to determine relations between 'from' and 'to'
 * entities of {@link org.mozkito.persistence.Entity}.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public enum Index {
	
	/** the from entity. */
	FROM,
	
	/** the one entity (in contrast to the 'OTHER' entity). */
	ONE,
	
	/** the other entity (in contrast to the 'ONE' entity). */
	OTHER,
	
	/** the to entity. */
	TO;
}
