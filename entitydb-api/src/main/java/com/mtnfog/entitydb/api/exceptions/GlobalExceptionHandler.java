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
package com.mtnfog.entitydb.api.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mtnfog.entitydb.model.exceptions.EntityStoreException;
import com.mtnfog.entitydb.model.exceptions.InvalidQueryException;
import com.mtnfog.entitydb.model.exceptions.api.BadRequestException;
import com.mtnfog.entitydb.model.exceptions.api.NotFoundException;
import com.mtnfog.entitydb.model.exceptions.api.UnableToQueueEntitiesException;
import com.mtnfog.entitydb.model.exceptions.api.UnauthorizedException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
	
	private static final Logger LOGGER = LogManager.getLogger(GlobalExceptionHandler.class);

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = {BadRequestException.class, InvalidQueryException.class, IllegalArgumentException.class, HttpMessageNotReadableException.class})
	public @ResponseBody String handleBaseException(Exception ex) {
		
		LOGGER.warn(ex.getMessage());
		
		return "Request was missing one or more required fields or is not formed correctly.";
		
	}
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(value = NotFoundException.class)
	public @ResponseBody String handleBaseException(NotFoundException ex) {
		
		LOGGER.warn(ex.getMessage());
		
		return "Not found.";
		
	}
	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(value = UnauthorizedException.class)
	public @ResponseBody String handleBaseException(UnauthorizedException ex) {
		
		LOGGER.warn(ex.getMessage());
		
		return "Unauthorized.";
		
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = {UnableToQueueEntitiesException.class, EntityStoreException.class})
	public @ResponseBody String handleBaseException(UnableToQueueEntitiesException ex) {
		
		LOGGER.error(ex.getMessage(), ex);
		
		return "Unable to queue entities.";
		
	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = Exception.class)	
	public @ResponseBody String handleException(Exception ex) {
		
		LOGGER.error(ex.getMessage(), ex);
		
		return "An unhandled error occurred.";
		
	}

}