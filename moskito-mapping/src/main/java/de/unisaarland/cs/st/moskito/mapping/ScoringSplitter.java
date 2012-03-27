/**
 * 
 */
package de.unisaarland.cs.st.moskito.mapping;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.moskito.mapping.model.FilteredMapping;
import de.unisaarland.cs.st.moskito.persistence.Annotated;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class ScoringSplitter.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class ScoringSplitter extends Sink<FilteredMapping> {
	
	/**
	 * Instantiates a new scoring splitter.
	 *
	 * @param threadGroup the thread group
	 * @param settings the settings
	 * @param finder the finder
	 * @param persistenceUtil the persistence util
	 */
	public ScoringSplitter(final Group threadGroup, final Settings settings, final MappingFinder finder,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<FilteredMapping, FilteredMapping>(this) {
			
			@Override
			public void preExecution() {
				persistenceUtil.beginTransaction();
				
			}
		};
		
		new PostExecutionHook<FilteredMapping, FilteredMapping>(this) {
			
			@Override
			public void postExecution() {
				persistenceUtil.commitTransaction();
				persistenceUtil.shutdown();
			}
			
		};
		
		new ProcessHook<FilteredMapping, FilteredMapping>(this) {
			
			int             i    = 0;
			List<Annotated> list = new LinkedList<Annotated>();
			
			@Override
			public void process() {
				if (Logger.logDebug()) {
					Logger.debug("Split analyzing " + getInputData());
				}
				
				final FilteredMapping data = getInputData();
				
				this.list.addAll(finder.split(data, persistenceUtil));
				
				for (final Annotated annotated : this.list) {
					
					if ((++this.i % 50) == 0) {
						persistenceUtil.commitTransaction();
						persistenceUtil.beginTransaction();
					}
					
					persistenceUtil.save(annotated);
				}
			}
		};
	}
	
}
