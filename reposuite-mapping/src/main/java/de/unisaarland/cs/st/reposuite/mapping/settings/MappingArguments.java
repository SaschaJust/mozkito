/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.settings;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.exceptions.WrongClassSearchMethodException;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine;
import de.unisaarland.cs.st.reposuite.mapping.filters.MappingFilter;
import de.unisaarland.cs.st.reposuite.mapping.finder.MappingFinder;
import de.unisaarland.cs.st.reposuite.mapping.register.Registered;
import de.unisaarland.cs.st.reposuite.mapping.selectors.MappingSelector;
import de.unisaarland.cs.st.reposuite.mapping.splitters.MappingSplitter;
import de.unisaarland.cs.st.reposuite.mapping.storages.MappingStorage;
import de.unisaarland.cs.st.reposuite.mapping.strategies.MappingStrategy;
import de.unisaarland.cs.st.reposuite.settings.ListArgument;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MappingArguments extends RepoSuiteArgumentSet {
	
	private final Set<MappingEngine>         engines    = new HashSet<MappingEngine>();
	private final Set<MappingFilter>         filters    = new HashSet<MappingFilter>();
	private final Set<MappingSelector<?, ?>> selectors  = new HashSet<MappingSelector<?, ?>>();
	private final Set<MappingSplitter>       splitters  = new HashSet<MappingSplitter>();
	private final Set<MappingStorage>        storages   = new HashSet<MappingStorage>();
	private final Set<MappingStrategy>       strategies = new HashSet<MappingStrategy>();
	
	/**
	 * @param isRequired 
	 * @param mappingSettings 
	 * 
	 */
	public MappingArguments(final MappingSettings settings, final boolean isRequired) {
		super();
		for (MappingEngine registered : register(settings, isRequired, MappingEngine.class)) {
			this.engines.add(registered);
		}
		
		for (MappingFilter registered : register(settings, isRequired, MappingFilter.class)) {
			this.filters.add(registered);
		}
		
		for (MappingSelector<?, ?> registered : register(settings, isRequired, MappingSelector.class)) {
			this.selectors.add(registered);
		}
		
		for (MappingSplitter registered : register(settings, isRequired, MappingSplitter.class)) {
			this.splitters.add(registered);
		}
		
		for (MappingStrategy registered : register(settings, isRequired, MappingStrategy.class)) {
			this.strategies.add(registered);
		}
	}
	
	/**
	 * @param registereds
	 * @return
	 */
	private String buildRegisteredList(final Collection<Class<Registered>> registereds) {
		StringBuilder builder = new StringBuilder();
		
		int max = 0;
		for (Class<Registered> klass : registereds) {
			max = Math.max(max, klass.getSimpleName().length());
		}
		
		for (Class<Registered> klass : registereds) {
			try {
				builder.append(FileUtils.lineSeparator);
				builder.append('\t').append("  ").append(String.format("%-" + max + "s", klass.getSimpleName()))
				       .append(" : ").append(klass.newInstance().getDescription());
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
		}
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgumentSet#getValue()
	 */
	@Override
	public MappingFinder getValue() {
		MappingFinder finder = new MappingFinder();
		
		for (MappingEngine engine : this.engines) {
			engine.init();
			finder.addEngine(engine);
		}
		
		for (MappingFilter filter : this.filters) {
			filter.init();
			finder.addFilter(filter);
		}
		
		for (MappingSelector<?, ?> selector : this.selectors) {
			selector.init();
			finder.addSelector(selector);
		}
		
		for (MappingSplitter splitter : this.splitters) {
			splitter.init();
			finder.addSplitter(splitter);
		}
		
		for (MappingStorage storage : this.storages) {
			storage.init();
			finder.addStorage(storage);
		}
		
		for (MappingStrategy strategy : this.strategies) {
			strategy.init();
			finder.addStrategy(strategy);
		}
		
		return finder;
	}
	
	/**
	 * @param <T>
	 * @param <K>
	 * @param settings
	 * @param isRequired
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	private <T extends Registered, K extends T> Set<K> register(final MappingSettings settings,
	                                                            final boolean isRequired,
	                                                            final Class<T> clazz) {
		// simpleName startsWith "Mapping
		// superclass == registered
		Set<K> registereds = new HashSet<K>();
		Package package1 = clazz.getPackage();
		try {
			Collection<?> registeredClasses = ClassFinder.getClassesExtendingClass(package1, clazz, Modifier.ABSTRACT
			        | Modifier.INTERFACE | Modifier.PRIVATE);
			
			if (!registeredClasses.isEmpty()) {
				boolean singleton = ((Class<Registered>) registeredClasses.iterator().next()).newInstance().singleton();
				addArgument(new ListArgument(settings, "mapping."
				        + clazz.getSimpleName().replace("Mapping", "").toLowerCase() + "s", "A list of "
				        + clazz.getSimpleName() + " that shall be used: (" + (singleton
				                                                                       ? "choose one"
				                                                                       : "all if none specified") + ")"
				        + buildRegisteredList((Collection<Class<Registered>>) registeredClasses), null, singleton));
				
				String engines = System.getProperty("mapping."
				        + clazz.getSimpleName().replace("Mapping", "").toLowerCase());
				Set<String> engineNames = new HashSet<String>();
				
				if (engines != null) {
					for (String engineName : engines.split(",")) {
						engineNames.add(MappingEngine.class.getPackage().getName() + "." + engineName);
					}
				}
				
				for (Object oklass : registeredClasses) {
					Class<K> klass = (Class<K>) oklass;
					if (engineNames.isEmpty() || engineNames.contains(klass.getCanonicalName())) {
						if ((klass.getModifiers() & Modifier.ABSTRACT) == 0) {
							if (Logger.logInfo()) {
								Logger.info("Adding new " + clazz.getSimpleName() + " " + klass.getCanonicalName());
							}
							
							K engine = klass.newInstance();
							engine.register(settings, this, isRequired);
							for (Class<? extends MappingStorage> storageClass : engine.storageDependency()) {
								MappingStorage storage = storageClass.newInstance();
								storage.register(settings, this, true);
								this.storages.add(storage);
							}
							registereds.add(engine);
						}
					} else {
						if (Logger.logInfo()) {
							Logger.info("Not loading available " + clazz.getSimpleName() + ": " + klass.getSimpleName());
						}
					}
				}
			}
		} catch (ClassNotFoundException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		} catch (WrongClassSearchMethodException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		} catch (IOException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		} catch (InstantiationException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
		return registereds;
	}
	
}
