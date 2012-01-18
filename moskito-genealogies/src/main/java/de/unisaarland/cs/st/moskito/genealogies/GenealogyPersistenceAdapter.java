package de.unisaarland.cs.st.moskito.genealogies;

import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public interface GenealogyPersistenceAdapter {
	
	public JavaChangeOperation loadById(final long id, Class<? extends JavaChangeOperation> clazz);
	
}
