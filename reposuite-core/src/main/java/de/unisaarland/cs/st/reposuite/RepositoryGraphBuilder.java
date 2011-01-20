/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import java.security.UnrecoverableEntryException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import de.unisaarland.cs.st.reposuite.persistence.HibernateUtil;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.RevDependency;
import de.unisaarland.cs.st.reposuite.rcs.elements.RevDependencyIterator;
import de.unisaarland.cs.st.reposuite.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteFilterThread;
import de.unisaarland.cs.st.reposuite.toolchain.RepoSuiteThreadGroup;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryGraphBuilder extends RepoSuiteFilterThread<RCSTransaction> {
	
	private final Repository                  repository;
	private final HibernateUtil               hibernateUtil;
	private final Map<String, RevDependency>  reverseDependencies = new HashMap<String, RevDependency>();
	private final Map<String, String>         latest              = new HashMap<String, String>();
	private final Map<String, RCSTransaction> cached              = new HashMap<String, RCSTransaction>();
	
	public RepositoryGraphBuilder(final RepoSuiteThreadGroup threadGroup, final RepositorySettings settings,
			final Repository repository, final HibernateUtil hibernateUtil) {
		super(threadGroup, RepositoryGraphBuilder.class.getSimpleName(), settings);
		this.repository = repository;
		this.hibernateUtil = hibernateUtil;
	}
	
	@Override
	public void run() {
		if (!checkConnections() || !checkNotShutdown()) {
			return;
		}
		
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
		}
		
		if (Logger.logInfo()) {
			Logger.info("Fetching reverse dependencies. This could take a while...");
		}
		
		for (RevDependencyIterator revdep = repository.getRevDependencyIterator(); revdep.hasNext();) {
			RevDependency rd = revdep.next();
			reverseDependencies.put(rd.getId(), rd);
		}
		
		if (Logger.logInfo()) {
			Logger.info("Reverse dependencies ready.");
		}
		
		RCSTransaction rcsTransaction = null;
		CountDownLatch latch = new CountDownLatch(1);
		
		try {
			while (!isShutdown() && ((rcsTransaction = read()) != null)) {
				if (Logger.logDebug()) {
					Logger.debug("Updating graph for " + rcsTransaction);
				}
				
				RevDependency revdep = reverseDependencies.get(rcsTransaction.getId());
				rcsTransaction.setBranch(revdep.getCommitBranch());
				rcsTransaction.addAllTags(revdep.getTagNames());
				for (String parent : revdep.getParents()) {
					RCSTransaction parentTransaction = null;
					if (!cached.containsKey(parent)) {
						Criteria parentCriteria = hibernateUtil.createCriteria(RCSTransaction.class).add(
								Restrictions.eq("id", parent));
						@SuppressWarnings("unchecked") List<RCSTransaction> parentList = parentCriteria.list();
						if (parentList.isEmpty()) {
							if (Logger.logError()) {
								Logger.error("Got child `" + rcsTransaction.getId()
										+ "` of unknown parent. This should not happen.");
							}
							throw new UnrecoverableEntryException(
							"Got child of unknown parent. This should not happen.");
						} else if (parentList.size() > 1) {
							if (Logger.logError()) {
								Logger.error("Could not fetch unique transaction for ID `" + parent + "`.");
							}
							throw new UnrecoverableEntryException("Could not fetch unique transaction for ID `"
									+ parent + "`.");
						} else {
							parentTransaction = parentList.get(0);
						}
					} else {
						parentTransaction = cached.get(parent);
					}
					if (parentTransaction != null) {
						rcsTransaction.addParent(parentTransaction);
					} else {
						if (Logger.logError()) {
							Logger.error("Got child `" + rcsTransaction.getId()
									+ "` of unknown parent. This should not happen.");
						}
						throw new UnrecoverableEntryException("Got child of unknown parent. This should not happen.");
					}
				}
				
				// detect new branch
				if (rcsTransaction.getParents().size() == 1) {
					RCSTransaction parentTransaction = rcsTransaction.getParents().iterator().next();
					if (!rcsTransaction.getBranch().equals(parentTransaction.getBranch())) {
						rcsTransaction.getBranch().setBegin(rcsTransaction);
					}
				} else if (rcsTransaction.getParents().isEmpty()) {
					if (rcsTransaction.getBranch().getBegin() == null) {
						rcsTransaction.getBranch().setBegin(rcsTransaction);
					}
				}
				
				// we have to store the current transaction before (!) we update
				// the parents' children
				latch = write(rcsTransaction);
				
				// detect branch merge
				if (revdep.getParents().size() > 1) {
					for (String parent : revdep.getParents()) {
						RCSTransaction parentTransaction = cached.get(parent);
						
						if (!parentTransaction.getBranch().getName().equals(rcsTransaction.getBranch().getName())) {
							// closed branch
							// remove parent transaction from cache
							
							// wait for children to be persisted
							latch.await();
							
							parentTransaction.addChild(rcsTransaction);
							hibernateUtil.update(cached.remove(parentTransaction.getId()));
							// remove branch from cache
							latest.remove(parentTransaction.getBranch().getName());
							
							parentTransaction.getBranch().setEnd(parentTransaction);
						}
					}
				}
				
				// ++++++ Update caches ++++++
				// remove old "latest transaction" from cache
				if (cached.containsKey(latest.get(revdep.getCommitBranch().getName()))) {
					latch.await();
					cached.get(latest.get(revdep.getCommitBranch().getName())).addChild(rcsTransaction);
					hibernateUtil.update(cached.remove(latest.get(revdep.getCommitBranch().getName())));
				}
				
				// add transaction to cache
				cached.put(rcsTransaction.getId(), rcsTransaction);
				
				// set new "latest transaction" to active branch cache
				latest.put(revdep.getCommitBranch().getName(), rcsTransaction.getId());
				// ------ Update caches ------
				
			}
			
			// wait for children to be persisted
			latch.await();
			
			// persist all remaining cached transactions
			hibernateUtil.commitTransaction();
			hibernateUtil.beginTransaction();
			for (RCSTransaction transaction : cached.values()) {
				hibernateUtil.update(transaction);
			}
			hibernateUtil.commitTransaction();
			
			finish();
		} catch (Exception e) {
			
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			shutdown();
		}
	}
}
