/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils.specification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (value = { ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface NotAllNull {
	
	String value() default "";
}
