package de.unisaarland.cs.st.moskito.testing.annotation.processors;

import java.lang.annotation.Annotation;

import de.unisaarland.cs.st.moskito.exceptions.TestSettingsError;

public interface MoskitoSettingsProcessor {
	
	void setup(Annotation annotation) throws TestSettingsError;
	
	void tearDown(Annotation annotation) throws TestSettingsError;
	
}
