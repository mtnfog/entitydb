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
package ai.philterd.entitydb.eql.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ai.philterd.entitydb.eql.Eql;
import ai.philterd.entitydb.model.eql.EntityQuery;
import ai.philterd.entitydb.model.exceptions.QueryGenerationException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.philterd.entitydb.model.entity.Entity;

/**
 * Static functions for applying EQL statements to entities.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class EqlFilters {

	private static final Logger LOGGER = LogManager.getLogger(EqlFilters.class);
		
	/**
	 * Determines if an entity satisfies (matches) an EQL statement. Note that this function
	 * internally calls the <code>filterEntities</code> function for evaluation by wrapping
	 * the entity and the EQL statements in collections.
	 * @param entity The {@link Entity entity} being tested.
	 * @param eql The EQL statement.
	 * @return <code>true</code> if the entity satisfies the EQL statement. Otherwise, <code>false</code>.
	 * @throws QueryGenerationException Thrown if the EQL statement is malformed.
	 */
	public static boolean isMatch(Entity entity, String eql) throws QueryGenerationException {

		Collection<Entity> entities = new ArrayList<Entity>();
		entities.add(entity);
		
		List<String> eqlStatements = new ArrayList<String>();
		eqlStatements.add(eql);
		
		Collection<Entity> matchedEntities = filterEntities(entities, eqlStatements);
		
		// If the collection is NOT empty the entity was matched.
		return !(matchedEntities.isEmpty());
		
	}
	
	/**
	 * Filters date entities.
	 * Non-date entities are not filtered out.
	 * @param entities The collection of entities.
	 * @param date The target date.
	 * @param dateComparison How to compare the dates.
	 * @return A filtered collection of date entities.
	 */
	public static Collection<Entity> filterEntities(Collection<Entity> entities, Date date, DateComparison dateComparison) {
			
		Collection<Entity> matchedEntities = new LinkedList<>();
		
		for(Entity entity : entities ) {
						
			if(entity.getType().equals("date")) {
				
				// The exact milliseconds of the date is stored in the metadata of the date.
				String milliseconds = entity.getMetadata().get("time");
				
				if(milliseconds != null) {	
					
					Date entityDate = new Date(Long.valueOf(milliseconds));
					
					if(dateComparison.equals(DateComparison.BEFORE) && entityDate.before(date)) {
						
						matchedEntities.add(entity);
						
					} else if(dateComparison.equals(DateComparison.AFTER) && entityDate.after(date)) {
						
						matchedEntities.add(entity);
						
					} else {
						
						// For some reason this date is missing its milliseconds metadata value. Do not include it since we don't know.
					}
				
				}
								
			} else {
				
				// Include it since it is not a date.
				matchedEntities.add(entity);
				
			}
			
		}
		
		return matchedEntities;
		
	}
	
	/**
	 * Filters date entities in a date window centered on a target date.
	 * Non-date entities are not filtered out.
	 * @param entities The collection of entities.
	 * @param targetDate The target date.
	 * @param minutes The size of the window in minutes.
	 * @return A filtered collection of date entities that exist in the given window.
	 */
	public static Collection<Entity> filterEntities(Collection<Entity> entities, Date targetDate, int minutes) {
		
		Date startDate = new Date(targetDate.getTime() - 5*60*1000);
		Date endDate = new Date(targetDate.getTime() + 5*60*1000);
		
		Collection<Entity> matchedEntities = new LinkedList<>();
		
		for(Entity entity : entities ) {
			
			if(entity.getType().equals("date")) {
				
				// The exact milliseconds of the date is stored in an metadata of the date.
				String milliseconds = entity.getMetadata().get("time");
				
				if(milliseconds != null) {	
					
					Date entityDate = new Date(Long.valueOf(milliseconds));
				
					if(entityDate.after(startDate) && entityDate.before(endDate)) {
						
						matchedEntities.add(entity);
						
					}
					
				} else {
					
					// For some reason this date is missing its milliseconds metadata value. Do not include it since we don't know.
				}
				
			} else {
				
				// Include it since it is not a date.
				matchedEntities.add(entity);
				
			}
			
		}
		
		return matchedEntities;
		
	}
	
	/**
	 * Filter a collection of entities based on given EQL statements.
	 * @param entities The collection of {@link Entity entities}.
	 * @param eqlStatement An EQL statement.
	 * @return A filtered collection of {@link Entity entities} containing only those
	 * entities that meet the criteria of at least one EQL statement.
	 * @throws QueryGenerationException 
	 */
	public static Collection<Entity> filterEntities(Collection<Entity> entities, String eqlStatement) throws QueryGenerationException {
		
		return filterEntities(entities, Arrays.asList(eqlStatement));
		
	}
		
	/**
	 * Filter a collection of entities based on given EQL statements.
	 * @param entities The collection of {@link Entity entities}.
	 * @param eqlStatements A list of EQL statements.
	 * @return A filtered collection of {@link Entity entities} containing only those
	 * entities that meet the criteria of at least one EQL statement.
	 * @throws QueryGenerationException 
	 */
	public static Collection<Entity> filterEntities(Collection<Entity> entities, List<String> eqlStatements) throws QueryGenerationException {
				
		// A universalMatch is when all entities match the filter. When this happens
		// there is no need to check each individual entity.
		boolean universalMatch = false;
		
		Collection<Entity> matchedEntities = new LinkedList<>();
		
		if(CollectionUtils.isEmpty(eqlStatements)) {
			
			// There are no statements so this is a universal match.			
			universalMatch = true;
			
		} else {		
		
			for(String eql : eqlStatements) {
				
				if("select * from entities".equalsIgnoreCase(eql)) {
					
					universalMatch = true;
					
				} else {
				
					final EntityQuery entityQuery = Eql.generate(eql);
					
					if(!universalMatch) {					
												
						for(Entity entity : entities) {
							
							if(entityQuery.isMatch(entity)) {
								
								if(passNotConditions(entity, entityQuery)) {
									matchedEntities.add(entity);
								}
								
							}
						}
						
					}
					
				}
				
			}
						
		}
		
		// If it is a universalMatch we return all entities.
		// Otherwise, we just return the entities that matched at least one EQL statement.
		
		if(universalMatch) {
			
			return entities;
			
		} else {
		
			return matchedEntities;
			
		}
		
	}
	
	private static boolean passNotConditions(Entity entity, EntityQuery entityQuery) {
		
		// Determine if the entity passes the NOT conditions of the query.
		
		if(StringUtils.isNotEmpty(entityQuery.getNotText()) && StringUtils.equals(entity.getText(), entityQuery.getNotText())) return false;
		if(StringUtils.isNotEmpty(entityQuery.getNotType()) && StringUtils.equals(entity.getType(), entityQuery.getNotType())) return false;
		if(StringUtils.isNotEmpty(entityQuery.getNotContext()) && StringUtils.equals(entity.getContext(), entityQuery.getNotContext())) return false;
		if(StringUtils.isNotEmpty(entityQuery.getNotDocumentId()) && StringUtils.equals(entity.getDocumentId(), entityQuery.getNotDocumentId())) return false;
		if(StringUtils.isNotEmpty(entityQuery.getNotLanguageCode()) && StringUtils.equals(entity.getLanguageCode(), entityQuery.getNotLanguageCode())) return false;
		if(StringUtils.isNotEmpty(entityQuery.getNotUri()) && StringUtils.equals(entity.getUri(), entityQuery.getNotUri())) return false;
		
		return true;
		
	}
	
}
