package de.unisaarland.cs.st.moskito.testing_impl;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import de.unisaarland.cs.st.moskito.testing.MoskitoTest;

public class MoskitoTestingInstrumenter {
	
	public CtClass process(final CtClass ctClass) {
		try {
			// final CtClass ctClass =
			// classPool.get(this.getClass().getCanonicalName());
			if (ctClass.getSuperclass().getName().equals(MoskitoTest.class.getCanonicalName())) {
				final ClassFile ccFile = ctClass.getClassFile();
				final ConstPool constpool = ccFile.getConstPool();
				final AnnotationsAttribute attrBeforeClass = new AnnotationsAttribute(constpool,
				                                                                      AnnotationsAttribute.visibleTag);
				final Annotation annotBeforeClass = new Annotation("BeforeClass", constpool);
				final Annotation annotAfterClass = new Annotation("AfterClass", constpool);
				final Annotation annotBefore = new Annotation("Before", constpool);
				final Annotation annotAfter = new Annotation("After", constpool);
				// annot.addMemberValue("value", new
				// IntegerMemberValue(ccFile.getConstPool(), 0));
				attrBeforeClass.addAnnotation(annotBeforeClass);
				attrBeforeClass.addAnnotation(annotAfterClass);
				attrBeforeClass.addAnnotation(annotBefore);
				attrBeforeClass.addAnnotation(annotAfter);
				// final CtMethod method = new CtMethod(null,
				// "__setUpBeforeClass",
				// null, ctClass);
				final CtMethod methodSetupBeforeClass = CtNewMethod.make("public static void __setupBeforeClass() throws Exception { MoskitoTest.setUpBeforeClass(); }",
				                                                         ctClass);
				final CtMethod methodTearDownAfterClass = CtNewMethod.make("public static void __tearDownAfterClass() throws Exception { MoskitoTest.tearDownAfterClass(); }",
				                                                           ctClass);
				final CtMethod methodSetup = CtNewMethod.make("public void __setup() throws Exception { MoskitoTest.setup(); }",
				                                              ctClass);
				final CtMethod methodTearDown = CtNewMethod.make("public void __tearDown() throws Exception { MoskitoTest.tearDown(); }",
				                                                 ctClass);
				
				methodSetupBeforeClass.getMethodInfo().addAttribute(attrBeforeClass);
				ctClass.addMethod(methodSetupBeforeClass);
				ctClass.addMethod(methodTearDownAfterClass);
				ctClass.addMethod(methodSetup);
				ctClass.addMethod(methodTearDown);
				return ctClass;
			}
		} catch (final NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final CannotCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
