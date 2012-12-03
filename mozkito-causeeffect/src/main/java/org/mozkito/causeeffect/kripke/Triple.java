/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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

package org.mozkito.causeeffect.kripke;

/**
 * The Class Triple.
 *
 * @param <A> the generic type
 * @param <B> the generic type
 * @param <C> the generic type
 */
public class Triple<A, B, C> {
	
	/** The a. */
	public A a;
	
	/** The b. */
	public B b;
	
	/** The c. */
	public C c;
	
	/**
	 * Instantiates a new triple.
	 *
	 * @param a the a
	 * @param b the b
	 * @param c the c
	 */
	public Triple(final A a, final B b, final C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
}
