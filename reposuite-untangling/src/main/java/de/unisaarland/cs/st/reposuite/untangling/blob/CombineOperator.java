package de.unisaarland.cs.st.reposuite.untangling.blob;


public interface CombineOperator<T> {
	
	public boolean canBeCombined(T t1,
	                             T t2);
}
