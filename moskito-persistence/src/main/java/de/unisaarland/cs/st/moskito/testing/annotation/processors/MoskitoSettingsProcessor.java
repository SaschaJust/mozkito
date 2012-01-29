package de.unisaarland.cs.st.moskito.testing.annotation.processors;

import java.lang.annotation.Annotation;

import de.unisaarland.cs.st.moskito.exceptions.TestSettingsError;

public interface MoskitoSettingsProcessor {
	
	void setup(Class<?> aClass,
	           Annotation annotation) throws TestSettingsError;
	
	void tearDown(Class<?> aClass,
	              Annotation annotation) throws TestSettingsError;
	
}
