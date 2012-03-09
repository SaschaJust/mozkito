package de.unisaarland.cs.st.moskito.bugs.tracker.jira;

import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import com.atlassian.jira.rest.client.IssueRestClient;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.domain.Field;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;

public class JiraParser_NetTest {
	
	@Test
	public void testLabels() {
		try {
			final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
			
			final JiraRestClient restClient = factory.create(new URI("http://jira.codehaus.org/"),
			                                                 new AnonymousAuthenticationHandler());
			final IssueRestClient issueClient = restClient.getIssueClient();
			final Issue issue = issueClient.getIssue("JETTY-1447", new NullProgressMonitor());
			final Field field = issue.getField("labels");
			System.out.println(field.getValue());
			// ["NumberFormatException","URL"]
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		
	}
}
