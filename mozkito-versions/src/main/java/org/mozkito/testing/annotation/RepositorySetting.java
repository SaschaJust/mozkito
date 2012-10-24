/**
 * 
 */
package org.mozkito.testing.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mozkito.testing.annotation.MozkitoSuiteAnnotation;
import org.mozkito.testing.annotation.processors.RepositorySettingsProcessor;
import org.mozkito.testing.annotation.type.SourceType;
import org.mozkito.versions.RepositoryType;


/**
 * The Interface RepositorySetting.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@MozkitoSuiteAnnotation (RepositorySettingsProcessor.class)
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
