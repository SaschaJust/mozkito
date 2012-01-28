package de.unisaarland.cs.st.moskito.testing.annotation.processors;

import java.lang.annotation.Annotation;

public interface MoskitoSettingsProcessor {
	
	void setup(Annotation annotation) throws Exception;
	
	void tearDown(Annotation annotation) throws Exception;
	
}
