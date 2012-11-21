package org.fiware.apps.marketplace.model;

public class ServiceOrdinalAttributeType extends ServiceAttributeType {
	
	public static final String baseTypeUri = "http://purl.org/goodrelations/v1#QualitativeValue";
	
	private String leftSiblingUri;
	private String rightSiblingUri;

	public String getLeftSiblingUri() {
		return leftSiblingUri;
	}

	public String getRightSiblingUri() {
		return rightSiblingUri;
	}

	public void setLeftSiblingUri(String leftSiblingUri) {
		this.leftSiblingUri = leftSiblingUri;
	}

	public void setRightSiblingUri(String rightSiblingUri) {
		this.rightSiblingUri = rightSiblingUri;
	}

	@Override
	public String toString() {
		return "(o) " + super.toString() + "\n\tleft Sibling " + leftSiblingUri + "\n\trightSibling " + rightSiblingUri;
	}
}