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

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.ownhero.dev.andama.messages.AccessLevel;
import net.ownhero.dev.andama.messages.IEvent;
import net.ownhero.dev.andama.messages.IEventListener;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.jibble.pircbot.PircBot;

/**
 * The Class IRCEventhandler.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class IRCEventhandler extends PircBot implements IEventListener {
	
	/** The Constant CHANNEL. */
	public static final String  CHANNEL           = "#mozkito";
	
	/** The Constant NAME. */
	public static final String  NAME              = "mozkito";
	
	/** The scheduled messages. */
	private final Queue<IEvent> scheduledMessages = new LinkedList<IEvent>();
	
	/**
	 * Instantiates a new iRC eventhandler.
	 * 
	 * @param owner
	 *            the owner
	 */
	public IRCEventhandler(final String owner) {
		setName(NAME);
		setLogin("otikzom");
	}
	
	/**
	 * Event to string.
	 * 
	 * @param event
	 *            the event
	 * @return the list
	 */
	private List<String> eventToString(@NotNull final IEvent event) {
		final List<String> list = new LinkedList<String>();
		list.add(String.format("New event [%s issued:%s/fired:%s instance:%s type:%s level:%s origin:%s]",
		                       event.getHandle(), event.getIssued(), event.getFired(), event.getHandle(),
		                       event.getType(), event.getLevel(), event.getOrigin()));
		list.add(String.format("Message: %s", event.getMessage()));
		return list;
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
				if (isConnected() && (getChannels().length > 0)) {
					for (final String s : eventToString(event)) {
						sendMessage(CHANNEL, s);
					}
				} else {
					this.scheduledMessages.add(event);
				}
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jibble.pircbot.PircBot#onJoin(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	protected void onJoin(final String channel,
	                      final String sender,
	                      final String login,
	                      final String hostname) {
		// PRECONDITIONS
		
		try {
			if (!this.scheduledMessages.isEmpty()) {
				while (!this.scheduledMessages.isEmpty()) {
					for (final String s : eventToString(this.scheduledMessages.poll())) {
						sendMessage(CHANNEL, s);
					}
				}
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
