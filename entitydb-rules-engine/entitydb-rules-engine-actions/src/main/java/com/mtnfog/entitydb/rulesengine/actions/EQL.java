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
package com.mtnfog.entitydb.rulesengine.actions;

import java.util.Collection;
import java.util.Date;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.eql.filters.EqlFilters;
import com.mtnfog.entitydb.eql.filters.comparisons.DateComparison;

public class EQL {
	
	public static boolean isMatch(Entity entity, String eqlStatement) {
		
		return EqlFilters.isMatch(entity, eqlStatement);
		
	}
	
	public static Collection<Entity> filterEntities(Collection<Entity> entities, Date date, DateComparison dateComparison) {
		
		return EqlFilters.filterEntities(entities, date, dateComparison);
		
	}
	
	public static Collection<Entity> filterEntities(Collection<Entity> entities, Date targetDate, int minutes) {
		
		return EqlFilters.filterEntities(entities, targetDate, minutes);
		
	}
	
}