/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.research.persons.github;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.event.Event;
import org.eclipse.egit.github.core.event.EventRepository;
import org.eclipse.egit.github.core.service.EventService;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

import org.mozkito.utilities.datastructures.Tuple;

/**
 * The Class GitHubAdapter.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class GitHubAdapter {
	
	/** The Constant MESSAGE_RATE_LIMIT. */
	static final String MESSAGE_RATE_LIMIT = "Rate limit exceeded";
	
	/** The Constant MAX_SEARCH_RATE. */
	static final int    MAX_SEARCH_RATE    = 20;
	
	/**
	 * Check rate limit.
	 * 
	 * @param service
	 *            the service
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static void checkRateLimit(final GitHubService service) throws IOException {
		if (service.getClient().getRemainingRequests() == 0) {
			throw new IOException(MESSAGE_RATE_LIMIT + ": " + service.getClient().getRequestLimit());
		}
		
		System.err.println(service.getClass().getSimpleName() + ": " + service.getClient().getRemainingRequests());
	}
	
	/**
	 * Throttle search.
	 * 
	 * @param record
	 *            the record
	 * @param max_rate
	 *            the max_rate
	 */
	public static void throttlePerMinute(final Tuple<DateTime, Integer> record,
	                                     final int max_rate) {
		final DateTime now = new DateTime();
		final DateTime time = record.getFirst().minusSeconds(record.getFirst().getSecondOfMinute());
		final Minutes minutes = Minutes.minutesBetween(time, now);
		if (minutes.getMinutes() == 0) {
			System.err.println("Calls made within this minute: " + record.getSecond());
			if (record.getSecond() >= (max_rate - 1)) {
				// reached limit
				try {
					final int sleepingSeconds = (Seconds.secondsBetween(time, new DateTime()).getSeconds() + 1);
					System.err.println("sleeping for: " + sleepingSeconds);
					
					Thread.sleep(sleepingSeconds * 1000);
					
					final DateTime store = now.minusSeconds(now.getSecondOfMinute());
					synchronized (record) {
						record.setFirst(store);
						record.setSecond(1);
					}
				} catch (final InterruptedException ignore) {
					// ignore
				}
			} else {
				synchronized (record) {
					record.setSecond(record.getSecond() + 1);
				}
			}
		} else {
			final DateTime store = now.minusSeconds(now.getSecondOfMinute());
			synchronized (record) {
				record.setFirst(store);
				record.setSecond(1);
			}
		}
	}
	
	/** The search throttling. */
	private final Tuple<DateTime, Integer> searchThrottling = new Tuple<DateTime, Integer>(new DateTime(), 0);
	// private Tuple<DateTime, Integer> requestThrottling = new Tuple<DateTime, Integer>(new DateTime(), 0);
	
	/** The user service. */
	private ExtendedUserService            userService;
	
	/** The repository service. */
	private RepositoryService              repositoryService;
	
	/** The event service. */
	private EventService                   eventService;
	/** The login at git hub. */
	private final Map<String, User>        loginAtGitHub    = new HashMap<>();
	
	/** The login404. */
	private final Set<String>              login404         = new HashSet<>();
	
	/** The contributions. */
	private final Map<User, Set<String>>   contributions    = new HashMap<>();
	
	/** The request counter. */
	private int                            requestCounter   = 0;
	
	/** The search counter. */
	private int                            searchCounter    = 0;
	
	/**
	 * Instantiates a new git hub adapter.
	 */
	public GitHubAdapter() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			final GitHubClient client = new GitHubClient();
			client.setOAuth2Token("d19785ed2d766f9ece077ecdc16ca80f92704bc4");
			this.userService = new ExtendedUserService(client);
			this.repositoryService = new RepositoryService(client);
			this.eventService = new EventService(client);
			System.err.println("Rate limit: " + client.getRequestLimit());
			System.err.println("Remaining: " + client.getRemainingRequests());
			
			// wait for the full minute to avoid test bursts to cause problems with the search rate limit
			try {
				DateTime now = new DateTime();
				final DateTime time = now.minusSeconds(now.getSecondOfMinute());
				
				Thread.sleep((60 - (((Seconds.secondsBetween(time, now).getSeconds())) + 1)) * 1000);
				
				now = new DateTime();
				final DateTime store = now.minusSeconds(now.getSecondOfMinute());
				synchronized (this.searchThrottling) {
					this.searchThrottling.setFirst(store);
					this.searchThrottling.setSecond(1);
				}
				
				this.userService.searchThrottling = this.searchThrottling;
			} catch (final InterruptedException ignore) {
				// ignore
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the contributions.
	 * 
	 * @param user
	 *            the user
	 * @return the contributions
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Set<String> getContributions(final User user) throws IOException {
		final Set<String> contributions = new HashSet<>();
		
		if (this.contributions.containsKey(user)) {
			return this.contributions.get(user);
		}
		
		checkRateLimit(this.eventService);
		final PageIterator<Event> userEventsIterator = this.eventService.pageUserEvents(user.getLogin());
		REPOSITORIES: for (Collection<Event> events = userEventsIterator.next(); userEventsIterator.hasNext(); events = userEventsIterator.next()) {
			++this.requestCounter;
			for (final Event event : events) {
				final EventRepository eventRepository = event.getRepo();
				if (eventRepository != null) {
					contributions.add(eventRepository.getName());
				}
			}
			
			checkRateLimit(this.eventService);
		}
		
		this.contributions.put(user, contributions);
		
		return contributions;
	}
	
	/**
	 * @return the requestCounter
	 */
	public int getRequestCounter() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.requestCounter;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return the searchCounter
	 */
	public int getSearchCounter() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.searchCounter;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the by login.
	 * 
	 * @param login
	 *            the login
	 * @return the by login
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public User getUserByLogin(final String login) throws IOException {
		if (this.login404.contains(login)) {
			return null;
		} else if (this.loginAtGitHub.containsKey(login)) {
			return this.loginAtGitHub.get(login);
		} else {
			checkRateLimit(this.userService);
			++this.requestCounter;
			return this.userService.getUser(login);
		}
		
	}
	
	/**
	 * Search users.
	 * 
	 * @param query
	 *            the query
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<SearchUser> searchUsers(final String query) throws IOException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			checkRateLimit(this.userService);
			throttlePerMinute(this.searchThrottling, MAX_SEARCH_RATE);
			++this.searchCounter;
			return this.userService.searchUsers(query);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Search users.
	 * 
	 * @param query
	 *            the query
	 * @param parameters
	 *            the parameters
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<SearchUser> searchUsers(final String query,
	                                    final Map<String, String> parameters) throws IOException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			checkRateLimit(this.userService);
			// throttlePerMinute(this.searchThrottling, MAX_SEARCH_RATE);
			++this.searchCounter;
			return this.userService.searchUsers(query, parameters);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("GitHubAdapter [requestCounter=");
		builder.append(this.requestCounter);
		builder.append(", searchCounter=");
		builder.append(this.searchCounter);
		builder.append("]");
		return builder.toString();
	}
}
