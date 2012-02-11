/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.andama.settings;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.exceptions.SettingsParseError;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.arguments.ListArgument;
import net.ownhero.dev.andama.settings.arguments.StringArgument;
import net.ownhero.dev.andama.settings.registerable.ArgumentProvider;
import net.ownhero.dev.andama.settings.requirements.Contains;
import net.ownhero.dev.andama.settings.requirements.Equals;
import net.ownhero.dev.andama.settings.requirements.Required;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 * @param <T>
 */
public abstract class ArgumentSet<T> implements IArgument<T> {
	
	public static <T> Collection<T> provideDynamicArguments(final ArgumentSet<?> argumentSet,
	                                                        final Class<T> superClass,
	                                                        final String description,
	                                                        final Requirement requirement,
	                                                        final String defaultValue,
	                                                        final String moduleName,
	                                                        final String provideGroupName,
	                                                        final boolean multiEnable) throws ArgumentRegistrationException {
		try {
			final Collection<T> instances = new LinkedList<T>();
			final Collection<Class<T>> classes = ClassFinder.getClassesOfInterface(superClass.getPackage(), superClass,
			                                                                       Modifier.ABSTRACT
			                                                                               | Modifier.INTERFACE
			                                                                               | Modifier.PRIVATE
			                                                                               | Modifier.PROTECTED);
			
			final DynamicArgumentSet<Boolean> set = new DynamicArgumentSet<Boolean>(argumentSet, "Arguments",
			        description, requirement, provideGroupName, provideGroupName) {
				
				@Override
				protected boolean init() {
					return true;
				}
			};
			
			final StringBuilder validArguments = new StringBuilder();
			for (final Class<?> c : classes) {
				validArguments.append(c.getSimpleName());
			}
			
			ListArgument listArgument = null;
			StringArgument stringArgument = null;
			
			if (multiEnable) {
				listArgument = new ListArgument(set, moduleName.toLowerCase() + "." + provideGroupName.toLowerCase(),
				                                "Enables " + provideGroupName + " in the " + moduleName
				                                        + " module. Valid arguments: " + validArguments, defaultValue,
				                                requirement);
			} else {
				stringArgument = new StringArgument(set, moduleName.toLowerCase() + "."
				        + provideGroupName.toLowerCase(), "Enables " + provideGroupName + " in the " + moduleName
				        + " module. Valid arguments: " + validArguments, defaultValue, requirement);
			}
			
			for (final Class<?> c : classes) {
				DynamicArgumentSet<Boolean> specificSet = null;
				if (multiEnable) {
					if (listArgument != null) {
						specificSet = new DynamicArgumentSet<Boolean>(set, c.getSimpleName(),
						        "Bundles the settings for " + c.getSimpleName(), new Contains(listArgument,
						                                                                      c.getSimpleName()),
						        provideGroupName, provideGroupName) {
							
							@Override
							protected boolean init() {
								return true;
							}
						};
					}
				} else {
					specificSet = new DynamicArgumentSet<Boolean>(set, c.getSimpleName(), "Bundles the settings for "
					        + c.getSimpleName(), new Equals(stringArgument, c.getSimpleName()), provideGroupName,
					        provideGroupName) {
						
						@Override
						protected boolean init() {
							return true;
						}
					};
				}
				
				@SuppressWarnings ("unchecked")
				final T o = (T) c.newInstance();
				final Method method = c.getMethod(ArgumentProvider.class.getMethods()[0].getName(),
				                                  DynamicArgumentSet.class);
				method.invoke(o, specificSet);
				instances.add(o);
			}
			
			return instances;
		} catch (final Exception e) {
			throw new UnrecoverableError(e);
		}
		
	}
	
	private final HashMap<String, Argument<?>>    arguments    = new HashMap<String, Argument<?>>();
	private final HashMap<String, ArgumentSet<?>> argumentSets = new HashMap<String, ArgumentSet<?>>();
	private final String                          description;
	private final Requirement                     requirements;
	private final Settings                        settings;
	private boolean                               init         = false;
	
	private T                                     cachedValue  = null;
	
	/**
	 * @throws ArgumentRegistrationException
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 */
	public ArgumentSet(final ArgumentSet<?> argumentSet, final String description, final Requirement requirements)
	        throws ArgumentRegistrationException {
		this.description = description;
		this.settings = argumentSet.getSettings();
		if (!argumentSet.addArgument(this)) {
			throw new ArgumentRegistrationException("Could not register argument set " + getHandle() + ".");
		}
		this.requirements = requirements;
	}
	
	/**
	 * @param settings
	 * @param description
	 */
	ArgumentSet(final Settings settings, final String description) {
		this.settings = settings;
		this.description = description;
		this.requirements = new Required();
		getSettings().addArgument(this);
	}
	
	/**
	 * Call this method to add an argument to the set of arguments. But be aware that you have to set all arguments
	 * before adding it to the MinerSettings!
	 * 
	 * @param argument
	 *            MinerArgument to be added
	 * @return <code>true</code> if the argument could be added. <code>false</code> otherwise.
	 */
	protected boolean addArgument(@NotNull final Argument<?> argument) {
		if (!getSettings().frozen()) {
			if (getSettings().hasSetting(argument.getName())) {
				// TODO Warn log
				return false;
			}
			
			if (this.arguments.containsKey(argument.getName())) {
				// TODO Warn log
				return false;
			}
			
			this.arguments.put(argument.getName(), argument);
			
			// tell settings who is responsible for this artifact
			getSettings().addArgumentMapping(argument.getName(), this);
			
			return true;
		} else {
			// TODO Warn log
			return false;
		}
	}
	
	public final boolean addArgument(@NotNull final ArgumentSet<?> argument) {
		if (!getSettings().frozen()) {
			if (getSettings().hasSetting(argument.getName())) {
				// TODO Warn log
				return false;
			}
			
			if (this.argumentSets.containsKey(argument.getName())) {
				// TODO Warn log
				return false;
			}
			
			this.argumentSets.put(argument.getName(), argument);
			
			// // tell settings who is responsible for this artifact
			// getSettings().addArgumentMapping(argument.getName(), this);
			
			return true;
		} else {
			// TODO Warn log
			return false;
		}
	}
	
	/**
	 * @param arg0
	 * @return
	 */
	@Override
	public final int compareTo(final IArgument<?> arg0) {
		if (this == arg0) {
			return 0;
		}
		
		final Set<IArgument<?>> dependencies = getDependencies();
		if (dependencies.contains(arg0)) {
			return 1;
		} else if (dependencies.contains(this)) {
			return 0;
		} else {
			int ret = -1;
			for (final IArgument<?> argX : dependencies) {
				ret = argX.compareTo(arg0);
				if (ret != 0) {
					return ret;
				}
			}
			return ret;
		}
	}
	
	/**
	 * @param name
	 * @return
	 */
	public final Argument<?> getArgument(final String name) {
		return getArguments().get(name);
	}
	
	/**
	 * Return the arguments held within the set.
	 * 
	 * @return
	 */
	Map<String, Argument<?>> getArguments() {
		return this.arguments;
	}
	
	/**
	 * @return
	 */
	public Map<String, ArgumentSet<?>> getArgumentSets() {
		return this.argumentSets;
	}
	
	/**
	 * @return
	 */
	protected final T getCachedValue() {
		return this.cachedValue;
	}
	
	public Collection<IArgument<?>> getChildren() {
		final LinkedList<IArgument<?>> list = new LinkedList<IArgument<?>>();
		list.addAll(getArguments().values());
		list.addAll(getArgumentSets().values());
		return list;
	}
	
	/**
	 * @return the dependees
	 */
	@Override
	public final Set<IArgument<?>> getDependencies() {
		return this.requirements.getDependencies();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getDescription()
	 */
	@Override
	public final String getDescription() {
		return this.description;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getHandle()
	 */
	@Override
	public final String getHandle() {
		return getClass().getSimpleName();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public final String getHelpString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append("[ ").append(getName()).append(" ]").append(FileUtils.lineSeparator);
		builder.append("|-Description: ").append(getDescription());
		builder.append(required()
		                         ? " (Required, due to " + getRequirements() + ")"
		                         : "");
		
		for (final IArgument<?> argument : getChildren()) {
			builder.append(FileUtils.lineSeparator).append(argument.getHelpString(1));
		}
		
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#toString(int)
	 */
	@Override
	public String getHelpString(final int indentation) {
		final StringBuilder builder = new StringBuilder();
		
		final StringBuilder indent = new StringBuilder();
		final StringBuilder header = new StringBuilder();
		for (int i = 0, j = 0; i < indentation; ++i, ++j) {
			indent.append("| ");
			if ((j + 1) < indentation) {
				header.append("| ");
			}
		}
		
		builder.append(header).append("|-[ ").append(getName()).append(" ]").append(FileUtils.lineSeparator);
		builder.append(header).append("| `-Description: ").append(getDescription());
		builder.append(required()
		                         ? " (Required, due to " + getRequirements() + ")"
		                         : "");
		
		for (final IArgument<?> argument : getChildren()) {
			builder.append(FileUtils.lineSeparator).append(indent).append(argument.getHelpString(indentation + 1));
		}
		
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getKeyValueSpan()
	 */
	@Override
	public Tuple<Integer, Integer> getKeyValueSpan() {
		final Tuple<Integer, Integer> tuple = new Tuple<Integer, Integer>(0, 0);
		
		for (final IArgument<?> arg : getChildren()) {
			final Tuple<Integer, Integer> span = arg.getKeyValueSpan();
			if (span.getFirst() > tuple.getFirst()) {
				tuple.setFirst(span.getFirst());
			}
			
			if (span.getSecond() > tuple.getSecond()) {
				tuple.setSecond(span.getSecond());
			}
		}
		return new Tuple<Integer, Integer>(tuple.getFirst(), tuple.getSecond());
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getName()
	 */
	@Override
	public String getName() {
		return getHandle();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getRequirements()
	 */
	@Override
	public Requirement getRequirements() {
		return this.requirements;
	}
	
	/**
	 * @return the settings
	 */
	@Override
	public final Settings getSettings() {
		return this.settings;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#getValue()
	 */
	@Override
	public final T getValue() {
		if (!this.init) {
			throw new UnrecoverableError("Calling getValue() on " + this.getClass().getSimpleName() + " and instance "
			        + getName() + " before calling init() is not allowed! Please fix your code.");
		}
		return this.getCachedValue();
	}
	
	/**
	 * @return
	 */
	protected abstract boolean init();
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#isInitialized()
	 */
	@Override
	public final boolean isInitialized() {
		return this.init;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#parse()
	 */
	@Override
	public void parse() throws SettingsParseError {
		LinkedList<IArgument<?>> list = new LinkedList<IArgument<?>>(this.arguments.values());
		list.addAll(this.argumentSets.values());
		LinkedList<IArgument<?>> list2 = new LinkedList<IArgument<?>>();
		
		while (!list.isEmpty()) {
			for (final IArgument<?> argument : list) {
				argument.parse();
				
				if (argument.getRequirements().getMissingRequirements() != null) {
					list2.add(argument);
				}
			}
			
			if (list2.isEmpty()) {
				break;
			} else if (list.size() == list2.size()) {
				throw new SettingsParseError(
				                             "Could not resolved dependencies. Arguments have unresolved dependencies: ",
				                             list2.iterator().next());
			} else {
				list = list2;
				list2 = new LinkedList<IArgument<?>>();
			}
		}
		
		boolean initResult = false;
		initResult = init();
		
		if (!initResult) {
			throw new SettingsParseError("Could not initialize '" + getName()
			        + "'. Please see error earlier error messages, refer to the argument help information, "
			        + "or review the init() method of the corresponding " + getHandle() + ".", this);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#required()
	 */
	@Override
	public boolean required() {
		return getRequirements().required();
	}
	
	/**
	 * @param cachedValue
	 */
	protected final void setCachedValue(final T cachedValue) {
		this.init = true;
		this.cachedValue = cachedValue;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final Tuple<Integer, Integer> span = getKeyValueSpan();
		return toString(span.getFirst(), span.getSecond());
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.AndamaArgumentInterface#toString(int)
	 */
	@Override
	public String toString(final int keyWidth,
	                       final int valueWidth) {
		final StringBuilder builder = new StringBuilder();
		
		builder.append("[").append(getName()).append(" - ").append(getDescription()).append("]");
		
		for (final IArgument<?> arg : getChildren()) {
			builder.append(FileUtils.lineSeparator).append(arg.toString(keyWidth, valueWidth));
		}
		
		return builder.toString();
	}
}
