package org.fiware.apps.marketplace.rdf;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.larq.ARQLuceneException;
import com.hp.hpl.jena.query.larq.IndexBuilderNode;
import com.hp.hpl.jena.query.larq.LARQ;

public class IndexBuilderNodeExtended extends IndexBuilderNode {

	public IndexBuilderNodeExtended(){
		super();
	}
	
	
	public IndexBuilderNodeExtended(String path){
		super(path);
	}
	
	public void index(Node node, String indexStr, String docId)
	{
		try {
			Document doc = new Document() ;
			LARQ.store(doc, node) ;
			LARQ.index(doc, indexStr) ;

			Field field = new Field("docId", docId, Field.Store.YES, Field.Index.TOKENIZED);	    
			doc.add(field);

			getIndexWriter().addDocument(doc) ;
		} catch (IOException ex)
		{ throw new ARQLuceneException("index", ex) ; }
	}


}
