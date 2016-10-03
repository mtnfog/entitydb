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
package com.mtnfog.entitydb.model.entitystore;

import com.mtnfog.entitydb.model.exceptions.MalformedAclException;
import com.mtnfog.entitydb.model.search.IndexedEntity;

public abstract class AbstractStoredEntity {
		
	public abstract String getId();
	public abstract int getVisible();
	public abstract String getAcl();
	public abstract String getText();
	public abstract String getType();
	public abstract String getContext();
	public abstract String getDocumentId();
	public abstract long getExtractionDate();
	public abstract String getLanguage();
	public abstract String getUri();
	public abstract double getConfidence();
	public abstract long getTimestamp();
	public abstract long getIndexed();
	
	/**
	 * Convert the object to a {@link StoredEntity}.
	 * @return An {@link IndexedEntity}.
	 * @throws MalformedAclException  Thrown if the ACL is malformed.
	 */
	public abstract IndexedEntity toIndexedEntity() throws MalformedAclException;
	
}