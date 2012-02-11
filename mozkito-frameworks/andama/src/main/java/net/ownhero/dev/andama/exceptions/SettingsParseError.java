/**
 * 
 */
package net.ownhero.dev.andama.exceptions;

import java.util.List;

import net.ownhero.dev.andama.settings.IArgument;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.JavaUtils;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class SettingsParseError extends Exception {
	
	/**
     * 
     */
	private static final long                serialVersionUID = 6569706686166731951L;
	private final IArgument<?> argument;
	
	/**
	 * @param arg0
	 */
	public SettingsParseError(final String arg0, final IArgument<?> argument) {
		super(arg0);
		this.argument = argument;
	}
	
	/**
	 * @param arg0
	 * @param arg1
	 */
	public SettingsParseError(final String arg0, final IArgument<?> argument, final Throwable arg1) {
		super(arg0, arg1);
		this.argument = argument;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.getMessage());
		builder.append(FileUtils.lineSeparator).append(this.argument.getName());
		List<Requirement> requirements = this.argument.getRequirements().getMissingRequirements();
		builder.append(FileUtils.lineSeparator).append("Total dependencies: ").append(this.argument.getRequirements());
		
		if (requirements != null) {
			builder.append(FileUtils.lineSeparator).append("Unresolved dependencies: ")
			       .append(JavaUtils.collectionToString(requirements));
		}
		
		return builder.toString();
	}
	
}
