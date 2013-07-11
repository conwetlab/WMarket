package org.fiware.apps.marketplace.tests;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.fiware.apps.marketplace.rdf.IndexBuilderStringExtended;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Node_Blank;
import com.hp.hpl.jena.graph.Node_Literal;
import com.hp.hpl.jena.graph.Node_URI;
import com.hp.hpl.jena.query.larq.IndexLARQ;
import com.hp.hpl.jena.query.larq.LARQ;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class OpenJenaTest {
	// some definitions
	static String serviceURI    = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/WarrantyManagementSolution_Master.rdf";
	static String serviceURIN3    = "http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/WarrantyManagementSolution_Master.n3";

	public static void main(String[] args) { 




		String result ="Result:";
		Model model = ModelFactory.createDefaultModel();


		try {
			URL url = new URL(serviceURI);
			InputStream in = url.openStream();

			if (in == null) {
				throw new IllegalArgumentException("Input not found");
			}

			// read the RDF/XML file
			model.read(in, null);
			// write it to standard out
			//model.write(System.out);
			//result = result+ model.toString();


		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IndexBuilderStringExtended larqBuilder = new IndexBuilderStringExtended("C:\\temp") ;		

		//IndexBuilderStringExtended larqBuilder = new IndexBuilderStringExtended() ;		


		// -- Create an index based on existing statements

		StmtIterator indexModel = model.listStatements();
		for ( ; indexModel.hasNext() ; ){	  			  

			Statement a = indexModel.next() ;	


			larqBuilder.indexStatement(a, "3131");		



			//	System.out.println(larqBuilder.getIndex().getLuceneReader().maxDoc());


			Node node = a.getObject().asNode() ;


			Node_Literal nodeLiteral;
			Node_URI nodeDoc;
			Node_Blank nodeBlank;
			if ( node.isLiteral() ){
				nodeLiteral=(Node_Literal)node ;
				System.out.println("literal: "+a.toString());
			}
			else if ( node.isURI() ){
				nodeDoc=(Node_URI)node ;
				System.out.println("uri: "+a.toString());
			}
			else if ( node.isBlank() ){
				nodeBlank=(Node_Blank)node ;
				System.out.println("blank: "+a.toString());
			}




		}



		//  larqBuilder.indexStatements(model.listStatements());			  
		// -- Finish indexing
		larqBuilder.closeWriter() ;
		// -- Create the access index  
		IndexLARQ index = larqBuilder.getIndex() ;		
		LARQ.setDefaultIndex(index);


		try {
			BooleanQuery a1AndT1 = new BooleanQuery();
			a1AndT1.add(new TermQuery(new Term("docId", "3131")), BooleanClause.Occur.MUST);
			a1AndT1.add(new TermQuery(new Term("index", "getting")), BooleanClause.Occur.MUST);
			BooleanQuery query = new BooleanQuery();
			query.add(a1AndT1, BooleanClause.Occur.SHOULD);

			IndexReader indexReader = index.getLuceneReader();
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			Hits hits;

			hits = indexSearcher.search(query);

			System.out.println("Number of hits: " + hits.length());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*


			String searchString="3131";
			Analyzer analyzer = new StandardAnalyzer();
			IndexReader indexReader = index.getLuceneReader();
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
			QueryParser queryParser = new QueryParser("docId", analyzer);
			Query query;

			query = queryParser.parse(searchString);

			Hits hits = indexSearcher.search(query);
			System.out.println("Number of hits: " + hits.length());

		 */




		/*

		// NodeIterator nIter = index.searchModelByIndex("marketplaces") ;
		Iterator<HitLARQ> hitLARQIter = index.search("leidig");


		for ( ; hitLARQIter.hasNext() ; )
		{	  			  

			HitLARQ hitlarq = hitLARQIter.next() ;	
			Node node = hitlarq.getNode();

			RDFNode d = ModelUtils.convertGraphNodeToRDFNode(hitlarq.getNode(), model);
			//	System.out.println("xxxxxxxx" + node.hashCode());

			result  = hitlarq.getLuceneDocId() + "  " + hitlarq.getScore() + " " + result + node.toString() + " : " +  node.getLiteral().toString()+"</br>";
			IndexReader reader = index.getLuceneReader();
			try {
				Document doc = reader.document(hitlarq.getLuceneDocId());
				Field val = doc.getField("lex");
				System.out.println("value: " + val.stringValue());
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		 */
		/*  
			  for ( ; nIter.hasNext() ; )
			  {	  

					  index.search

				  RDFNode node = nIter.nextNode() ;	



				  Literal lit = (Literal)node;

			    result  = result + node.toString() + " : " + lit.toString()+"</br>";


			  }*/

		System.out.println(result);

		/*

			  String queryString = StringUtils.join("\n", new String[]{
					  "PREFIX foaf: <http://xmlns.com/foaf/0.1/>",
			            "SELECT * {" ,
			            "    ?lit pf:textMatch '+text'",
			            "}"
			        }) ;
			 Query query = QueryFactory.create(queryString) ;
			 QueryExecution qExec = QueryExecutionFactory.create(query, model) ;
			 ResultSetFormatter.out(System.out, qExec.execSelect(), query) ;

		 */

	}



}
