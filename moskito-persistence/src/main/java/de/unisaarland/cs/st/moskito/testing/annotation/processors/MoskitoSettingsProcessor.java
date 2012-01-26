package de.unisaarland.cs.st.moskito.testing.annotation.processors;

import java.lang.annotation.Annotation;

public interface MoskitoSettingsProcessor {
	
	void evaluate(Annotation annotation) throws Exception;
	
}
