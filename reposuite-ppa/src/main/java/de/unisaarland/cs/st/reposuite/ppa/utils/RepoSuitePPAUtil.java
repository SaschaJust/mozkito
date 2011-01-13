/*******************************************************************************
 * PPA - Partial Program Analysis for Java
 * Copyright (C) 2008 Barthelemy Dagenais
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library. If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.txt>
 * 
 * Contributors:
 *     Puneet Kapur - 2009
 *******************************************************************************/
package de.unisaarland.cs.st.reposuite.ppa.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PPAASTParser;
import org.eclipse.jdt.core.dom.PPABindingsUtil;
import org.eclipse.jdt.core.dom.PPAEngine;
import org.eclipse.jdt.core.dom.PPATypeRegistry;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;

import de.unisaarland.cs.st.reposuite.ppa.PPAOptions;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;
import de.unisaarland.cs.st.reposuite.utils.specification.NoneNull;
import org.eclipse.jdt.core.dom.ASTParser;

/**
 * The Class MyPPAUtil.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class RepoSuitePPAUtil {

    /**
     * Utility method that builds a full qualified class.
     *
     * @param parentName
     *            the parent name
     * @param name
     *            the name
     * @param params
     *            the params
     * @return the method name
     */
    public static String getMethodName(final String parentName, final String name, final List<String> params) {
        StringBuilder ss = new StringBuilder();
        ss.append(parentName);
        ss.append(".");
        ss.append(name);
        ss.append("(");
        if (params.size() > 0) {
            ss.append(params.get(0));
        }
        for (int i = 1; i < params.size(); ++i) {
            ss.append(",");
            ss.append(params.get(i));
        }
        ss.append(")");
        return ss.toString();
    }

    /**
     * Gets the package from file.
     *
     * @param file
     *            the file
     * @return the package from file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static String getPackageFromFile(final File file) throws IOException {
        String packageName = "";
        PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
        parser2.setStatementsRecovery(true);
        parser2.setResolveBindings(true);

        try {
            String content = FileUtils.readFileToString(file);
            parser2.setSource(content.toCharArray());
            CompilationUnit cu = (CompilationUnit) parser2.createAST(null);
            PackageDeclaration pDec = cu.getPackage();
            if (pDec != null) {
                packageName = pDec.getName().getFullyQualifiedName();
            }
            return packageName;
        } catch (IOException e) {
            if (Logger.logError()) {
                Logger.error(e.getMessage(), e);
            }
        }
        return null;
    }
    private final File baseSrcDir;

    public RepoSuitePPAUtil(final File baseSrcDir) {
        this.baseSrcDir = baseSrcDir;
    }

    /**
     * Copy java source file.
     *
     * @param destBaseDir
     *            the dest base dir
     * @param srcFile
     *            the src file
     * @param packageName
     *            the package name
     * @param destFileName
     *            the dest file name
     * @return the file
     * @throws CoreException
     *             the core exception
     */
    public File copyJavaSourceFile(final File srcFile, final String packageName)
            throws CoreException {

        String[] packageArray = PPABindingsUtil.getPackageArray(packageName);
        StringBuilder packageNameBuilder = new StringBuilder();
        if (packageArray.length > 0) {
            packageNameBuilder.append(packageArray[0]);
        }
        for (int i = 1; i < packageArray.length; ++i) {
            packageNameBuilder.append(FileUtils.fileSeparator);
            packageNameBuilder.append(packageArray[i]);
        }
        File newDir = new File(this.baseSrcDir.getAbsolutePath() + FileUtils.fileSeparator
                + packageNameBuilder.toString());
        if (!newDir.mkdirs()) {
            if (Logger.logError()) {
                Logger.error("Could not copy file `" + srcFile.getAbsolutePath() + "` to destination directory: `"
                        + newDir.getAbsolutePath() + "`");
                return null;
            }
        }
        try {
            FileUtils.copyFileToDirectory(srcFile, newDir);
            File file = new File(newDir.getAbsolutePath() + FileUtils.fileSeparator + srcFile.getName());
            return file;
        } catch (IOException e) {
            if (Logger.logError()) {
                Logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * Gets the cU.
     *
     * @param baseSrcDir
     *            the base src dir
     * @param file
     *            the file
     * @param options
     *            the options
     * @return the cU
     * @throws IOException
     * @throws CoreException
     */
    @NoneNull
    public CompilationUnit getPPACU(final File file, final PPAOptions options) throws IOException, CoreException {

        if (!file.getAbsolutePath().startsWith(baseSrcDir.getAbsolutePath())) {
            String packageName = getPackageFromFile(file);
            copyJavaSourceFile(file, packageName);
        }

        CompilationUnit cu = null;
        try {
            String content = FileUtils.readFileToString(file);
            PPATypeRegistry registry = new PPATypeRegistry(baseSrcDir);
            ASTNode node = null;
            PPAASTParser parser2 = new PPAASTParser(AST.JLS3);
            parser2.setStatementsRecovery(true);
            parser2.setResolveBindings(true);
            parser2.setSource(content.toCharArray());
            node = parser2.createAST(null);
            PPAEngine ppaEngine = new PPAEngine(registry, options);

            cu = (CompilationUnit) node;

            ppaEngine.addUnitToProcess(cu);
            ppaEngine.doPPA();
            ppaEngine.reset();
        } catch (NullPointerException e) {
            if (Logger.logWarn()) {
                Logger.warn("Error while getting CU from PPA. Catched NullPointerException raised by jdt compile. Skipping file unit `"
                        + file.getAbsolutePath() + "`");
            }
        } catch (AbortCompilation e) {
            if (Logger.logWarn()) {
                Logger.warn("Abort compilation of CU of file " + file.getAbsolutePath()
                        + "` Reason: " + e.getMessage());
            }
        } catch (Exception e) {
            if (Logger.logError()) {
                Logger.error("Unknown error while getting CU from PPA", e);
            }
        }

        return cu;
    }

    /**
     * Gets the cU.
     *
     * @param baseSrcDir
     *            the base src dir
     * @param file
     *            the file
     * @param options
     *            the options
     * @return the cU
     * @throws IOException
     * @throws CoreException
     */
    @NoneNull
    public static CompilationUnit getCU(final File file) throws IOException{
        String content = FileUtils.readFileToString(file);
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setStatementsRecovery(true);
        parser.setResolveBindings(true);
        parser.setSource(content.toCharArray());
        ASTNode node = parser.createAST(null);
        return (CompilationUnit) node;
    }
}
