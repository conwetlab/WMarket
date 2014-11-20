package org.fiware.apps.marketplace.bo.impl;

import java.util.ArrayList;
import java.util.List;

import org.fiware.apps.marketplace.bo.VocabularyBo;
import org.fiware.apps.marketplace.utils.PropertiesUtil;
import org.springframework.stereotype.Service;

@Service("vocabularyBo")
public class VocabularyBoImpl implements VocabularyBo {

	// TODO Replace with a suitable mechanism
	
	private List<String> vocabularyList;
	
	public VocabularyBoImpl(){
		vocabularyList = new ArrayList<String>();
		String[] vocabularyNames = PropertiesUtil.getProperty("vocabulary.NameList").split(",");
		for(String vocabularyName : vocabularyNames) {
			vocabularyList.add(PropertiesUtil.getProperty("vocabulary." + vocabularyName));
		}
	}
	
	@Override
	public List<String> getVocabularyUris() {
		return vocabularyList;
	}

}
