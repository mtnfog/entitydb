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
package com.mtnfog.entitydb.model.audit;

/**
 * Interface for audit log stores.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public interface AuditLogger {

	/**
	 * For events that are caused by the system and not an individual user.
	 */
	public static final String SYSTEM = "SYSTEM";
	
	/**
	 * Write the audit log to the 
	 * @param entityId The ID of the entity.
	 * @param timestamp When the event took place.
	 * @param userIdentifier A unique identifier of the user.
	 * @param auditAction The {@link AuditAction action} being audited.
	 * @param entityDbId An identifier for the EntityDB installation.
	 * @return <code>true</code> when the audit operation succeeds; <code>false</code> otherwise.
	 */
	public boolean audit(String entityId, long timestamp, String userIdentifier, AuditAction auditAction, String entityDbId);
	
	/**
	 * Write the audit log to the 
	 * @param query The ID of the entity.
	 * @param timestamp When the event took place.
	 * @param userIdentifier A unique identifier of the user.
	 * @param entityDbId An identifier for the EntityDB installation.
	 * @return <code>true</code> when the audit operation succeeds; <code>false</code> otherwise.
	 */
	public boolean audit(String query, long timestamp, String userIdentifier, String entityDbId);
	
	/**
	 * Closes and releases any resources.
	 */
	public void close();
	
}