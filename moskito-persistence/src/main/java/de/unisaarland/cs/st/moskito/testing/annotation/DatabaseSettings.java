/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.unisaarland.cs.st.moskito.persistence.ConnectOptions;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

/**
 * @author just
 * 
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@MoskitoTestingAnnotation
@Target (value = { ElementType.METHOD })
public @interface DatabaseSettings {
	
	String database() default "moskito_junit";
	
	String driver() default "org.postgresql.Driver";
	
	String hostname() default "grid1.st.cs.uni-saarland.de";
	
	ConnectOptions options() default de.unisaarland.cs.st.moskito.persistence.ConnectOptions.DROPIFEXISTS;
	
	String password() default "miner";
	
	String type() default "postgresql";
	
	String unit();
	
	String username() default "miner";
	
	Class<? extends PersistenceUtil> util() default de.unisaarland.cs.st.moskito.persistence.OpenJPAUtil.class;
}
