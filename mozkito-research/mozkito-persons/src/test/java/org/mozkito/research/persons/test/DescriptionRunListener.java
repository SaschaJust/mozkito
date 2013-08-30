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

package org.mozkito.research.persons.test;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class DescriptionRunListener extends RunListener {
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.junit.runner.notification.RunListener#testAssumptionFailure(org.junit.runner.notification.Failure)
	 */
	@Override
	public void testAssumptionFailure(final Failure failure) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			try {
				testFailure(failure);
			} catch (final Exception ignore) {
				// ignore
			};
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.junit.runner.notification.RunListener#testFailure(org.junit.runner.notification.Failure)
	 */
	@Override
	public void testFailure(final Failure failure) throws Exception {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final Description description = failure.getDescription().getAnnotation(Description.class);
			if (description != null) {
				System.out.println("Test failed, while testing: " + description.value());
			}
			
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.junit.runner.notification.RunListener#testStarted(org.junit.runner.Description)
	 */
	@Override
	public void testStarted(final org.junit.runner.Description description) throws Exception {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final Description descriptionA = description.getAnnotation(Description.class);
			if (descriptionA != null) {
				System.out.println("Testing: " + descriptionA.value());
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
}
