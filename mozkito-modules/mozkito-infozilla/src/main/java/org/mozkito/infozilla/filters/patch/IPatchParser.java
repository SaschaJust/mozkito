/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.infozilla.filters.patch;

import java.util.List;

import org.mozkito.infozilla.model.patch.Patch;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public interface IPatchParser {
	
	/**
	 * Parses a given text for all Patches inside using a 2 line lookahead Fuzzy Parser approach.
	 * 
	 * @param text
	 *            The text to extract Patches from.
	 * @return a list of found patches
	 */
	public abstract List<Patch> parseForPatches(String text);
	
}
