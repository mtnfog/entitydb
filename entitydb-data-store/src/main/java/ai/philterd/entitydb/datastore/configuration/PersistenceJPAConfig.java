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
package ai.philterd.entitydb.datastore.configuration;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import ai.philterd.entitydb.configuration.EntityDbProperties;

@Configuration
@EnableTransactionManagement
public class PersistenceJPAConfig {
	
	private static final Logger LOGGER = LogManager.getLogger(PersistenceJPAConfig.class);
	
	private static final EntityDbProperties entityDbProperties = ConfigFactory.create(EntityDbProperties.class);

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {		

		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
		entityManager.setDataSource(dataSource());
		entityManager.setPackagesToScan(new String[] { "ai.philterd.entitydb.model.datastore.entities" });

		entityManager.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		entityManager.setJpaProperties(getProperties());

		return entityManager;

	}	

	@Bean
	public DataSource dataSource() {
		
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		if(StringUtils.equals(entityDbProperties.getDataStoreDatabase(), "internal")) {
		
			dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
			dataSource.setUrl("jdbc:hsqldb:mem:entitydb-data-store");
			dataSource.setUsername("sa");
			dataSource.setPassword("");
		
		} else if(StringUtils.equals(entityDbProperties.getDataStoreDatabase(), "mysql")) {
						
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource.setUrl(entityDbProperties.getJdbcUrl());
			dataSource.setUsername(entityDbProperties.getDataStoreUsername());
			dataSource.setPassword(entityDbProperties.getDataStorePassword());
			
		} else {
			
			LOGGER.warn("Invalid datastore {}. Using the internal store.", entityDbProperties.getDataStoreDatabase());
			
			dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
			dataSource.setUrl("jdbc:hsqldb:mem:entitydb-data-store");
			dataSource.setUsername("sa");
			dataSource.setPassword("");
			
		}

		return dataSource;

	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {

		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);

		return transactionManager;

	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
	
	private Properties getProperties() {
		
		Properties properties = new Properties();
		
		if(StringUtils.equals(entityDbProperties.getDataStoreDatabase(), "internal")) {
			
			properties.setProperty("hibernate.hbm2ddl.auto", "create");
			properties.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
			
		} else if(StringUtils.equals(entityDbProperties.getDataStoreDatabase(), "mysql")) {
			
			properties.setProperty("hibernate.hbm2ddl.auto", "validate");
			properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
			
		} else {
			
			properties.setProperty("hibernate.hbm2ddl.auto", "create");
			properties.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
			
		}
		
		return properties;
		
	}

}