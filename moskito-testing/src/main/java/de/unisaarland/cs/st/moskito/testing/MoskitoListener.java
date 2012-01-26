/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * @author just
 * 
 */
public class MoskitoListener extends RunListener {
	
	/**
	 * 
	 */
	public MoskitoListener() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void testAssumptionFailure(final Failure failure) {
		// TODO Auto-generated method stub
		super.testAssumptionFailure(failure);
	}
	
	@Override
	public void testFailure(final Failure failure) throws Exception {
		// TODO Auto-generated method stub
		super.testFailure(failure);
	}
	
	@Override
	public void testFinished(final Description description) throws Exception {
		// TODO Auto-generated method stub
		super.testFinished(description);
	}
	
	@Override
	public void testIgnored(final Description description) throws Exception {
		// TODO Auto-generated method stub
		super.testIgnored(description);
	}
	
	@Override
	public void testRunFinished(final Result result) throws Exception {
		// TODO Auto-generated method stub
		super.testRunFinished(result);
	}
	
	@Override
	public void testRunStarted(final Description description) throws Exception {
		// TODO Auto-generated method stub
		super.testRunStarted(description);
	}
	
	@Override
	public void testStarted(final Description description) throws Exception {
		// TODO Auto-generated method stub
		super.testStarted(description);
	}
}
