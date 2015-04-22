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
import java.nio.file.Paths;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;

import org.apache.catalina.startup.Tomcat;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class UsersServiceIT {
	
	private static TemporaryFolder baseDir = new TemporaryFolder();	
	private static Tomcat tomcat = new Tomcat();
	private static String END_POINT;
	
	@BeforeClass
	public static void startUp() throws Exception {
		
		// Initialize baseDir and the webapps directory
		baseDir.create();
		File webApps = baseDir.newFolder("webapps");
		
		// Set up Tomcat
		tomcat.setPort(0);											// Automatic port
		tomcat.setBaseDir(baseDir.getRoot().getAbsolutePath());		// Base Dir
		tomcat.addContext("/", webApps.getAbsolutePath());			// Context
		
		// The name of the project
		String projectDirectory = Paths.get(".").toAbsolutePath().toString();
		tomcat.addWebapp("FiwareMarketplace", projectDirectory + "/target/FiwareMarketplace.war");
		
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
	
	@Test
	public void testUserCreation() throws InterruptedException {
		
		SerializableUser user = new SerializableUser();
		user.setDisplayName("FIWARE Example");
		user.setEmail("example_mail@fiware.com");
		user.setPassword("password1!a");
		
		System.out.println(END_POINT + "/api/v2/user");

		
		Client client = ClientBuilder.newClient();
		Response response = client.target(END_POINT + "/api/v2/user").request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(user, MediaType.APPLICATION_JSON));
						
		assertThat(response.getStatus()).isEqualTo(201);
		assertThat(response.getHeaderString("Location")).isEqualTo(END_POINT + "/api/v2/user/fiware-example");
		
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
