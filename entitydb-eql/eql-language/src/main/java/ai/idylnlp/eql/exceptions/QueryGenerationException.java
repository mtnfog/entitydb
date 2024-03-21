/*******************************************************************************
 * Copyright 2019 Mountain Fog, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/*
 * (C) Copyright 2017 Mountain Fog, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ai.idylnlp.eql.exceptions;

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
