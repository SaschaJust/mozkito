/**
 * 
 */
package de.unisaarland.cs.st.reposuite.infozilla.settings;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.infozilla.filters.InfozillaFilter;
import de.unisaarland.cs.st.reposuite.infozilla.filters.InfozillaFilterChain;
import de.unisaarland.cs.st.reposuite.settings.ListArgument;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet;
import de.unisaarland.cs.st.reposuite.utils.ClassFinder;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class InfozillaArguments extends RepoSuiteArgumentSet {
	
	private final Set<InfozillaFilter> filters = new HashSet<InfozillaFilter>();
	
	public InfozillaArguments(final InfozillaSettings settings, final boolean isRequired) {
		super();
		
		try {
			Package package1 = InfozillaFilter.class.getPackage();
			Collection<Class<?>> classesExtendingClass = ClassFinder.getClassesExtendingClass(package1,
			                                                                                  InfozillaFilter.class);
			
			addArgument(new ListArgument(settings, "mapping.filters", "A list of mapping filters that shall be used.",
			                             buildFilterList(classesExtendingClass), false));
			
			String filters = System.getProperty("mapping.filters");
			Set<String> filterNames = new HashSet<String>();
			
			if (filters != null) {
				for (String filterName : filters.split(",")) {
					filterNames.add(InfozillaFilter.class.getPackage().getName() + "." + filterName);
				}
				
			}
			
			for (Class<?> klass : classesExtendingClass) {
				if (filterNames.isEmpty() || filterNames.contains(klass.getCanonicalName())) {
					if (Logger.logInfo()) {
						Logger.info("Adding new InfozillaFilter " + klass.getCanonicalName());
					}
					
					Constructor<?> constructor = klass.getConstructor(InfozillaSettings.class);
					InfozillaFilter filter = (InfozillaFilter) constructor.newInstance(settings);
					filter.register(settings, this, isRequired);
					this.filters.add(filter);
				} else {
					if (Logger.logInfo()) {
						Logger.info("Not loading available filter: " + klass.getSimpleName());
					}
				}
			}
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new RuntimeException();
		}
	}
	
	/**
	 * @param filters
	 * @return
	 */
	private String buildFilterList(final Collection<Class<?>> filters) {
		StringBuilder builder = new StringBuilder();
		for (Class<?> klass : filters) {
			if (builder.length() != 0) {
				builder.append(",");
			}
			builder.append(klass.getSimpleName());
		}
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public InfozillaFilterChain getValue() {
		InfozillaFilterChain chain = new InfozillaFilterChain();
		
		for (InfozillaFilter filter : this.filters) {
			filter.init();
			chain.addFilter(filter);
		}
		
		return chain;
	}
	
}