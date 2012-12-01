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
package org.mozkito.testing.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mozkito.testing.annotation.processors.RepositoryProcessor;
import org.mozkito.testing.annotation.type.SourceType;
import org.mozkito.versions.RepositoryType;

/**
 * The Interface RepositorySetting.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@EnvironmentProcessor (RepositoryProcessor.class)
@Target (value = { ElementType.TYPE })
public @interface RepositorySetting {
	
	/**
	 * Base dir.
	 * 
	 * @return the string
	 */
	String baseDir() default "";
	
	/**
	 * Id to access the repository.
	 * 
	 * @return the string
	 */
	String id();
	
	/**
	 * Source type.
	 * 
	 * @return the source type
	 */
	SourceType sourceType() default SourceType.RESOURCE;
	
	/**
	 * Type of the repository, like GIT, MERCURIAL, SVN...
	 * 
	 * @return the repository type
	 */
	RepositoryType type();
	
	/**
	 * URI to the repository. This can either be an URL to a remote repository, e.g.
	 * 
	 * <pre>
	 * file:///path/to/the/repository
	 * </pre>
	 * 
	 * or
	 * 
	 * <pre>
	 * https://github.com/some/project.git
	 * </pre>
	 * 
	 * , or the name of a file, if the repository has been stored as an archive in the local resources. Make sure to set
	 * the sourceType accordingly.
	 * 
	 * @return the URI to the repository
	 */
	String uri();
}
