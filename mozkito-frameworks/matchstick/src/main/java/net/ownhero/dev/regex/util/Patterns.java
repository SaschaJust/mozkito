/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.regex.util;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public final class Patterns {
	
	public static final String GIT_LOG_DATE_FORMAT = "({EEE}[A-Za-z]{3})\\s+({MMM}[A-Za-z]{3})\\s+({d}\\d{1,2})\\s+({HH}[0-2]\\d):({mm}[0-5]\\d):({ss}[0-5]\\d)\\s+({yyyy}\\d{4})\\s+({Z}[+-]\\d{4})";
	public static final String EMAIL_ADDRESS       = "((?#email user)[A-Za-z0-9._%-+]+@(((?#email real domain)[A-Za-z0-9.-]+\\.[A-Za-z]{2,4})|((?#email anonymous domain)\\p{XDigit}{4,}(-\\p{XDigit}{4,}){4})))";
	
}
