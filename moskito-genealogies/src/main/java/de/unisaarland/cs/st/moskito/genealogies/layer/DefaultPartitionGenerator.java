package de.unisaarland.cs.st.moskito.genealogies.layer;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.PartitionGenerator;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public class DefaultPartitionGenerator implements
PartitionGenerator<Collection<JavaChangeOperation>, Collection<Collection<JavaChangeOperation>>> {
	
	public <T> DefaultPartitionGenerator(ChangeGenealogy<T> genealogy) {
		//TODO extract the partition table (if exists) from the directory within
		//the CoreChaneGenealogy
	}
	
	@Override
	public Collection<Collection<JavaChangeOperation>> partition(Collection<JavaChangeOperation> input) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
