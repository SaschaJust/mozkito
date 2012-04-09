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
package dev.ownhero.net.andama.eventhandlers.irc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.andama.messages.EventsOptions;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.ISettings;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.LongArgument.Options;
import net.ownhero.dev.hiari.settings.SettingsProvider;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

/**
 * The Class IRCThread.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class IRCThread extends Thread implements SettingsProvider {
	
	private static class IRCEventOptions extends ArgumentSetOptions<Boolean, ArgumentSet<Boolean, IRCEventOptions>> {
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
		 */
		public IRCEventOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, "irc", "Encapsulates settings for the IRCEventHandler.", requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public Boolean init() {
			// PRECONDITIONS
			
			try {
				return true;
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
		 */
		@Override
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
		                                                                                    SettingsParseError {
			// PRECONDITIONS
			
			try {
				return new HashMap<String, IOptions<?, ?>>();
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The bot. */
	private final IRCEventhandler                                 bot;
	
	private ISettings                                             settings;
	private Options                                               portOption;
	private net.ownhero.dev.hiari.settings.StringArgument.Options hostOption;
	private StringArgument                                        hostArgument;
	private String                                                host;
	private LongArgument                                          portArgument;
	private int                                                   port;
	
	/**
     * 
     */
	public IRCThread(final String owner) {
		// PRECONDITIONS
		
		try {
			this.bot = new IRCEventhandler(owner);;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the bot.
	 * 
	 * @return the bot
	 */
	public final IRCEventhandler getBot() {
		// PRECONDITIONS
		
		try {
			return this.bot;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.bot, "Field '%s' in '%s'.", "bot", getClass().getSimpleName());
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void init() {
		// PRECONDITIONS
		
		try {
			this.hostArgument = this.settings.getArgument(this.hostOption);
			this.host = this.hostArgument.getValue();
			this.portArgument = this.settings.getArgument(this.portOption);
			this.port = (int) (long) this.portArgument.getValue();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	@Override
	public ArgumentSet<?, ?> provide(final ArgumentSet<?, ?> root) throws ArgumentRegistrationException,
	                                                              ArgumentSetRegistrationException,
	                                                              SettingsParseError {
		// PRECONDITIONS
		
		try {
			this.settings = root.getSettings();
			final EventsOptions eventsOptions = new EventsOptions(root, Requirement.required);
			ArgumentSet<Boolean, EventsOptions> set = root.getSettings().getArgumentSet(eventsOptions);
			if (set == null) {
				set = ArgumentSetFactory.create(eventsOptions);
			}
			final ArgumentSet<Boolean, IRCEventOptions> returnSet = ArgumentSetFactory.create(new IRCEventOptions(
			                                                                                                      set,
			                                                                                                      Requirement.required));
			this.hostOption = new StringArgument.Options(returnSet, "host", "Hostname used to connect the bot to.",
			                                             "irc.own-hero.net", Requirement.required);
			this.portOption = new LongArgument.Options(returnSet, "port", "The port used to connect the bot.", 6667l,
			                                           Requirement.required);
			
			return returnSet;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#init()
	 */
	@Override
	public void run() {
		// PRECONDITIONS
		
		try {
			
			// Enable debugging output.
			this.bot.setVerbose(true);
			
			// Connect to the IRC server.
			try {
				this.bot.connect(this.host, this.port);
			} catch (final NickAlreadyInUseException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			} catch (final IOException | IrcException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			}
			
			// Join the #pircbot channel.
			this.bot.joinChannel(IRCEventhandler.CHANNEL);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#provide(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	/**
	 * Shutdown.
	 */
	public synchronized void shutdown() {
		if ((this.bot != null) && this.bot.isConnected()) {
			this.bot.disconnect();
		}
	}
}
