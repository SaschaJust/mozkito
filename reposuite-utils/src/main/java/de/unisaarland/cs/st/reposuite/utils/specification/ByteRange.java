package de.unisaarland.cs.st.reposuite.utils.specification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Condition to guaranty an byte to be in a certain range.
 * <ul>
 * <li>If {@link ByteRange#min()} is not specified, it is set to
 * {@link Byte#MIN_VALUE}.</li>
 * <li>If {@link ByteRange#max()} is not specified, {@link Byte#MAX_VALUE} is
 * used.</li>
 * <li> {@link ByteRange#value()} holds the specification/condition string, i.e.
 * the reason why this assumption is made/can be made.</li>
 * </ul>
 * 
 * This can be used only on PARAMETERs.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (value = { ElementType.PARAMETER })
public @interface ByteRange {
	
	/**
	 * @return the max value the target variable/parameter must have.
	 */
	byte max() default Byte.MAX_VALUE;
	
	/**
	 * @return the min value the target variable/parameter must have.
	 */
	byte min() default Byte.MIN_VALUE;
	
	/**
	 * @return the corresponding specification/condition/assertion string.
	 */
	String value() default "";
}
