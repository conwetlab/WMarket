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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class SeleniumIT extends AbstractIT {

	private WebDriver driver;

	@Override
	public void specificSetUp() {
		driver = new FirefoxDriver();
		startMockServer();
	}

	@After
	public void quitDriver() {
		driver.quit();
		wireMock.stop();
	}

	// LIST OF SHORTCUTS

	private void fillField(WebElement formElement, String fieldName, String fieldValue) {
		String formName = formElement.getAttribute("name");
		WebDriverWait wait = new WebDriverWait(driver, 5);
		WebElement fieldElement = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector("form[name='" + formName + "'] [name='" + fieldName + "']")));

		fieldElement.clear();
		fieldElement.sendKeys(fieldValue);
	}

	private void completeField(WebElement formElement, String fieldName, String fieldValue) {
		String formName = formElement.getAttribute("name");
		WebDriverWait wait = new WebDriverWait(driver, 5);
		WebElement fieldElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(
				"form[name='" + formName + "'] [name='" + fieldName + "']")));

		fieldElement.sendKeys(fieldValue);
	}

	private boolean existsElement(By by) {
		return driver.findElements(by).size() != 0;
	}

	private void submitForm(WebElement formElement) {
		formElement.findElement(By.cssSelector("button[type='submit']")).click();
	}

	private WebElement submitFormExpectError(WebElement formElement, String fieldName, String fieldError) {
		String formName = formElement.getAttribute("name");
		WebDriverWait wait = new WebDriverWait(driver, 5);

		// Submit
		submitForm(formElement);

		formElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.name(formName)));
		verifyFieldError(formElement, fieldName, fieldError);

		return formElement;
	}

	private void verifyAlertContent(String textContent) {
		WebDriverWait wait = new WebDriverWait(driver, 5);
		WebElement alert = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector(".alert-dismissible")));

		assertEquals(textContent, alert.getText());
	}

	private void verifyFieldValue(WebElement formElement, String fieldName, String fieldValue) {
		assertEquals(fieldValue, formElement.findElement(By.name(fieldName)).getAttribute("value"));
	}

	private void verifyFieldError(WebElement formElement, String fieldName, String fieldErrorMessage) {
		String formName = formElement.getAttribute("name");
		WebDriverWait wait = new WebDriverWait(driver, 5);
		WebElement fieldError = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector("form[name='" + formName + "'] [name='" + fieldName + "'] + p.field-error")));

		assertEquals(fieldErrorMessage, fieldError.getText());
	}

	private void clickOnSettingPanelItem(String textContent) {
		WebDriverWait wait = new WebDriverWait(driver, 5);

		driver.findElement(By.id("toggle-right-sidebar")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(textContent))).click();
	}

	private void clickOnOperationPanelItem(String textContent) {
		WebDriverWait wait = new WebDriverWait(driver, 5);

		driver.findElement(By.id("toggle-left-sidebar")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(textContent))).click();
	}

	// LIST OF OPERATIONS

	private void loginUser(String username, String password) {
		// Find form
		WebElement formElement = driver.findElement(By.name("login_form"));

		// Fill the form fields
		fillField(formElement, "username", username);
		fillField(formElement, "password", password);

		// Submit
		submitForm(formElement);
	}

	private void loginUserExpectError(String username, String password) {
		String errorMessage = "The username and password do not match.";

		loginUser(username, password);
		assertEquals(errorMessage, driver.findElement(By.cssSelector(".alert-danger")).getText());
	}

	private void loginDefaultUser() {
		String displayName 	= "Default User";
		String email 		= "defaultuser@domain.org";
		String password 	= "fiware1!";

		createUser(displayName, email, password);

		driver.get(endPoint);
		loginUser(email, password);
	}

	private void registerUser(String displayName, String email, String password) {
		// Find form
		WebElement formElement = driver.findElement(By.name("registration_form"));

		// Fill the form fields
		fillField(formElement, "displayName", displayName);
		fillField(formElement, "email", email);
		fillField(formElement, "password", password);
		fillField(formElement, "passwordConfirm", password);

		// Submit
		submitForm(formElement);
	}

	private void updateUser(String email, String company) {
		// Find form
		WebElement formElement = driver.findElement(By.name("account_update_form"));

		// Fill the form fields
		fillField(formElement, "email", email);
		fillField(formElement, "company", company);

		// Submit
		submitForm(formElement);

		formElement = driver.findElement(By.name("account_update_form"));

		// Verify the field values
		verifyFieldValue(formElement, "email", email);
		verifyFieldValue(formElement, "company", company);
	}

	private void updateUserPassword(String oldPassword, String newPassword) {
		// Find form
		WebElement formElement = driver.findElement(By.name("account_password_update_form"));

		// Fill the form fields
		fillField(formElement, "oldPassword", oldPassword);
		fillField(formElement, "password", newPassword);
		fillField(formElement, "passwordConfirm", newPassword);

		// Submit
		submitForm(formElement);
	}

	private void registerStore(String displayName, String url) {
		// Find form
		WebElement formElement = driver.findElement(By.name("store_form"));

		// Fill the form fields
		fillField(formElement, "displayName", displayName);
		fillField(formElement, "url", url);

		// Submit
		submitForm(formElement);
	}

	private void registerStore(String displayName, String url, String imagePath) {
		// Find form
		WebElement formElement = driver.findElement(By.name("store_form"));

		// Fill the form fields
		fillField(formElement, "displayName", displayName);
		fillField(formElement, "url", url);
		completeField(formElement, "imageBase64", imagePath);

		// Submit
		submitForm(formElement);
	}

	private void updateStoreDisplayName(String displayName) {
		// Find form
		WebElement formElement = driver.findElement(By.name("store_form"));

		// Fill the form fields
		fillField(formElement, "displayName", displayName);

		// Submit
		submitForm(formElement);

		formElement = driver.findElement(By.name("store_form"));

		// Verify the field values
		verifyFieldValue(formElement, "displayName", displayName);
		assertEquals(displayName, driver.findElement(By.cssSelector(".panel-title.store-displayname")).getText());
	}

	private void registerDescription(String displayName, String url) {
		// Find form
		WebElement formElement = driver.findElement(By.name("description_create_form"));

		// Fill the form fields
		fillField(formElement, "displayName", displayName);
		fillField(formElement, "url", url);

		// Submit
		submitForm(formElement);
	}

	private void updateDescription(String displayName) {
		// Find form
		WebElement formElement = driver.findElement(By.name("description_update_form"));

		// Fill the form fields
		fillField(formElement, "displayName", displayName);

		// Submit
		submitForm(formElement);

		formElement = driver.findElement(By.name("description_update_form"));

		// Verify the field values
		verifyFieldValue(formElement, "displayName", displayName);
	}

	// LIST OF TESTS

	@Test
	public void when_UserIsAnonymous_Expect_RedirectLoginView() {
		driver.get(endPoint);
		assertEquals("Sign In - WMarket", driver.getTitle());
	}

	@Test
	public void should_RedirectLoginView_And_Can_LoginUser_When_RegistrationFormIsSubmitted() {
		String displayName 	= "New User";
		String email 		= "newuser@domain.org";
		String password 	= "fiware1!";

		driver.get(endPoint + "/register");

		registerUser(displayName, email, password);
		verifyAlertContent("You was registered successfully. You can log in right now.");

		loginUser(email, password);
		assertEquals(displayName, driver.findElement(By.id("toggle-right-sidebar")).getText());
	}

	@Test
	public void should_DisplayErrorMessage_When_RegistrationFormIsSubmitted_And_DisplayNameIsInvalid() {
		driver.get(endPoint + "/register");
		// Find form
		WebElement formElement = driver.findElement(By.name("registration_form"));

		// Submit
		submitFormExpectError(formElement, "displayName", "This field is required.");
	}

	@Test
	public void should_DisplayErrorMessage_When_RegistrationFormIsSubmitted_And_EmailIsInvalid() {
		String displayName 	= "Default User";
		String email 		= "defaultuser@domain.org";
		String password 	= "fiware1!";

		createUser(displayName, email, password);
		driver.get(endPoint + "/register");

		// Find form
		WebElement formElement = driver.findElement(By.name("registration_form"));

		// Fill the form fields
		fillField(formElement, "displayName", displayName);
		fillField(formElement, "password", password);
		fillField(formElement, "passwordConfirm", password);

		formElement = submitFormExpectError(formElement, "email", "This field is required.");

		fillField(formElement, "email", "invalid_email");
		formElement = submitFormExpectError(formElement, "email", "This field must be a valid email address.");

		fillField(formElement, "email", email);
		formElement = submitFormExpectError(formElement, "email", "This email is already registered.");
	}

	@Test
	public void should_DisplayErrorMessage_When_RegistrationFormIsSubmitted_And_PasswordIsInvalid() {
		String displayName 	= "New User";
		String email 		= "newuser@domain.org";
		String password 	= "fiware1!";

		driver.get(endPoint + "/register");

		// Find form
		WebElement formElement = driver.findElement(By.name("registration_form"));

		// Fill the form fields
		fillField(formElement, "displayName", displayName);
		fillField(formElement, "email", email);

		formElement = submitFormExpectError(formElement, "password", "This field is required.");

		fillField(formElement, "password", password);
		formElement = submitFormExpectError(formElement, "passwordConfirm", "This field is required.");

		fillField(formElement, "passwordConfirm", "fiware2!");
		formElement = submitFormExpectError(formElement, "passwordConfirm", "The two passwords do not match.");
	}

	@Test
	public void given_UserIsAuthenticated_When_LogoutIsClicked_Then_RedirectLoginView() {
		loginDefaultUser();

		clickOnSettingPanelItem("Log out");
		verifyAlertContent("You've logged out successfully.");
	}

	@Test
	public void should_DisplayNotificationAlert_When_UserEmailOrUserCompanyAreUpdated() {
		String email 	= "newuser@domain.org";
		String company 	= "FIWARE Lab";

		loginDefaultUser();

		clickOnSettingPanelItem("Settings");
		updateUser(email, company);
		verifyAlertContent("Your profile was updated successfully.");
	}

	@Test
	public void should_DisplayErrorMessage_When_AccountUpdateFormIsSubmitted_And_EmailIsInvalid() {
		String displayName 	= "New User";
		String email 		= "newuser@domain.org";
		String password 	= "fiware1!";

		createUser(displayName, email, password);

		loginDefaultUser();
		driver.get(endPoint + "/account");

		// Find form
		WebElement formElement = driver.findElement(By.name("account_update_form"));

		fillField(formElement, "email", "invalid_email");
		formElement = submitFormExpectError(formElement, "email", "This field must be a valid email address.");

		fillField(formElement, "email", email);
		formElement = submitFormExpectError(formElement, "email", "This email is already registered.");
	}

	@Test
	public void should_RedirectLoginView_And_Can_LoginUser_When_UserPasswordIsUpdated() {
		String displayName 	= "Default User";
		String email 		= "defaultuser@domain.org";
		String oldPassword 	= "fiware1!";
		String newPassword 	= "fiware2!";

		loginDefaultUser();

		driver.get(endPoint + "/account/password");
		updateUserPassword(oldPassword, newPassword);

		assertEquals("Sign In - WMarket", driver.getTitle());
		verifyAlertContent("Your password was changed. Please sign in again.");

		loginUser(email, newPassword);
		assertEquals(displayName, driver.findElement(By.id("toggle-right-sidebar")).getText());
	}

	@Test
	public void should_RedirectLoginView_And_CanNot_LoginUser_When_UserIsDeleted() {
		String email 	= "defaultuser@domain.org";
		String password = "fiware1!";

		loginDefaultUser();

		clickOnSettingPanelItem("Settings");

		driver.findElement(By.linkText("Delete account")).click();
		assertEquals("Sign In - WMarket", driver.getTitle());
		verifyAlertContent("Your account was deleted successfully.");

		loginUserExpectError(email, password);
	}

	@Test
	public void given_UserIsAuthenticated_When_StoreCreateFormIsSubmitted_Then_RedirectStoreView() {
		String displayName 	= "FIWARE Store";
		String url 			= "http://store.fiware.es";

		loginDefaultUser();

		clickOnOperationPanelItem("Register a new store");
		registerStore(displayName, url);
		assertEquals(displayName + " - Offerings - WMarket", driver.getTitle());
	}

	@Test
	public void should_DisplayErrorMessage_When_StoreCreateFormIsSubmitted_And_DisplayNameIsInvalid() {

		loginDefaultUser();
		driver.get(endPoint + "/stores/register");

		// Find form
		WebElement formElement = driver.findElement(By.name("store_form"));

		formElement = submitFormExpectError(formElement, "displayName", "This field is required.");
	}

	@Test
	public void should_DisplayErrorMessage_When_StoreCreateFormIsSubmitted_And_URLIsInvalid() {
		String displayName 	= "FIWARE Store";

		loginDefaultUser();
		driver.get(endPoint + "/stores/register");

		WebElement formElement = driver.findElement(By.name("store_form"));
		fillField(formElement, "displayName", displayName);

		formElement = submitFormExpectError(formElement, "url", "This field is required.");

		fillField(formElement, "url", "invalid_url");
		formElement = submitFormExpectError(formElement, "url", "This field must be a valid URL.");
	}

	@Test
	public void should_DisplayStoreImage_When_StoreIsCreated_And_ImageIsUploaded() {
		File image = new File("src/test/resources/image.png");

		String displayName	= "FIWARE Store";
		String url			= "http://store.fiware.es";
		String imagePath	= image.getAbsolutePath();

		loginDefaultUser();
		driver.get(endPoint + "/stores/register");

		registerStore(displayName, url, imagePath);
		assertTrue(existsElement(By.xpath("//img[contains(@src, '/WMarket/media/store/fiware-store.png')]")));
	}

	@Test
	public void given_UserIsOwner_When_StoreDisplayNameIsUpdated_Then_DisplayNotificationAlert() {
		String displayName 	= "FIWARE Store";
		String url 			= "http://store.fiware.es";

		loginDefaultUser();

		clickOnOperationPanelItem("Register a new store");
		registerStore(displayName, url);

		driver.findElement(By.xpath("//a[contains(@href, '/WMarket/stores/fiware-store/about')]")).click();

		displayName = "FIWARE New Store";
		updateStoreDisplayName(displayName);
		verifyAlertContent("The store '" + displayName + "' was updated successfully.");
	}

	@Test
	public void should_DisplayNotificationAlert_And_RedirectHomeView_When_UserIsOwner_And_StoreIsDeleted() {
		String displayName 	= "FIWARE Store";
		String url 			= "http://store.fiware.es";

		loginDefaultUser();

		clickOnOperationPanelItem("Register a new store");
		registerStore(displayName, url);

		driver.findElement(By.xpath("//a[contains(@href, 'javascript:deleteStore()')]")).click();
		verifyAlertContent("The store '" + displayName + "' was deleted successfully.");
	}

	@Test
	public void should_DisplayStoreCreateLink_When_NoStoreAvailable() {
		loginDefaultUser();

		clickOnOperationPanelItem("Upload a new description");
		driver.findElement(By.linkText("register a store")).click();
		assertEquals("New Store - WMarket", driver.getTitle());
	}

	@Test
	public void should_RedirectStoreView_When_DescriptionCreateFormIsSubmitted() {
		String displayName	= "FIWARE Store";
		String url			= "http://store.fiware.es";

		loginDefaultUser();
		driver.get(endPoint + "/stores/register");
		registerStore(displayName, url);

		driver.get(endPoint + "/descriptions/register");

		registerDescription("New description", defaultUSDLPath);
		assertEquals(displayName + " - Offerings - WMarket", driver.getTitle());
		verifyAlertContent("The description 'New description' was uploaded successfully.");
	}

	@Test
	public void should_DisplayNotificationAlert_When_DescriptionIsUpdated() {
		String displayName	= "FIWARE Store";
		String url			= "http://store.fiware.es";

		loginDefaultUser();
		driver.get(endPoint + "/stores/register");
		registerStore(displayName, url);

		driver.get(endPoint + "/descriptions/register");

		registerDescription("Test description", defaultUSDLPath);
		clickOnOperationPanelItem("My descriptions");
		driver.findElement(
				By.xpath("//a[contains(@href, '/WMarket/stores/fiware-store/descriptions/test-description')]")).click();

		updateDescription("New description");
		verifyAlertContent("The description 'New description' was updated successfully.");
	}

	@Test
	public void should_DisplayNotificationAlert_When_DescriptionIsDeleted() {
		String displayName	= "FIWARE Store";
		String url			= "http://store.fiware.es";

		loginDefaultUser();
		driver.get(endPoint + "/stores/register");
		registerStore(displayName, url);

		driver.get(endPoint + "/descriptions/register");

		registerDescription("Test description", defaultUSDLPath);
		clickOnOperationPanelItem("My descriptions");
		driver.findElement(
				By.xpath("//a[contains(@href, '/WMarket/stores/fiware-store/descriptions/test-description')]")).click();

		driver.findElement(By.xpath("//a[contains(@href, 'javascript:deleteDescription()')]")).click();
		verifyAlertContent("The description '" + "Test description" + "' was deleted successfully.");
	}

	@Test
	public void when_DescriptionIsUploaded_Expect_OfferingHasDetailView() {
		String displayName	= "FIWARE Store";
		String url			= "http://store.fiware.es";

		loginDefaultUser();
		driver.get(endPoint + "/stores/register");
		registerStore(displayName, url);

		driver.get(endPoint + "/descriptions/register");

		registerDescription("New description", defaultUSDLPath);
		driver.findElement(By.cssSelector(".offering-item .panel-title")).click();
		assertEquals("OrionStarterKit" + " - WMarket", driver.getTitle());
	}

}
