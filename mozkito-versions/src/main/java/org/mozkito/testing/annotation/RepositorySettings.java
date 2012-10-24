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


/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@MozkitoSuiteAnnotation (RepositorySettingsProcessor.class)
@Target (value = { ElementType.TYPE })
public @interface RepositorySettings {
	
	RepositorySetting[] value();
}
