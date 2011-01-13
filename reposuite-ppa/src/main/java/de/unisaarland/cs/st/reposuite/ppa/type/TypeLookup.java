package de.unisaarland.cs.st.reposuite.ppa.type;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.ppa.PPA;
import de.unisaarland.cs.st.reposuite.ppa.model.JavaClassDefinition;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

public class TypeLookup {
	
	//mapping from typeName -> <file path, md5 hash>
	private final Map<String, File> fileNameCache = new HashMap<String, File>();
	private final Map<File, Long> analyzedFiles = new HashMap<File, Long>();
	private final File sourceDir;
	private long lastModified;
	private final Map<String, JavaClassDefinition> classDefCache = new HashMap<String, JavaClassDefinition>();
	
	public TypeLookup(final File sourceDir) {
		this.sourceDir = sourceDir;
		this.lastModified = sourceDir.lastModified();
		
		//cache file names
		cacheFileNames();
		
	}
	
	private boolean cacheFileNames() {
		if (this.lastModified == sourceDir.lastModified()) {
			return true;
		}
		this.lastModified = sourceDir.lastModified();
		if (Logger.logDebug()) {
			Logger.debug("Caching file names for type resolution ... ");
		}
		try {
			Iterator<File> fileIterator = FileUtils.getFileIterator(sourceDir, new String[]{"java"}, true);
			while (fileIterator.hasNext()) {
				File file = fileIterator.next();
				String typeName = file.getName().replace(".java", "");
				fileNameCache.put(typeName, file);
			}
			if (Logger.logDebug()) {
				Logger.debug("done");
			}
			return true;
		} catch (IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			if (Logger.logDebug()) {
				Logger.debug("failed!");
			}
			return false;
		}
		
	}
	
	/**
	 * Attempts to find a file within the given directory in which the given
	 * qualified type name is declared.
	 * 
	 * @param qualifiedTypeName
	 * @return
	 */
	public File findType(final String qualifiedTypeName) {
		
		String typePackageName = null;
		if(qualifiedTypeName.contains(".")){
			String[] typeNameParts = qualifiedTypeName.split(".");
			String shortTypeName = typeNameParts[typeNameParts.length - 1];
			
			//also erase the last dot of the package name
			int endIndex = qualifiedTypeName.length() - shortTypeName.length() - 2;
			typePackageName = qualifiedTypeName.substring(0, endIndex);
		}
		
		
		//check if the type was already cached and file did not change since then
		if (classDefCache.containsKey(qualifiedTypeName)) {
			JavaClassDefinition classDef = classDefCache.get(qualifiedTypeName);
			File typeFile = new File(sourceDir.getAbsoluteFile() + FileUtils.fileSeparator + classDef.getFilePath());
			if (typeFile.exists() && (!typeFile.isDirectory())
					&& (classDef.getTimestamp().getMillis() == typeFile.lastModified())) {
				// file did not change
				return typeFile;
			}
		}
		
		//if the type file is cached, analyze the file
		File typeFile = fileNameCache.get(qualifiedTypeName);
		if ((typeFile != null) && typeFile.exists() && (!typeFile.isDirectory())) {
			//found file.
			if (findTypInFile(typeFile, qualifiedTypeName)) {
				return typeFile;
			}
		}
		
		
		//iterate over all files within the directory representing the package of the type
		if(typePackageName != null){
			Iterator<File> dirIter = FileUtils.getSubDirectoryIterator(sourceDir);
			while(dirIter.hasNext()){
				File dir = dirIter.next();
				String dirNameAsPackage = dir.getAbsolutePath().replace(FileUtils.fileSeparator, ".");
				if (dirNameAsPackage.contains(typePackageName)) {
					//found right directory
					try {
						Iterator<File> sourceIter = FileUtils.getFileIterator(dir, new String[] { "java" }, false);
						while (sourceIter.hasNext()) {
							File sourceFile = sourceIter.next();
							if (findTypInFile(typeFile, qualifiedTypeName)) {
								return sourceFile;
							}
						}
					} catch (IOException e) {
						if (Logger.logError()) {
							Logger.error(e.getMessage(), e);
						}
					}
					break;
				}
			}
		}
		
		//iterate over all non-analyzed files and analyzed files changed and analyze them
		try {
			Iterator<File> fileIter = FileUtils.getFileIterator(sourceDir, new String[] { "java" }, true);
			while (fileIter.hasNext()) {
				File sourceFile = fileIter.next();
				if (analyzedFiles.containsKey(sourceFile)) {
					if (analyzedFiles.get(sourceFile) == sourceFile.lastModified()) {
						continue;
					}
				}
				if (findTypInFile(typeFile, qualifiedTypeName)) {
					return sourceFile;
				}
			}
		} catch (IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
		return null;
	}
	
	
	/**
	 * Analyzes the source code file and caches detected class definitions
	 * 
	 * @param sourceFile
	 * @param qualifiedTypeName
	 * @return true if the given qualified type name is found within the source
	 *         code file. false otherwise.
	 */
	private boolean findTypInFile(final File sourceFile, final String qualifiedTypeName) {
		// Analyze classes defined. add file to analyzed files
		//update classDefCache
		
		boolean result = false;
		Set<JavaClassDefinition> typeDefs = PPA.getJavaClassDefinitions(sourceFile);
		for (JavaClassDefinition typeDef : typeDefs) {
			classDefCache.put(typeDef.getFullQualifiedName(), typeDef);
			if (typeDef.getFullQualifiedName().equals(qualifiedTypeName)) {
				result = true;
			}
		}
		analyzedFiles.put(sourceFile, sourceFile.lastModified());
		
		return result;
	}
}
