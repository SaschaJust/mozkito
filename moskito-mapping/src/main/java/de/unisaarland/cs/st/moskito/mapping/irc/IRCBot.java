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

import net.ownhero.dev.andama.messages.AccessLevel;
import net.ownhero.dev.andama.messages.IEvent;
import net.ownhero.dev.andama.messages.IEventListener;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.jibble.pircbot.PircBot;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class IRCBot extends PircBot implements IEventListener {
	
	public static final String CHANNEL = "#mozkito";
	public static final String NAME    = "mozkito";
	
	public IRCBot() {
		setName(NAME);
		setLogin("otikzom");
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.messages.IEventListener#handle(net.ownhero.dev.andama.messages.IEvent)
	 */
	@Override
	public void handle(@NotNull final IEvent event) {
		// PRECONDITIONS
		
		try {
			if (event.getLevel().equals(AccessLevel.PUBLIC)) {
				sendMessage(CHANNEL, event.getMessage());
				event.accept();
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jibble.pircbot.PircBot#onMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	protected void onMessage(final String channel,
	                         final String sender,
	                         final String login,
	                         final String hostname,
	                         final String message) {
		// PRECONDITIONS
		
		try {
			if (message.equalsIgnoreCase("status")) {
				final String time = new java.util.Date().toString();
				sendMessage(channel, sender + ": I'm up and running. Current time: " + time);
			}
		} finally {
			// POSTCONDITIONS
		}
	}
}
