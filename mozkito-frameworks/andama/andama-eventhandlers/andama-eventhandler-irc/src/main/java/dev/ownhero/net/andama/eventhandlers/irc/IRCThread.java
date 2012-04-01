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

import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class IRCThread extends Thread {
	
	private final IRCBot bot = new IRCBot(); ;
	
	/**
	 * @return the bot
	 */
	public final IRCBot getBot() {
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
	public void run() {
		// PRECONDITIONS
		
		try {
			
			// Enable debugging output.
			this.bot.setVerbose(true);
			
			// Connect to the IRC server.
			try {
				this.bot.connect("irc.own-hero.net", 6667);
			} catch (final NickAlreadyInUseException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			} catch (final IOException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			} catch (final IrcException e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
			}
			
			// Join the #pircbot channel.
			this.bot.joinChannel(IRCBot.CHANNEL);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public synchronized void shutdown() {
		if ((this.bot != null) && this.bot.isConnected()) {
			this.bot.disconnect();
		}
	}
}
