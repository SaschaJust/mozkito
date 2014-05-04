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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.hiari.settings.BooleanArgument;
import net.ownhero.dev.hiari.settings.DoubleArgument;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.ListArgument;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.SetArgument;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
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
	 * All.
	 * 
	 * @param requirements
	 *            the requirements
	 * @return the requirement
	 */
	public static Requirement all(@NotNull @NotEmpty final Collection<Requirement> requirements) {
		return new All(requirements);
	}
	
	/**
	 * All.
	 * 
	 * @param expressions
	 *            the expressions
	 * @return the requirement
	 */
	public static Requirement all(@NotNull @NotEmpty final Requirement... expressions) {
		return new All(expressions);
	}
	
	/**
	 * And.
	 * 
	 * @param requirement1
	 *            the requirement1
	 * @param requirement2
	 *            the requirement2
	 * @return the requirement
	 */
	public static Requirement and(@NotNull final Requirement requirement1,
	                              @NotNull final Requirement requirement2) {
		return new And(requirement1, requirement2);
	}
	
	/**
	 * Any.
	 * 
	 * @param requirements
	 *            the requirements
	 * @return the requirement
	 */
	public static Requirement any(@NotNull @NotEmpty final Collection<Requirement> requirements) {
		return new Any(requirements);
	}
	
	/**
	 * Any.
	 * 
	 * @param expressions
	 *            the expressions
	 * @return the requirement
	 */
	public static Requirement any(@NotNull @NotEmpty final Requirement... expressions) {
		return new Any(expressions);
	}
	
	/**
	 * Contains.
	 * 
	 * @param option
	 *            the option
	 * @param depender
	 *            the depender
	 * @return the requirement
	 */
	public static Requirement contains(@NotNull final ListArgument.Options option,
	                                   @NotNull final IArgument<?, ?> depender) {
		return new Contains(option, depender);
	}
	
	/**
	 * Contains.
	 * 
	 * @param option
	 *            the option
	 * @param value
	 *            the value
	 * @return the requirement
	 */
	public static Requirement contains(@NotNull final ListArgument.Options option,
	                                   @NotNull final String value) {
		return new Contains(option, value);
	}
	
	/**
	 * Contains.
	 * 
	 * @param option
	 *            the option
	 * @param depender
	 *            the depender
	 * @return the requirement
	 */
	public static Requirement contains(@NotNull final SetArgument.Options option,
	                                   @NotNull final IArgument<?, ?> depender) {
		return new Contains(option, depender);
		
	}
	
	/**
	 * Contains.
	 * 
	 * @param option
	 *            the option
	 * @param value
	 *            the value
	 * @return the requirement
	 */
	public static Requirement contains(@NotNull final SetArgument.Options option,
	                                   @NotNull final String value) {
		return new Contains(option, value);
	}
	
	/**
	 * Equals.
	 * 
	 * @param argument
	 *            the argument
	 * @param value
	 *            the value
	 * @return the requirement
	 */
	public static Requirement equals(@NotNull final BooleanArgument.Options argument,
	                                 @NotNull final boolean value) {
		return new Equals(argument, value);
	}
	
	/**
	 * Equals.
	 * 
	 * @param argument
	 *            the argument
	 * @param value
	 *            the value
	 * @return the requirement
	 */
	public static Requirement equals(@NotNull final DoubleArgument.Options argument,
	                                 @NotNull final double value) {
		return new Equals(argument, value);
	}
	
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
	 * Equals.
	 * 
	 * @param argument
	 *            the argument
	 * @param value
	 *            the value
	 * @return the requirement
	 */
	public static Requirement equals(@NotNull final LongArgument.Options argument,
	                                 @NotNull final long value) {
		return new Equals(argument, value);
	}
	
	/**
	 * Equals.
	 * 
	 * @param argument
	 *            the argument
	 * @param depender
	 *            the depender
	 * @return the requirement
	 */
	public static Requirement equals(@NotNull final StringArgument.Options argument,
	                                 @NotNull final IOptions<?, ?> depender) {
		return new Equals(argument, depender);
	}
	
	/**
	 * Equals.
	 * 
	 * @param argument
	 *            the argument
	 * @param value
	 *            the value
	 * @return the requirement
	 */
	public static Requirement equals(@NotNull final StringArgument.Options argument,
	                                 @NotNull final String value) {
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
	 * Or.
	 * 
	 * @param requirement1
	 *            the requirement1
	 * @param requirement2
	 *            the requirement2
	 * @return the requirement
	 */
	public static Requirement or(@NotNull final Requirement requirement1,
	                             @NotNull final Requirement requirement2) {
		return new Or(requirement1, requirement2);
	}
	
	// /**
	// * Prints the graph.
	// *
	// * @param arguments
	// * the arguments
	// */
	// public static void printGraph(final Collection<ArgumentSet<?, ?>> arguments) {
	// final StringBuilder builder = new StringBuilder();
	//
	// for (final IArgument<?, ?> aai : arguments) {
	// for (final IOptions<?, ?> aaiDependency : aai.getDependencies()) {
	//				builder.append(aai.getName()).append(" --> ").append(aaiDependency.getName()) //$NON-NLS-1$
	// .append(FileUtils.lineSeparator);
	// }
	// }
	//
	//		final Tuple<Integer, List<String>> execute = CommandExecutor.execute("graph-easy", //$NON-NLS-1$
	// null,
	// FileUtils.tmpDir,
	// new ByteArrayInputStream(
	// builder.toString()
	// .getBytes()),
	// null);
	// for (final String output : execute.getSecond()) {
	// System.out.println(output);
	// }
	// }
	
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
	 * Xor.
	 * 
	 * @param requirement1
	 *            the requirement1
	 * @param requirement2
	 *            the requirement2
	 * @return the requirement
	 */
	public static Requirement xor(@NotNull final Requirement requirement1,
	                              @NotNull final Requirement requirement2) {
		return new Xor(requirement1, requirement2);
	}
	
	/**
	 * Required.
	 * 
	 * @return true, if successful
	 */
	public abstract boolean check();
	
	/**
	 * Gets the dependencies.
	 * 
	 * @return the dependencies
	 */
	public abstract Set<IOptions<?, ?>> getDependencies();
	
	/**
	 * Gets the required dependencies.
	 * 
	 * @return the required dependencies
	 */
	public abstract List<Requirement> getFailedChecks();
	
	/**
	 * Gets the handle.
	 * 
	 * @return the handle
	 */
	public final String getHandle() {
		return getClass().getSimpleName();
	}
}
