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

import static org.eclipse.egit.github.core.client.IGitHubConstants.CHARSET_UTF8;
import static org.eclipse.egit.github.core.client.IGitHubConstants.PARAM_LANGUAGE;
import static org.eclipse.egit.github.core.client.IGitHubConstants.PARAM_START_PAGE;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_FIRST;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_SIZE;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.eclipse.egit.github.core.IResourceProvider;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;
import org.eclipse.egit.github.core.service.UserService;

/**
 * The Class ExtendedUserService.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ExtendedUserService extends UserService {
	
	/**
	 * The Class UserContainter.
	 */
	private static class UserContainter implements IResourceProvider<SearchUser> {
		
		/** The users. */
		@SerializedName ("items")
		private List<SearchUser> users;
		
		/**
		 * Gets the resources.
		 * 
		 * @return the resources
		 * @see org.eclipse.egit.github.core.IResourceProvider#getResources()
		 */
		public List<SearchUser> getResources() {
			return this.users;
		}
	}
	
	/** The Constant SEGMENT_SEARCH. */
	public static final String SEGMENT_SEARCH = "/search";
	
	/** The Constant SEGMENT_USERS. */
	public static final String SEGMENT_USERS  = "/users";
	
	/**
	 * Instantiates a new extended user service.
	 */
	public ExtendedUserService() {
		super();
	}
	
	/**
	 * Instantiates a new extended user service.
	 * 
	 * @param client
	 *            the client
	 */
	public ExtendedUserService(final GitHubClient client) {
		super(client);
		PRECONDITIONS: {
			// none
		}
		
		try {
			// body
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the users.
	 * 
	 * @param filterData
	 *            the filter data
	 * @return the users
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<User> getUsers(final Map<String, String> filterData) throws IOException {
		return getAll(pageUsers(filterData));
	}
	
	/**
	 * Page users.
	 * 
	 * @param filterData
	 *            the filter data
	 * @return the page iterator
	 */
	public PageIterator<User> pageUsers(final Map<String, String> filterData) {
		return pageUsers(filterData, PAGE_SIZE);
	}
	
	/**
	 * Page users.
	 * 
	 * @param filterData
	 *            the filter data
	 * @param size
	 *            the size
	 * @return the page iterator
	 */
	public PageIterator<User> pageUsers(final Map<String, String> filterData,
	                                    final int size) {
		return pageUsers(filterData, PAGE_FIRST, size);
	}
	
	/**
	 * Page repositories.
	 * 
	 * @param filterData
	 *            the filter data
	 * @param start
	 *            the start
	 * @param size
	 *            the size
	 * @return the page iterator
	 */
	public PageIterator<User> pageUsers(final Map<String, String> filterData,
	                                    final int start,
	                                    final int size) {
		final PagedRequest<User> request = createPagedRequest(start, size);
		request.setUri(SEGMENT_SEARCH + SEGMENT_USERS);
		request.setParams(filterData);
		request.setType(new TypeToken<List<User>>() {/* stub */
		}.getType());
		System.err.println(request.generateUri());
		return createPageIterator(request);
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
			return searchUsers(query, -1);
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
	 * @param startPage
	 *            the start page
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<SearchUser> searchUsers(final String query,
	                                    final int startPage) throws IOException {
		return searchUsers(query, (String) null, startPage);
	}
	
	/**
	 * Search users.
	 * 
	 * @param query
	 *            the query
	 * @param params
	 *            the params
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<SearchUser> searchUsers(final String query,
	                                    final Map<String, String> params) throws IOException {
		return searchUsers(query, params, -1);
	}
	
	/**
	 * Search users.
	 * 
	 * @param query
	 *            the query
	 * @param queryParams
	 *            the query params
	 * @param startPage
	 *            the start page
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<SearchUser> searchUsers(final String query,
	                                    final Map<String, String> queryParams,
	                                    final int startPage) throws IOException {
		if (queryParams == null) {
			throw new IllegalArgumentException("Params cannot be null"); //$NON-NLS-1$
		}
		if (queryParams.isEmpty()) {
			throw new IllegalArgumentException("Params cannot be empty"); //$NON-NLS-1$
		}
		
		final StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(query.replace(" ", "%20"));
		
		for (final Entry<String, String> param : queryParams.entrySet()) {
			queryBuilder.append('+').append(param.getKey()).append(':').append(param.getValue());
		}
		return searchUsers(query.toString(), startPage);
	}
	
	/**
	 * Search users.
	 * 
	 * @param query
	 *            the query
	 * @param language
	 *            the language
	 * @param startPage
	 *            the start page
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<SearchUser> searchUsers(final String query,
	                                    final String language,
	                                    final int startPage) throws IOException {
		if (query == null) {
			throw new IllegalArgumentException("Query cannot be null"); //$NON-NLS-1$
		}
		if (query.length() == 0) {
			throw new IllegalArgumentException("Query cannot be empty"); //$NON-NLS-1$
		}
		
		// final StringBuilder uri = new StringBuilder(SEGMENT_LEGACY + SEGMENT_USERS + SEGMENT_SEARCH);
		final StringBuilder uri = new StringBuilder(SEGMENT_SEARCH + SEGMENT_USERS);
		final String encodedQuery = URLEncoder.encode(query, CHARSET_UTF8);
		//												.replace("+", "%20") //$NON-NLS-1$ //$NON-NLS-2$
		//		                                      .replace(".", "%2E"); //$NON-NLS-1$ //$NON-NLS-2$
		
		uri.append("?q=").append(encodedQuery);
		
		final PagedRequest<SearchUser> request = createPagedRequest();
		
		final Map<String, String> params = new HashMap<String, String>(2, 1);
		if ((language != null) && (language.length() > 0)) {
			params.put(PARAM_LANGUAGE, language);
		}
		if (startPage > 0) {
			params.put(PARAM_START_PAGE, Integer.toString(startPage));
		}
		if (!params.isEmpty()) {
			request.setParams(params);
		}
		
		request.setUri(uri);
		request.setType(UserContainter.class);
		request.setResponseContentType("application/vnd.github.preview.text-match+json");
		return getAll(request);
	}
}
