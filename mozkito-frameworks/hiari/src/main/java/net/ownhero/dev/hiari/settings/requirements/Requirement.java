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
package net.ownhero.dev.hiari.settings.requirements;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

/**
 * The Class Requirement.
 */
public abstract class Requirement {
	
	/** The Constant required. */
	public static final Requirement required = new Required();
	
	/** The Constant optional. */
	public static final Requirement optional = new Optional();
	
	/**
	 * Equals.
	 * 
	 * @param argument
	 *            the argument
	 * @param value
	 *            the value
	 * @return the requirement
	 */
	public static Requirement equals(@NotNull final EnumArgument.Options<?> argument,
	                                 @NotNull final Enum<?> value) {
		return new Equals(argument, value);
	}
	
	/**
	 * Iff.
	 * 
	 * @param option
	 *            the option
	 * @return the requirement
	 */
	public static Requirement iff(@NotNull final IOptions<?, ?> option) {
		return new If(option);
	}
	
	/**
	 * Not.
	 * 
	 * @param requirement
	 *            the requirement
	 * @return the requirement
	 */
	public static Requirement not(final Requirement requirement) {
		return new Not(requirement);
	}
	
	/**
	 * Prints the graph.
	 * 
	 * @param arguments
	 *            the arguments
	 */
	public static void printGraph(final Collection<ArgumentSet<?, ?>> arguments) {
		final StringBuilder builder = new StringBuilder();
		
		for (final IArgument<?, ?> aai : arguments) {
			for (final IOptions<?, ?> aaiDependency : aai.getDependencies()) {
				builder.append(aai.getName()).append(" --> ").append(aaiDependency.getName())
				       .append(FileUtils.lineSeparator);
			}
		}
		
		final Tuple<Integer, List<String>> execute = CommandExecutor.execute("graph-easy",
		                                                                     null,
		                                                                     FileUtils.tmpDir,
		                                                                     new ByteArrayInputStream(
		                                                                                              builder.toString()
		                                                                                                     .getBytes()),
		                                                                     null);
		for (final String output : execute.getSecond()) {
			System.out.println(output);
		}
	}
	
	/**
	 * Unset.
	 * 
	 * @param option
	 *            the option
	 * @return the requirement
	 */
	public static Requirement unset(@NotNull final IOptions<?, ?> option) {
		return Requirement.not(Requirement.iff(option));
	}
	
	/**
	 * Gets the dependencies.
	 * 
	 * @return the dependencies
	 */
	public abstract Set<IOptions<?, ?>> getDependencies();
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	public final String getHandle() {
		return getClass().getSimpleName();
	}
	
	/**
	 * Gets the required dependencies.
	 * 
	 * @return the required dependencies
	 */
	public abstract List<Requirement> getRequiredDependencies();
	
	/**
	 * Required.
	 * 
	 * @return true, if successful
	 */
	public abstract boolean required();
}
