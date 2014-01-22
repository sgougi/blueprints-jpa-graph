/*
 * Copyright since 2014 Shigeru GOUGI (sgougi@gmail.com)
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
package com.tinkerpop.blueprints.impls.jpa.internal.dampers;

import com.tinkerpop.blueprints.impls.jpa.JpaGraph;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaEntity;
import com.tinkerpop.blueprints.impls.jpa.internal.models.index.BpJpaIndexBase;

public interface Damper {

	void remove(JpaGraph jpaGraph, BpJpaEntity entity);

	void remove(JpaGraph jpaGraph, BpJpaIndexBase indexBase);

	void beforeFetch(JpaGraph jpaGraph);

	void persist(JpaGraph jpaGraph, BpJpaEntity entity);
	
	void persist(JpaGraph jpaGraph, BpJpaIndexBase indexBase);

	boolean isHibernate();

	boolean isObjectDB();

	void flat();
	
}