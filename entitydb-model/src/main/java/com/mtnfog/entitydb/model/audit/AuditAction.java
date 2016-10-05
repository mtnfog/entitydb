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
package com.mtnfog.entitydb.model.audit;

/**
 * An audited action.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public enum AuditAction {

	/**
	 * An entity was visible due to a search.
	 */
	SEARCH_RESULT("search_result"),
	
	/**
	 * A query that was executed.
	 */
	QUERY("query"),
	
	/**
	 * An entity was stored.
	 */
	STORED("stored"),
	
	/**
	 * An entity was skipped because it already exists in the store.
	 */
	SKIPPED("skipped"),
	
	/**
	 * The entity was indexed.
	 */
	INDEXED("indexed");
	
	private String auditAction;
	
	private AuditAction(String auditAction) {
		
		this.auditAction = auditAction;
		
	}
	
	/**
	 * Gets the name of the audit action.
	 * @return The name of the audit action.
	 */
	@Override
	public String toString() {
		
		return auditAction;
		
	}
	
}