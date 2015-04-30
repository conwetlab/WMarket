package org.fiware.apps.marketplace.controllers.web.forms;

import javax.ws.rs.FormParam;

import org.apache.commons.codec.binary.Base64;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class StoreForm {

    private String displayName;
    private String url;
    private String comment;
    private String imageName;
    private String imageData;

    public StoreForm() {}

    public String getDisplayName() {
        return displayName;
    }

    public String getUrl() {
        return url;
    }

    public String getComment() {
        return comment;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageData() {
        return imageData;
    }

    @FormParam("displayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @FormParam("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @FormParam("comment")
    public void setComment(String comment) {
        this.comment = comment;
    }

    @FormParam("imageName")
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @FormParam("imageData")
    @PartType("application/octet-stream")
    public void setImageData(byte[] imageData) {
        if (imageData != null && imageData.length > 0) {
            this.imageData = Base64.encodeBase64String(imageData);
        }
    }

}
