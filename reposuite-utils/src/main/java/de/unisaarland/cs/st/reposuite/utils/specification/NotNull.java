/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils.specification;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@ConditionPattern ("notNull(%s)")
@Target (value = { ElementType.PARAMETER })
public @interface NotNull {
	
	String value() default "";
}
