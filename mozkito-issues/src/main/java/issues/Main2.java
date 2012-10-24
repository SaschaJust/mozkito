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
package issues;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * The Class Main2.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class Main2 {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(final String[] args) {
		// Create a new instance of the Firefox driver
		// Notice that the remainder of the code relies on the interface,
		// not the implementation.
		final WebDriver driver = new FirefoxDriver();
		
		// And now use this to visit Google
		driver.get("http://ankhsvn.open.collab.net/issues/query.cgi");
		// Alternatively the same thing can be done like this
		// driver.navigate().to("http://www.google.com");
		
		// Find the text input element by its name
		final WebElement element = driver.findElement(By.name("Submit query"));
		
		element.click();
		
		// // Enter something to search for
		// element.sendKeys("Cheese!");
		//
		// // Now submit the form. WebDriver will find the form for us from the element
		// element.submit();
		
		// Check the title of the page
		System.out.println("Page title is: " + driver.getTitle());
		
		// Google's search is rendered dynamically with JavaScript.
		// Wait for the page to load, timeout after 10 seconds
		(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
			
			@Override
			public Boolean apply(final WebDriver d) {
				return d.getTitle().toLowerCase().endsWith("issue list");
			}
		});
		
		System.err.println(driver.getPageSource());
		
		// Should see: "cheese! - Google Search"
		System.out.println("Page title is: " + driver.getTitle());
		
		// Close the browser
		driver.quit();
	}
	
}
