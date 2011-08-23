package de.unisaarland.cs.st.reposuite.mapping.selectors;

import java.util.List;

import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.mapping.register.Registered;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public abstract class MappingSelector extends Registered {
	
	/**
	 * @param element
	 * @return
	 */
	public abstract <T extends MappableEntity> List<T> parse(MappableEntity element, Class<T> targetType);
	
}
