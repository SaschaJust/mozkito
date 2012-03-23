/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.hiari.settings;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.ownhero.dev.hiari.settings.StringArgument.Options;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.If;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.conditions.Condition;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MailOptions extends ArgumentSetOptions<Properties, ArgumentSet<Properties, MailOptions>> {
	
	private Options hostOption;
	private Options toOption;
	private Options subjectOption;
	private Options senderNameOption;
	private Options senderAddressOption;
	private Options passwordOption;
	
	private Options usernameOption;
	
	private Options senderHostOption;
	
	/**
	 * @param argumentSet
	 * @param name
	 * @param description
	 * @param requirements
	 * @param configurator
	 */
	public MailOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
		super(argumentSet, "mail", "Configures the settings for the crash mailer.", requirements);
	}
	
	/**
	 * @return the hostOption
	 */
	public final Options getHostOption() {
		// PRECONDITIONS
		
		try {
			return this.hostOption;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.hostOption, "Field '%s' in '%s'.", "hostOption", getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the passwordOption
	 */
	public final Options getPasswordOption() {
		// PRECONDITIONS
		
		try {
			return this.passwordOption;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.passwordOption, "Field '%s' in '%s'.", "passwordOption", getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the senderAddressOption
	 */
	public final Options getSenderAddressOption() {
		// PRECONDITIONS
		
		try {
			return this.senderAddressOption;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.senderAddressOption, "Field '%s' in '%s'.", "senderAddressOption",
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the senderHostOption
	 */
	public final Options getSenderHostOption() {
		// PRECONDITIONS
		
		try {
			return this.senderHostOption;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.senderHostOption, "Field '%s' in '%s'.", "senderHostOption",
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the senderNameOption
	 */
	public final Options getSenderNameOption() {
		// PRECONDITIONS
		
		try {
			return this.senderNameOption;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.senderNameOption, "Field '%s' in '%s'.", "senderNameOption",
			                  getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the subjectOption
	 */
	public final Options getSubjectOption() {
		// PRECONDITIONS
		
		try {
			return this.subjectOption;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.subjectOption, "Field '%s' in '%s'.", "subjectOption", getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the toOption
	 */
	public final Options getToOption() {
		// PRECONDITIONS
		
		try {
			return this.toOption;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.toOption, "Field '%s' in '%s'.", "toOption", getClass().getSimpleName());
		}
	}
	
	/**
	 * @return the usernameOption
	 */
	public final Options getUsernameOption() {
		// PRECONDITIONS
		
		try {
			return this.usernameOption;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.usernameOption, "Field '%s' in '%s'.", "usernameOption", getClass().getSimpleName());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ArgumentSetConfigurator#init()
	 */
	@Override
	public Properties init() {
		
		final Properties properties = new Properties() {
			
			private static final long serialVersionUID = -4075576523389682827L;
			
			{
				put("mail.smtp.host", getSettings().getArgument(getHostOption()).getValue());
				put("mail.transport.protocol", "smtp");
				put("mail.to", getSettings().getArgument(getToOption()).getValue());
				put("mail.subject", getSettings().getArgument(getSubjectOption()).getValue());
				put("mail.sender.name", getSettings().getArgument(getSenderNameOption()).getValue());
				put("mail.sender.address", getSettings().getArgument(getSenderAddressOption()).getValue());
				put("mail.sender.host", getSettings().getArgument(getSenderHostOption()).getValue());
				
				if (getSettings().getArgument(getUsernameOption()).getValue() != null) {
					put("mail.username", getSettings().getArgument(getUsernameOption()).getValue());
				}
				
				if (getSettings().getArgument(getPasswordOption()).getValue() != null) {
					put("mail.password", getSettings().getArgument(getPasswordOption()).getValue());
				}
				
			}
		};
		
		return properties;
	}
	
	private void req(final IOptions<?, ?> option,
	                 final Map<String, IOptions<?, ?>> map) {
		map.put(option.getName(), option);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ArgumentSetConfigurator#requirements()
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
		
		try {
			this.hostOption = new StringArgument.Options(set, "host", "The hostname of the mail server",
			                                             "mail.andama.org", Requirement.required);
			req(this.hostOption, map);
			
			this.toOption = new StringArgument.Options(set, "to", "The recipient of the crash mail", "mail@andama.org",
			                                           Requirement.required);
			req(this.toOption, map);
			
			this.subjectOption = new StringArgument.Options(set, "subject", "The subject of the crash mail",
			                                                "Application Crash Report", Requirement.required);
			req(this.subjectOption, map);
			
			this.senderNameOption = new StringArgument.Options(set, "senderName",
			                                                   "The name of the sender of the crash mail",
			                                                   "Andama Application", Requirement.required);
			req(this.senderNameOption, map);
			
			this.senderAddressOption = new StringArgument.Options(set, "senderAddress",
			                                                      "The address of the sender of the crash mail",
			                                                      "andama-crasher@st.cs.uni-saarland.de",
			                                                      Requirement.required);
			
			req(this.senderAddressOption, map);
			
			this.passwordOption = new StringArgument.Options(set, "password", "The smtp login password", null,
			                                                 Requirement.optional, true);
			req(this.passwordOption, map);
			
			this.usernameOption = new StringArgument.Options(set, "username", "The smtp login username", null,
			                                                 new If(map.get("password")));
			req(this.usernameOption, map);
			
			try {
				this.senderHostOption = new StringArgument.Options(set, "senderHost",
				                                                   "The hostname the crash mail is sent from",
				                                                   InetAddress.getLocalHost().getHostName(),
				                                                   Requirement.required);
				req(this.senderHostOption, map);
			} catch (final UnknownHostException e) {
				this.senderHostOption = new StringArgument.Options(set, "senderHost",
				                                                   "The hostname the crash mail is sent from",
				                                                   "localhost", Requirement.required);
				req(this.senderHostOption, map);
			}
			
			return map;
		} finally {
			// Condition.notNull(this.username, "Field '%s' in %s.", "username", getHandle());
			// Condition.notNull(this.password, "Field '%s' in %s.", "password", getHandle());
			// Condition.notNull(this.host, "Field '%s' in %s.", "host", getHandle());
			// Condition.notNull(this.to, "Field '%s' in %s.", "to", getHandle());
			// Condition.notNull(this.subject, "Field '%s' in %s.", "subject", getHandle());
			// Condition.notNull(this.senderName, "Field '%s' in %s.", "senderName", getHandle());
			// Condition.notNull(this.senderAddress, "Field '%s' in %s.", "senderAddress", getHandle());
			// Condition.notNull(getSenderHost(), "Field '%s' in %s.", "senderHost", getHandle());
		}
	}
	
}
