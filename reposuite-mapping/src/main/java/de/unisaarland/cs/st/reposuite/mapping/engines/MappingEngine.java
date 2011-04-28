/**
 * 
 */
package de.unisaarland.cs.st.reposuite.mapping.engines;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public abstract class MappingEngine {
	
	private MappingSettings settings;
	private boolean         registered  = false;
	private boolean         initialized = false;
	
	public MappingEngine(final MappingSettings settings) {
		setSettings(settings);
	}
	
	/**
	 * @return the settings
	 */
	public MappingSettings getSettings() {
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
	 * @param util
	 */
	public void loadData(final PersistenceUtil util) {
		
	}
	
	/**
	 * @param settings
	 * @param arguments
	 * @param isRequired
	 */
	@NoneNull
	public void register(final MappingSettings settings,
	                     final MappingArguments arguments,
	                     final boolean isRequired) {
		setRegistered(true);
	}
	
	/**
	 * @param transaction
	 * @param report
	 * @param score
	 */
	@NoneNull
	public void score(final RCSTransaction transaction,
	                  final Report report,
	                  final MapScore score) {
		Condition.check(isInitialized(), "The engine has to be initialized before it can be used. Engine: %s",
		                this.getClass().getSimpleName());
	}
	
	/**
	 * @param initialized the initialized to set
	 */
	void setInitialized(final boolean initialized) {
		this.initialized = initialized;
	}
	
	/**
	 * @param registered the registered to set
	 */
	void setRegistered(final boolean registered) {
		this.registered = registered;
	}
	
	/**
	 * @param settings the settings to set
	 */
	public void setSettings(final MappingSettings settings) {
		this.settings = settings;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MappingEngine [class=");
		builder.append(this.getClass().getSimpleName());
		builder.append("registered=");
		builder.append(this.registered);
		builder.append(", initialized=");
		builder.append(this.initialized);
		builder.append("]");
		return builder.toString();
	}
	
}
