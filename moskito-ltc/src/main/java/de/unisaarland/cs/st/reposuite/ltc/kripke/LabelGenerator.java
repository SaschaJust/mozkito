package de.unisaarland.cs.st.reposuite.ltc.kripke;

import java.util.Collection;

public interface LabelGenerator<T> {
	
	public Collection<Label> getLabels(T t);
	
}
