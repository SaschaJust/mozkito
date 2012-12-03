/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.mozkito.versions.RepositoryFactoryTest;
import org.mozkito.versions.collections.TransactionSetTest;
import org.mozkito.versions.elements.RCSFileManagerTest;
import org.mozkito.versions.elements.RevDependencyTest;
import org.mozkito.versions.git.GitLogParserTest;
import org.mozkito.versions.git.GitRepositoryTest;
import org.mozkito.versions.git.GitRevDependencyGraphTest;
import org.mozkito.versions.git.GitTransactionIteratorTest;
import org.mozkito.versions.mercurial.MercurialLogParserTest;
import org.mozkito.versions.mercurial.MercurialRepositoryTest;
import org.mozkito.versions.model.RCSFileTest;
import org.mozkito.versions.model.RCSTransactionTest;
import org.mozkito.versions.subversion.SubversionRepositoryTest;

/**
 * The Class AllTests.
 */
@RunWith (Suite.class)
@SuiteClasses ({ RepositoryFactoryTest.class, TransactionSetTest.class, GitLogParserTest.class,
        GitRepositoryTest.class, GitRevDependencyGraphTest.class, GitTransactionIteratorTest.class, RCSFileTest.class,
        RCSTransactionTest.class, MercurialRepositoryTest.class, MercurialLogParserTest.class,
        SubversionRepositoryTest.class, RCSFileManagerTest.class, RevDependencyTest.class })
public class AllTests {
	// stub
}
