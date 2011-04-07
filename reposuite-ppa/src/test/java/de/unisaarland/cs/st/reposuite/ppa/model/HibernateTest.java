package de.unisaarland.cs.st.reposuite.ppa.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Properties;

import org.hibernate.SQLQuery;
import org.joda.time.DateTime;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.elements.ChangeType;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSFileManager;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSRevision;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;


public class HibernateTest {
	
	@Test
	public void test() {
		String url = "jdbc:postgresql://quentin.cs.uni-saarland.de/reposuitePPATest?useUnicode=true&characterEncoding=UTF-8";
		Properties properties = new Properties();
		properties.put("hibernate.connection.url", url);
		properties.put("hibernate.hbm2ddl.auto", "update");
		properties.put("hibernate.connection.autocommit", "false");
		properties.put("hibernate.show_sql", "false");
		properties.put("hibernate.connection.driver_class", "org.postgresql.Driver");
		properties.put("hibernate.connection.username", "miner");
		properties.put("hibernate.connection.password", "miner");
		properties.put("hibernate.hbm2ddl.auto", "create-drop");
		
		try {
			HibernateUtil.createSessionFactory(properties);
			HibernateUtil hibernateUtil = HibernateUtil.getInstance();
			
			hibernateUtil.beginTransaction();
			JavaElementCache cache = new JavaElementCache();
			JavaElementLocation<JavaClassDefinition> classDefinition = cache.getClassDefinition("a.A", "a.java", 0, 30, 123, 5, "a");
			DateTime now = new DateTime();
			Person p = new Person("kim","","");
			RCSTransaction transaction = RCSTransaction.createTransaction("1","",now,p,"1");
			RCSFile file = new RCSFileManager().createFile("a.java", transaction);
			RCSRevision rev = new RCSRevision(transaction, file, ChangeType.Added);
			JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, classDefinition, rev);
			hibernateUtil.saveOrUpdate(transaction);
			hibernateUtil.saveOrUpdate(op);
			hibernateUtil.commitTransaction();
			hibernateUtil.beginTransaction();
			
			SQLQuery query = hibernateUtil.createSQLQuery("SELECT * FROM javachangeoperation WHERE revision_transaction_id = '1'",
			                                              JavaChangeOperation.class);
			List<JavaChangeOperation> list = query.list();
			assertEquals(1, list.size());
		} catch (UninitializedDatabaseException e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
}
