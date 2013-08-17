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
import java.util.List;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import org.mozkito.research.persons.Gravatar;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class GitHub {
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			
			final GitHubClient client = new GitHubClient();
			client.setOAuth2Token("d19785ed2d766f9ece077ecdc16ca80f92704bc4");
			// client.setCredentials("sjust83", "XXXXX");
			
			final ExtendedUserService euService = new ExtendedUserService(client);
			new RepositoryService(client);
			
			try {
				// final HashMap<String, String> filterData = new HashMap<String, String>();
				// filterData.put("name", "mozkito");
				// final List<SearchRepository> repositories = repoService.searchRepositories("mozkito");
				// System.out.println(repositories.size());
				
				final List<SearchUser> users = euService.searchUsers("sascha");
				for (final SearchUser user : users) {
					if (user.getLogin().equals("sjust83")) {
						final User user2 = euService.getUser(user.getLogin());
						assert user2 != null;
						System.err.println(user.getLogin());
						System.err.println(user.getAvatarUrl());
						
						System.err.println(user2.getName());
						System.err.println(user2.getEmail());
						
						System.err.println(user2.getGravatarId());
						System.err.println(user2.getUrl());
						
						final Gravatar gravatar = Gravatar.fromHash(user2.getGravatarId());
						final Gravatar gravatar2 = Gravatar.fromEmail(user2.getEmail());
						System.err.println(gravatar.equals(gravatar2));
					}
				}
			} catch (final IOException e1) {
				e1.printStackTrace();
			}
			
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
