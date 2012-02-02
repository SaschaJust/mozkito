/**
 * 
 */
package net.ownhero.dev.andama.settings.dependencies;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.settings.AndamaArgument;
import net.ownhero.dev.andama.settings.AndamaArgumentInterface;
import net.ownhero.dev.andama.settings.BooleanArgument;
import net.ownhero.dev.andama.settings.DoubleArgument;
import net.ownhero.dev.andama.settings.EnumArgument;
import net.ownhero.dev.andama.settings.LongArgument;
import net.ownhero.dev.andama.settings.StringArgument;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Equals extends Requirement {
	
	private final AndamaArgument<?>    argument;
	private AndamaArgumentInterface<?> depender;
	private Object                     value;
	
	/**
	 * @param argument
	 * @param value
	 */
	public Equals(final BooleanArgument argument, final boolean value) {
		this.argument = argument;
		this.value = value;
	}
	
	/**
	 * @param argument
	 * @param value
	 */
	public Equals(final DoubleArgument argument, final double value) {
		this.argument = argument;
		this.value = value;
	}
	
	/**
	 * @param argument
	 * @param value
	 */
	public Equals(final EnumArgument argument, final Enum<?> value) {
		this.argument = argument;
		this.value = value;
	}
	
	/**
	 * @param argument
	 * @param value
	 */
	public Equals(final LongArgument argument, final long value) {
		this.argument = argument;
		this.value = value;
	}
	
	/**
	 * @param argument
	 * @param depender
	 */
	public Equals(final StringArgument argument, final AndamaArgumentInterface<?> depender) {
		this.argument = argument;
		this.depender = depender;
	}
	
	/**
	 * @param argument
	 * @param value
	 */
	public Equals(final StringArgument argument, final String value) {
		this.argument = argument;
		this.value = value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.dependencies.Expression#check()
	 */
	@Override
	public boolean check() {
		if (this.argument != null) {
			if (this.depender != null) {
				return this.argument.equals(this.depender.getName());
			} else {
				return this.argument.getValue().equals(this.value);
			}
		} else {
			return false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.dependencies.Expression#getDependencies()
	 */
	@Override
	public Set<AndamaArgumentInterface<?>> getDependencies() {
		HashSet<AndamaArgumentInterface<?>> dependencies = new HashSet<AndamaArgumentInterface<?>>();
		dependencies.add(this.argument);
		
		return dependencies;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.settings.dependencies.Expression#getFailureCause()
	 */
	@Override
	public List<Requirement> getMissingRequirements() {
		return check()
		              ? null
		              : new LinkedList<Requirement>() {
			              
			              private static final long serialVersionUID = 1L;
			              
			              {
				              add(Equals.this);
			              }
		              };
	}
	
}
