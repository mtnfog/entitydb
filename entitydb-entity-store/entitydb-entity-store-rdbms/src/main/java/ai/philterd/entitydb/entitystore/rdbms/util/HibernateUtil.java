/*
 * Copyright 2024 Philterd, LLC
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ai.philterd.entitydb.entitystore.rdbms.util;

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
 * @author Philterd, LLC
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
	    	configuration.addResource("RdbmsStoredEntityMetadata.hbm.xml");
	
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