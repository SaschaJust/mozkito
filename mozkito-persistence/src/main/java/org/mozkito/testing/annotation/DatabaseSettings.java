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
package org.mozkito.testing.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mozkito.persistence.ConnectOptions;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.testing.annotation.processors.DatabaseSettingsProcessor;

/**
 * The Interface DatabaseSettings.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@MozkitoTestAnnotation (DatabaseSettingsProcessor.class)
@Target (value = { ElementType.TYPE })
public @interface DatabaseSettings {
	
	/**
	 * Database.
	 * 
	 * @return the string
	 */
	String database() default "test.db";
	
	/**
	 * Driver.
	 * 
	 * @return the string
	 */
	String driver() default "org.apache.derby.jdbc.EmbeddedDriver";
	
	/**
	 * Hostname.
	 * 
	 * @return the string
	 */
	String hostname() default "";
	
	/**
	 * Options.
	 * 
	 * @return the connect options
	 */
	ConnectOptions options() default org.mozkito.persistence.ConnectOptions.DB_DROP_CREATE;
	
	/**
	 * Password.
	 * 
	 * @return the string
	 */
	String password() default "";
	
	/**
	 * Type.
	 * 
	 * @return the string
	 */
	String type() default "DERBY";
	
	/**
	 * Unit.
	 * 
	 * @return the string
	 */
	String unit();
	
	/**
	 * Username.
	 * 
	 * @return the string
	 */
	String username() default "";
	
	/**
	 * Util.
	 * 
	 * @return the class<? extends persistence util>
	 */
	Class<? extends PersistenceUtil> util() default org.mozkito.persistence.OpenJPAUtil.class;
}
