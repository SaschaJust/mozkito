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
package net.ownhero.dev.andama.eventhandlers.email;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.ownhero.dev.andama.messages.IEvent;
import net.ownhero.dev.andama.messages.IEventListener;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class EmailEventHandler.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class EmailEventHandler implements IEventListener {
	
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
	public void handle(final IEvent event) {
		// PRECONDITIONS
		
		try {
			try {
				final Properties mailProps = new Properties();
				
				final Session session = Session.getDefaultInstance(mailProps, null);
				final Transport transport = session.getTransport();
				final MimeMessage message = new MimeMessage(session);
				message.setSubject(mailProps.getProperty("mail.subject"));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailProps.getProperty("mail.to")));
				message.setFrom(new InternetAddress(mailProps.getProperty("mail.sender.address"),
				                                    mailProps.getProperty("mail.sender.name")));
				message.setSender(new InternetAddress(mailProps.getProperty("mail.sender.address"),
				                                      mailProps.getProperty("mail.sender.name")));
				message.setContent(eventToString(event), "text/plain");
				if (mailProps.contains("mail.username") && mailProps.contains("mail.password")) {
					transport.connect(mailProps.getProperty("mail.username"), mailProps.getProperty("mail.password"));
				} else {
					transport.connect();
				}
				transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
				transport.close();
			} catch (final MessagingException e) {
				if (Logger.logWarn()) {
					Logger.warn(e);
				}
			} catch (final UnsupportedEncodingException e) {
				if (Logger.logWarn()) {
					Logger.warn(e);
				}
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
}
