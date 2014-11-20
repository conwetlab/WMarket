package org.fiware.apps.marketplace.bo.impl;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.fiware.apps.marketplace.bo.SearchBo;
import org.fiware.apps.marketplace.bo.ServiceBo;
import org.fiware.apps.marketplace.model.SearchResult;
import org.fiware.apps.marketplace.model.SearchResultEntryMatch;
import org.fiware.apps.marketplace.model.Service;
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
	ServiceBo serviceBo = (ServiceBo)appContext.getBean("serviceBo");

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
				Service s = serviceBo.findById(Integer.parseInt(val.stringValue()));

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
