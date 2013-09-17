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

package org.mozkito.infozilla.elements;

import java.util.LinkedList;
import java.util.List;

/**
 * The Enum Roman.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public enum Roman {
	
	/** The i. */
	I (1),
	/** The iv. */
	IV (4),
	/** The v. */
	V (5),
	/** The ix. */
	IX (9),
	/** The x. */
	X (10),
	/** The xl. */
	XL (40),
	/** The l. */
	L (50),
	/** The xc. */
	XC (90),
	/** The c. */
	C (100),
	/** The cd. */
	CD (400),
	/** The d. */
	D (500),
	/** The cm. */
	CM (900),
	/** The m. */
	M (1000);
	
	/**
	 * Convert.
	 * 
	 * @param value
	 *            the value
	 * @return the string
	 */
	public static String convert(final int value) {
		int n = value;
		
		if (n <= 0) {
			throw new IllegalArgumentException();
		}
		
		final StringBuilder buf = new StringBuilder();
		
		final Roman[] values = Roman.values();
		for (int i = values.length - 1; i >= 0; i--) {
			while (n >= values[i].weight) {
				buf.append(values[i]);
				n -= values[i].weight;
			}
			
			if (n == 0) {
				break;
			}
		}
		return buf.toString();
	}
	
	/**
	 * Convert.
	 * 
	 * @param romanNumber
	 *            the roman number
	 * @return the int
	 */
	public static int convert(final String romanNumber) {
		int result = 0;
		int mediator = 0;
		
		final int length = romanNumber.length(); // get number string length
		
		for (int i = 0; i < (length - 1); i++) { // this loop will add each Roman character value
		
			if (getInt(romanNumber.charAt(i)) > getInt(romanNumber.charAt(i + 1))) {
				result = result + getInt(romanNumber.charAt(i)) + mediator;
				mediator = 0;
			} else if (getInt(romanNumber.charAt(i)) == getInt(romanNumber.charAt(i + 1))) {
				mediator = mediator + getInt(romanNumber.charAt(i));
			} else if (getInt(romanNumber.charAt(i)) < getInt(romanNumber.charAt(i + 1))) {
				mediator = -mediator - getInt(romanNumber.charAt(i));
			}
		}
		
		result = result + mediator + getInt(romanNumber.charAt(length - 1));
		
		return result;
		
	}
	
	/**
	 * From.
	 * 
	 * @param value
	 *            the value
	 * @return the roman[]
	 */
	public static Roman[] from(final int value) {
		int n = value;
		final List<Roman> list = new LinkedList<>();
		
		if (n <= 0) {
			throw new IllegalArgumentException();
		}
		
		final Roman[] values = Roman.values();
		for (int i = values.length - 1; i >= 0; i--) {
			while (n >= values[i].weight) {
				list.add(values[i]);
				n -= values[i].weight;
			}
			
			if (n == 0) {
				break;
			}
		}
		
		return list.toArray(new Roman[0]);
	}
	
	/**
	 * Gets the int.
	 * 
	 * @param charAt
	 *            the char at
	 * @return the int
	 */
	private static int getInt(final char charAt) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return Roman.valueOf("" + charAt).weight;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/** The weight. */
	int weight;
	
	/**
	 * Instantiates a new Roman.
	 * 
	 * @param weight
	 *            the weight
	 */
	Roman(final int weight) {
		this.weight = weight;
	}
}
