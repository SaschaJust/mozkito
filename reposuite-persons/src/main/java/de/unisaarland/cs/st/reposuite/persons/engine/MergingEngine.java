/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons.engine;

import java.util.List;
import java.util.Map;

import net.ownhero.dev.kanuni.conditions.Condition;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.persistence.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.persons.elements.PersonBucket;
import de.unisaarland.cs.st.reposuite.persons.processing.PersonManager;
import de.unisaarland.cs.st.reposuite.persons.settings.PersonsArguments;
import de.unisaarland.cs.st.reposuite.persons.settings.PersonsSettings;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class MergingEngine {
	
	private PersonsSettings settings;
	private boolean         registered  = false;
	private boolean         initialized = false;
	
	public MergingEngine() {
		
	}
	
	/**
	 * @param person
	 * @param container
	 * @param manager
	 * @param features
	 * @return
	 */
	public abstract List<PersonBucket> collides(Person person,
	                                            PersonContainer container,
	                                            PersonManager manager,
	                                            Map<Class<? extends MergingEngine>, Boolean> features);
	
	/**
	 * @return
	 */
	public abstract String getDescription();
	
	/**
	 * @return the settings
	 */
	public PersonsSettings getSettings() {
		return this.settings;
	}
	
	/**
	 * 
	 */
	public void init() {
		Condition.check(isRegistered(), "The engine has to be registered before it is initialized. Engine: %s",
		                this.getClass().getSimpleName());
		setInitialized(true);
	}
	
	/**
	 * @return the initialized
	 */
	public boolean isInitialized() {
		return this.initialized;
	}
	
	/**
	 * @return the registered
	 */
	public boolean isRegistered() {
		return this.registered;
	}
	
	/**
	 * @param settings
	 * @param personsArguments
	 * @param isRequired
	 */
	public void register(final PersonsSettings settings,
	                     final PersonsArguments personsArguments,
	                     final boolean isRequired) {
		setSettings(settings);
		setRegistered(true);
	}
	
	/**
	 * @param initialized the initialized to set
	 */
	public void setInitialized(final boolean initialized) {
		this.initialized = initialized;
	}
	
	/**
	 * @param registered the registered to set
	 */
	public void setRegistered(final boolean registered) {
		this.registered = registered;
	}
	
	/**
	 * @param settings the settings to set
	 */
	public void setSettings(final PersonsSettings settings) {
		this.settings = settings;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MergingEngine [settings=");
		builder.append(this.settings);
		builder.append(", registered=");
		builder.append(this.registered);
		builder.append(", initialized=");
		builder.append(this.initialized);
		builder.append("]");
		return builder.toString();
	}
}
