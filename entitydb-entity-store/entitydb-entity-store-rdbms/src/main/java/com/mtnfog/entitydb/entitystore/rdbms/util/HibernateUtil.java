/**
 * Copyright © 2016 Mountain Fog, Inc. (support@mtnfog.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For proprietary licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.entitystore.rdbms.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * Utility class for the database connection provided
 * by Hibernate.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class HibernateUtil {
	
	private static final Logger LOGGER = LogManager.getLogger(HibernateUtil.class);
	  
    private static SessionFactory sessionFactory;
  
    /**
     * Gets a session factory for the database connection.
     * @param jdbcUrl The JDBC connection string for the database.
	 * @param jdbcDriver The JDBC driver name.
	 * @param userName The database user name.
	 * @param password The database password.
	 * @param dialect The Hibernate dialect to use. Refer to the Hibernate documentation for the
	 * appropriate dialect for your database.
     * @return A configured Hibernate {@link SessionFactory}.
     */
    public static SessionFactory getSessionFactory(String jdbcUrl, String jdbcDriver, String userName, String password, String dialect, String schemaExport) {
    	
    	if(sessionFactory == null || !sessionFactory.isClosed()) {
    		
    		LOGGER.debug("jdbcUrl: {}", jdbcUrl);
    		LOGGER.debug("jdbcDriver: {}", jdbcDriver);
    		LOGGER.debug("userName: {}", userName);
    		LOGGER.debug("dialect: {}", dialect);
    		LOGGER.debug("schemaExport: {}", schemaExport);
    	
	    	Configuration configuration = new Configuration();
	
	    	configuration.setProperty("hibernate.connection.driver_class", jdbcDriver);
	    	configuration.setProperty("hibernate.connection.url", jdbcUrl);
	    	configuration.setProperty("hibernate.connection.username", userName);
	    	configuration.setProperty("hibernate.connection.password", password);
	    	configuration.setProperty("dialect", dialect);
	    	configuration.setProperty("hibernate.hbm2ddl.auto", schemaExport);
	    	configuration.setProperty("hibernate.current_session_context_class", "thread");
	    	
	    	configuration.setProperty("hibernate.show_sql", "false");
	    	configuration.setProperty("hibernate.format_sql", "true");
	    	configuration.setProperty("hibernate.use_sql_comments", "true");
	    		    	
	    	configuration.addResource("RdbmsStoredEntity.hbm.xml");
	    	configuration.addResource("RdbmsStoredEntityEnrichment.hbm.xml");
	
	    	ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();    	
	    	sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	        
    	}
    	
    	return sessionFactory;

    }

    /**
     * Closes the session factory if it is open.
     */
    public static void close() {
    	
    	if(sessionFactory != null) {
    		sessionFactory.close();
    	}
    	
    }
  
}