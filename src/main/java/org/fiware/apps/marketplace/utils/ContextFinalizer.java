package org.fiware.apps.marketplace.utils;

import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

public class ContextFinalizer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// Nothing to do...
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			com.mysql.jdbc.AbandonedConnectionCleanupThread.shutdown();
		} catch (Throwable t) {
			
		}
		
		// This manually deregisters JDBC driver, which prevents Tomcat 7 from complaining about memory leaks
		Enumeration<java .sql.Driver> drivers = java.sql.DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			java.sql.Driver driver = drivers.nextElement();
			try {
				java.sql.DriverManager.deregisterDriver(driver);
			} catch (Throwable t) {}
		}
		
		// Destroy loggers
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		lc.stop();
		
		try { 
			Thread.sleep(2000L); 
		} catch (Exception e) {
			
		}		
	}

}
