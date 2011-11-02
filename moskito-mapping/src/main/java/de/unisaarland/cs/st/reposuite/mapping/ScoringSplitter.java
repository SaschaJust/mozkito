/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSink;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.reposuite.mapping.model.FilteredMapping;
import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ScoringSplitter extends AndamaSink<FilteredMapping> {
	
	public ScoringSplitter(final AndamaGroup threadGroup, final AndamaSettings settings, final MappingFinder finder,
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
				
				FilteredMapping data = getInputData();
				
				this.list.addAll(finder.split(data));
				
				for (Annotated annotated : this.list) {
					
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
