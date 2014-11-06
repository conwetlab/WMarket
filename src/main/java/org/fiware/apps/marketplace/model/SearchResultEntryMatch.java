package org.fiware.apps.marketplace.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "match")
public class SearchResultEntryMatch {
	
	private String text;
	private float luceneScore;
	private String literal;
	
	
	public SearchResultEntryMatch(){
	}
	
	public SearchResultEntryMatch(String text, float luceneScore) {
		this.text = text;
		this.luceneScore = luceneScore;
	}
	
	@XmlElement
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}	

	@XmlElement
	public float getLuceneScore() {
		return luceneScore;
	}
	public void setLuceneScore(float luceneScore) {
		this.luceneScore = luceneScore;
	}
	
	@XmlElement
	public String getLiteral() {
		return literal;
	}

	public void setLiteral(String literal) {
		this.literal = literal;
	}
}
