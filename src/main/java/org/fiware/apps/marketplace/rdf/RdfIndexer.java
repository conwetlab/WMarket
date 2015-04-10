package org.fiware.apps.marketplace.rdf;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
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

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.StaleReaderException;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.LockObtainFailedException;
import org.fiware.apps.marketplace.model.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

@Service("rdfIndexer")
public class RdfIndexer {
	
	@Value("${lucene.IndexPath}") private String lucenePath;
	@Autowired private RdfHelper rdfHelper;
	
	private static final Logger logger = LoggerFactory.getLogger(RdfIndexer.class);	
	
	public void indexOrUpdateService(Description description) throws MalformedURLException {

		Model model = rdfHelper.loadModel(description.getUrl());
		String serviceId = description.getId().toString();
		IndexBuilderStringExtended larqBuilder = new IndexBuilderStringExtended(lucenePath, serviceId);	
		
		// Delete previous indexes for this description (if any)
		// If a description is being updated, a JenaException should have been thrown previously and
		// this method won't be called, so the previous index is not deleted.
		deleteService(description);

		StmtIterator indexModel = model.listStatements();
		while(indexModel.hasNext()) {	  			  
			Statement statement = indexModel.next();	
			larqBuilder.indexStatement(statement);
		}

		larqBuilder.closeWriter();

	}

	public void deleteService(Description description) {

		Analyzer analyzer = new StandardAnalyzer();
		IndexWriter indexWriter = null;
		
		try {
			indexWriter = new IndexWriter(lucenePath, analyzer);
			indexWriter.deleteDocuments(new Term ("docId", description.getId().toString()));
		} catch (StaleReaderException e) {
			logger.error("Deleting Service from Index - StaleReaderException", e);
		} catch (CorruptIndexException e) {
			logger.error("Deleting Service from Index - CorruptIndexException", e);
		} catch (LockObtainFailedException e) {
			logger.error("Deleting Service from Index - LockObtainFailedException", e);
		} catch (IOException e) {
			logger.error("Deleting Service from Index - IOException", e);
		}

		try {
			indexWriter.optimize();
			indexWriter.close();
		} catch (IOException e) {
			logger.error("Deleting Service form Index (Optimizing) - IOException", e);
		}

	}

}
