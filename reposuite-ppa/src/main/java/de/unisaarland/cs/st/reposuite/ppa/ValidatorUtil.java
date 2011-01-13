/*******************************************************************************
 * PPA - Partial Program Analysis for Java
 * Copyright (C) 2008 Barthelemy Dagenais
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library. If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.txt>
 *******************************************************************************/
package de.unisaarland.cs.st.reposuite.ppa;
public class ValidatorUtil {
	public static boolean validateEmpty(final String input, final String name, final boolean throwException) {
		boolean valid = (input != null) && (input.trim().length() > 0);
		if (!valid && throwException) {
			throw new IllegalArgumentException("This parameter, " + name + " is empty.");
		}
		return valid;
	}
	
	public static boolean validateNull(final Object input, final String name, final boolean throwException) {
		boolean valid = input != null;
		if (!valid && throwException) {
			throw new IllegalArgumentException("This parameter, " + name + " is null.");
		}
		return valid;
	}
	
	public static boolean validateNull(final Object[] input, final String name, final boolean throwException) {
		boolean valid = input != null;
		
		if (input != null) {
			for (Object object : input) {
				valid = object != null;
				if (!valid) {
					break;
				}
			}
		}
		
		if (!valid && throwException) {
			throw new IllegalArgumentException("This parameter, " + name + " is null.");
		}
		return valid;
	}
}
