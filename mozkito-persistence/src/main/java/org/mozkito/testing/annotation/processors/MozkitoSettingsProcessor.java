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

package org.mozkito.testing.annotation.processors;

import java.lang.annotation.Annotation;

import org.mozkito.exceptions.TestSettingsError;
import org.mozkito.testing.DatabaseTest;

/**
 * The Interface MozkitoSettingsProcessor.
 */
public interface MozkitoSettingsProcessor {
	
	/**
	 * Setup.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param test
	 *            the test
	 * @param annotation
	 *            the annotation
	 * @throws TestSettingsError
	 *             the test settings error
	 */
	<T extends DatabaseTest> void setup(T test,
	                                         Annotation annotation) throws TestSettingsError;
	
	/**
	 * Tear down.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param test
	 *            the test
	 * @param annotation
	 *            the annotation
	 * @throws TestSettingsError
	 *             the test settings error
	 */
	<T extends DatabaseTest> void tearDown(T test,
	                                            Annotation annotation) throws TestSettingsError;
	
}
