/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.ownhero.dev.andama.settings;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.exceptions.SettingsParseError;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.requirements.Required;
import net.ownhero.dev.andama.settings.requirements.Requirement;
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
public abstract class AndamaArgumentSet<T> implements AndamaArgumentInterface<T> {
	
	private final HashMap<String, AndamaArgument<?>>    arguments    = new HashMap<String, AndamaArgument<?>>();
	private final HashMap<String, AndamaArgumentSet<?>> argumentSets = new HashMap<String, AndamaArgumentSet<?>>();
	private final String                                name;
	private final String                                description;
	private final Requirement                           requirements;
	private final AndamaSettings                        settings;
	private boolean                                     init         = false;
	private T                                           cachedValue  = null;
	
	/**
	 * @throws ArgumentRegistrationException
	 * @see de.unisaarland.cs.st.reposuite.settings.RepoSuiteArgument
	 */
	public AndamaArgumentSet(final AndamaArgumentSet<?> argumentSet, final String description,
	        final Requirement requirements) throws ArgumentRegistrationException {
		this.name = getHandle();
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
	AndamaArgumentSet(final AndamaSettings settings, final String description) {
		this.settings = settings;
		this.description = description;
		this.name = (getHandle() != null) && (getHandle().length() > 0)
		                                                               ? getHandle()
		                                                               : "ROOT";
		this.requirements = new Required();
		getSettings().addArgument(this);
	}
	
	/**
	 * Call this method to add an argument to the set of arguments. But be aware
	 * that you have to set all arguments before adding it to the MinerSettings!
	 * 
	 * @param argument
	 *            MinerArgument to be added
	 * @return <code>true</code> if the argument could be added.
	 *         <code>false</code> otherwise.
	 */
	public final boolean addArgument(@NotNull final AndamaArgument<?> argument) {
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
	
	public final boolean addArgument(@NotNull final AndamaArgumentSet<?> argument) {
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
	public final int compareTo(final AndamaArgumentInterface<?> arg0) {
		if (this == arg0) {
			return 0;
		}
		
		Set<AndamaArgumentInterface<?>> dependencies = getDependencies();
		if (dependencies.contains(arg0)) {
			return 1;
		} else if (dependencies.contains(this)) {
			return 0;
		} else {
			int ret = -1;
			for (AndamaArgumentInterface<?> argX : dependencies) {
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
	public final AndamaArgument<?> getArgument(final String name) {
		return getArguments().get(name);
	}
	
	/**
	 * Return the arguments held within the set.
	 * 
	 * @return
	 */
	public Map<String, AndamaArgument<?>> getArguments() {
		return this.arguments;
	}
	
	/**
	 * @return
	 */
	public Map<String, AndamaArgumentSet<?>> getArgumentSets() {
		return this.argumentSets;
	}
	
	/**
	 * @return
	 */
	protected final T getCachedValue() {
		return this.cachedValue;
	}
	
	public Collection<AndamaArgumentInterface<?>> getChildren() {
		LinkedList<AndamaArgumentInterface<?>> list = new LinkedList<AndamaArgumentInterface<?>>();
		list.addAll(getArguments().values());
		list.addAll(getArgumentSets().values());
		return list;
	}
	
	/**
	 * @return the dependees
	 */
	@Override
	public final Set<AndamaArgumentInterface<?>> getDependencies() {
		return this.requirements.getDependencies();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.AndamaArgumentInterface#getDescription()
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
		StringBuilder builder = new StringBuilder();
		
		builder.append("[ ").append(getName()).append(" ]").append(FileUtils.lineSeparator);
		builder.append("|-Description: ").append(getDescription());
		builder.append(required()
		                         ? " (Required, due to " + getRequirements() + ")"
		                         : "");
		
		for (AndamaArgumentInterface<?> argument : getChildren()) {
			builder.append(FileUtils.lineSeparator).append(argument.getHelpString(1));
		}
		
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.AndamaArgumentInterface#toString(int)
	 */
	@Override
	public String getHelpString(final int indentation) {
		StringBuilder builder = new StringBuilder();
		
		StringBuilder indent = new StringBuilder();
		StringBuilder header = new StringBuilder();
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
		
		for (AndamaArgumentInterface<?> argument : getChildren()) {
			builder.append(FileUtils.lineSeparator).append(indent).append(argument.getHelpString(indentation + 1));
		}
		
		return builder.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.AndamaArgumentInterface#getKeyValueSpan()
	 */
	@Override
	public Tuple<Integer, Integer> getKeyValueSpan() {
		Tuple<Integer, Integer> tuple = new Tuple<Integer, Integer>(0, 0);
		
		for (AndamaArgumentInterface<?> arg : getChildren()) {
			Tuple<Integer, Integer> span = arg.getKeyValueSpan();
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
	public final String getName() {
		return this.name;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.AndamaArgumentInterface#getRequirements()
	 */
	@Override
	public Requirement getRequirements() {
		return this.requirements;
	}
	
	/**
	 * @return the settings
	 */
	@Override
	public final AndamaSettings getSettings() {
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
	 * @see
	 * net.ownhero.dev.andama.settings.AndamaArgumentInterface#isInitialized()
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
		LinkedList<AndamaArgumentInterface<?>> list = new LinkedList<AndamaArgumentInterface<?>>(
		                                                                                         this.arguments.values());
		list.addAll(this.argumentSets.values());
		LinkedList<AndamaArgumentInterface<?>> list2 = new LinkedList<AndamaArgumentInterface<?>>();
		
		while (!list.isEmpty()) {
			for (AndamaArgumentInterface<?> argument : list) {
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
				list2 = new LinkedList<AndamaArgumentInterface<?>>();
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
		Tuple<Integer, Integer> span = getKeyValueSpan();
		return toString(span.getFirst(), span.getSecond());
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.AndamaArgumentInterface#toString(int)
	 */
	@Override
	public String toString(final int keyWidth,
	                       final int valueWidth) {
		StringBuilder builder = new StringBuilder();
		
		builder.append("[").append(getName()).append(" - ").append(getDescription()).append("]");
		
		for (AndamaArgumentInterface<?> arg : getChildren()) {
			builder.append(FileUtils.lineSeparator).append(arg.toString(keyWidth, valueWidth));
		}
		
		return builder.toString();
	}
}
