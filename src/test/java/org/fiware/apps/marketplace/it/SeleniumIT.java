package org.fiware.apps.marketplace.it;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2015 CoNWeT Lab, Universidad Politécnica de Madrid
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holders nor the names of its contributors
 *    may be used to endorse or promote products derived from this software 
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;


public class SeleniumIT extends AbstractIT {
	
	private WebDriver driver;

	@Override
	public void specificSetUp() {
		driver = new FirefoxDriver();
	}
	
	@After
	public void quitDriver() {
		driver.quit();
	}
	
	private void registerUser(String displayName, String email, String password, String passwordConfirm) {
		// Access Sign Up page
		driver.findElement(By.cssSelector("span.text-plain")).click();
	    driver.findElement(By.name("displayName")).clear();
	    
	    // Enter registration details
	    driver.findElement(By.name("displayName")).sendKeys(displayName);
	    driver.findElement(By.name("email")).clear();
	    driver.findElement(By.name("email")).sendKeys(email);
	    driver.findElement(By.name("password")).clear();
	    driver.findElement(By.name("password")).sendKeys(password);
	    driver.findElement(By.name("passwordConfirm")).clear();
	    driver.findElement(By.name("passwordConfirm")).sendKeys(passwordConfirm);
	    driver.findElement(By.xpath("//button[@type='submit']")).click();
	}
	
	private void logIn(String userNameOrMail, String password) {
	    driver.findElement(By.name("username")).clear();
	    driver.findElement(By.name("username")).sendKeys(userNameOrMail);
	    driver.findElement(By.name("password")).clear();
	    driver.findElement(By.name("password")).sendKeys(password);
	    driver.findElement(By.xpath("//button[@type='submit']")).click();
	}
	
	@Test
	public void testRegisterAndLogIn() {
		
		String displayName = "FIWARE Example";
		String email = "fiware@fiware.com";
		String password = "fiware1!";

		// Access the URL
		driver.get(endPoint);
	    
		registerUser(displayName, email, password, password);
		// When the user is properly registered, the system goes back to the log in page
		logIn(email, password);
		
	    // Check that the user is logged in...
	    assertThat(driver.findElement(By.id("toggle-right-sidebar")).getText()).isEqualTo(displayName);	    
	}

}
