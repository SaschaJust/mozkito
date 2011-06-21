/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.ppa.eclipse;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.a7soft.examxml.ExamXML;

import de.unisaarland.cs.st.reposuite.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.CommandExecutor;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.ioda.Tuple;

public class EclipsePPALongTest {
	
	private static String  BASIC_VMARGS    = "-vmargs -DdisableCrashEmail -Ddatabase.host=quentin.cs.uni-saarland.de -Ddatabase.name=reposuite_ppa_test -Ddatabase.user=miner -Ddatabase.password=miner -Dlog.level=warn -Drepository.type=GIT";
	
	private static File    eclipseBinDir;
	
	private static File    repoZip         = null;
	private static File    compXML         = null;
	@SuppressWarnings ("unused")
	private static File    compXML2        = null;
	@SuppressWarnings ("unused")
	private static File    compXML2_altern = null;
	private static File    compXML3        = null;
	private static File    compXML4        = null;
	private static File    tmpDir          = null;
	private static boolean mac             = false;
	
	@AfterClass
	public static void afterClass() {
		try {
			FileUtils.deleteDirectory(tmpDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@BeforeClass
	public static void beforeClass() {
		
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			mac = true;
		}
		
		try {
			repoZip = new File(EclipsePPALongTest.class.getResource(FileUtils.fileSeparator
			                                                                + "reposuite_28_01_2011.git.zip").toURI());
			compXML = new File(EclipsePPALongTest.class.getResource(FileUtils.fileSeparator + "ppa_comp.xml").toURI());
			compXML2 = new File(EclipsePPALongTest.class.getResource(FileUtils.fileSeparator + "ppa_comp_2.xml")
			                                            .toURI());
			compXML2_altern = new File(EclipsePPALongTest.class.getResource(FileUtils.fileSeparator
			                                                                        + "ppa_comp_2_altern.xml").toURI());
			compXML3 = new File(EclipsePPALongTest.class.getResource(FileUtils.fileSeparator + "ppa_comp_3.xml")
			                                            .toURI());
			compXML4 = new File(EclipsePPALongTest.class.getResource(FileUtils.fileSeparator + "ppa_comp_4.xml")
			                                            .toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		repoZip.getParentFile();
		File reposuiteBaseDir = repoZip.getParentFile().getParentFile().getParentFile().getParentFile();
		
		StringBuilder eclipsePath = new StringBuilder();
		eclipsePath.append(reposuiteBaseDir.getAbsolutePath());
		eclipsePath.append(FileUtils.fileSeparator);
		eclipsePath.append("eclipse-apps");
		eclipsePath.append(FileUtils.fileSeparator);
		if (mac) {
			eclipsePath.append("mac");
		} else {
			eclipsePath.append("linux");
		}
		eclipsePath.append(FileUtils.fileSeparator);
		eclipsePath.append("reposuite-ppa.zip");
		
		File eclipseFile = new File(eclipsePath.toString());
		
		if (!eclipseFile.exists()) {
			throw new UnrecoverableError("Could not find reposuite-ppa.zip file in `" + eclipseFile.getPath() + "`");
		}
		
		// unzip the eclipse app to tmp-directory
		tmpDir = FileUtils.createRandomDir("reposuite_ppa_test", "", FileShutdownAction.DELETE);
		FileUtils.unzip(eclipseFile, tmpDir);
		
		StringBuilder eclipseBinDirPath = new StringBuilder();
		eclipseBinDirPath.append(tmpDir.getAbsolutePath());
		eclipseBinDirPath.append(FileUtils.fileSeparator);
		if (mac) {
			eclipseBinDirPath.append("eclipse");
			eclipseBinDirPath.append(FileUtils.fileSeparator);
			eclipseBinDirPath.append("Eclipse.app");
			eclipseBinDirPath.append(FileUtils.fileSeparator);
			eclipseBinDirPath.append("Contents");
			eclipseBinDirPath.append(FileUtils.fileSeparator);
			eclipseBinDirPath.append("MacOS");
			eclipseBinDirPath.append(FileUtils.fileSeparator);
		} else {
			eclipseBinDirPath.append("bin");
			eclipseBinDirPath.append(FileUtils.fileSeparator);
			eclipseBinDirPath.append("linux");
			eclipseBinDirPath.append(FileUtils.fileSeparator);
			eclipseBinDirPath.append("eclipse");
			eclipseBinDirPath.append(FileUtils.fileSeparator);
		}
		
		eclipseBinDir = new File(eclipseBinDirPath.toString());
		
		Tuple<Integer, List<String>> response = CommandExecutor.execute("chmod", new String[] { "+x", "eclipse" },
		                                                                eclipseBinDir, null, null);
		
		if (response.getFirst() != 0) {
			if (Logger.logError()) {
				Logger.error("Could not make eclipse executable in dir `" + eclipseBinDir.getAbsolutePath()
				        + "` with arguments. Abort");
			}
			fail();
		}
		
		File repoDir = FileUtils.createRandomDir("reposuite_ppa_test", "repo", FileShutdownAction.DELETE);
		FileUtils.unzip(repoZip, repoDir);
		String repoPath = repoDir.getAbsolutePath() + FileUtils.fileSeparator + "reposuite_28_01_2011.git";
		
		BASIC_VMARGS += " -Drepository.uri=" + repoPath;
		
	}
	
	private static boolean compareXML(final File original,
	                                  final File tocompare) {
		
		String result = "fail!";
		try {
			String originalContent = FileUtils.readFileToString(original);
			String toCompareContent = FileUtils.readFileToString(tocompare);
			result = ExamXML.compareXMLString(originalContent, toCompareContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (Logger.logDebug()) {
			Logger.debug(result);
		}
		return result.trim().equals("");
	}
	
	private static Tuple<Integer, List<String>> runEclipse(final String[] args) {
		CommandExecutor.execute("chmod", new String[] { "+x", "eclipse" }, eclipseBinDir, null, null);
		Tuple<Integer, List<String>> response = CommandExecutor.execute(eclipseBinDir + FileUtils.fileSeparator
		        + "eclipse", args, eclipseBinDir, null, null);
		if (response.getFirst() != 0) {
			throw new UnrecoverableError("Could not execute eclipse in directory `" + eclipseBinDir.getAbsolutePath()
			        + "` with arguments `" + StringUtils.join(args, " ") + "`. Abort");
		}
		return response;
	}
	
	@Test
	public void testEclipseApp1() {
		String VMARGS = BASIC_VMARGS
		        + " -Doutput.xml=/tmp/ppa.xml -DtestCaseTransactions=f99a3ff4615653855c254874f3d4fe0d084f34d2";
		runEclipse(VMARGS.split(" "));
		File tmpFile = new File("/tmp/ppa.xml");
		assertTrue(compareXML(compXML, tmpFile));
		tmpFile.delete();
	}
	
	// @Test
	// public void testEclipseApp2() {
	// String VMARGS = BASIC_VMARGS
	// +
	// " -Doutput.xml=/tmp/ppa2.xml -DtestCaseTransactions=dfd5e8d5eb594e29f507896a744d2bdabfc55cdf";
	// runEclipse(VMARGS.split(" "));
	// File tmpFile = new File("/tmp/ppa2.xml");
	// if (!compareXML(compXML2, tmpFile)) {
	// assertTrue(compareXML(compXML2_altern, tmpFile));
	// }
	// tmpFile.delete();
	// }
	
	@Test
	public void testEclipseApp3() {
		String VMARGS = BASIC_VMARGS
		        + " -Doutput.xml=/tmp/ppa3.xml -DtestCaseTransactions=0309f53f798d178aaf519333755c0f62500fcca9";
		runEclipse(VMARGS.split(" "));
		File tmpFile = new File("/tmp/pp3.xml");
		assertTrue(compareXML(compXML3, tmpFile));
		tmpFile.delete();
	}
	
	@Test
	public void testEclipseApp4() {
		String VMARGS = BASIC_VMARGS
		        + " -Doutput.xml=/tmp/ppa4.xml -DtestCaseTransactions=ff1ba504345e9df2b9feb0c678779945017236cc";
		runEclipse(VMARGS.split(" "));
		File tmpFile = new File("/tmp/pp4.xml");
		assertTrue(compareXML(compXML4, tmpFile));
		tmpFile.delete();
	}
}
