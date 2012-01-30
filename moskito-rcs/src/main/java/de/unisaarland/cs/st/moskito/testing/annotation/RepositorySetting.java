/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.unisaarland.cs.st.moskito.rcs.RepositoryType;
import de.unisaarland.cs.st.moskito.testing.annotation.processors.RepositorySettingsProcessor;
import de.unisaarland.cs.st.moskito.testing.annotation.type.SourceType;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@MoskitoSuiteAnnotation (RepositorySettingsProcessor.class)
@Target (value = { ElementType.TYPE })
public @interface RepositorySetting {
	
	String baseDir() default "";
	
	SourceType sourceType() default de.unisaarland.cs.st.moskito.testing.annotation.type.SourceType.RESOURCE;
	
	RepositoryType type();
	
	String uri();
}
