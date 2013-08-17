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

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

/**
 * The Class SearchUser.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class SearchUser implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5061565798171482473L;
	
	/** The login. */
	private String            login;
	
	/** The id. */
	private int               id;
	
	/** The avatar url. */
	@SerializedName ("avatar_url")
	private String            avatarUrl;
	
	/** The gravatar id. */
	@SerializedName ("gravatar_id")
	private String            gravatarId;
	
	/** The url. */
	private String            url;
	
	/** The html url. */
	@SerializedName ("html_url")
	private String            htmlUrl;
	
	/** The followers url. */
	@SerializedName ("followers_url")
	private String            followersUrl;
	
	/** The subscriptions url. */
	@SerializedName ("subscriptions_url")
	private String            subscriptionsUrl;
	
	/** The organizations url. */
	@SerializedName ("organizations_url")
	private String            organizationsUrl;
	
	/** The repos url. */
	@SerializedName ("repos_url")
	private String            reposUrl;
	
	/** The received events url. */
	@SerializedName ("received_events_url")
	private String            receivedEventsUrl;
	
	/** The type. */
	private String            type;
	
	/** The score. */
	double                    score;
	
	/**
	 * Gets the avatar url.
	 * 
	 * @return the avatarUrl
	 */
	public String getAvatarUrl() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.avatarUrl;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the followers url.
	 * 
	 * @return the followersUrl
	 */
	public String getFollowersUrl() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.followersUrl;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the gravatar id.
	 * 
	 * @return the gravatarId
	 */
	public String getGravatarId() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.gravatarId;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the html url.
	 * 
	 * @return the htmlUrl
	 */
	public String getHtmlUrl() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.htmlUrl;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public int getId() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.id;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the login.
	 * 
	 * @return the login
	 */
	public String getLogin() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.login;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the organizations url.
	 * 
	 * @return the organizationsUrl
	 */
	public String getOrganizationsUrl() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.organizationsUrl;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the received events url.
	 * 
	 * @return the receivedEventsUrl
	 */
	public String getReceivedEventsUrl() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.receivedEventsUrl;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the repos url.
	 * 
	 * @return the reposUrl
	 */
	public String getReposUrl() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.reposUrl;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the score.
	 * 
	 * @return the score
	 */
	public double getScore() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.score;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the subscriptions url.
	 * 
	 * @return the subscriptionsUrl
	 */
	public String getSubscriptionsUrl() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.subscriptionsUrl;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public String getType() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.type;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the url.
	 * 
	 * @return the url
	 */
	public String getUrl() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.url;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
}
