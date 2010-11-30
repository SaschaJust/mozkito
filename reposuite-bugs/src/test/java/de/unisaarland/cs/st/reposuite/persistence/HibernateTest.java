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

import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Status;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.Tuple;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class HibernateTest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String url = "jdbc:postgresql://quentin.cs.uni-saarland.de/reposuiteTest?useUnicode=true&characterEncoding=UTF-8";
		
		Properties properties = new Properties();
		properties.put("hibernate.connection.url", url);
		properties.put("hibernate.hbm2ddl.auto", "update");
		properties.put("hibernate.connection.autocommit", "false");
		properties.put("hibernate.show_sql", "false");
		properties.put("hibernate.connection.driver_class", "org.postgresql.Driver");
		properties.put("hibernate.connection.username", "miner");
		properties.put("hibernate.connection.password", "miner");
		properties.put("hibernate.hbm2ddl.auto", "create-drop");
		
		HibernateUtil.createSessionFactory(properties);
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		HibernateUtil.shutdown();
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
		// Adding ||submitter|| --> ||Person [generatedId=0,
		// usernames=[yokolet], emailAddresses=[], fullnames=[Yoko Harada],
		// transactions=[], hashcode=-310434987]|| from setSubmitter
		// Adding ||author|| --> ||Person [generatedId=0, usernames=[headius],
		// emailAddresses=[], fullnames=[], transactions=[],
		// hashcode=795341638]|| from setAuthor
		// Adding ||author|| --> ||Person [generatedId=0, usernames=[yokolet],
		// emailAddresses=[], fullnames=[], transactions=[],
		// hashcode=-1001108160]|| from setAuthor
		// Adding ||author|| --> ||Person [generatedId=0, usernames=[yokolet],
		// emailAddresses=[], fullnames=[], transactions=[],
		// hashcode=-1001108160]|| from setAuthor
		// Adding ||author|| --> ||Person [generatedId=0, usernames=[headius],
		// emailAddresses=[], fullnames=[], transactions=[],
		// hashcode=795341638]|| from setAuthor
		// Adding ||author|| --> ||Person [generatedId=0, usernames=[yokolet],
		// emailAddresses=[], fullnames=[], transactions=[],
		// hashcode=-1001108160]|| from setAuthor
		// Adding ||resolver|| --> ||Person [generatedId=0, usernames=[yokolet],
		// emailAddresses=[], fullnames=[], transactions=[],
		// hashcode=-1001108160]|| from setResolver
		// Adding ||author|| --> ||Person [generatedId=0, usernames=[yokolet],
		// emailAddresses=[], fullnames=[], transactions=[],
		// hashcode=-1001108160]|| from setAuthor
		// Adding ||author|| --> ||Person [generatedId=0, usernames=[],
		// emailAddresses=[], fullnames=[Yoko Harada], transactions=[],
		// hashcode=690702964]|| from setAuthor
		// Adding ||assignedTo|| --> ||Person [generatedId=0, usernames=[enebo],
		// emailAddresses=[], fullnames=[Thomas E Enebo], transactions=[],
		// hashcode=-1107881130]|| from setAssignedTo
		try {
			HibernateUtil hibernateUtil = HibernateUtil.getInstance();
			
			Person submitter = new Person("yokolet", "Yoko Harada", null);
			Person resolver = new Person("yokolet", null, null);
			Person assignedTo = new Person("enebo", "thomas E Enebo", null);
			Person historyAuthor1 = new Person("yokolet", null, null);
			Person historyAuthor2 = new Person(null, "Yoko Harada", null);
			Person commentAuthor1 = new Person("headius", null, null);
			Person commentAuthor2 = new Person("yokolet", null, null);
			Person commentAuthor3 = new Person("yokolet", null, null);
			Person commentAuthor4 = new Person("headius", null, null);
			Person commentAuthor5 = new Person("yokolet", null, null);
			
			Report report = new Report();
			report.setSubmitter(submitter);
			report.setAssignedTo(assignedTo);
			report.setResolver(resolver);
			
			report.addHistoryElement(new HistoryElement(historyAuthor1, new DateTime(),
			                                            new HashMap<String, Tuple<?, ?>>() {
				                                            
				                                            {
					                                            put("status", new Tuple<Status, Status>(Status.NEW,
					                                                                                    Status.CLOSED));
				                                            }
			                                            }));
			report.addHistoryElement(new HistoryElement(historyAuthor2, new DateTime(),
			                                            new HashMap<String, Tuple<?, ?>>() {
				                                            
				                                            {
					                                            put("status", new Tuple<Status, Status>(Status.NEW,
					                                                                                    Status.CLOSED));
				                                            }
			                                            }));
			
			report.addComment(new Comment(1, commentAuthor1, new DateTime(), "comment1"));
			report.addComment(new Comment(2, commentAuthor2, new DateTime(), "comment2"));
			report.addComment(new Comment(3, commentAuthor3, new DateTime(), "comment3"));
			report.addComment(new Comment(4, commentAuthor4, new DateTime(), "comment4"));
			report.addComment(new Comment(5, commentAuthor5, new DateTime(), "comment5"));
			
			hibernateUtil.beginTransaction();
			hibernateUtil.save(report);
			hibernateUtil.commitTransaction();
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
}
