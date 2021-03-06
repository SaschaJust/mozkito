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

package org.mozkito.research.persons.engines;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.eclipse.egit.github.core.User;

import org.mozkito.persons.model.Person;
import org.mozkito.research.persons.Gravatar;
import org.mozkito.research.persons.github.GitHubAdapter;
import org.mozkito.research.persons.github.SearchUser;
import org.mozkito.utilities.datastructures.Tuple;

/**
 * The Class GitHubEngine.
 * 
 * TODO consider fetch limit TODO read auth token from file TODO somehow set affiliation TODO somehow set repoName
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class GitHubEngine extends Engine {
	
	/** The Constant DEFAULT_CONFIDENCE. */
	private static final Double     DEFAULT_CONFIDENCE = 1.0d;
	
	/** The email to git hub. */
	private final Map<String, User> emailToGitHub      = new HashMap<>();
	
	/** The user to git hub. */
	private final Map<String, User> userToGitHub       = new HashMap<>();
	
	/** The fullname to git hub. */
	private final Map<String, User> fullnameToGitHub   = new HashMap<>();
	
	/** The email404. */
	private final Set<String>       email404           = new HashSet<>();
	
	/** The user404. */
	private final Set<String>       user404            = new HashSet<>();
	
	/** The fullname404. */
	private final Set<String>       fullname404        = new HashSet<>();
	
	/** The affiliation. */
	private String                  affiliation;
	
	/** The repo name. */
	private String                  repositoryName;
	
	/** The git hub adapter. */
	private GitHubAdapter           gitHubAdapter;
	
	/**
	 * Instantiates a new git hub engine.
	 */
	public GitHubEngine() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
			this.affiliation = "mozkito";
			this.repositoryName = "mozkito";
			this.gitHubAdapter = new GitHubAdapter();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Check fetch.
	 * 
	 * @param key
	 *            the key
	 * @param set404
	 *            the set404
	 * @param fetched
	 *            the fetched
	 * @return the tuple
	 */
	private Tuple<Boolean, User> checkFetch(final String key,
	                                        final Set<String> set404,
	                                        final Map<String, User> fetched) {
		if (set404.contains(key)) {
			return new Tuple<Boolean, User>(false, null);
		} else if (fetched.containsKey(key)) {
			return new Tuple<>(true, fetched.get(key));
		}
		
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.research.persons.engines.Engine#confidence(org.mozkito.persons.model.Person,
	 *      org.mozkito.persons.model.Person)
	 */
	@Override
	public Double confidence(final Person p1,
	                         final Person p2) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final User u1 = fetchGitHub(p1);
			final User u2 = fetchGitHub(p2);
			
			if (u1 != null) {
				if (u2 != null) {
					if (u1.getLogin().equals(u2.getLogin())) {
						return DEFAULT_CONFIDENCE;
					} else {
						return 0d;
					}
				} else {
					return personToUserCompare(p2, u1);
				}
				
			} else {
				if (u2 != null) {
					return personToUserCompare(p1, u2);
				}
			}
			
			return 0d;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Fetch by email.
	 * 
	 * @param email
	 *            the email
	 * @return the user
	 */
	private User fetchByEmail(final String email) {
		PRECONDITIONS: {
			if (email == null) {
				throw new NullPointerException("email");
			}
		}
		
		try {
			try {
				List<SearchUser> users = this.gitHubAdapter.searchUsers(email);
				
				SANITY: {
					assert users != null;
				}
				
				User user = null;
				if (!users.isEmpty()) {
					SEARCHUSERS: for (final SearchUser searchUser : users) {
						final User u = this.gitHubAdapter.getUserByLogin(searchUser.getLogin());
						if (u.getEmail().equalsIgnoreCase(email)) {
							user = u;
							break SEARCHUSERS;
						}
					}
				}
				
				if (user == null) {
					// apply some heuristics
					
					// fetch gravatar
					final Gravatar gravatar = Gravatar.fromEmail(email);
					
					// first search for the email prefix
					SANITY: {
						assert email.contains("@");
					}
					final String[] split = email.split("@");
					
					SANITY: {
						assert split != null;
						assert split.length == 2;
					}
					final String prefix = split[0];
					final String host = split[1];
					users = this.gitHubAdapter.searchUsers(prefix);
					
					SANITY: {
						assert users != null;
					}
					
					if (!users.isEmpty()) {
						final Set<User> candidates = new HashSet<>();
						
						for (final SearchUser sUser : users) {
							final User u = this.gitHubAdapter.getUserByLogin(sUser.getLogin());
							
							SANITY: {
								assert u != null;
							}
							
							if ((u.getEmail() != null)
							        && (u.getEmail().contains("@" + host) || u.getEmail()
							                                                  .contains("@" + this.affiliation))) {
								// check if email has the same hostname or contains the affiliation
								candidates.add(u);
							} else if ((u.getCompany() != null)
							        && u.getCompany().toLowerCase().contains(this.affiliation)) {
								// check if company contains affiliation
								candidates.add(u);
							} else if ((sUser.getOrganizationsUrl() != null)
							        && (sUser.getOrganizationsUrl().contains(host) || sUser.getOrganizationsUrl()
							                                                               .contains(this.affiliation))) {
								// check if organization url contains affiliation
								candidates.add(u);
							} else {
								// check email gravatar against gravatar hash from github
								if (gravatar != null) {
									Gravatar g2 = Gravatar.fromHash(u.getGravatarId());
									if ((g2 != null) && gravatar.equals(g2)) {
										candidates.add(u);
									} else {
										// check gravatar of github email against email gravatar (which have to be
										// different at this point)
										if (u.getEmail() != null) {
											SANITY: {
												assert !email.equalsIgnoreCase(u.getEmail());
											}
											
											g2 = Gravatar.fromEmail(u.getEmail());
											if ((g2 != null) && gravatar.equals(g2)) {
												candidates.add(u);
											}
										}
									}
								}
							}
						}
						
						if (candidates.isEmpty()) {
							// no candidate found yet
							// check affiliated repositories (this is quite expensive. don't do this if you already have
							// your match)
							
							for (final SearchUser sUser : users) {
								final User u = this.gitHubAdapter.getUserByLogin(sUser.getLogin());
								
								// check repositories owned by the user
								// final List<Repository> repositories = this.repoService.getRepositories(u.getLogin());
								//
								// REPOSITORIES: for (final Repository repository : repositories) {
								// if (repository.getName().equalsIgnoreCase(this.repoName)) {
								// candidates.add(u);
								// }
								// break REPOSITORIES;
								// }
								//
								// check repository contributions
								// final PageIterator<Event> userEventsIterator =
								// this.gitHubAdapter.pageUserEvents(u.getLogin());
								// REPOSITORIES: for (Collection<Event> events = userEventsIterator.next();
								// userEventsIterator.hasNext(); events = userEventsIterator.next()) {
								// for (final Event event : events) {
								// final EventRepository eventRepository = event.getRepo();
								// if (eventRepository != null) {
								// if (eventRepository.getName().equalsIgnoreCase(this.reposityName)) {
								// candidates.add(u);
								// }
								// }
								// }
								// }
								final Set<String> contributions = this.gitHubAdapter.getContributions(u);
								if (contributions.contains(this.repositoryName)) {
									candidates.add(u);
								}
							}
						}
						
						if (candidates.size() > 1) {
							if (Logger.logWarn()) {
								Logger.warn("Multiple choices found for email '%s'. Cannot decide.", email);
								for (final SearchUser sUser : users) {
									final User theChoice = this.gitHubAdapter.getUserByLogin(sUser.getLogin());
									Logger.warn("login=%s, name=%s, email=%s, affiliation=%s", theChoice.getLogin(),
									            theChoice.getName(), theChoice.getEmail(), theChoice.getCompany());
								}
							}
						} else if (candidates.isEmpty()) {
							// nothing found
						} else {
							user = candidates.iterator().next();
						}
					}
				}
				
				return user;
			} catch (final IOException e) {
				throw new UnrecoverableError(e);
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Fetch by fullname.
	 * 
	 * @param fullname
	 *            the fullname
	 * @return the user
	 */
	private User fetchByFullname(final String fullname) {
		PRECONDITIONS: {
			if (fullname == null) {
				throw new NullPointerException("email");
			}
		}
		
		try {
			final Map<String, String> parameters = new HashMap<>();
			parameters.put("login", fullname);
			try {
				final List<SearchUser> users = this.gitHubAdapter.searchUsers(fullname, parameters);
				
				SANITY: {
					assert users != null;
					assert users.size() <= 1 : "There can't be more than 1 user with the same email.";
				}
				
				User user = null;
				if (!users.isEmpty()) {
					SANITY: {
						assert users.iterator().hasNext();
					}
					
					final SearchUser searchUser = users.iterator().next();
					user = this.gitHubAdapter.getUserByLogin(searchUser.getLogin());
				}
				
				return user;
			} catch (final IOException e) {
				throw new UnrecoverableError(e);
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Fetch by username.
	 * 
	 * @param username
	 *            the username
	 * @return the user
	 */
	private User fetchByUsername(final String username) {
		PRECONDITIONS: {
			if (username == null) {
				throw new NullPointerException("username");
			}
		}
		
		try {
			User user = null;
			try {
				user = this.gitHubAdapter.getUserByLogin(username);
			} catch (final IOException e1) {
				throw new UnrecoverableError(e1);
			}
			
			if (user == null) {
				// perform search based on heuristics
				// final Map<String, String> parameters = new HashMap<>();
				// parameters.put("login", username);
				// try {
				// final List<SearchUser> users = this.service.searchUsers(username, parameters);
				//
				// SANITY: {
				// assert users != null;
				// assert users.size() <= 1 : "There can't be more than 1 user with the same login.";
				// }
				//
				// User user = null;
				// if (!users.isEmpty()) {
				// SANITY: {
				// assert users.iterator().hasNext();
				// }
				//
				// final SearchUser searchUser = users.iterator().next();
				// user = this.service.getUser(searchUser.getLogin());
				// }
				//
				// return user;
				// } catch (final IOException e) {
				// throw new UnrecoverableError(e);
				// }
			}
			
			return user;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Fetch git hub.
	 * 
	 * @param person
	 *            the person
	 * @return the user
	 */
	private User fetchGitHub(final Person person) {
		PRECONDITIONS: {
			if (person == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			SANITY: {
				assert this.gitHubAdapter != null;
			}
			
			User user = null;
			
			// check if we already attempted to find the user
			
			EMAILS: for (final String email : person.getEmailAddresses()) {
				final Tuple<Boolean, User> check = checkFetch(email, this.email404, this.emailToGitHub);
				if (check != null) {
					return check.getSecond();
				} else {
					user = fetchByEmail(email);
					if (user != null) {
						return user;
					}
				}
			}
			
			USERNAMES: for (final String username : person.getUsernames()) {
				final Tuple<Boolean, User> check = checkFetch(username, this.user404, this.userToGitHub);
				if (check != null) {
					return check.getSecond();
				} else {
					user = fetchByUsername(username);
					if (user != null) {
						return user;
					}
				}
			}
			
			FULLNAMES: for (final String fullname : person.getFullnames()) {
				final Tuple<Boolean, User> check = checkFetch(fullname, this.fullname404, this.fullnameToGitHub);
				if (check != null) {
					return check.getSecond();
				} else {
					user = fetchByFullname(fullname);
					if (user != null) {
						return user;
					}
				}
			}
			
			return user;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the affiliation.
	 * 
	 * @return the affiliation
	 */
	public String getAffiliation() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.affiliation;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.affiliation, "Field '%s' in '%s'.", "affiliation", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Gets the reposity name.
	 * 
	 * @return the reposityName
	 */
	public String getReposityName() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.repositoryName;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.repositoryName,
				                  "Field '%s' in '%s'.", "repositoryName", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Person to user compare.
	 * 
	 * @param person
	 *            the person
	 * @param user
	 *            the user
	 * @return the double
	 */
	private Double personToUserCompare(final Person person,
	                                   final User user) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			
			// TODO improve this
			if (person.getUsernames().contains(user.getLogin()) || person.getEmailAddresses().contains(user.getEmail())) {
				return 1d;
			} else {
				return 0d;
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the affiliation.
	 * 
	 * @param affiliation
	 *            the affiliation to set
	 */
	public void setAffiliation(final String affiliation) {
		PRECONDITIONS: {
			Condition.notNull(affiliation, "Argument '%s' in '%s'.", "affiliation", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		try {
			this.affiliation = affiliation;
		} finally {
			POSTCONDITIONS: {
				CompareCondition.equals(this.affiliation, affiliation,
				                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
			}
		}
	}
	
	/**
	 * Sets the reposity name.
	 * 
	 * @param reposityName
	 *            the reposityName to set
	 */
	public void setReposityName(final String reposityName) {
		PRECONDITIONS: {
			Condition.notNull(reposityName, "Argument '%s' in '%s'.", "reposityName", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		try {
			this.repositoryName = reposityName;
		} finally {
			POSTCONDITIONS: {
				CompareCondition.equals(this.repositoryName, reposityName,
				                        "After setting a value, the corresponding field has to hold the same value as used as a parameter within the setter."); //$NON-NLS-1$
			}
		}
	}
	
}
