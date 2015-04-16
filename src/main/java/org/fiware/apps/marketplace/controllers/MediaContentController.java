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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Path("media")
public class MediaContentController {

	@Value("${media.folder}") private String mediaFolder;

	@GET
	@Path("{fileName: .*}")
	@Produces({"image/png"})
	public Response getImage(@Context UriInfo uri, @PathParam("fileName") String fileName) {

		try {
			// Path need to be normalized to avoid Directory traversal attack
			String filePath = mediaFolder + "/" + fileName;
			String normalizedPath = new URI(filePath).normalize().getPath();
			File imageFile = new File(normalizedPath);
	
			// Check that the new path starts with the URL where media is supposed to be stored
			if (normalizedPath.startsWith(mediaFolder) && imageFile.exists()) {
				
				BufferedImage image = ImageIO.read(imageFile);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(image, "png", baos);
				byte[] imageData = baos.toByteArray();
	
				// Return the image
				return Response.ok().entity(imageData).build();
				
			} else {
				return Response.status(Status.NOT_FOUND).build();
			}
		
		} catch (Exception ex) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
	}

}
