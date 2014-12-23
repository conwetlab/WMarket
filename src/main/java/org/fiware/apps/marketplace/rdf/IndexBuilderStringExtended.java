package org.fiware.apps.marketplace.rdf;

/*
 * #%L
 * FiwareMarketplace
 * %%
 * Copyright (C) 2012 SAP
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
