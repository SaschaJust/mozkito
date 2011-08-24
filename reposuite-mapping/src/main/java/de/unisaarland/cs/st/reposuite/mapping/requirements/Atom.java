/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.mapping.requirements;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.reposuite.mapping.mappable.FieldKey;

public class Atom extends Expression {
	
	private final Index         idx;
	private Class<?>            type;
	private final Set<FieldKey> keys = new HashSet<FieldKey>();
	
	public Atom(Index idx, Class<?> type) {
		this.idx = idx;
		this.type = type;
	}
	
	public Atom(Index idx, FieldKey key) {
		this.idx = idx;
		keys.add(key);
	}
	
	public Atom(Index idx, FieldKey... keys) {
		this.idx = idx;
		CollectionUtils.addAll(this.keys, keys);
	}
	
	public Index getIdx() {
		return idx;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public Set<FieldKey> getKeys() {
		return keys;
	}
}
