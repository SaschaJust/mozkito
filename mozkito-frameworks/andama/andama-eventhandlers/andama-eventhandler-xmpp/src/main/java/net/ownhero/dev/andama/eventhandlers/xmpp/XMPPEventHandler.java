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
package net.ownhero.dev.andama.eventhandlers.xmpp;

import net.ownhero.dev.andama.messages.IEvent;
import net.ownhero.dev.andama.messages.IEventListener;
import net.ownhero.dev.andama.messages.StartupEvent;
import net.ownhero.dev.kisa.Logger;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.spark.util.DummySSLSocketFactory;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class XMPPEventHandler implements IEventListener {
	
	public static void main(final String[] args) {
		final XMPPEventHandler handler = new XMPPEventHandler();
		handler.handle(new StartupEvent("test"));
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.messages.IEventListener#handle(net.ownhero.dev.andama.messages.IEvent)
	 */
	@Override
	public void handle(final IEvent event) {
		// PRECONDITIONS
		
		try {
			final ConnectionConfiguration connConfig = new ConnectionConfiguration("talk.mozkito.org", 5223,
			                                                                       "mozkito.org");
			connConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.enabled);
			connConfig.setSocketFactory(new DummySSLSocketFactory());
			
			final XMPPConnection connection = new XMPPConnection(connConfig);
			
			try {
				connection.connect();
				
				// login with username and password
				connection.login("mozkito", "XXX");
				
				// set presence status info
				Presence presence = new Presence(Presence.Type.available);
				connection.sendPacket(presence);
				
				// send a message to somebody
				final Message msg = new Message("methos@mozkito.org", Message.Type.chat);
				msg.setBody("hello");
				connection.sendPacket(msg);
				
				presence = new Presence(Presence.Type.unavailable);
				connection.sendPacket(presence);
			} catch (final XMPPException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			}
		} finally {
			// POSTCONDITIONS
		}
	}
}
