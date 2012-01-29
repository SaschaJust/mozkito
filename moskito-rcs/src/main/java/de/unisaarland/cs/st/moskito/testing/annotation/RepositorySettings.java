/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.unisaarland.cs.st.moskito.testing.annotation.processors.RepositorySettingsProcessor;

/**
 * @author just
 * 
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@MoskitoSuiteAnnotation (RepositorySettingsProcessor.class)
@Target (value = { ElementType.TYPE })
public @interface RepositorySettings {
	
	RepositorySetting[] value();
}
