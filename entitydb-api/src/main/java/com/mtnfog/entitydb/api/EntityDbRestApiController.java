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
package com.mtnfog.entitydb.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.api.exceptions.BadRequestException;
import com.mtnfog.entitydb.api.exceptions.InternalServerErrorException;
import com.mtnfog.entitydb.api.exceptions.NotFoundException;
import com.mtnfog.entitydb.api.exceptions.UnableToQueueEntitiesException;
import com.mtnfog.entitydb.api.exceptions.UnauthorizedException;
import com.mtnfog.entitydb.model.search.SearchIndex;
import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.entitydb.model.services.EntityAclService;
import com.mtnfog.entitydb.model.services.EntityQueryService;
import com.mtnfog.entitydb.model.services.EntityQueueService;
import com.mtnfog.entitydb.model.services.UserService;
import com.mtnfog.entitydb.model.status.Status;
import com.mtnfog.entitydb.model.domain.ContinuousQuery;
import com.mtnfog.entitydb.model.domain.Notification;
import com.mtnfog.entitydb.model.entitystore.EntityStore;
import com.mtnfog.entitydb.model.entitystore.QueryResult;
import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.exceptions.MalformedQueryException;
import com.mtnfog.entitydb.model.exceptions.NonexistantEntityException;
import com.mtnfog.entitydb.model.exceptions.QueryExecutionException;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

@RestController
@Validated
public class EntityDbRestApiController {
	
	private static final Logger LOGGER = LogManager.getLogger(EntityDbRestApiController.class);
	
	@Autowired
	private EntityQueryService entityQueryService;
	
	@Autowired
	private SearchIndex searchIndex;
	
	@Autowired
	private EntityAclService entityAclService;
	
	@Autowired
	public EntityStore<?> entityStore;
	
	@Autowired
	public UserService userService;
	
	@Autowired
	public EntityQueueService entityQueueService;
		
	/**
	 * Gets the status.
	 * @return The {@link Status status}.
	 * @throws EntityStoreException Thrown if the entity store cannot get the number of stored entities.
	 */
	@RequestMapping(value = "/api/status", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Status status(@RequestHeader(value="Authorization") String authorization) throws EntityStoreException {
		
		return new Status(searchIndex.getCount(), entityStore.getEntityCount());		
		
	}
	
	/**
	 * Only returns HTTP 200 OK responses. This function is for application-level
	 * monitoring by load balancers and other monitors. Note that there is no
	 * authentication on this endpoint.
	 */
	@RequestMapping(value = "/api/health", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public void health() {	
		// Only returns HTTP OK to be used for application monitoring.
	}
	
	/**
	 * Gets a list of notifications for a user.
	 * @param authorization The user's API key.
	 * @return A list of {@link Notification notifications}. 
	 */
	@RequestMapping(value = "/api/user/notifications", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<Notification> notifications(
			@RequestHeader(value="Authorization", required=false) String authorization) {
	
		try {
		
			return userService.getUserNotifications(authorization);
			
		} catch (Exception ex) {
			
			throw new InternalServerErrorException("Unable to get user's notifications.", ex);
			
		}
		
	}
	
	/**
	 * Gets the continuous queries for a user identified by the API key.
	 * @param authorization The user's API key.
	 * @return A list of {@link ContinuousQueries queries}.
	 */
	@RequestMapping(value = "/api/user/continuousqueries", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<ContinuousQuery> continuousQueries(
			@RequestHeader(value="Authorization") String authorization) {
	
		try {
		
			return userService.getUserContinuousQueries(authorization);
			
		} catch (Exception ex) {
			
			throw new InternalServerErrorException("Unable to get user's continuous queries.", ex);
			
		}
		
	}
					
	/**
	 * Queues an entities for ingest.
	 * @param entities A collection of {@link Entities}.
	 * @param acl An optional ACL for all entities. If not specified all entities
	 * are visible to all users. 
	 * @param authorization The user's API key.
	 * @throws MalformedAclException Thrown if the ACL is invalid.
	 * @throws Exception Thrown if the entities cannot be queued for ingestion.
	 * Check the server log for more information.
	 */
	@RequestMapping(value = "/api/entity", method = {RequestMethod.PUT, RequestMethod.POST})
	@ResponseStatus(HttpStatus.OK)
	public void store(
			@RequestBody Collection<Entity> entities, 
			@RequestParam(value="acl", required=false, defaultValue=Acl.WORLD) String acl,
			@RequestHeader(value="Authorization") String authorization) {
					
		try {					
			
			entityQueueService.queueIngest(entities, acl, authorization);
			
		} catch (MalformedAclException ex) {
			
			throw new BadRequestException("The ACL [" + acl + "] is malformed.", ex);			
			
		} catch (Exception ex) {
			
			throw new UnableToQueueEntitiesException("Unable to queue entities.", ex);
			
		}
		
		LOGGER.debug("Successfully queued {} entities for ingest.", entities.size());
			
	}
	
	/**
	 * Queues an entity ACL modification.
	 * @param entityId The ID of the entity.
	 * @param acl The updated ACL for the entity.
	 * @param authorization The user's API key.
	 * @throws NonexistantEntityException Thrown if the entity having the entity ID does not exist.
	 * @throws MalformedAclException Thrown if the ACL is invalid.
	 * @throws InternalServerErrorException Thrown if the entity's ACL cannot
	 * be updated for other reasons. Check the server log for more information on the cause.
	 */
	@RequestMapping(value = "/api/entity/{entityId}/acl", method = {RequestMethod.PUT, RequestMethod.POST})	
	@ResponseStatus(HttpStatus.OK)
	public void store(
			@PathVariable String entityId,
			@RequestParam(value="acl", required=true) String acl,
			@RequestHeader(value="Authorization") String authorization) {
					
		try {
			
			entityAclService.queueEntityAclUpdate(entityId, acl, authorization);
			
		} catch (NonexistantEntityException ex) {
			
			throw new NotFoundException("The entity to update was not found.");
			
		} catch (MalformedAclException ex) {
			
			throw new BadRequestException("The received ACL is malformed.", ex);			
			
		} catch (Exception ex) {
			
			throw new InternalServerErrorException("Unable to update the entity's ACL.", ex);
			
		}
		
		LOGGER.info("Successfully queued ACL update request for entity {}.", entityId);
			
	}
	
	/**
	 * Executes an EQL query against the indexed entities.
	 * @param query The EQL query.
	 * @param continuous An optional parameter enables this query as a
	 * continuous query. Set to 1 to enable and 0 to disable (default).
	 * @param days The number of days to run this query continuously.
	 * If not provided the default value is 90 days. Specify -1 for a non-expiring continuous query.
	 * @param authorization The user's API key.
	 * @return A {@link QueryResult} containing the entities.
	 * @throws UnauthorizedException Thrown if the authentication is invalid.
	 * @throws BadRequestException Thrown if the EQL query is malformed.
	 */
	@RequestMapping(value = "/api/eql", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<QueryResult> eql(
			@RequestParam(value = "query") String query,
			@RequestParam(value = "continuous", required = false, defaultValue = "0") int continuous,
			@RequestParam(value = "days", required = false, defaultValue = "90") int days,
			@RequestHeader(value = "Authorization") String authorization)	{			
					
		LOGGER.trace("Received EQL query: {}", query);
				
		try {
		
			QueryResult queryResult = entityQueryService.eql(query, authorization, continuous, days);
			
			// Return OK unless it is set to be a continuous query.
			HttpStatus status = HttpStatus.OK;
			
			if(continuous != 0) {
				status = HttpStatus.CREATED;
			}
			
			return new ResponseEntity<QueryResult>(queryResult, status);
			
		} catch (QueryExecutionException ex) {
			
			throw new InternalServerErrorException("Unable to execute the query.", ex);
			
		} catch (MalformedQueryException ex) {
			
			throw new BadRequestException("The received query is malformed.", ex);	

		}
		
	}

}