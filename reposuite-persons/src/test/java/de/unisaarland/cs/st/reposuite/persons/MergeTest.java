/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persons;

import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.utils.LogLevel;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class MergeTest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Logger.setLogLevel(LogLevel.OFF);
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
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// set JavaVM arguments
		Properties sysProperties = System.getProperties();
		sysProperties.setProperty("database.user", "miner");
		sysProperties.setProperty("database.password", "miner");
		sysProperties.setProperty("database.name", "reposuiteTestPersons");
		sysProperties.setProperty("database.host", "quentin.cs.uni-saarland.de");
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test for {@link Person}, {@link PersonContainer}, {@link PersonManager}
	 */
	@Test
	public void testMergePerson() {
		
		// HibernateUtil hibernateUtil;
		// try {
		// hibernateUtil = HibernateUtil.getInstance();
		// Criteria criteria = hibernateUtil.createCriteria(Person.class);
		// int personCount = criteria.list().size();
		//
		// Person[] persons = new Person[] { new Person("just", null, null),
		// new Person(null, null, "sascha.just@st.cs.uni-saarland.de"), new
		// Person(null, "Sascha Just", null),
		// new Person("just", "Sascha Just", null),
		// new Person(null, "Sascha Just", "sascha.just@st.cs.uni-saarland.de")
		// };
		//
		// RCSTransaction rcsTransaction = null;
		//
		// hibernateUtil.beginTransaction();
		//
		// int i = 0;
		// for (Person person : persons) {
		// rcsTransaction = RCSTransaction.createTransaction("" + ++i, "test",
		// new DateTime(), person, "");
		// hibernateUtil.saveOrUpdate(rcsTransaction);
		// }
		//
		// hibernateUtil.commitTransaction();
		//
		// Persons personsMerger = new Persons();
		// personsMerger.run();
		//
		// criteria = hibernateUtil.createCriteria(Person.class);
		// @SuppressWarnings ("unchecked")
		// List<Person> list = criteria.list();
		// Person person = (Person) CollectionUtils.find(list, new Predicate() {
		//
		// @Override
		// public boolean evaluate(final Object object) {
		// Person p = (Person) object;
		// return (p.getUsernames().size() == 1) &&
		// p.getUsernames().iterator().next().equals("just");
		// }
		// });
		//
		// assertTrue(!list.isEmpty());
		// assertEquals(personCount + 1, list.size());
		// assertEquals(1, person.getUsernames().size());
		// assertEquals("just", person.getUsernames().iterator().next());
		// assertEquals(1, person.getEmailAddresses().size());
		// assertEquals("sascha.just@st.cs.uni-saarland.de",
		// person.getEmailAddresses().iterator().next());
		// assertEquals(1, person.getFullnames().size());
		// assertEquals("Sascha Just", person.getFullnames().iterator().next());
		// assertEquals(persons.length, person.getTransactions().size());
		// for (Person p : persons) {
		// if (p != person) {
		// assertEquals(0, p.getTransactions().size());
		// }
		// }
		// } catch (UninitializedDatabaseException e) {
		// fail();
		// }
	}
	
	@Test
	public void testMergePersonSingleContainer() {
		HibernateUtil hibernateUtil;
		// try {
		// hibernateUtil = HibernateUtil.getInstance();
		// Criteria criteria = hibernateUtil.createCriteria(Person.class);
		// int personCount = criteria.list().size();
		//
		// PersonContainer personContainer = new PersonContainer();
		// Person[] persons = new Person[] { new Person("pan", null, null),
		// new Person(null, null, "peter.pan@st.cs.uni-saarland.de"), new
		// Person(null, "Peter Pan", null),
		// new Person("pan", "Peter Pan", null),
		// new Person(null, "Peter Pan", "peter.pan@st.cs.uni-saarland.de") };
		//
		// for (int i = 0; i < persons.length; ++i) {
		// personContainer.add("contrib_" + i, persons[i]);
		// }
		//
		// hibernateUtil.beginTransaction();
		// hibernateUtil.save(personContainer);
		// hibernateUtil.commitTransaction();
		//
		// Persons personsMerger = new Persons();
		// personsMerger.run();
		//
		// criteria = hibernateUtil.createCriteria(Person.class);
		// @SuppressWarnings ("unchecked")
		// List<Person> list = criteria.list();
		// Person person = (Person) CollectionUtils.find(list, new Predicate() {
		//
		// @Override
		// public boolean evaluate(final Object object) {
		// Person p = (Person) object;
		// return (p.getUsernames().size() == 1) &&
		// p.getUsernames().iterator().next().equals("pan");
		// }
		// });
		//
		// assertTrue(!list.isEmpty());
		// assertEquals(personCount + 1, list.size());
		// assertEquals(1, person.getUsernames().size());
		// assertEquals("pan", person.getUsernames().iterator().next());
		// assertEquals(1, person.getEmailAddresses().size());
		// assertEquals("peter.pan@st.cs.uni-saarland.de",
		// person.getEmailAddresses().iterator().next());
		// assertEquals(1, person.getFullnames().size());
		// assertEquals("Peter Pan", person.getFullnames().iterator().next());
		//
		// } catch (UninitializedDatabaseException e) {
		// fail();
		// }
	}
}
