package org.fiware.apps.marketplace.rdf;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.larq.ARQLuceneException;
import com.hp.hpl.jena.query.larq.IndexBuilderNode;
import com.hp.hpl.jena.query.larq.IndexBuilderString;
import com.hp.hpl.jena.rdf.model.Statement;

public class IndexBuilderStringExtended extends IndexBuilderString {


//	protected IndexBuilderNodeExtended index;
	private Set<Node> seen = new HashSet<Node>() ;

	public IndexBuilderStringExtended(){        	
		index = new IndexBuilderNodeExtended();
	}

	public IndexBuilderStringExtended(String path){        
		 index = new IndexBuilderNodeExtended(path);
	}

	public void indexStatement(Statement s, String docId)
	{
		if ( ! indexThisStatement(s) )
			return ;

		try {
			if ( s.getObject().isLiteral() )
			{
				Node node = s.getObject().asNode() ;
				if ( ! seen.contains(node) )
				{
					if ( indexThisLiteral(s.getLiteral())){
						IndexBuilderNodeExtended indexExt = (IndexBuilderNodeExtended) index;                    	
						indexExt.index(node, node.getLiteralLexicalForm(), docId) ;
						
					}
						
					seen.add(node) ;
				}
			}
		} catch (Exception e)
		{ throw new ARQLuceneException("indexStatement", e) ; }
	}



}
