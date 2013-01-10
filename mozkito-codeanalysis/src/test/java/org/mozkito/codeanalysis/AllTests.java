/*******************************************************************************
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
 ******************************************************************************/
package org.mozkito.codeanalysis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.mozkito.codeanalysis.model.ChangeOperationsTest;
import org.mozkito.codeanalysis.model.JavaChangeOperationTest;
import org.mozkito.codeanalysis.model.JavaClassDefinitionTest;
import org.mozkito.codeanalysis.model.JavaElementLocationSetTest;
import org.mozkito.codeanalysis.model.JavaElementLocationTest;
import org.mozkito.codeanalysis.model.JavaElementTest;
import org.mozkito.codeanalysis.model.JavaMethodCallTest;
import org.mozkito.codeanalysis.model.JavaMethodDefinitionTest;
import org.mozkito.codeanalysis.utils.PPAUtilsTest;

/**
 * The Class AllTests.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@RunWith (Suite.class)
@SuiteClasses ({ ChangeOperationsTest.class, JavaChangeOperationTest.class, JavaClassDefinitionTest.class,
        JavaElementLocationSetTest.class, JavaElementLocationTest.class, JavaElementTest.class,
        JavaMethodCallTest.class, JavaMethodDefinitionTest.class, PPAUtilsTest.class })
public class AllTests {
	// nothing here
}
