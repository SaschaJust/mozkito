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

package org.mozkito.mappings.utils;

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
