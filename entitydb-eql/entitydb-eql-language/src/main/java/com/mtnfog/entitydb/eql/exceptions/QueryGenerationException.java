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
package com.mtnfog.entitydb.eql.exceptions;

/**
 * An exception thrown when generating an {@link EntityQuery query}
 * from an EQL query.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class QueryGenerationException extends RuntimeException {

	private static final long serialVersionUID = 2407358322163930971L;

	/**
	 * Creates a new exception.
	 * @param message The message of the exception.
	 */
	public QueryGenerationException(String message) {
		super(message);
	}
	
	/**
	 * Creates a new exception.
	 * @param message The message of the exception.
	 * @param throwable The exception.
	 */
	public QueryGenerationException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}