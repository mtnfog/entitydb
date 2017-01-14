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
package com.mtnfog.entitydb.audit;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fluentd.logger.FluentLogger;

import com.mtnfog.entitydb.model.audit.AuditAction;
import com.mtnfog.entitydb.model.audit.AuditLogger;

/**
 * Implementation of {@link AuditLogger} that used Fluentd
 * to log audit events.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class FluentdAuditLogger extends AbstractAuditLogger implements AuditLogger {
	
	private static final Logger LOGGER = LogManager.getLogger(FluentdAuditLogger.class);

	private static final FluentLogger FLUENT_LOGGER = FluentLogger.getLogger("entitydb");
	
	public FluentdAuditLogger(String systemId) {
		super(systemId);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean audit(String entityId, long timestamp, String userIdentifier, AuditAction auditAction) {
		
		Map<String, Object> data = new HashMap<String, Object>();      		
		
		data.put("entityId", entityId);		
		data.put("timestamp", timestamp);
        data.put("userIdentifier", userIdentifier);
        data.put("action", auditAction.toString());
        data.put("systemId", systemId);
        
        return FLUENT_LOGGER.log("follow", data);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean audit(String query, long timestamp, String userName) {
		
		Map<String, Object> data = new HashMap<String, Object>();      		
		
		data.put("query", query);		
		data.put("timestamp", timestamp);
        data.put("userName", userName);
        data.put("action", AuditAction.QUERY.toString());
        data.put("systemId", systemId);
        
        return FLUENT_LOGGER.log("follow", data);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {
		
		FLUENT_LOGGER.close();
		
	}
 
}