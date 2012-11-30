package org.mozkito;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.mozkito.versions.RepositoryFactoryTest;
import org.mozkito.versions.collections.TransactionSetTest;
import org.mozkito.versions.git.GitLogParserTest;
import org.mozkito.versions.git.GitRepositoryTest;
import org.mozkito.versions.git.GitRevDependencyGraphTest;
import org.mozkito.versions.git.GitTransactionIteratorTest;
import org.mozkito.versions.mercurial.MercurialLogParserTest;
import org.mozkito.versions.mercurial.MercurialRepositoryTest;
import org.mozkito.versions.model.RCSFileTest;
import org.mozkito.versions.model.RCSTransactionTest;
import org.mozkito.versions.subversion.SubversionRepositoryTest;

@RunWith (Suite.class)
@SuiteClasses ({ RepositoryFactoryTest.class, TransactionSetTest.class, GitLogParserTest.class,
        GitRepositoryTest.class, GitRevDependencyGraphTest.class, GitTransactionIteratorTest.class, RCSFileTest.class,
        RCSTransactionTest.class, MercurialRepositoryTest.class, MercurialLogParserTest.class,
        SubversionRepositoryTest.class })
public class AllTests {
	
}
