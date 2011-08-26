package de.unisaarland.cs.st.reposuite.mapping.requirements;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.reposuite.mapping.mappable.FieldKey;

public class Atom extends Expression {
	
	private final int           idx;
	private Class<?>            type;
	private final Set<FieldKey> keys = new HashSet<FieldKey>();
	
	public Atom(int idx, Class<?> type) {
		this.idx = idx;
		this.type = type;
	}
	
	public Atom(int idx, FieldKey key) {
		this.idx = idx;
		keys.add(key);
	}
	
	public Atom(int idx, FieldKey... keys) {
		this.idx = idx;
		CollectionUtils.addAll(this.keys, keys);
	}
	
	public int getIdx() {
		return idx;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public Set<FieldKey> getKeys() {
		return keys;
	}
}
