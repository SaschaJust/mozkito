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
package de.unisaarland.cs.st.mozkito.testing.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.unisaarland.cs.st.mozkito.persistence.ConnectOptions;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.mozkito.testing.annotation.processors.DatabaseSettingsProcessor;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@MoskitoTestAnnotation (DatabaseSettingsProcessor.class)
@Target (value = { ElementType.METHOD, ElementType.TYPE })
public @interface DatabaseSettings {
	
	String database() default "moskito_junit";
	
	String driver() default "org.postgresql.Driver";
	
	String hostname() default "grid1.st.cs.uni-saarland.de";
	
	ConnectOptions options() default de.unisaarland.cs.st.mozkito.persistence.ConnectOptions.DB_DROP_CREATE;
	
	String password() default "miner";
	
	String type() default "postgresql";
	
	String unit();
	
	String username() default "miner";
	
	Class<? extends PersistenceUtil> util() default de.unisaarland.cs.st.mozkito.persistence.OpenJPAUtil.class;
}
