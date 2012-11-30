/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 */
package org.mozkito.testing.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mozkito.testing.annotation.processors.RepositorySettingsProcessor;
import org.mozkito.testing.annotation.type.SourceType;
import org.mozkito.versions.RepositoryType;

/**
 * The Interface RepositorySetting.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@MozkitoTestAnnotation (RepositorySettingsProcessor.class)
@Target (value = { ElementType.TYPE })
public @interface RepositorySetting {
	
	/**
	 * Base dir.
	 * 
	 * @return the string
	 */
	String baseDir() default "";
	
	/**
	 * Source type.
	 * 
	 * @return the source type
	 */
	SourceType sourceType() default org.mozkito.testing.annotation.type.SourceType.RESOURCE;
	
	/**
	 * Type.
	 * 
	 * @return the repository type
	 */
	RepositoryType type();
	
	/**
	 * Uri.
	 * 
	 * @return the string
	 */
	String uri();
}
