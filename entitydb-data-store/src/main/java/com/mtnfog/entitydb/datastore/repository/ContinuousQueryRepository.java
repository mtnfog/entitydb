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
package com.mtnfog.entitydb.datastore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mtnfog.entitydb.model.datastore.entities.ContinuousQueryEntity;
import com.mtnfog.entitydb.model.datastore.entities.UserEntity;

@Repository
public interface ContinuousQueryRepository extends CrudRepository<ContinuousQueryEntity, Long> {
	
	@Query(value = "SELECT * FROM ContinuousQueries t WHERE DATEDIFF(NOW(), t.timestamp) <= t.days OR Days = -1", nativeQuery=true)	 
	public List<ContinuousQueryEntity> getNonExpiredContinuousQueries();
	 
	public List<ContinuousQueryEntity> findByUserOrderByIdDesc(UserEntity userEntity);
		
	@Override
	public <S extends ContinuousQueryEntity> S save(S continuousQueryEntity);
	
	@Override
	public void delete(ContinuousQueryEntity continuousQueryEntity);
	
}