/**
 * 
 */
package nullpointer;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class TestNullpointer {
	
	private static Session session;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AnnotationConfiguration annotationConfiguration = new AnnotationConfiguration();
		// annotationConfiguration.addAnnotatedClass(A.class);
		// annotationConfiguration.addAnnotatedClass(APK.class);
		annotationConfiguration.addAnnotatedClass(B.class);
		// annotationConfiguration.addAnnotatedClass(C.class);
		SessionFactory sessionFactory = annotationConfiguration.configure().buildSessionFactory();
		session = sessionFactory.openSession();
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
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public final void testNullpointerException() {
		Transaction transaction = session.beginTransaction();
		A a = new B("first", "second", "third");
		session.save(a);
		transaction.commit();
		session.close();
	}
}
