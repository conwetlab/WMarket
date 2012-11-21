package org.fiware.apps.marketplace.rdf;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.StaleReaderException;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.LockObtainFailedException;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.util.PropertiesUtil;

import com.hp.hpl.jena.query.larq.IndexLARQ;
import com.hp.hpl.jena.query.larq.LARQ;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class RdfIndexer {
	public static void indexService(Service service){

		String lucenePath = (PropertiesUtil.getProperty("lucene.IndexPath"));

		Model model = RdfHelper.loadModel(service.getUrl());

		IndexBuilderStringExtended larqBuilder = new IndexBuilderStringExtended(lucenePath) ;	

		StmtIterator indexModel = model.listStatements();
		for ( ; indexModel.hasNext() ; ){	  			  
			Statement a = indexModel.next() ;	
			larqBuilder.indexStatement(a, service.getId().toString());		
		}

		larqBuilder.closeWriter();

	}


	public static void deleteService(Service service){


		String lucenePath = (PropertiesUtil.getProperty("lucene.IndexPath"));
		Analyzer analyzer = new StandardAnalyzer();
		IndexWriter indexWriter = null;
		try {

			indexWriter = new IndexWriter(lucenePath, analyzer);
			indexWriter.deleteDocuments(new Term ("docId", service.getId().toString()));


		} catch (StaleReaderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			indexWriter.optimize();
			indexWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

}
