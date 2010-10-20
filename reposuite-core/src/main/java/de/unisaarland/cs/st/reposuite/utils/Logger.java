package de.unisaarland.cs.st.reposuite.utils;

import org.slf4j.LoggerFactory;

public class Logger {
	
	private static Tuple<org.slf4j.Logger, String> tags() {
		Throwable throwable = new Throwable();
		assert (throwable != null);
		
		throwable.fillInStackTrace();
		assert (throwable.getStackTrace().length > 2);
		
		Integer lineNumber = throwable.getStackTrace()[2].getLineNumber();
		String methodName = throwable.getStackTrace()[2].getMethodName();
		String className = throwable.getStackTrace()[2].getClassName();
		
		org.slf4j.Logger logger = LoggerFactory.getLogger(className);
		
		assert (lineNumber != null);
		assert (lineNumber > 0);
		assert (methodName != null);
		assert (className != null);
		
		return new Tuple<org.slf4j.Logger, String>(logger, className + "::" + methodName + "#" + lineNumber);
	}
	
	public static void warn(String message) {
		Tuple<org.slf4j.Logger, String> ret = tags();
		assert (ret.getFirst() != null);
		assert (ret.getSecond() != null);
		ret.getFirst().warn("[" + ret.getSecond() + "] " + message);
		
	}
}
