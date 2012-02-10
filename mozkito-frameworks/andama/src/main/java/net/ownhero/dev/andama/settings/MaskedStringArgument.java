/**
 * 
 */
package net.ownhero.dev.andama.settings;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.NotEmptyString;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MaskedStringArgument extends StringArgument {
	
	/**
	 * @param settings
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param isRequired
	 * @throws ArgumentRegistrationException
	 */
	public MaskedStringArgument(@NotNull final AndamaArgumentSet<?> argumentSet,
	        @NotNull @NotEmptyString final String name, @NotNull @NotEmptyString final String description,
	        final String defaultValue, @NotNull final Requirement requirements) throws ArgumentRegistrationException {
		super(argumentSet, name, description, defaultValue, requirements, true);
	}
	
}
