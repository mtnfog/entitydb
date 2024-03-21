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
package ai.philterd.entitydb.api.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ai.philterd.entitydb.model.exceptions.EntityStoreException;
import ai.philterd.entitydb.model.exceptions.InvalidQueryException;

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
	@ResponseBody
	public String handleBaseException(Exception ex) {
		
		LOGGER.warn(ex.getMessage());
		
		return "Request was missing one or more required fields or is not formed correctly.";
		
	}
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(value = NotFoundException.class)
	@ResponseBody
	public String handleBaseException(NotFoundException ex) {
		
		LOGGER.warn(ex.getMessage());
		
		return "Not found.";
		
	}
	
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(value = UnauthorizedException.class)
	@ResponseBody
	public String handleBaseException(UnauthorizedException ex) {
		
		LOGGER.warn(ex.getMessage());
		
		return "Unauthorized.";
		
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = {UnableToQueueEntitiesException.class, EntityStoreException.class})
	@ResponseBody
	public String handleBaseException(UnableToQueueEntitiesException ex) {
		
		LOGGER.error(ex.getMessage(), ex);
		
		return "Unable to queue entities.";
		
	}
	
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = Exception.class)	
	@ResponseBody
	public String handleException(Exception ex) {
		
		LOGGER.error(ex.getMessage(), ex);
		
		return "An unhandled error occurred.";
		
	}

}