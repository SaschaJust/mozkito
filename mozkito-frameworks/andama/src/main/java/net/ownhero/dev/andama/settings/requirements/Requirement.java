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
package net.ownhero.dev.andama.settings.requirements;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.settings.AndamaArgumentInterface;
import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.Tuple;

public abstract class Requirement {
	
	/**
	 * @param arguments
	 */
	public static void printGraph(final Collection<AndamaArgumentSet<?>> arguments) {
		StringBuilder builder = new StringBuilder();
		
		for (AndamaArgumentInterface<?> aai : arguments) {
			for (AndamaArgumentInterface<?> aaiDependency : aai.getDependencies()) {
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
		for (String output : execute.getSecond()) {
			System.out.println(output);
		}
	}
	
	/**
	 * @return
	 */
	public abstract Set<AndamaArgumentInterface<?>> getDependencies();
	
	/**
	 * @return
	 */
	public final String getHandle() {
		return getClass().getSimpleName();
	}
	
	/**
	 * @return
	 */
	public abstract List<Requirement> getMissingRequirements();
	
	/**
	 * @return
	 */
	public abstract boolean required();
}
