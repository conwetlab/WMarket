package org.fiware.apps.marketplace.controllers;

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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.test.util.ReflectionTestUtils;

public class MediaContentControllerTest {
	
    // Controller
	private MediaContentController controller = new MediaContentController();
	
	// Temporary Folders
    @Rule public TemporaryFolder mediaDirectory = new TemporaryFolder();
    @Rule public TemporaryFolder unaccesibleDirectory = new TemporaryFolder();
	
    // Static content
	private static final String IMAGE_NAME = "image.png";
	private static final String IMAGE_PATH = "src/test/resources";
	
	// AUXILIAR FUNCTION
	private void copyImageIntoTemporaryFolder(TemporaryFolder folder) throws IOException {
		Files.copy(Paths.get(IMAGE_PATH, IMAGE_NAME), 
				Paths.get(folder.getRoot().getAbsolutePath(), IMAGE_NAME));
	}
	
	@Before
	public void setUp() throws IOException {

		// Put an image into the media folder
		copyImageIntoTemporaryFolder(mediaDirectory);
		
		// Set the folder where the media is stored
		ReflectionTestUtils.setField(controller, "mediaFolder", 
				mediaDirectory.getRoot().getAbsoluteFile().toString());
	}
	
	@Test
	public void testGetExistingImage() throws IOException {
		Response response = controller.getImage(IMAGE_NAME);
		
		// Read the image and check that the content returned by the 
		BufferedImage image = ImageIO.read(Paths.get(IMAGE_PATH, IMAGE_NAME).toFile());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", baos);		// Only PNG is accepted
		byte[] imageData = baos.toByteArray();
		
		// Assertions: Status == OK, Entity == Image data
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getEntity()).isEqualTo(imageData);

	}
	
	@Test
	public void testGetNonExistingImage() {
		Response response = controller.getImage("A" + IMAGE_NAME);
		assertThat(response.getStatus()).isEqualTo(404);	
	}
	
	@Test
	public void testDirectoryTraversalAttack() throws IOException {
		// Copy the image into the other directory
		copyImageIntoTemporaryFolder(unaccesibleDirectory);
		
		// Try to get the image
		Response response = controller.getImage("../" +unaccesibleDirectory.getRoot().getName() + "/" + IMAGE_NAME);
		
		// 404 should be returned (even if the image exists)
		assertThat(response.getStatus()).isEqualTo(404);
		
	}
}
