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
import com.mtnfog.entitydb.model.exceptions.api.NotFoundException;
import com.mtnfog.entitydb.model.exceptions.api.UnableToQueueEntitiesException;
import com.mtnfog.entitydb.model.exceptions.api.UnauthorizedException;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

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
					
	@RequestMapping(value = "/api/entity", method = {RequestMethod.PUT, RequestMethod.POST})
	@ResponseStatus(HttpStatus.OK)
	public void store(
			@RequestBody Collection<Entity> entities, 
			@RequestParam(value="acl", required=false, defaultValue=Acl.WORLD) String acl,
			@RequestHeader(value="Authorization", required=false) String authorization) {
					
		try {
						
			queuePublisher.queueIngest(entities, acl, authorization);
			
		} catch (MalformedAclException ex) {
			
			throw new BadRequestException("The ACL [" + acl + "] is malformed.", ex);			
			
		} catch (Exception ex) {
			
			throw new UnableToQueueEntitiesException("Unable to queue entities.", ex);
			
		}
		
		LOGGER.info("Successfully queued {} entities.", entities.size());
			
	}
	
	@RequestMapping(value = "/api/entity/{entityId}/acl", method = {RequestMethod.PUT, RequestMethod.POST})
	@ResponseStatus(HttpStatus.OK)
	public void store(
			@PathVariable String entityId,
			@RequestParam(value="acl", required=true) String acl,
			@RequestHeader(value="Authorization", required=false) String authorization) {
					
		try {
			
			entityAclService.updateEntityAcl(entityId, acl, authorization);
			
		} catch (NonexistantEntityException ex) {
			
			throw new NotFoundException("The entity was not found.");
			
		} catch (MalformedAclException ex) {
			
			throw new BadRequestException("The ACL is malformed.", ex);			
			
		} catch (Exception ex) {
			
			throw new UnableToQueueEntitiesException("Unable to queue entities.", ex);
			
		}
		
		LOGGER.info("Successfully queued ACL update request.");
			
	}
	
	@RequestMapping(value = "/api/eql", method = RequestMethod.GET)
	public @ResponseBody QueryResult eql(
			HttpServletRequest httpServletRequest,
			@RequestParam(value = "query") String query,
			@RequestParam(value = "continuous", required = false, defaultValue = "0") int continuous,
			@RequestParam(value = "days", required = false, defaultValue = "90") int days,
			@RequestHeader(value = "Authorization", required = false) String authorization)			
		throws UnauthorizedException, BadRequestException, MalformedAclException {			
					
		LOGGER.trace("Received EQL query: {}", query);
				
		return entityQueryService.eql(query, authorization, continuous, days);
		
	}
	
	/*@RequestMapping(value = "/api/query", method = RequestMethod.GET)
	public @ResponseBody QueryResult query(
			HttpServletRequest httpServletRequest,
			@RequestParam(value = "entity", required = false) String entityText,
			@RequestParam(value = "minConfidence", required = false, defaultValue = "0") int minConfidence,
			@RequestParam(value = "maxConfidence", required = false, defaultValue = "100") int maxConfidence,
			@RequestParam(value = "context", required = false) String context,
			@RequestParam(value = "documentId", required = false) String documentId,
			@RequestParam(value = "enrichment", required = false) String enrichment,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "language", required = false) String language,
			@RequestParam(value = "uri", required = false) String uri,
			@RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
			@RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
			@RequestHeader(value = "Authorization", required = false) String authorization)
		throws UnauthorizedException, BadRequestException, QueryGenerationException, EntityStoreException, MalformedAclException, InvalidQueryException {
						
		return entityQueryService.query(entityText, minConfidence,
				maxConfidence, context, documentId, enrichment, type, 
				language, uri, offset, limit, authorization);
		
	}*/

}