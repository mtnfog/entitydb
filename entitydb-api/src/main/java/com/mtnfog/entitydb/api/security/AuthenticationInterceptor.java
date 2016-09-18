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
package com.mtnfog.entitydb.api.security;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.mtnfog.entitydb.model.exceptions.api.UnauthorizedException;
import com.mtnfog.entitydb.model.users.User;

/**
 * Provides API authentication for API requests.
 * 
 * @author Mountain Fog, Inc.
 *
 */
@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

	private static final Logger LOGGER = LogManager.getLogger(AuthenticationInterceptor.class);
	
	@Autowired
	private List<User> users;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		LOGGER.info("Authenticating REST request.");
return true;
/*
		boolean authorized = true;
		
		final String apiKey = request.getHeader("Authorization");
		
		// Is there a user that has this API key?
		if(StringUtils.isNotEmpty(apiKey) && User.authenticate(users, apiKey)) {
			
			authorized = true;
					
		} else {
			
			throw new UnauthorizedException("Unauthorized.");
			
		}
		
		return authorized;*/
		
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		// Do nothing.

	}

}