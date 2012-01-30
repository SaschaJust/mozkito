/**
 * 
 */
package de.unisaarland.cs.st.moskito.mapping.engines;

import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.AndamaSettings;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class StacktraceParserEngine extends MappingEngine {
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.Registered#getDescription()
	 */
	@Override
	public String getDescription() {
		// TODO this requires PPA
		return "Analyzes stacktraces with one of the entities to contain JavaElements, that have been touched in the transaction under suspect. "
		        + "Additionally, the change made to the JavaElement has to be a fix.";
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.Registered#init()
	 */
	@Override
	public void init() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.registerable.Registered#register(net.ownhero.dev.andama.settings.AndamaSettings,
	 * net.ownhero.dev.andama.settings.AndamaArgumentSet)
	 */
	@Override
	public void register(final AndamaSettings settings,
	                     final AndamaArgumentSet<?> arguments) {
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#score(de.
	 * unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.model.Mapping)
	 */
	@Override
	public void score(final MappableEntity from,
	                  final MappableEntity to,
	                  final Mapping score) {
		final double confidence = 0;
		
		addFeature(score, confidence, null, null, null, null, null, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new Atom(Index.FROM, FieldKey.BODY);
		
	}
	
}
