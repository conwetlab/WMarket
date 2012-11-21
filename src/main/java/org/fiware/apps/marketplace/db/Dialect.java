package org.fiware.apps.marketplace.db;

enum Dialect {
	MYSQL("org.hibernate.dialect.MySQLInnoDBDialect"),
	MYSQL5("org.hibernate.dialect.MySQL5InnoDBDialect"), 
	ORACLE("org.unhcr.omss.db.oracle.OracleDialectDeferredFK"), 
	SYBASE("org.hibernate.dialect.SybaseAnywhereDialect");

	private String className;

	private Dialect(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}
}
