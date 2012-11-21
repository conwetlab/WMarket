package org.fiware.apps.marketplace.db;


import org.fiware.apps.marketplace.model.Localuser;
import org.fiware.apps.marketplace.model.Rating;
import org.fiware.apps.marketplace.model.RatingCategory;
import org.fiware.apps.marketplace.model.RatingCategoryEntry;
import org.fiware.apps.marketplace.model.RatingObject;
import org.fiware.apps.marketplace.model.RatingSystem;
import org.fiware.apps.marketplace.model.Service;
import org.fiware.apps.marketplace.model.Store;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class HibernateDDLGenerator {
	/**
	 * @param args
	 */
	public static void main(String[] args) { 
		new HibernateDDLGenerator().execute(Dialect.MYSQL5,  Store.class, Service.class, Localuser.class, Rating.class, RatingCategory.class, RatingCategoryEntry.class, RatingObject.class, RatingSystem.class);
	}
	private void execute(Dialect dialect, Class<?>... classes) {
		AnnotationConfiguration configuration = new AnnotationConfiguration();
		configuration.setProperty(Environment.DIALECT, dialect.getClassName());
		for (Class<?> entityClass : classes) {
			configuration.addAnnotatedClass(entityClass);
		}
		
		SchemaExport schemaExport = new SchemaExport(configuration);	
		schemaExport.setDelimiter(";");
		schemaExport.setOutputFile(String.format("database/%s_%s.%s ", new Object[] {"ddl", dialect.name().toLowerCase(), "sql" }));
		schemaExport.create(true, false);
		


	}
}
