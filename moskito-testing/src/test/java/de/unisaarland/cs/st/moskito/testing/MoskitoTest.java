/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing;

import java.lang.annotation.Annotation;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;

import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.testing.annotation.MoskitoTestingAnnotation;
import de.unisaarland.cs.st.moskito.testing.annotation.processors.MoskitoSettingsProcessor;
import de.unisaarland.cs.st.moskito.testing_impl.MoskitoSuite;

/**
 * @author just
 * 
 */
@RunWith (MoskitoSuite.class)
public abstract class MoskitoTest {
	
	private static PersistenceUtil persistenceUtil = null;
	
	static {
		ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
		// KanuniAgent.initialize();
	}
	
	/**
	 * @return the persistenceUtil for this test
	 */
	public static PersistenceUtil getPersistenceUtil() {
		return persistenceUtil;
	}
	
	/**
	 * @param util
	 */
	private static void setPersistenceUtil(final PersistenceUtil util) {
		MoskitoTest.persistenceUtil = util;
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	public static void setUpBeforeClass(final Annotation[] annotations) throws Exception {
		for (final Annotation annotation : annotations) {
			if (annotation.annotationType().getAnnotation(MoskitoTestingAnnotation.class) != null) {
				final String name = annotation.annotationType().getName();
				final String[] split = name.split("\\.");
				// FIXME check length and stuff
				final String simpleName = split[split.length - 1] + "Processor";
				final String fqName = MoskitoSettingsProcessor.class.getPackage().getName() + "." + simpleName;
				final Class<? extends MoskitoSettingsProcessor> processorClass = (Class<? extends MoskitoSettingsProcessor>) Class.forName(fqName);
				final MoskitoSettingsProcessor processor = processorClass.newInstance();
				processor.evaluate(annotation);
			}
		}
		
		setPersistenceUtil(PersistenceManager.getUtil());
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	public MoskitoTest() {
		
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
	
}
