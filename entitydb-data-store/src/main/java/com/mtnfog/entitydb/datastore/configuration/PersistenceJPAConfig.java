/**
 * Copyright © 2017 Mountain Fog, Inc. (support@mtnfog.com)
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
package com.mtnfog.entitydb.datastore.configuration;

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

import com.mtnfog.entitydb.configuration.EntityDbProperties;

@Configuration
@EnableTransactionManagement
public class PersistenceJPAConfig {
	
	private static final Logger LOGGER = LogManager.getLogger(PersistenceJPAConfig.class);
	
	private static final EntityDbProperties entityDbProperties = ConfigFactory.create(EntityDbProperties.class);

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {		

		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
		entityManager.setDataSource(dataSource());
		entityManager.setPackagesToScan(new String[] { "com.mtnfog.entitydb.model.datastore.entities" });

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