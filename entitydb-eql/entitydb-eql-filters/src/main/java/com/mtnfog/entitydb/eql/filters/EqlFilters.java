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
package com.mtnfog.entitydb.eql.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mtnfog.entitydb.eql.filters.comparisons.DateComparison;
import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.eql.Eql;
import com.mtnfog.entitydb.eql.exceptions.QueryGenerationException;
import com.mtnfog.entitydb.eql.model.EntityQuery;

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
				
				// The exact milliseconds of the date is stored in an enrichment of the date.
				String milliseconds = entity.getMetadata().get("time");
				
				if(milliseconds != null) {	
					
					Date entityDate = new Date(Long.valueOf(milliseconds));
					
					if(dateComparison.equals(DateComparison.BEFORE) && entityDate.before(date)) {
						
						matchedEntities.add(entity);
						
					} else if(dateComparison.equals(DateComparison.AFTER) && entityDate.after(date)) {
						
						matchedEntities.add(entity);
						
					} else {
						
						// For some reason this date is missing its milliseconds enrichment value. Do not include it since we don't know.
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
	 * @param value The size of the window.
	 * @return A filtered collection of date entities that exist in the given window.
	 */
	public static Collection<Entity> filterEntities(Collection<Entity> entities, Date targetDate, int minutes) {
		
		Date startDate = new Date(targetDate.getTime() - 5*60*1000);
		Date endDate = new Date(targetDate.getTime() + 5*60*1000);
		
		Collection<Entity> matchedEntities = new LinkedList<>();
		
		for(Entity entity : entities ) {
			
			if(entity.getType().equals("date")) {
				
				// The exact milliseconds of the date is stored in an enrichment of the date.
				String milliseconds = entity.getMetadata().get("time");
				
				if(milliseconds != null) {	
					
					Date entityDate = new Date(Long.valueOf(milliseconds));
				
					if(entityDate.after(startDate) && entityDate.before(endDate)) {
						
						matchedEntities.add(entity);
						
					}
					
				} else {
					
					// For some reason this date is missing its milliseconds enrichment value. Do not include it since we don't know.
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
				
					EntityQuery entityQuery = Eql.generate(eql);												
					
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
		// Otherwise we just return the entities that matched at least one EQL statement.
		
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