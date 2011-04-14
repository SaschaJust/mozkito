package de.unisaarland.cs.st.reposuite.persistence;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.LogLevel;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class OpenJPATest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Logger.setLogLevel(LogLevel.DEBUG);
		Properties properties = new Properties();
		String url = "jdbc:postgresql://quentin.cs.uni-saarland.de/reposuiteTest";
		properties.put("openjpa.ConnectionURL", url);
		properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(SchemaAction='add,deleteTableContents')");
		properties.put("openjpa.ConnectionDriverName", "org.postgresql.Driver");
		properties.put("openjpa.ConnectionUserName", "miner");
		properties.put("openjpa.ConnectionPassword", "miner");
		properties.put("openjpa.persistence-unit", "bugs");
		
		OpenJPAUtil.createSessionFactory(properties);
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		try {
			OpenJPAUtil.getInstance().shutdown();
		} catch (UninitializedDatabaseException e) {
			
		}
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@SuppressWarnings ("serial")
	@Test
	public void testOrphanPerson() {
		try {
			PersistenceUtil persistenceUtil = OpenJPAUtil.getInstance();
			
			Person submitter = new Person("yokolet", "Yoko Harada", null);
			Person historyAuthor1 = new Person("yokolet", null, null);
			Person historyAuthor2 = new Person(null, "Yoko Harada", null);
			Person commentAuthor2 = new Person("yokolet", null, null);
			
			Report report = new Report();
			report.setSubmitter(submitter);
			
			report.addHistoryElement(new HistoryElement(historyAuthor1, new DateTime(), new HashMap<String, Enum<?>>() {
				
				{
					put("status", Status.CLOSED);
				}
			}));
			
			report.addHistoryElement(new HistoryElement(historyAuthor2, new DateTime(), new HashMap<String, Enum<?>>() {
				
				{
					put("status", Status.CLOSED);
				}
			}));
			
			report.addComment(new Comment(2, commentAuthor2, new DateTime(), "comment2"));
			
			persistenceUtil.beginTransaction();
			persistenceUtil.save(report);
			persistenceUtil.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
