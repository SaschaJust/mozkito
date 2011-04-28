package de.unisaarland.cs.st.reposuite.ppa.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.OpenJPAUtil;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.elements.RCSFileManager;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSBranch;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.utils.LogLevel;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class OpenJPATest {
	
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
		properties.put("openjpa.persistence-unit", "ppa");
		
		OpenJPAUtil.createSessionFactory(properties);
	}
	
	@Test
	public void test() {
		try {
			PersistenceUtil persistenceUtil = OpenJPAUtil.getInstance();
			
			persistenceUtil.beginTransaction();
			JavaElementCache cache = new JavaElementCache();
			JavaElementLocation classDefinition = cache.getClassDefinition("a.A", "a.java", 0, 30, 123, 5);
			DateTime now = new DateTime();
			
			Person p = new Person("kim", "", "");
			RCSTransaction transaction = RCSTransaction.createTransaction("1", "", now, p, "1");
			transaction.setBranch(new RCSBranch("master"));
			RCSFile file = new RCSFileManager().createFile("a.java", transaction);
			RCSRevision rev = new RCSRevision(transaction, file, ChangeType.Added);
			JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, classDefinition, rev);
			persistenceUtil.save(transaction);
			persistenceUtil.save(op);
			persistenceUtil.commitTransaction();
			persistenceUtil.beginTransaction();
			
			Criteria<JavaChangeOperation> criteria = persistenceUtil.createCriteria(JavaChangeOperation.class);
			List<JavaChangeOperation> list = persistenceUtil.load(criteria);
			assertEquals(1, list.size());
		} catch (UninitializedDatabaseException e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
}
