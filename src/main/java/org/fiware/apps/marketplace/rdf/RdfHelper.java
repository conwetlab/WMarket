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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.util.FmtUtils;

public class RdfHelper {

	private Model model;
	
	private static final Logger logger = LoggerFactory.getLogger(RdfHelper.class);

	/**
	 * Preliminary List of prefixes for queries
	 * TODO: Resolve prefixes reasonably depending on the preferences...
	 */
	private static final String QUERY_PREFIXES = "PREFIX gr: <http://purl.org/goodrelations/v1#> "
			+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX usdl: <http://www.linked-usdl.org/ns/usdl-core#>  "
			+ "PREFIX legal: <http://www.linked-usdl.org/ns/usdl-legal#>  "
			+ "PREFIX price: <http://www.linked-usdl.org/ns/usdl-pricing#>  "
			+ "PREFIX sla: <http://www.linked-usdl.org/ns/usdl-sla#> "
			+ "PREFIX gn: <http://www.geonames.org/ontology#> "
			+ "PREFIX dcterms: <http://purl.org/dc/terms/> "
			+ "PREFIX pav: <http://purl.org/pav/> "
			+ "PREFIX genVoc: <http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/vocabulary/Vocabulary_General_001#> "
			+ "PREFIX cloud: <http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/vocabulary/Vocabulary_Cloud_004#> "
			+ "PREFIX os: <http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/vocabulary/Vocabulary_OperatingSystem_003#> "
			+ "PREFIX support: <http://appsnserv.testbed.fi-ware.eu/cloudservices/rdf/vocabulary/Vocabulary_Support_003#> ";
	
	/**
	 * Constructor for RdfHelper
	 * @param descriptionURL The URL where the RDF is hosted
	 * @throws IOException When the RDF cannot be read
	 */
	public RdfHelper(String descriptionURL) throws IOException {
		this.model = ModelFactory.createDefaultModel();
		URL url = new URL(descriptionURL);
		model.read(url.openStream(), null);
	}
	
	/**
	 * Returns the list of prefixes needed to parse service descriptions 
	 * @return The list of prefixes to perform queries against service descriptions
	 */
	public static String getQueryPrefixes() {
		return QUERY_PREFIXES;
	}
	
	/**
	 * Returns the model that will be used to perform the queries
	 * @return The model that will be used to perform the queries and that is based on the given description
	 */
	public Model getModel() {
		return this.model;
	}

	/**
	 * Executes the given query and returns the results as list or an empty list
	 * when failed.
	 * @param queryString The query to be performed
	 * @return The list of solutions
	 */
	public List<QuerySolution> query(String queryString) {
		
		Query query = QueryFactory.create(QUERY_PREFIXES + queryString);
		List<QuerySolution> solutions = new ArrayList<QuerySolution>();
		QueryExecution queryExec = null;

		try {
			
			queryExec = QueryExecutionFactory.create(query, model);
			ResultSet results = queryExec.execSelect();

			while(results.hasNext()) {
				solutions.add(results.nextSolution());
			}
			
		} catch (Exception ex) {
			logger.warn("SPARQL query failed {}", queryString, ex);
			
		} finally {
			if (queryExec != null) {
				queryExec.close();
			}
		}
		
		return solutions;
	}
	
	/**
	 * Returns the literals that are found via the given query
	 * @param query The query to be performed
	 * @param queriedVar The variable where the literal to be returned is store
	 * @return Literals found via the given query
	 */
	public List<String> queryLiterals(String query, String queriedVar) {
		List<String> literals = new ArrayList<>();
		List<QuerySolution> solutions = this.query(query);
		
		for (QuerySolution solution: solutions) {
			literals.add(solution.getLiteral(queriedVar).getLexicalForm());
		}
		
		return literals;
	}

	/**
	 * Returns the literal that is found via the given query
	 * @param query The query to be performed
	 * @param queriedVar The variable where the literal to be returned is stored
	 * @return The literal or null
	 */
	public String queryLiteral(String query, String queriedVar) {
		
		List<String> literals = queryLiterals(query, queriedVar);
		
		if (literals.isEmpty()) {
			return null;
		} else {
			return literals.get(0);
		}
		
	}

	/**
	 * Returns a list of URIs which are found via the given query.
	 * @param query The query to be performed
	 * @param queriedVar The variable where the URLs to be returned is stored
	 * @return List of found URIs or empty list
	 */
	public List<String> queryUris(String query, String queriedVar) {

		List<String> uris = new ArrayList<String>();
		
		for (QuerySolution solution : this.query(query)) {
			Resource res = solution.getResource(queriedVar);
			if (res != null) {
				uris.add(FmtUtils.stringForNode(res.asNode()));
			}
		}

		return uris;
	}

	/**
	 * Returns the first found URI found via the given query.
	 * @param query The query to be performed
	 * @param queriedVar The variable where the URL to be returned is stored
	 * @return Found URI or null
	 */
	public String queryUri(String query, String queriedVar) {
		
		List<String> uris = queryUris(query, queriedVar);
		String uri = null;
		
		if (uris.size() > 0) {
			uri = uris.get(0);
		}
		
		return uri;
	}

	/**
	 * Returns the URI of the object of the corresponding triple or null if such
	 * a triple does not exist.
	 * @param subject The subject to be queried
	 * @param predicate The predicate whose URIs want to be obtained
	 * @return URI or null
	 */
	public String getObjectUri(String subject, String predicate) {
		
		String queriedVar = "x";
		String query = "SELECT ?" + queriedVar + " WHERE { " + subject + " " + predicate + " ?" + queriedVar + " . } ";
		
		logger.info("Executing JENA query {}", query);
		return queryUri(query, queriedVar);
	}

	/**
	 * Returns the URIs of the object of the corresponding triple or null if such
	 * a triple does not exist.
	 * @param subject The subject to be queried
	 * @param predicate The predicate whose URI want to be obtained
	 * @return URI or null
	 */
	public List<String> getObjectUris(String subject, String predicate) {
		
		String queriedVar = "x";
		String query = "SELECT ?" + queriedVar + " WHERE { " + subject + " " + predicate + " ?" + queriedVar + " . } ";
		
		logger.info("Executing JENA query {}", query);
		return queryUris(query, queriedVar);
	}

	/**
	 * Returns the literal of the object of the corresponding triple of null if the
	 * literal cannot be found
	 * @param subject The subject to be queried
	 * @param predicate The predicate whose literal want to be obtained
	 * @return The literal or null
	 */
	public String getLiteral(String subject, String predicate) {
		
		String queriedVar = "x";
		String query = "SELECT ?" + queriedVar + " WHERE { " + subject + " " + predicate + " ?" + queriedVar + " . } ";
		
		logger.info("Executing JENA query {}", query);
		return queryLiteral(query, queriedVar);
	}
	
	/**
	 * Returns the literals of the blank nodes attached to the corresponding triples
	 * @param subject Triple subject
	 * @param predicate Triple predicate
	 * @return The literals of the blank nodes attached to the corresponding triples
	 */
	public List<String> getBlankNodesLabels(String subject, String predicate) {
		
		String queriedVar = "y";
		String query = "SELECT ?" + queriedVar + " WHERE { " + subject + " " + predicate + " ?x . ?x rdfs:label ?" + 
				queriedVar + " . } ";
		
		logger.info("Executing JENA query {}", query);
		return queryLiterals(query, queriedVar);
	}
	
	private Map<String, List<Object>> parseNode(Resource resource) {
		
		Map<String, List<Object>> nodeProperties = new HashMap<>();
		StmtIterator it = resource.listProperties();
		
		while(it.hasNext()) {
			
			Statement st = it.next();
			String predicateName = st.getPredicate().getLocalName();
			List<Object> predicateValues = nodeProperties.get(predicateName);
			
			if (predicateValues == null) {
				predicateValues = new ArrayList<Object>();
				nodeProperties.put(predicateName, predicateValues);
			}
			
			if (st.getObject().isLiteral()) {
				predicateValues.add(st.getObject().asLiteral().getString());
			} else if (st.getObject().isResource()) {
				predicateValues.add(parseNode(st.getObject().asResource()));
			}
		}
		
		return nodeProperties;
	}
	
	/**
	 * Given a specific blank node of a subject, this method will parse it to generate a map
	 * that will contain all its predicates
	 * @param subject The subject that contains the blank node
	 * @param predicate The predicate to obtain the blank node
	 * @return A map with all the predicates contained in the blank node defined by the query
	 * that will be performed based on the given subject and predicate
	 */
	public List<Map<String, List<Object>>> getBlankNodesProperties(String subject, String predicate) {
				
		String queriedVar = "x";
		String query = "SELECT ?" + queriedVar +  " WHERE { " + subject + " " + predicate + " ?" + queriedVar + " . } ";
		List<QuerySolution> solutions = this.query(query);
		List<Map<String, List<Object>>> results = new ArrayList<>();

		for (QuerySolution solution: solutions) {	
			Resource resource = solution.get(queriedVar).asResource();
			results.add(parseNode(resource));
		}
		
		return results;
	}
}
