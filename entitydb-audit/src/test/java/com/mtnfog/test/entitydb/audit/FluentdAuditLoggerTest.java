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
package com.mtnfog.test.entitydb.audit;

import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

import com.mtnfog.entitydb.audit.FluentdAuditLogger;
import com.mtnfog.entitydb.model.audit.AuditAction;

public class FluentdAuditLoggerTest {

	@Test
	@Ignore
	public void logEntity() {
		
		FluentdAuditLogger logger = new FluentdAuditLogger("junit");
		
		String entityId = UUID.randomUUID().toString();
		long timestamp = System.currentTimeMillis();
		String apiKey = "apikey";
		
		logger.audit(entityId, timestamp, apiKey, AuditAction.SEARCH_RESULT);
		
	}
	
}