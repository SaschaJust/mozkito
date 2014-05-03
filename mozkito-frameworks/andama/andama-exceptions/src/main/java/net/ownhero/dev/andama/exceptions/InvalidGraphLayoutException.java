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
package net.ownhero.dev.andama.exceptions;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

/**
 * The Class InvalidGraphLayoutException.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class InvalidGraphLayoutException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5758728046599090552L;
	
	/**
	 * Instantiates a new invalid graph layout exception.
	 */
	public InvalidGraphLayoutException() {
		super();
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated constructor stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Instantiates a new invalid graph layout exception.
	 *
	 * @param arg0 the arg0
	 */
	public InvalidGraphLayoutException(@NotNull final String arg0) {
		super(arg0);
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated constructor stub
			
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Instantiates a new invalid graph layout exception.
	 *
	 * @param arg0 the arg0
	 * @param arg1 the arg1
	 */
	public InvalidGraphLayoutException(@NotNull final String arg0, @NotNull final Throwable arg1) {
		super(arg0, arg1);
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated constructor stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Instantiates a new invalid graph layout exception.
	 *
	 * @param arg0 the arg0
	 */
	public InvalidGraphLayoutException(@NotNull final Throwable arg0) {
		super(arg0);
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated constructor stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
