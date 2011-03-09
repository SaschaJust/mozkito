package de.unisaarland.cs.st.reposuite.ppa;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;

import de.unisaarland.cs.st.reposuite.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaElement;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodCall;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaMethodDefinition;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteSinkThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class ChangeOperationPersister extends RepoSuiteSinkThread<JavaChangeOperation> {
	
	/** The method calls by name. */
	public static Map<String, JavaClassDefinition>  classDefs   = Collections
	.synchronizedMap(new HashMap<String, JavaClassDefinition>());
	public static Map<String, JavaMethodDefinition> methodDefs  = Collections
	.synchronizedMap(new HashMap<String, JavaMethodDefinition>());
	public static Map<String, JavaMethodCall>       methodCalls = Collections
	.synchronizedMap(new HashMap<String, JavaMethodCall>());
	
	public ChangeOperationPersister(final RepoSuiteThreadGroup threadGroup, final RepoSuiteSettings settings) {
		super(threadGroup, ChangeOperationPersister.class.getSimpleName(), settings);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public void run() {
		
		try {
			
			HibernateUtil hibernateUtil = HibernateUtil.getInstance(false);
			
			if (!checkConnections()) {
				return;
			}
			
			if (!checkNotShutdown()) {
				return;
			}
			
			if (Logger.logInfo()) {
				Logger.info("Starting " + getHandle());
			}
			hibernateUtil.beginTransaction();
			
			
			//fill cache
			Criteria criteria = hibernateUtil.createCriteria(JavaClassDefinition.class);
			List<JavaClassDefinition> classList = criteria.list();
			for (JavaClassDefinition def : classList) {
				classDefs.put(def.getFullQualifiedName(), def);
			}
			criteria = hibernateUtil.createCriteria(JavaMethodDefinition.class);
			List<JavaMethodDefinition> methodList = criteria.list();
			for (JavaMethodDefinition def : methodList) {
				methodDefs.put(def.getFullQualifiedName(), def);
			}
			criteria = hibernateUtil.createCriteria(JavaMethodCall.class);
			List<JavaMethodCall> callList = criteria.list();
			for (JavaMethodCall def : callList) {
				methodCalls.put(def.getFullQualifiedName(), def);
			}
			
			JavaChangeOperation currentOperation;
			String lastTransactionId = "";
			
			try {
				while (!isShutdown() && ((currentOperation = read()) != null)) {
					
					if (Logger.logDebug()) {
						Logger.debug("Storing " + currentOperation);
					}
					
					String currentTransactionId = currentOperation.getRevision().getTransaction().getId();
					
					if (lastTransactionId.equals("")) {
						lastTransactionId = currentTransactionId;
					}
					
					if (!currentTransactionId.equals(lastTransactionId)) {
						hibernateUtil.commitTransaction();
						lastTransactionId = currentTransactionId;
						hibernateUtil.beginTransaction();
					}
					
					JavaElement currentElement = currentOperation.getChangedElementLocation().getElement();
					
					if (currentElement instanceof JavaClassDefinition) {
						if (!classDefs.containsKey(currentElement.getFullQualifiedName())) {
							classDefs.put(currentElement.getFullQualifiedName(), (JavaClassDefinition) currentElement);
						} else {
							currentOperation.getChangedElementLocation().setElement(
									classDefs.get(currentElement.getFullQualifiedName()));
						}
					} else if (currentElement instanceof JavaMethodDefinition) {
						if (!methodDefs.containsKey(currentElement.getFullQualifiedName())) {
							methodDefs
							.put(currentElement.getFullQualifiedName(), (JavaMethodDefinition) currentElement);
						} else {
							currentOperation.getChangedElementLocation().setElement(
									methodDefs.get(currentElement.getFullQualifiedName()));
						}
					} else if (currentElement instanceof JavaMethodCall) {
						if (!methodCalls.containsKey(currentElement.getFullQualifiedName())) {
							methodCalls.put(currentElement.getFullQualifiedName(), (JavaMethodCall) currentElement);
						} else {
							currentOperation.getChangedElementLocation().setElement(
									methodCalls.get(currentElement.getFullQualifiedName()));
						}
					}
					
					hibernateUtil.saveOrUpdate(currentOperation);
				}
				hibernateUtil.commitTransaction();
				if (Logger.logInfo()) {
					Logger.info("ChangeOperationPersister done. Terminating... ");
				}
				finish();
			} catch (InterruptedException e) {
				
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				shutdown();
			}
		} catch (UninitializedDatabaseException e) {
			throw new UnrecoverableError(e.getMessage(), e);
		}
	}
}
