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
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;

import org.apache.catalina.startup.Tomcat;
import org.fiware.apps.marketplace.model.APIError;
import org.fiware.apps.marketplace.model.ErrorType;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ch.vorburger.mariadb4j.DB;


public class UsersServiceIT {
	
	private static TemporaryFolder baseDir = new TemporaryFolder();	
	private static Tomcat tomcat = new Tomcat();
	private static DB EMBEDDED_DATABASE;
	private static String END_POINT;
	private static String MODIFIED_WAR_NAME = "WMarket-Integration.war";
	private static String DATABASE = "marketplace-test";
	
	private static int getFreePort() throws IOException {
		
		ServerSocket socket = new ServerSocket(0);
		int port = socket.getLocalPort();
		socket.close();
		
		return port;
	}
	
	@BeforeClass
	public static void startUp() throws Exception {
		
		// Initialize DB
		int port = getFreePort();
		EMBEDDED_DATABASE = DB.newEmbeddedDB(port);
		EMBEDDED_DATABASE.start();
		EMBEDDED_DATABASE.createDB(DATABASE);
						
		// Initialize baseDir and the webapps directory
		baseDir.create();
		File webApps = baseDir.newFolder("webapps");
		
		// Set up Tomcat
		tomcat.setPort(0);											// Automatic port
		tomcat.setBaseDir(baseDir.getRoot().getAbsolutePath());		// Base Dir
		tomcat.addContext("/", webApps.getAbsolutePath());			// Context
		
		// Create properties
		Properties properties = new Properties();
		properties.setProperty("jdbc.driverClassName", "com.mysql.jdbc.Driver");
		properties.setProperty("jdbc.url", String.format("jdbc:mysql://localhost:%d/%s", port, DATABASE));
		properties.setProperty("jdbc.username", "root");
		properties.setProperty("jdbc.password", "");
		
		File propertiesFile = baseDir.newFile("properties.properties");
		propertiesFile.createNewFile();
		properties.store(new FileOutputStream(propertiesFile), "");
		
		// Copy the WAR (the original one cannot be modified)
		String projectDirectory = Paths.get(".").toAbsolutePath().toString();
		String modifiedWarPath = projectDirectory + "/target/" + MODIFIED_WAR_NAME;
		
	    Path originalWar = Paths.get(projectDirectory + "/target/FiwareMarketplace.war");
	    Path modifiedWar = Paths.get(modifiedWarPath);
	    Files.copy(originalWar, modifiedWar, StandardCopyOption.REPLACE_EXISTING);
	    
	    // Modify properties using the file created previously
	    FileSystem fs = FileSystems.newFileSystem(modifiedWar, null);
        Path fileInsideZipPath = fs.getPath("WEB-INF/classes/properties/database.properties");
        // Copy properties into the WAR
        Files.copy(propertiesFile.toPath(), fileInsideZipPath, StandardCopyOption.REPLACE_EXISTING);
        fs.close();
        
        // Add modified WAR       
		tomcat.addWebapp("FiwareMarketplace", modifiedWarPath);
        
		// Start up
		tomcat.start();

		// End Point depends on the Tomcat port
		END_POINT = String.format("http://localhost:%d/FiwareMarketplace", tomcat.getConnector().getLocalPort());
	}
	
	@AfterClass
	public static void tearDown() {
		// Delete
		baseDir.delete();
	}
	
	@Before
	public void setUp() throws Exception {
		// Truncate all tables...
		EMBEDDED_DATABASE.run("DELETE FROM offerings;", "root", null, DATABASE);
		EMBEDDED_DATABASE.run("DELETE FROM descriptions;", "root", null, DATABASE);
		EMBEDDED_DATABASE.run("DELETE FROM stores;", "root", null, DATABASE);
		EMBEDDED_DATABASE.run("DELETE FROM users;", "root", null, DATABASE);
	}
	
	private Response createUser(String displayName, String email, String password) {
		
		SerializableUser user = new SerializableUser();
		user.setDisplayName(displayName);
		user.setEmail(email);
		user.setPassword(password);
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(END_POINT + "/api/v2/user").request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(user, MediaType.APPLICATION_JSON));
		
		return response;

	}
	
	private void checkAPIError(Response response, int status, String field, String message, ErrorType errorType) {
		
		assertThat(response.getStatus()).isEqualTo(status);
				
		APIError error = response.readEntity(APIError.class);
		assertThat(error.getField()).isEqualTo(field);
		assertThat(error.getErrorMessage()).isEqualTo(message);
		assertThat(error.getErrorType()).isEqualTo(errorType);

	}
		
	@Test
	public void testUserCreation() {
		
		Response response = createUser("FIWARE Example", "example@example.com", "password!1a");
		
		assertThat(response.getStatus()).isEqualTo(201);
		assertThat(response.getHeaderString("Location")).isEqualTo(END_POINT + "/api/v2/user/fiware-example");
	}
	
	@Test
	public void testUserCreationUserExists() throws InterruptedException {
		
		String mail = "example@example.com";
		
		createUser("FIWARE Example", mail, "password!1a");
		Response userExistResponse = createUser("Other user name", mail, "anotherPassword!1");
				
		checkAPIError(userExistResponse, 400, "email", "This email is already registered.", ErrorType.VALIDATION_ERROR);		
	}
	
	@Test
	public void testUserCreationInvalidDisplayName() {
		Response response = createUser("FIWARE Example 1", "example@example.com", "password!1");
		checkAPIError(response, 400, "displayName", "This field only accepts letters and white spaces.", ErrorType.VALIDATION_ERROR);	

	}
	
	@Test
	public void testUserCreationDisplayNameMissing() {
		Response response = createUser("", "example@example.com", "password!1");
		// userName is got based on displayName. userName is checked previously
		checkAPIError(response, 400, "userName", "This field is required.", ErrorType.VALIDATION_ERROR);	
	}
	
	@Test
	public void testUserCrationDisplayNameTooLong() {
		Response response = createUser("ABCDEFGHIJKMLNOPQRSTUVWXYZABCDEFGHIJKMLNOPQRSTUVWXYZ", "example@example.com", "password!1");
		checkAPIError(response, 400, "displayName", "This field must not exceed 30 chars.", ErrorType.VALIDATION_ERROR);	
	}
	
	public void testUserCrationDisplayNameTooShort() {
		Response response = createUser("a", "example@example.com", "password!1");
		checkAPIError(response, 400, "displayName", "This field must be at least 3 chars.", ErrorType.VALIDATION_ERROR);	
	}
	
	@Test
	public void testUserCreationInvalidMail() {
		Response response = createUser("FIWARE Example", "example@examplecom", "password!1");
		checkAPIError(response, 400, "email", "This field must be a valid email.", ErrorType.VALIDATION_ERROR);	
	}
	
	@Test
	public void testUserCreationDisplayEmailMissing() {
		Response response = createUser("FIWARE Example", "", "password!1");
		checkAPIError(response, 400, "email", "This field is required.", ErrorType.VALIDATION_ERROR);	
	}
	
	@Test
	public void testUserCreationInvalidPassword() {
		Response response = createUser("FIWARE Example", "example@example.com", "password!");
		checkAPIError(response, 400, "password", "Password must contain one number, one letter and one "
				+ "unique character such as !#$%&?", ErrorType.VALIDATION_ERROR);	
	}
	
	@Test
	public void testUserCreationDisplayPasswordMissing() {
		Response response = createUser("FIWARE Example", "example@example.com", "");
		checkAPIError(response, 400, "password", "This field is required.", ErrorType.VALIDATION_ERROR);	
	}
	
	@Test
	public void testUserCrationPasswordTooLong() {
		Response response = createUser("FIWARE EXAMPLE", "example@example.com", "ABCDEFGHIJKMLNOPQRSTUVWXYZABCDEFGHIJKMLNOPQRSTU!1VWXYZ");
		checkAPIError(response, 400, "password", "This field must not exceed 30 chars.", ErrorType.VALIDATION_ERROR);	
	}
	
	public void testUserCrationPasswordTooShort() {
		Response response = createUser("FIWARE EXAMPLE", "example@example.com", "passw1!");
		checkAPIError(response, 400, "password", "This field must be at least 8 chars.", ErrorType.VALIDATION_ERROR);	
	}
	
	public static class SerializableUser {
		
		private String displayName;
		private String email;
		private String password;
		
		@XmlElement
		public String getDisplayName() {
			return displayName;
		}
		
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
		
		@XmlElement
		public String getEmail() {
			return email;
		}
		
		public void setEmail(String email) {
			this.email = email;
		}
		
		@XmlElement
		public String getPassword() {
			return password;
		}
		
		public void setPassword(String password) {
			this.password = password;
		}
		
	}

}
