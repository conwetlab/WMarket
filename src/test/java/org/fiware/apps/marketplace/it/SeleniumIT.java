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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class SeleniumIT extends AbstractIT {

	private static WebDriver driver;
	
	private static final String REQUIRED_FIELD = "This field is required.";
	private static final String INVALID_ADDRESS = "This field must be a valid email address.";
	private static final String EMAIL_REGISTERED = "This email is already registered.";
	private static final String INVALID_URL = "This field must be a valid URL.";
	private static final String MIN_LENGTH = "This field must contain at least %d chars.";
	private static final String MAX_LENGTH = "This field must not exceed %d chars.";
	private static final String DESCRIPTION_REGISTERED = "This name is already in use in this Store.";
	private static final String NO_REVIEWS = "This %s has not been reviewed yet. You can be the first.";
	private static final String STORE_NAME_ALREADY_IN_USE = "This name is already in use.";
	private static final String DESCRIPTION_NAME_ALREADY_IN_USE =  "This name is already in use in this Store.";
	private static final String STORE_URL_ALREADY_IN_USE = "This URL is already in use.";
	private static final String DESCRIPTION_URL_ALREADY_IN_USE = "This URL is already in use in this Store.";
	private static final String ACCOUNT_UPDATE_FORM = "account_update_form";
	private static final String REGISTRATION_FORM = "registration_form";
	private static final String STORE_FORM = "store_form";
	private static final String DESCRIPTION_CREATION_FORM = "description_create_form";
	private static final String DESCRIPTION_UPDATE_FORM = "description_update_form";
	private static final String REVIEW_FORM = "review_form";
	
	@BeforeClass
	public static void initBrowser() {
		driver = new FirefoxDriver();
		// Avoid Jenkinks failures
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		// Increase browser size to avoid Jenkins failures
		driver.manage().window().setSize(new Dimension(1024, 768));
	}
	
	@AfterClass
	public static void quitBrowser() {
		driver.quit();
	}

	@Before
	public void setUp() {
		
		try {
			// Close alerts if present
			driver.switchTo().alert().accept();
		} catch (NoAlertPresentException ex) {
			// Nothing to do...
		}
		
		// Restart cookies and browser
		driver.manage().deleteAllCookies();
		driver.get("about:blank");
		
		startMockServer();
	}

	@After
	public void quitDriver() {
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
		
		assertThat(alert.getText()).isEqualTo(textContent);	
	}

	private void verifyFieldValue(WebElement formElement, String fieldName, String fieldValue) {
		assertThat(formElement.findElement(By.name(fieldName)).getAttribute("value")).isEqualTo(fieldValue);
	}

	private void verifyFieldError(WebElement formElement, String fieldName, String fieldErrorMessage) {
		String formName = formElement.getAttribute("name");
		WebDriverWait wait = new WebDriverWait(driver, 5);
		WebElement fieldError = wait.until(ExpectedConditions.presenceOfElementLocated(
				By.cssSelector("form[name='" + formName + "'] [name='" + fieldName + "'] + p.field-error")));

		assertThat(fieldError.getText()).isEqualTo(fieldErrorMessage);
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


	private boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	
	private void verifyEntityAverageScore(double expectedScore) {
		assertThat(driver.findElement(By.cssSelector(".rating-overall")).getText())
				.isEqualTo(new Double(expectedScore).toString());
	}

	// LIST OF OPERATIONS

	private void loginUser(String username, String password, boolean isProvider) {
		// Find form
		WebElement formElement = driver.findElement(By.name("login_form"));

		// Fill the form fields
		fillField(formElement, "username", username);
		fillField(formElement, "password", password);
		
		// Submit
		submitForm(formElement);
		
		String currentURL = driver.getCurrentUrl();
		
		if (isProvider) {
			// Set user as provider
			try {
				clickOnSettingPanelItem("Settings");
				driver.findElement(By.cssSelector("form > button.btn.btn-success")).click();
			} catch (Exception e) {
				// Nothing to do... This exceptions can happen in two different cases:
				// 1) The user is already a provider
				// 2) The credentials are not valid
			}
		    
		    // Back to the previous URL
		    driver.get(currentURL);
		}
	}

	private void loginUserExpectError(String username, String password) {
		String errorMessage = "The username and password do not match.";

		loginUser(username, password, true);
		assertThat(driver.findElement(By.cssSelector(".alert-danger")).getText()).isEqualTo(errorMessage);
	}
	
	private void loginUser(String displayName, String email, String password) {
		createUser(displayName, email, password);

		driver.get(endPoint);
		loginUser(email, password, true);
	}

	private void loginDefaultUser() {
		String displayName 	= "Default User";
		String email 		= "defaultuser@domain.org";
		String password 	= "fiware1!";
		
		loginUser(displayName, email, password);
	}

	private void registerUser(String displayName, String email, String password) {
		// Find form
		WebElement formElement = driver.findElement(By.name(REGISTRATION_FORM));

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
		WebElement formElement = driver.findElement(By.name(ACCOUNT_UPDATE_FORM));

		// Fill the form fields
		fillField(formElement, "email", email);
		fillField(formElement, "company", company);

		// Submit
		submitForm(formElement);

		formElement = driver.findElement(By.name(ACCOUNT_UPDATE_FORM));

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
	
	public void logOut() {
		  driver.findElement(By.id("toggle-right-sidebar")).click();
		  driver.findElement(By.cssSelector("a.list-group-item.link-logout > span.item-text")).click();
	}
	  
	private void registerStore(String displayName, String url) {
		// Find form
		WebElement formElement = driver.findElement(By.name(STORE_FORM));

		// Fill the form fields
		fillField(formElement, "displayName", displayName);
		fillField(formElement, "url", url);

		// Submit
		submitForm(formElement);
	}

	private void registerStore(String displayName, String url, String imagePath) {
		// Find form
		WebElement formElement = driver.findElement(By.name(STORE_FORM));

		// Fill the form fields
		fillField(formElement, "displayName", displayName);
		fillField(formElement, "url", url);
		completeField(formElement, "imageBase64", imagePath);

		// Submit
		submitForm(formElement);
	}

	private void updateStoreDisplayName(String displayName) {
		// Find form
		WebElement formElement = driver.findElement(By.name(STORE_FORM));

		// Fill the form fields
		fillField(formElement, "displayName", displayName);

		// Submit
		submitForm(formElement);

		formElement = driver.findElement(By.name(STORE_FORM));

		// Verify the field values
		verifyFieldValue(formElement, "displayName", displayName);
		assertThat(driver.findElement(By.cssSelector(".panel-title.store-displayname"))
				.getText()).isEqualTo(displayName);
	}

	private void registerDescription(String displayName, String url) {
		// Find form
		WebElement formElement = driver.findElement(By.name(DESCRIPTION_CREATION_FORM));

		// Fill the form fields
		fillField(formElement, "displayName", displayName);
		fillField(formElement, "url", url);

		// Submit
		submitForm(formElement);
	}

	private void updateDescription(String displayName) {
		// Find form
		WebElement formElement = driver.findElement(By.name(DESCRIPTION_UPDATE_FORM));

		// Fill the form fields
		fillField(formElement, "displayName", displayName);

		// Submit
		submitForm(formElement);

		formElement = driver.findElement(By.name(DESCRIPTION_UPDATE_FORM));

		// Verify the field values
		verifyFieldValue(formElement, "displayName", displayName);
	}
	
	private void createUpdateReview(int nStars, String commentText) {
		driver.findElement(By.cssSelector("label[for='star" + nStars + "']")).click();
		WebElement formElement = driver.findElement(By.name(REVIEW_FORM));
		fillField(formElement, "comment", commentText);
		// This is not a normal form. The user is not redirected to a new page.
		// The request to create the review is made via AJAX
	    driver.findElement(By.cssSelector(".btn-update[data-submit]")).click();
	}
	
	private void deleteReview() {
		driver.findElement(By.cssSelector("label[for='star1']")).click();
	    driver.findElement(By.cssSelector(".btn-delete[data-submit]")).click();
	}
	
	private void createUserStoreAndDescription(String userName, String userMail, String userPass,
			String storeDisplayName, String storeUrl, String descriptionDisplayName, String descriptionUSDL) {
		loginUser(userName, userMail, userPass);
		driver.get(endPoint + "/stores/register");
		registerStore(storeDisplayName, storeUrl);
		driver.get(endPoint + "/descriptions/register");
		registerDescription(descriptionDisplayName, descriptionUSDL);
	}
	
	private void createUserStoreAndDescriptionDefaultCredentials(String storeDisplayName, String storeUrl, 
			String descriptionDisplayName, String descriptionUSDL) {
		createUserStoreAndDescription("Default User", "defaultuser@domain.com", "fiware1!", storeDisplayName, storeUrl, 
				descriptionDisplayName, descriptionUSDL);
	}

	// LIST OF TESTS

	@Test
	public void when_UserIsAnonymous_Expect_RedirectLoginView() {
		driver.get(endPoint);
		assertThat(driver.getTitle()).isEqualTo("Sign In - WMarket");
	}

	@Test
	public void should_RedirectLoginView_And_Can_LoginUser_When_RegistrationFormIsSubmitted() {
		String displayName 	= "New User";
		String email 		= "newuser@domain.org";
		String password 	= "fiware1!";

		driver.get(endPoint + "/register");

		registerUser(displayName, email, password);
		verifyAlertContent("You was registered successfully. You can log in right now.");

		loginUser(email, password, true);
		assertThat(driver.findElement(By.id("toggle-right-sidebar")).getText()).isEqualTo(displayName);
	}

	@Test
	public void should_DisplayErrorMessage_When_RegistrationFormIsSubmitted_And_DisplayNameIsInvalid() {
		driver.get(endPoint + "/register");
		// Find form
		WebElement formElement = driver.findElement(By.name(REGISTRATION_FORM));

		// Submit
		submitFormExpectError(formElement, "displayName", REQUIRED_FIELD);
	}

	@Test
	public void should_DisplayErrorMessage_When_RegistrationFormIsSubmitted_And_EmailIsInvalid() {
		String displayName 	= "Default User";
		String email 		= "defaultuser@domain.org";
		String password 	= "fiware1!";

		createUser(displayName, email, password);
		driver.get(endPoint + "/register");

		// Find form
		WebElement formElement = driver.findElement(By.name(REGISTRATION_FORM));

		// Fill the form fields
		fillField(formElement, "displayName", displayName);
		fillField(formElement, "password", password);
		fillField(formElement, "passwordConfirm", password);

		formElement = submitFormExpectError(formElement, "email", REQUIRED_FIELD);

		fillField(formElement, "email", "invalid_email");
		formElement = submitFormExpectError(formElement, "email", INVALID_ADDRESS);

		fillField(formElement, "email", email);
		formElement = submitFormExpectError(formElement, "email", EMAIL_REGISTERED);
	}

	@Test
	public void should_DisplayErrorMessage_When_RegistrationFormIsSubmitted_And_PasswordIsInvalid() {
		String displayName 	= "New User";
		String email 		= "newuser@domain.org";
		String password 	= "fiware1!";

		driver.get(endPoint + "/register");

		// Find form
		WebElement formElement = driver.findElement(By.name(REGISTRATION_FORM));

		// Fill the form fields
		fillField(formElement, "displayName", displayName);
		fillField(formElement, "email", email);

		formElement = submitFormExpectError(formElement, "password", REQUIRED_FIELD);

		fillField(formElement, "password", password);
		formElement = submitFormExpectError(formElement, "passwordConfirm", REQUIRED_FIELD);

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
		WebElement formElement = driver.findElement(By.name(ACCOUNT_UPDATE_FORM));

		fillField(formElement, "email", "invalid_email");
		formElement = submitFormExpectError(formElement, "email", INVALID_ADDRESS);

		fillField(formElement, "email", email);
		formElement = submitFormExpectError(formElement, "email", EMAIL_REGISTERED);
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

		assertThat(driver.getTitle()).isEqualTo("Sign In - WMarket");
		verifyAlertContent("Your password was changed. Please sign in again.");

		loginUser(email, newPassword, true);
		assertThat(driver.findElement(By.id("toggle-right-sidebar")).getText()).isEqualTo(displayName);
	}

	@Test
	public void should_RedirectLoginView_And_CanNot_LoginUser_When_UserIsDeleted() {
		String email 	= "defaultuser@domain.org";
		String password = "fiware1!";

		loginDefaultUser();

		clickOnSettingPanelItem("Settings");

		driver.findElement(By.cssSelector(".delete-account")).click();
		driver.findElement(By.cssSelector(".modal-delete")).findElement(By.cssSelector(".btn-delete[data-submit]")).click();
		assertThat(driver.getTitle()).isEqualTo("Sign In - WMarket");
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
		assertThat(driver.getTitle()).isEqualTo(displayName + " - Offerings - WMarket");
	}

	@Test
	public void should_DisplayErrorMessage_When_StoreCreateFormIsSubmitted_And_DisplayNameIsInvalid() {

		loginDefaultUser();
		driver.get(endPoint + "/stores/register");

		// Find form
		WebElement formElement = driver.findElement(By.name(STORE_FORM));

		formElement = submitFormExpectError(formElement, "displayName", REQUIRED_FIELD);
	}
	
	@Test
	public void should_DisplayErrorMessage_When_StoreCreateFormIsSubmitted_And_DisplayNameIsInUse() {

		loginDefaultUser();
		
		String repeatedDisplayName = "WStore";
		
		// Register the first Store
		driver.get(endPoint + "/stores/register");
		registerStore(repeatedDisplayName, "http://store.lab.fiware.org");

		// Registered the second Store
		driver.get(endPoint + "/stores/register");
		registerStore(repeatedDisplayName, "http://store2.lab.fiware.org");
		
		WebElement formElement = driver.findElement(By.name(STORE_FORM));
		verifyFieldError(formElement, "displayName", STORE_NAME_ALREADY_IN_USE);
	}

	@Test
	public void should_DisplayErrorMessage_When_StoreCreateFormIsSubmitted_And_URLIsInvalid() {
		String displayName 	= "FIWARE Store";

		loginDefaultUser();
		driver.get(endPoint + "/stores/register");

		WebElement formElement = driver.findElement(By.name(STORE_FORM));
		fillField(formElement, "displayName", displayName);

		formElement = submitFormExpectError(formElement, "url", REQUIRED_FIELD);

		fillField(formElement, "url", "invalid_url");
		formElement = submitFormExpectError(formElement, "url", INVALID_URL);
	}
	
	@Test
	public void should_DisplayErrorMessage_When_StoreCreateFormIsSubmitted_And_URLIsInUse() {

		loginDefaultUser();
		
		String repeatedURL = "http://store.lab.fiware.org";
		
		// Register the first Store
		driver.get(endPoint + "/stores/register");
		registerStore("wstore", repeatedURL);

		// Registered the second Store
		driver.get(endPoint + "/stores/register");
		registerStore("wstore2", repeatedURL);
		
		WebElement formElement = driver.findElement(By.name(STORE_FORM));
		verifyFieldError(formElement, "url", STORE_URL_ALREADY_IN_USE);
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
		assertThat(existsElement(By.xpath("//img[contains(@src, '/WMarket/media/store/fiware-store.png')]"))).isTrue();
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
	
	private void given_UserIsOwner_When_StoreFieldIsUpdatedWithOneInUse_Then_ErrorIsShown(String displayNameStore1, 
			String urlStore1, String displayNameStore2, String urlStore2, String field, String fieldNewValue,
			String expectedErrorMessage) {
		
		
		loginDefaultUser();

		clickOnOperationPanelItem("Register a new store");
		registerStore(displayNameStore1, urlStore1);
		
		clickOnOperationPanelItem("Register a new store");
		registerStore(displayNameStore2, urlStore2);

		driver.findElement(By.xpath("//a[contains(@href, '/WMarket/stores/" + displayNameStore2 + "/about')]")).click();
		
		// Fill the form field & submit
		WebElement formElement = driver.findElement(By.name(STORE_FORM));
		fillField(formElement, field, fieldNewValue);
		submitForm(formElement);
		
		// Check that the error message has been shown
		// The page is updated, so the form is different
		formElement = driver.findElement(By.name(STORE_FORM));
		verifyFieldError(formElement, field, expectedErrorMessage);

	}
	
	@Test
	public void given_UserIsOwner_When_StoreDisplayNameIsUpdatedWithOneInUse_Then_ErrorIsShown() {
		String displayNameStore1 	= "FIWARE Store";
		String urlStore1	        = "http://store.fiware.es";
		String displayNameStore2 	= "fiware-store-1";
		String urlStore2	        = "http://store2.fiware.es";
		
		given_UserIsOwner_When_StoreFieldIsUpdatedWithOneInUse_Then_ErrorIsShown(displayNameStore1, urlStore1, 
				displayNameStore2, urlStore2, "displayName", displayNameStore1, STORE_NAME_ALREADY_IN_USE);
	}
	
	@Test
	public void given_UserIsOwner_When_StoreURLIsUpdatedWithOneInUse_Then_ErrorIsShown() {
		String displayNameStore1 	= "FIWARE Store";
		String urlStore1	        = "http://store.fiware.es";
		String displayNameStore2 	= "fiware-store-1";
		String urlStore2	        = "http://store2.fiware.es";

		given_UserIsOwner_When_StoreFieldIsUpdatedWithOneInUse_Then_ErrorIsShown(displayNameStore1, urlStore1, 
				displayNameStore2, urlStore2, "url", urlStore1, STORE_URL_ALREADY_IN_USE);
	}

	@Test
	public void should_DisplayNotificationAlert_And_RedirectHomeView_When_UserIsOwner_And_StoreIsDeleted() {
		String displayName 	= "FIWARE Store";
		String url 			= "http://store.fiware.es";

		loginDefaultUser();

		clickOnOperationPanelItem("Register a new store");
		registerStore(displayName, url);

		driver.findElement(By.cssSelector(".delete-store")).click();
		driver.findElement(By.cssSelector(".modal-delete")).findElement(By.cssSelector(".btn-delete[data-submit]")).click();

		verifyAlertContent("The store '" + displayName + "' was deleted successfully.");
	}

	@Test
	public void should_DisplayStoreCreateLink_When_NoStoreAvailable() {
		loginDefaultUser();

		clickOnOperationPanelItem("Upload a new description");
		driver.findElement(By.linkText("register a store")).click();
		assertThat(driver.getTitle()).isEqualTo("New Store - WMarket");
	}

	@Test
	public void should_RedirectStoreView_When_DescriptionCreateFormIsSubmitted() {
		String displayName	   = "FIWARE Store";
		String url			   = "http://store.fiware.es";
		String descriptionName = "New description";

		createUserStoreAndDescriptionDefaultCredentials(displayName, url, descriptionName, defaultUSDLPath);

		assertThat(driver.getTitle()).isEqualTo(displayName + " - Offerings - WMarket");
		verifyAlertContent("The description '" + descriptionName + "' was uploaded successfully.");
	}

	@Test
	public void should_DisplayErrorMessage_When_DescriptionCreateFormIsSubmitted_And_DisplayNameIsInvalid() {
		String displayName = "FIWARE Store";
		String url = "http://store.fiware.es";
		String descriptionDisplayName = "New description";

		createUserStoreAndDescriptionDefaultCredentials(displayName, url, descriptionDisplayName, defaultUSDLPath);
		
		driver.get(endPoint + "/descriptions/register");

		// Find form
		WebElement formElement = driver.findElement(By.name(DESCRIPTION_CREATION_FORM));

		formElement = submitFormExpectError(formElement, "displayName", REQUIRED_FIELD);

		fillField(formElement, "displayName", "FI");
		formElement = submitFormExpectError(formElement, "displayName", String.format(MIN_LENGTH, 3));

		fillField(formElement, "displayName", "FIWARE Store extra chars");
		formElement = submitFormExpectError(formElement, "displayName", String.format(MAX_LENGTH, 20));

		fillField(formElement, "displayName", "FIWARE $invalid");
		formElement = submitFormExpectError(formElement, "displayName", "This field must contain alphanumerics (and -,_,.).");

		fillField(formElement, "displayName", descriptionDisplayName);
		fillField(formElement, "url", defaultUSDLPath);
		formElement = submitFormExpectError(formElement, "displayName", DESCRIPTION_REGISTERED);
	}
	
	@Test
	public void should_DisplayErrorMessage_When_DescriptionCreateFormIsSubmitted_And_DisplayNameIsInUse() {
		String displayName	   = "FIWARE Store";
		String url			   = "http://store.fiware.es";
		String descriptionName = "New description";

		createUserStoreAndDescriptionDefaultCredentials(displayName, url, descriptionName, defaultUSDLPath);

		assertThat(driver.getTitle()).isEqualTo(displayName + " - Offerings - WMarket");
		verifyAlertContent("The description '" + descriptionName + "' was uploaded successfully.");

		driver.get(endPoint + "/descriptions/register");
		registerDescription(descriptionName, defaultUSDLPath);
		
		WebElement formElement = driver.findElement(By.name(DESCRIPTION_CREATION_FORM));
		verifyFieldError(formElement, "displayName", DESCRIPTION_NAME_ALREADY_IN_USE);
	}
	
	@Test
	public void should_DisplayErrorMessage_When_DescriptionCreateFormIsSubmitted_And_URLNameIsInUse() {
		String displayName	   = "FIWARE Store";
		String url			   = "http://store.fiware.es";
		String descriptionName = "New description";

		createUserStoreAndDescriptionDefaultCredentials(displayName, url, descriptionName, defaultUSDLPath);

		assertThat(driver.getTitle()).isEqualTo(displayName + " - Offerings - WMarket");
		verifyAlertContent("The description '" + descriptionName + "' was uploaded successfully.");

		driver.get(endPoint + "/descriptions/register");
		registerDescription(descriptionName + "a", defaultUSDLPath);
		
		WebElement formElement = driver.findElement(By.name(DESCRIPTION_CREATION_FORM));
		verifyFieldError(formElement, "url", DESCRIPTION_URL_ALREADY_IN_USE);
	}

	@Test
	public void should_DisplayNotificationAlert_When_DescriptionIsUpdated() {
		String displayName	= "FIWARE Store";
		String url			= "http://store.fiware.es";
		
		createUserStoreAndDescriptionDefaultCredentials(displayName, url, "Test description", defaultUSDLPath);

		clickOnOperationPanelItem("My descriptions");
		driver.findElement(
				By.xpath("//a[contains(@href, '/WMarket/stores/fiware-store/descriptions/test-description')]")).click();

		String newDipslayName = "New description";
		updateDescription(newDipslayName);
		verifyAlertContent("The description '" + newDipslayName + "' was updated successfully.");
	}
	
	private void given_UserIsOwner_When_DescriptionFieldIsUpdatedWithOneInUse_Then_ErrorIsShown(
			String descriptionName1, String urlDescription1, String descriptionName2, String urlDescription2, 
			String field, String fieldNewValue,String expectedErrorMessage) {
		
		String storeDisplayName	= "FIWARE Store";
		String urlStore         = "http://store.fiware.es";
				
		// Register the first description
		createUserStoreAndDescriptionDefaultCredentials(storeDisplayName, urlStore, descriptionName1, 
				urlDescription1);
		
		// Register the second description
		driver.get(endPoint + "/descriptions/register");
		registerDescription(descriptionName2, urlDescription2);

		clickOnOperationPanelItem("My descriptions");
		driver.findElement(
				By.xpath("//a[contains(@href, '/WMarket/stores/fiware-store/descriptions/" + descriptionName2 + "')]"))
				.click();
		
		// Fill the form field & submit
		WebElement formElement = driver.findElement(By.name(DESCRIPTION_UPDATE_FORM));
		fillField(formElement, field, fieldNewValue);
		submitForm(formElement);
		
		// Check that the error message has been shown
		// The page is updated, so the form is different
		formElement = driver.findElement(By.name(DESCRIPTION_UPDATE_FORM));
		verifyFieldError(formElement, field, expectedErrorMessage);
	}
	
	@Test
	public void given_UserIsOwner_When_DescriptionDisplayNameIsUpdatedWithOneInUse_Then_ErrorIsShown() {
		
		String descriptionName1 = "description-1";
		String urlDescription1 = defaultUSDLPath;
		String descriptionName2 = "description-2";
		String urlDescription2 = secondaryUSDLPath;

		given_UserIsOwner_When_DescriptionFieldIsUpdatedWithOneInUse_Then_ErrorIsShown(descriptionName1, 
				urlDescription1, descriptionName2, urlDescription2, "displayName", descriptionName1, 
				DESCRIPTION_NAME_ALREADY_IN_USE); 
	}
	
	@Test
	public void given_UserIsOwner_When_DescriptionURLIsUpdatedWithOneInUse_Then_ErrorIsShown() {
		
		String descriptionName1 = "description-1";
		String urlDescription1 = defaultUSDLPath;
		String descriptionName2 = "description-2";
		String urlDescription2 = secondaryUSDLPath;

		given_UserIsOwner_When_DescriptionFieldIsUpdatedWithOneInUse_Then_ErrorIsShown(descriptionName1, 
				urlDescription1, descriptionName2, urlDescription2, "url", urlDescription1, 
				DESCRIPTION_URL_ALREADY_IN_USE); 
	}
	
	@Test
	public void should_DisplayNotificationAlert_When_DescriptionIsDeleted() {
		String displayName	   = "FIWARE Store";
		String url			   = "http://store.fiware.es";
		String descriptionName = "Test description";

		createUserStoreAndDescriptionDefaultCredentials(displayName, url, descriptionName, defaultUSDLPath);

		clickOnOperationPanelItem("My descriptions");
		driver.findElement(
				By.xpath("//a[contains(@href, '/WMarket/stores/fiware-store/descriptions/test-description')]")).click();

		driver.findElement(By.cssSelector(".delete-description")).click();
		driver.findElement(By.cssSelector(".btn-delete[data-submit]")).click();

		verifyAlertContent("The description '" + descriptionName + "' was deleted successfully.");
	}

	@Test
	public void when_DescriptionIsUploaded_Expect_OfferingHasDetailView() {
		String displayName	= "FIWARE Store";
		String url			= "http://store.fiware.es";

		createUserStoreAndDescriptionDefaultCredentials(displayName, url, "New description", defaultUSDLPath);
		
		driver.findElement(By.cssSelector(".offering-item .panel-title")).click();
		assertThat(driver.getTitle()).isEqualTo("OrionStarterKit" + " - WMarket");
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////// BOOKMARKS //////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void given_OfferingIsAvailable_When_BookmarkAdded_Expect_ShownInBookmarkListView() {
		String displayName	= "FIWARE Store";
		String url			= "http://store.fiware.es";

		createUserStoreAndDescriptionDefaultCredentials(displayName, url, "New description", defaultUSDLPath);
		
		driver.findElement(By.cssSelector(".offering-item .panel-title")).click();
		driver.findElement(By.linkText("Add bookmark")).click();
		clickOnOperationPanelItem("My bookmarks");

		assertThat(isElementPresent(By.linkText("OrionStarterKit"))).isTrue();
	}
	
	@Test
	public void given_OfferingIsAvailable_When_BookmarkRemoved_Expect_FadedInBookmarkListView() {
		String displayName = "FIWARE Store";
		String url = "http://store.fiware.es";

		createUserStoreAndDescriptionDefaultCredentials(displayName, url, "New description", defaultUSDLPath);

		driver.findElement(By.cssSelector(".offering-item .panel-title")).click();
		driver.findElement(By.linkText("Add bookmark")).click();
		clickOnOperationPanelItem("My bookmarks");

		driver.findElement(By.linkText("OrionStarterKit")).click();
		driver.findElement(By.linkText("Remove bookmark")).click();

		clickOnOperationPanelItem("My bookmarks");
		assertThat(!isElementPresent(By.linkText("OrionStarterKit"))).isTrue();
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////// REVIEWS ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void createReviewAncVerify(String userName) {
		String commentText  = "Example Review!";
		int nStars         = 4;
		
		// Review
		createUpdateReview(nStars, commentText);
	    
		// Check review value
		assertThat(driver.findElement(By.cssSelector("div.author-name")).getText()).isEqualTo(userName);
		assertThat(driver.findElement(By.cssSelector("div.review-body")).getText()).isEqualTo(commentText);
		verifyEntityAverageScore(nStars);
		
		// Open review dialog and check that comment contains user comment
		driver.findElement(By.cssSelector("label[for='star1']")).click();
		assertThat(driver.findElement(By.name("comment")).getAttribute("value")).isEqualTo(commentText);
	}
	
	private void createAndUpdateReviewAndVerify(String userName) throws Exception {
		
		String initialReview = "Example Review!";
		String finalReview   = "Updated Review!";
		int initialStars     = 4;
		int finalStars       = 2;
		
		// Review & Update
		createUpdateReview(initialStars, initialReview);
		Thread.sleep(1000);		// Wait 1 second so the review dialog can disappear
		createUpdateReview(finalStars, finalReview);
		Thread.sleep(1000);		// Wait 1 second so the reviews can be updated appropriately
	    
		// Check review value
		assertThat(driver.findElement(By.cssSelector("div.author-name")).getText()).isEqualTo(userName);
		assertThat(driver.findElement(By.cssSelector("div.review-body")).getText()).isEqualTo(finalReview);
		verifyEntityAverageScore(finalStars);

	}
	
	private void createAndDeleteReviewAndVerify(String entity) throws Exception {
		
		String commentText = "Example Review!";
		int stars          = 4;
		
		createUpdateReview(stars, commentText);
		Thread.sleep(1000);		// Wait 1 second so the review dialog can disappear
		deleteReview();
		Thread.sleep(1000);		// Wait 1 second so the reviews can be updated appropriately
	    
		// Check that there are no reviews available
		assertThat(driver.findElement(By.cssSelector("div.alert.alert-warning")).getText())
				.isEqualTo(String.format(NO_REVIEWS, entity));
		verifyEntityAverageScore(0);
		
		// Open dialog again and check that text is empty
		driver.findElement(By.cssSelector("label[for='star1']")).click();
		assertThat(driver.findElement(By.name("comment")).getAttribute("value")).isEqualTo("");
	}
	
	@Test
	public void given_OfferingIsAvailable_When_CreateReview_Expect_ReviewsListIsUpdated() {
		String displayName	= "FIWARE Store";
		String url			= "http://store.fiware.es";
		String userName     = "User A";
		String userMail     = "example@example.com";
		String userPass     = "fiware1!";

		createUserStoreAndDescription(userName, userMail, userPass, displayName, url, 
				"New Description", defaultUSDLPath);
		
		// Access the offering
		driver.findElement(By.cssSelector(".offering-item .panel-title")).click();
		
		createReviewAncVerify(userName);
	}
	
	@Test
	public void given_StoreIsAvailable_When_CreateReview_Expect_ReviewsListIsUpdated() {
		String displayName	= "FIWARE Store";
		String url			= "http://store.fiware.es";
		String userName     = "User A";
		String userMail     = "example@example.com";
		String userPass     = "fiware1!";

		loginUser(userName, userMail, userPass);
		driver.get(endPoint + "/stores/register");
		registerStore(displayName, url);
		
		// Access store reviews
	    driver.findElement(By.linkText("About & reviews")).click();
	    
		createReviewAncVerify(userName);
	}
	
	@Test
	public void given_OfferingIsAvailable_When_CreateReviewAndChangeRating_Expect_ReviewsListIsUpdated() {
		String displayName	= "FIWARE Store";
		String url			= "http://store.fiware.es";
		String userName     = "User A";
		String userMail     = "example@example.com";
		String userPass     = "fiware1!";
		String commentText  = "Example Review!";
		int initialStars   = 4;
		int finalStars     = 2;

		createUserStoreAndDescription(userName, userMail, userPass, displayName, url, 
				"New Description", defaultUSDLPath);
		
		// Access the offering
		driver.findElement(By.cssSelector(".offering-item .panel-title")).click();
		
		// Review
		driver.findElement(By.cssSelector("label[for='star" + initialStars + "']")).click();
		driver.findElement(By.cssSelector("label[for='form_star" + finalStars + "']")).click();
		WebElement formElement = driver.findElement(By.name(REVIEW_FORM));
		fillField(formElement, "comment", commentText);
		// This is not a normal form. The user is not redirected to a new page.
		// The request to create the review is made via AJAX
	    driver.findElement(By.xpath("(//button[@type='button'])[5]")).click();
	    
		// Check review value
		assertThat(driver.findElement(By.cssSelector("div.author-name")).getText()).isEqualTo(userName);
		assertThat(driver.findElement(By.cssSelector("div.review-body")).getText()).isEqualTo(commentText);
		verifyEntityAverageScore(finalStars);	
	}
	
	
	@Test
	public void given_OfferingIsRated_When_ReviewUpdated_Expect_ReviewListIsUpdated() throws Exception {
		
		String displayName	 = "FIWARE Store";
		String url			 = "http://store.fiware.es";
		String userName      = "User A";
		String userMail      = "example@example.com";
		String userPass      = "fiware1!";

		createUserStoreAndDescription(userName, userMail, userPass, displayName, url, 
				"New Description", defaultUSDLPath);
		
		// Access the offering
		driver.findElement(By.cssSelector(".offering-item .panel-title")).click();
		
		createAndUpdateReviewAndVerify(userName);
	}
	
	@Test
	public void given_StoreIsRated_When_ReviewUpdated_Expect_ReviewListIsUpdated() throws Exception {
		
		String displayName	 = "FIWARE Store";
		String url			 = "http://store.fiware.es";
		String userName      = "User A";
		String userMail      = "example@example.com";
		String userPass      = "fiware1!";

		loginUser(userName, userMail, userPass);
		driver.get(endPoint + "/stores/register");
		registerStore(displayName, url);
		
		// Access store reviews
	    driver.findElement(By.linkText("About & reviews")).click();
		
		createAndUpdateReviewAndVerify(userName);
	}
	
	@Test
	public void given_OfferingIsRated_When_ReviewDeleted_Expect_ReviewListIsUpdated() throws Exception {
		String displayName = "FIWARE Store";
		String url		   = "http://store.fiware.es";
		String userName    = "User A";
		String userMail    = "example@example.com";
		String userPass    = "fiware1!";

		createUserStoreAndDescription(userName, userMail, userPass, displayName, url, 
				"New Description", defaultUSDLPath);
		
		// Access the offering
		driver.findElement(By.cssSelector(".offering-item .panel-title")).click();
		
		createAndDeleteReviewAndVerify("offering");
	}
	
	@Test
	public void given_StoreIsRated_When_ReviewDeleted_Expect_ReviewListIsUpdated() throws Exception {
		String displayName = "FIWARE Store";
		String url		   = "http://store.fiware.es";
		String userName    = "User A";
		String userMail    = "example@example.com";
		String userPass    = "fiware1!";

		loginUser(userName, userMail, userPass);
		driver.get(endPoint + "/stores/register");
		registerStore(displayName, url);
		
		// Access store reviews
	    driver.findElement(By.linkText("About & reviews")).click();
		
		createAndDeleteReviewAndVerify("store");
	}
	
	@Test
	public void given_OfferingIsAvailable_When_TwoUserReview_Expect_AverageScore() throws Exception {
		
		String displayName	      = "FIWARE Store";
		String url			      = "http://store.fiware.es";
		String firstUserName      = "User A";
		String firstUserMail      = "example@example.com";
		String firstUserPass      = "fiware1!";
		String firstUserReview    = "Review number #1";
		int firstUserStars        = 3;
		String secondUserName     = "User B";
		String secondUserMail     = "example1@example.com";
		String secondUserPass     = "fiware1!";
		String secondUserReview   = "Review number #2";
		int sedondUserStars       = 2;
		double averageScore      = (((double) firstUserStars) + ((double) sedondUserStars)) / 2D;

		createUserStoreAndDescription(firstUserName, firstUserMail, firstUserPass, displayName, url, 
				"New Description", defaultUSDLPath);
		
		driver.findElement(By.cssSelector(".offering-item .panel-title")).click();	// Access offering
		createUpdateReview(firstUserStars, firstUserReview);						// First user review
		logOut();																	// Log out
		loginUser(secondUserName, secondUserMail, secondUserPass);					// Login the second user
		driver.findElement(By.cssSelector(".offering-item .panel-title")).click();	// Access offering
		createUpdateReview(sedondUserStars, secondUserReview);						// Second user review
		Thread.sleep(1000);															// Wait page updates
	    
		// Check review values. Review at the top should be last created
		assertThat(driver.findElements(By.cssSelector("div.author-name")).get(0).getText())
				.isEqualTo(secondUserName);
		assertThat(driver.findElements(By.cssSelector("div.review-body")).get(0).getText())
				.isEqualTo(secondUserReview);
		
		assertThat(driver.findElements(By.cssSelector("div.author-name")).get(1).getText())
				.isEqualTo(firstUserName);
		assertThat(driver.findElements(By.cssSelector("div.review-body")).get(1).getText())
				.isEqualTo(firstUserReview);

		verifyEntityAverageScore(averageScore);	
		
	}

	/////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////// CATEGORIES /////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void when_DescriptionIsUploaded_Expect_CategoryViewIsAvailable() {
		String displayName = "FIWARE Store";
		String url         = "http://store.fiware.es";
		String userName    = "User A";
		String userMail    = "example@example.com";
		String userPass    = "fiware1!";

		createUserStoreAndDescription(userName, userMail, userPass, displayName, url, 
			"New Description", defaultUSDLPath);

		driver.get(endPoint + "/");
		assertThat(driver.findElements(By.cssSelector(".category-title")).size()).isEqualTo(2);

		driver.get(endPoint + "/category/dataset");
		assertThat(driver.findElements(By.cssSelector(".offering-item")).size()).isEqualTo(1);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////// LAST VIEWED OFFERINGS ////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////
	
	private void scrollToElement(WebElement element){
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
	}
	
	private void viewOfferings(String[] offeringsToView) {
		for (String offeringName: offeringsToView) {
			driver.get(endPoint + "/");
		    driver.findElement(By.linkText(offeringName)).click();
		    
		    try {
		    	Thread.sleep(250);
		    } catch (InterruptedException ex) {
		    	// Not expected
		    }
		}
	}
	
	private void checkDivOfferings(String cssSelector, List<String> expectedOfferings) {
		
		// Go to the main page
		driver.get(endPoint + "/");
		
		List<String> viewedOfferings = new ArrayList<>();
		List<WebElement> elements = driver.findElements((By.cssSelector(cssSelector)));
		for (WebElement element: elements) {
			// Non-visible elements don't return their text
			scrollToElement(element);
			viewedOfferings.add(element.getText());
		}
		
		// Check that last Viewed Offerings are in the correct order
		assertThat(viewedOfferings).isEqualTo(expectedOfferings);
	}
	
	private List<String> inverseOrderNotRepeated(String[]... originals) {
		
		List<String> originalsReversed = new ArrayList<>();
		
		for (int i = originals.length - 1; i >= 0; i--) {
			String[] original = originals[i];
			
			// If a copy is not created, the original is also reversed
			String[] copy = new String[original.length];
			System.arraycopy(original, 0, copy, 0, original.length);
			
			List<String> reversed = Arrays.asList(copy);
			Collections.reverse(reversed);

			for (String offering: reversed) {
				if (!originalsReversed.contains(offering)) {
					originalsReversed.add(offering);
				}
			}

		}
		
		return originalsReversed;
	}

	@Test
	public void when_OfferingsViewed_Expect_CorrectOrderInLastViewedSection() throws InterruptedException {
		String displayName       = "FIWARE Store";
		String url               = "http://store.fiware.es";
		String userName          = "User A";
		String userMail          = "example@example.com";
		String lastViewedCss     = "[app-order=\"lastviewed\"] .offering-item .panel-title";
		String userPass          = "fiware1!";
		String[] offeringsToView = {"Interface Designed", "OrionStarterKit", "Led Panel", "KurentoStarterKit",
				"Evolution of Madrid Transport Pass Visualization", "Cosmos", "WStore", "Orion Context Broker", 
				"Madrid Public Transport Pass Price Evolution - FIWARE Developers Week", "ChartStarterKit", 
				"MultimediaPack", "ChartStarterKit"};

		createUserStoreAndDescription(userName, userMail, userPass, displayName, url, 
			"Example Offerings", complexDescriptionUSDLPath);

		viewOfferings(offeringsToView);
		
		List<String> expectedOfferings = inverseOrderNotRepeated(offeringsToView).subList(0, 10);				
		checkDivOfferings(lastViewedCss, expectedOfferings);
	}
	
	@Test
	public void when_OfferingsViewedByOtherUsers_Expect_CorrectOrderViewedByOthers() {
		String displayName         = "FIWARE Store";
		String url                 = "http://store.fiware.es";
		String viewedByOthersCss   = "[app-order=\"viewedByOthers\"] .offering-item .panel-title";
		String usersPass           = "fiware1!";
		String user1Name           = "User A";
		String user1Mail           = "example@example.com";
		String user2Name           = "User B";
		String user2Mail           = "example1@example.com";
		String user3Name           = "User C";
		String user3Mail           = "example2@example.com";
		String[] offeringsToViewU2 = {"Interface Designed", "OrionStarterKit", "Led Panel", "KurentoStarterKit",
				"Evolution of Madrid Transport Pass Visualization"};
		String[] offeringsToViewU3 = { "Cosmos", "WStore", "Orion Context Broker", "MultimediaPack",
				"Madrid Public Transport Pass Price Evolution - FIWARE Developers Week", "ChartStarterKit",
				"Led Panel"};
		
		// Create a set of offerings
		createUserStoreAndDescription(user1Name, user1Mail, usersPass, displayName, url, 
				"Example Offerings", complexDescriptionUSDLPath);
		
		// Log out the user
		logOut();
		
		// Register two new users
		createUser(user2Name, user2Mail, usersPass);
		createUser(user3Name, user3Mail, usersPass);
		
		// View offerings with the second user
		loginUser(user2Mail, usersPass, false);
		viewOfferings(offeringsToViewU2);
		logOut();
		
		// View Offerings with the third user
		// Offerings viewed by other users are also checked
		loginUser(user3Mail, usersPass, false);
		viewOfferings(offeringsToViewU3);
		
		List<String> viewedByOthersForUser3 = inverseOrderNotRepeated(offeringsToViewU2);
		checkDivOfferings(viewedByOthersCss, viewedByOthersForUser3);
		
		logOut();
			    
	    // Check offerings viewed by others with user 1
	    loginUser(user1Mail, usersPass, false);
	    List<String> viewedByOthersForUser1 = inverseOrderNotRepeated(offeringsToViewU2, 
	    		offeringsToViewU3);
	    
	    checkDivOfferings(viewedByOthersCss, viewedByOthersForUser1);
	}
}
