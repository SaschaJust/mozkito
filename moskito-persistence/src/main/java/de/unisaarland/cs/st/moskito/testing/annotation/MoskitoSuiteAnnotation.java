/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.unisaarland.cs.st.moskito.testing.annotation.processors.MoskitoSettingsProcessor;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@Target (value = { ElementType.ANNOTATION_TYPE })
public @interface MoskitoSuiteAnnotation {
	
	Class<? extends MoskitoSettingsProcessor> value();
}
