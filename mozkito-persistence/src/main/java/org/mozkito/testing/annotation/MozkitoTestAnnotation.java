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
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@Target (value = { ElementType.ANNOTATION_TYPE })
public @interface MozkitoTestAnnotation {
	
	/**
	 * Value.
	 * 
	 * @return the class<? extends mozkito settings processor>
	 */
	Class<? extends MozkitoSettingsProcessor> value();
}
