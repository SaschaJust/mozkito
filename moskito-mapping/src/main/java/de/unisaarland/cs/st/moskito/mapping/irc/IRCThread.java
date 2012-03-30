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
package de.unisaarland.cs.st.moskito.mapping.irc;

import java.io.IOException;

import net.ownhero.dev.kisa.Logger;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class IRCThread extends Thread {
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// PRECONDITIONS
		
		try {
			
			// Now start our bot up.
			final IRCBot bot = new IRCBot();
			
			// Enable debugging output.
			bot.setVerbose(true);
			
			// Connect to the IRC server.
			try {
				bot.connect("irc.own-hero.net", 6667);
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
			bot.joinChannel(IRCBot.CHANNEL);
		} finally {
			// POSTCONDITIONS
		}
	}
}
