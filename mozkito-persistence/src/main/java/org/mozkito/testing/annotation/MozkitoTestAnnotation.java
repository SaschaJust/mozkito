/**
 * 
 */
package org.mozkito.testing.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mozkito.testing.annotation.processors.MozkitoSettingsProcessor;


/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@Target (value = { ElementType.ANNOTATION_TYPE })
public @interface MozkitoTestAnnotation {
	
	Class<? extends MozkitoSettingsProcessor> value();
}
