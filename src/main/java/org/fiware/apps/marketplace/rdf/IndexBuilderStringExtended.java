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

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.larq.ARQLuceneException;
import com.hp.hpl.jena.query.larq.IndexBuilderString;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * Helper to index a document. The document should be received 
 * through the constructor
 * @author aitor
 *
 */
public class IndexBuilderStringExtended extends IndexBuilderString {

	// private IndexBuilderNodeExtended index;
	private Set<Node> indexedNodes = new HashSet<Node>();
	private String docId;

	/**
	 * Helper to index a document. A generic path will be used to store
	 * the document
	 * @param docId The document to be indexed
	 */
	public IndexBuilderStringExtended(String docId) {
		this.index = new IndexBuilderNodeExtended();
		this.docId = docId;
	}

	/**
	 * Helper to index a document
	 * @param path The Lucene path where the Index will be stored
	 * @param docId The document to be indexed
	 */
	public IndexBuilderStringExtended(String path, String docId) {
		this.index = new IndexBuilderNodeExtended(path);
		this.docId = docId;
	}

	/**
	 * Function to index a statement that belongs to the service
	 * that is being indexed
	 * @param statement The statement to be indexed associated
	 * to the service
	 */
	public void indexStatement(Statement statement) {
		if (indexThisStatement(statement)) {
			try {
				if (statement.getObject().isLiteral()) {
					
					Node node = statement.getObject().asNode();
					
					if (!indexedNodes.contains(node)){
						if (indexThisLiteral(statement.getLiteral())){
							IndexBuilderNodeExtended indexExt = (IndexBuilderNodeExtended) index;
							indexExt.index(node, node.getLiteralLexicalForm(), this.docId);
						}

						indexedNodes.add(node);
					}
				}
			} catch (Exception e) { 
				throw new ARQLuceneException("indexStatement", e); 
			}
		}
	}
}
