package org.fiware.apps.marketplace.rdf;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;

public class RdfHelper {

	public static Model loadModel(String serviceURI) {
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
			// model.write(System.out);
			// result = result+ model.toString()
			in.close();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
	}

	/**
	 * Returns the model of the RDF-file located at the given URL or null if
	 * reading failed.
	 * 
	 * @param uri
	 * @return
	 */
	public static Model getModelFromUri(String uri) {
		InputStream in = null;
		try {	
			int posOfHash = uri.lastIndexOf("#");
			String shortUri = uri;
			if(posOfHash > 0)
				shortUri = shortUri.substring(0, posOfHash);
			
			Model model = ModelFactory.createDefaultModel();

			// Turtle models don't seem to work...
			FileManager.get().readModel(model, uri);

			return model;
		} catch (Exception ex) {
			System.out.println("RdfHelper - Model could not be read: " + uri);
			System.out.println(ex.getMessage());
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	// TODO resolve prefixes reasonably...
	/**
	 * Preliminary List of prefixes for queries
	 */
	public static final String queryPrefixes = "PREFIX gr: <http://purl.org/goodrelations/v1#> "
			+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX usdl: <http://www.linked-usdl.org/ns/usdl-core#>  "
			+ "PREFIX legal: <http://www.linked-usdl.org/ns/usdl-legal#>  "
			+ "PREFIX price: <http://www.linked-usdl.org/ns/usdl-pricing#>  "
			+ "PREFIX sla: <http://www.linked-usdl.org/ns/usdl-sla#> "
			+ "PREFIX gn: <http://www.geonames.org/ontology#> "
			+ "PREFIX dcterms: <http://purl.org/dc/terms/> "
			+ "PREFIX genVoc: <http://qkad00202897a.dhcp.qkal.sap.corp:7777/data/rdf/vocabulary/Vocabulary_General_001#> "
			+ "PREFIX cloud: <http://qkad00202897a.dhcp.qkal.sap.corp:7777/data/rdf/vocabulary/Vocabulary_Cloud_004#> "
			+ "PREFIX os: <http://qkad00202897a.dhcp.qkal.sap.corp:7777/data/rdf/vocabulary/Vocabulary_OperatingSystem_003#> "
			+ "PREFIX support: <http://qkad00202897a.dhcp.qkal.sap.corp:7777/data/rdf/vocabulary/Vocabulary_Support_003#> ";

	/**
	 * Returns a list of URIs which are found via the given query.
	 * 
	 * @param query
	 * @param queriedVar
	 * @param model
	 * @return List of found URIs or empty list
	 */
	public static List<String> queryUris(String query, String queriedVar,
			Model model) {
		List<String> uris = new ArrayList<String>();
		for (QuerySolution solution : RdfHelper.query(query, model)) {
			Resource res = solution.getResource(queriedVar);
			if (res != null)
				uris.add(res.getURI());
		}
		return uris;
	}

	/**
	 * Returns the first found URI found via the given query.
	 * 
	 * @param query
	 * @param queriedVar
	 * @param model
	 * @return Found URI or null
	 */
	public static String queryUri(String query, String queriedVar, Model model) {
		List<QuerySolution> solutions = RdfHelper.query(query, model);
		if (solutions.size() > 0) {
			Resource resource = solutions.get(0).getResource(queriedVar);
			if (resource != null)
				return resource.getURI();
		}
		return null;
	}

	/**
	 * Executes the given query and returns the results as list or an empty list
	 * when failed.
	 * 
	 * @param queryString
	 * @param model
	 * @return
	 */
	public static List<QuerySolution> query(String queryString, Model model) {
		Query query = null;
		QueryExecution queryExec = null;
		List<QuerySolution> solutions = new ArrayList<QuerySolution>();
		try {
			query = QueryFactory.create(queryString);
			queryExec = QueryExecutionFactory.create(query, model);
			ResultSet results = queryExec.execSelect();
			for (; results.hasNext();) {
				solutions.add(results.nextSolution());
			}
		} catch (Exception ex) {
			System.out.println("RdfHelper - SPARQL query failed: \n"
					+ queryString.replace(queryPrefixes, ""));
			System.out.println(ex.getMessage());
		} finally {
			if (queryExec != null)
				queryExec.close();
		}
		return solutions;
	}

	/**
	 * Returns the URI of the object of the corresponding triple or null if such
	 * a triple does not exist.
	 * 
	 * @param subject
	 * @param predicate
	 * @param model
	 * @return URI or null
	 */
	public static String getObjectUri(String subject, String predicate,
			Model model) {
		String query = queryPrefixes + "SELECT ?x WHERE { <" + subject + "> "
				+ predicate + " ?x . } ";
		return queryUri(query, "x", model);
	}

	public static List<String> getObjectUris(String subject, String predicate,
			Model model) {
		String query = queryPrefixes + "SELECT ?x WHERE { <" + subject + "> "
				+ predicate + " ?x . } ";
		return queryUris(query, "x", model);
	}
	
	public static String getLiteral(String subject, String predicate, Model model) {
		String query = queryPrefixes + "SELECT ?x WHERE { <" + subject + "> "
				+ predicate + " ?x . } ";
		return queryLiteral(query, "x", model);
	}
	
	public static String queryLiteral(String query, String queriedVar, Model model) {
		List<QuerySolution> solutions = RdfHelper.query(query, model);
		if (solutions.size() > 0) {
			Literal literal = solutions.get(0).getLiteral(queriedVar);
			if (literal != null)
				return literal.getLexicalForm();
		}
		return null;
	}
	

}
