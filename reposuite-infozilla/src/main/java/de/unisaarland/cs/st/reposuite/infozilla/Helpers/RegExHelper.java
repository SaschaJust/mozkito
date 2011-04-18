/**
 * 
 * RegExHelper.java
 * 
 * @author Nicolas Bettenburg � 2009-2010, all rights reserved.
 ******************************************************************** 
 *         This file is part of infoZilla. * * InfoZilla is non-free software:
 *         you may not redistribute it * and/or modify it without the permission
 *         of the original author. * * InfoZilla is distributed in the hope that
 *         it will be useful, * but WITHOUT ANY WARRANTY; without even the
 *         implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR
 *         PURPOSE. *
 ******************************************************************** 
 * 
 */

package de.unisaarland.cs.st.reposuite.infozilla.Helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExHelper {
	
	public static Iterable<MatchResult> findMatches(final Pattern p,
	                                                CharSequence s) {
		List<MatchResult> results = new ArrayList<MatchResult>();
		
		for (Matcher m = p.matcher(s); m.find();) {
			results.add(m.toMatchResult());
		}
		return results;
	}
	
	public static String makeLinuxNewlines(String input) {
		String output = input.replaceAll("(([\r][\n])|([\r]))", "\n");
		return output;
	}
	
}
