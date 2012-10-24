/**
 * 
 */
package de.unisaarland.cs.st.mozkito.testing.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.unisaarland.cs.st.mozkito.testing.annotation.MoskitoSuiteAnnotation;
import de.unisaarland.cs.st.mozkito.testing.annotation.processors.RepositorySettingsProcessor;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@MoskitoSuiteAnnotation (RepositorySettingsProcessor.class)
@Target (value = { ElementType.TYPE })
public @interface RepositorySettings {
	
	RepositorySetting[] value();
}
