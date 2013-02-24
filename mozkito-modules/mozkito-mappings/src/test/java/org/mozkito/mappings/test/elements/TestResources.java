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

package org.mozkito.mappings.test.elements;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The Interface TestResources.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Retention (RetentionPolicy.RUNTIME)
public @interface TestResources {
	
	/**
	 * From.
	 * 
	 * @return the string
	 */
	String from();
	
	/**
	 * To.
	 * 
	 * @return the string
	 */
	String to();
	
}
