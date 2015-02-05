package org.fiware.apps.marketplace.bo.impl;

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
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.fiware.apps.marketplace.bo.SearchBo;
import org.fiware.apps.marketplace.bo.OfferingsDescriptionBo;
import org.fiware.apps.marketplace.model.SearchResult;
import org.fiware.apps.marketplace.model.SearchResultEntryMatch;
import org.fiware.apps.marketplace.model.OfferingsDescription;
import org.fiware.apps.marketplace.rdf.IndexBuilderStringExtended;
import org.fiware.apps.marketplace.utils.ApplicationContextProvider;
import org.fiware.apps.marketplace.utils.PropertiesUtil;
import org.springframework.context.ApplicationContext;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.larq.HitLARQ;
import com.hp.hpl.jena.query.larq.IndexLARQ;
import com.hp.hpl.jena.query.larq.LARQ;

@org.springframework.stereotype.Service("searchBo")
public class SearchBoImpl implements SearchBo {

	ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();	
	OfferingsDescriptionBo offeringsDescriptionBo = 
			(OfferingsDescriptionBo) appContext.getBean("offeringsDescriptionBo");

	@Override
	public SearchResult searchByKeyword(String searchstring) {

		SearchResult result = new SearchResult();
		
		String lucenePath = (PropertiesUtil.getProperty("lucene.IndexPath"));
		IndexBuilderStringExtended larqBuilder = new IndexBuilderStringExtended(lucenePath) ;	
		larqBuilder.closeWriter() ;
		IndexLARQ index = larqBuilder.getIndex() ;		
		LARQ.setDefaultIndex(index);

		Iterator<HitLARQ> hitLARQIter = index.search(searchstring);		
		IndexReader reader = index.getLuceneReader();
		
		
		for ( ; hitLARQIter.hasNext() ; )
		{	  			  

			HitLARQ hitlarq = hitLARQIter.next() ;	
			Node node = hitlarq.getNode();			

			
			try {
				Document doc = reader.document(hitlarq.getLuceneDocId());
				Field val = doc.getField("docId");
				OfferingsDescription s = offeringsDescriptionBo.findById(Integer.parseInt(val.stringValue()));

				SearchResultEntryMatch match = new SearchResultEntryMatch(node.toString(), hitlarq.getScore());

				if ( node.isLiteral() ){
					match.setLiteral(node.getLiteral().getLexicalForm());					
				}
				
				if(s!=null){
					result.addSearchResult(s, match);	
				}


			} catch (CorruptIndexException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		
		}
		
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;


	}


}
