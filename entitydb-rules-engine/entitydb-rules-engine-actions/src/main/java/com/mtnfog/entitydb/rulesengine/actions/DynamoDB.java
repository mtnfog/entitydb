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
package com.mtnfog.entitydb.rulesengine.actions;

import java.util.Collection;

import com.mtnfog.entity.Entity;
import com.mtnfog.entitydb.model.integrations.Integration;
import com.mtnfog.entitydb.model.integrations.IntegrationException;
import com.mtnfog.idyl.sdk.integrations.aws.DynamoDBIntegration;

public class DynamoDB extends DynamoDBIntegration implements Integration {
	
	public DynamoDB(String tableName, String endpoint) {
		super(tableName, endpoint);
	}
		
	public DynamoDB(String tableName, String endpoint, String accessKey, String secretKey) {
		super(tableName, endpoint, accessKey, secretKey);
	}
	
	@Override
	public void process(Collection<Entity> entities) throws IntegrationException {
		super.process(entities);
	}
	
}