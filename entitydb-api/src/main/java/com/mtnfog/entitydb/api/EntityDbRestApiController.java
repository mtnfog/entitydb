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
 * For commercial licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
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
import com.mtnfog.entitydb.model.queue.QueuePublisher;
import com.mtnfog.entitydb.model.security.Acl;
import com.mtnfog.entitydb.services.EntityAclService;
import com.mtnfog.entitydb.services.EntityQueryService;
import com.mtnfog.entitydb.model.entitystore.QueryResult;
import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.exceptions.NonexistantEntityException;
import com.mtnfog.entitydb.model.exceptions.api.BadRequestException;
import com.mtnfog.entitydb.model.exceptions.api.InternalServerErrorException;
import com.mtnfog.entitydb.model.exceptions.api.NotFoundException;
import com.mtnfog.entitydb.model.exceptions.api.UnableToQueueEntitiesException;
import com.mtnfog.entitydb.model.exceptions.api.UnauthorizedException;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

@RestController
@Validated
public class EntityDbRestApiController {
	
	private static final Logger LOGGER = LogManager.getLogger(EntityDbRestApiController.class);
	
	@Autowired
	private EntityQueryService entityQueryService;
	
	@Autowired
	private EntityAclService entityAclService;
	
	@Autowired
	private QueuePublisher queuePublisher;
					
	/**
	 * Queues an entities for ingest.
	 * @param entities A collection of {@link Entities}.
	 * @param acl An optional ACL for all entities. If not specified all entities
	 * are visible to all users. 
	 * @param authorization An optional Authorization header.
	 * @throws MalformedAclException Thrown if the ACL is invalid.
	 * @throws Exception Thrown if the entities cannot be queued for ingestion.
	 * Check the server log for more information.
	 */
	@RequestMapping(value = "/api/entity", method = {RequestMethod.PUT, RequestMethod.POST})
	@ResponseStatus(HttpStatus.OK)
	public void store(
			@RequestBody Collection<Entity> entities, 
			@RequestParam(value="acl", required=false, defaultValue=Acl.WORLD) String acl,
			@RequestHeader(value="Authorization", required=false) String authorization)
			throws MalformedAclException, Exception {
					
		try {
						
			queuePublisher.queueIngest(entities, acl, authorization);
			
		} catch (MalformedAclException ex) {
			
			throw new BadRequestException("The ACL [" + acl + "] is malformed.", ex);			
			
		} catch (Exception ex) {
			
			throw new UnableToQueueEntitiesException("Unable to queue entities.", ex);
			
		}
		
		LOGGER.info("Successfully queued {} entities.", entities.size());
			
	}
	
	/**
	 * Modifies an entity's ACL.
	 * @param entityId The ID of the entity.
	 * @param acl The updated ACL for the entity.
	 * @param authorization An optional Authorization header.
	 * @throws NonexistantEntityException Thrown if the entity ID is invalid.
	 * @throws MalformedAclException Thrown if the ACL is invalid.
	 * @throws InternalServerErrorException Thrown if the entity's ACL cannot
	 * be updated for other reasons. Check the server log for more information on the cause.
	 */
	@RequestMapping(value = "/api/entity/{entityId}/acl", method = {RequestMethod.PUT, RequestMethod.POST})	
	@ResponseStatus(HttpStatus.OK)
	public void store(
			@PathVariable String entityId,
			@RequestParam(value="acl", required=true) String acl,
			@RequestHeader(value="Authorization", required=false) String authorization)
			throws NonexistantEntityException, MalformedAclException, InternalServerErrorException {
					
		try {
			
			entityAclService.updateEntityAcl(entityId, acl, authorization);
			
		} catch (NonexistantEntityException ex) {
			
			throw new NotFoundException("The entity was not found.");
			
		} catch (MalformedAclException ex) {
			
			throw new BadRequestException("The ACL is malformed.", ex);			
			
		} catch (Exception ex) {
			
			throw new InternalServerErrorException("Unable to update entity's ACL.", ex);
			
		}
		
		LOGGER.info("Successfully queued ACL update request.");
			
	}
	
	/**
	 * Executes an EQL query against the indexed entities.
	 * @param query The EQL query.
	 * @param continuous An optional parameter enables this query as a
	 * continuous query. Set to 1 to enable and 0 to disable (default).
	 * @param days The number of days to run this query continuously.
	 * If not provided the default value is 90 days.
	 * @param authorization An optional Authorization header.
	 * @return A {@link QueryResult} containing the entities.
	 * @throws UnauthorizedException Thrown if the authentication is invalid.
	 * @throws BadRequestException Thrown if the EQL query is malformed.
	 */
	@RequestMapping(value = "/api/eql", method = RequestMethod.GET)
	public @ResponseBody QueryResult eql(
			@RequestParam(value = "query") String query,
			@RequestParam(value = "continuous", required = false, defaultValue = "0") int continuous,
			@RequestParam(value = "days", required = false, defaultValue = "90") int days,
			@RequestHeader(value = "Authorization", required = false) String authorization)			
		throws UnauthorizedException, BadRequestException {			
					
		LOGGER.trace("Received EQL query: {}", query);
				
		return entityQueryService.eql(query, authorization, continuous, days);
		
	}

}