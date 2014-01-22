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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.tinkerpop.blueprints.impls.jpa.JpaGraph;
import com.tinkerpop.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaEntity;
import com.tinkerpop.blueprints.impls.jpa.internal.models.index.BpJpaIndexBase;

final public class DefaultDamper implements Damper {

	private final boolean isHibernate;

	public DefaultDamper(EntityManagerFactory entityManagerFactory) {
		if (entityManagerFactory == null) throw BpJpaExceptionFactory.cannotBeNull("entityManagerFactory");
		this.isHibernate = entityManagerFactory.getClass().getName().startsWith("org.hibernate");
	}

	@Override
	public void remove(JpaGraph jpaGraph, BpJpaEntity entity) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (entity == null) throw BpJpaExceptionFactory.cannotBeNull("entity");	
		entity.markRemoved();
		EntityManager em = jpaGraph.getRawGraph();
		em.remove(entity);
	}

	@Override
	public void remove(JpaGraph jpaGraph, BpJpaIndexBase indexBase) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (indexBase == null) throw BpJpaExceptionFactory.cannotBeNull("indexBase");
		indexBase.markRemoved();
		EntityManager em = jpaGraph.getRawGraph();
		em.remove(indexBase);
	}
	
	@Override
	public void beforeFetch(JpaGraph jpaGraph) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");	
	}

	@Override
	public void persist(JpaGraph jpaGraph, BpJpaEntity entity) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (entity == null) throw BpJpaExceptionFactory.cannotBeNull("entity");	
		jpaGraph.getRawGraph().persist(entity);
	}

	@Override
	public void persist(JpaGraph jpaGraph, BpJpaIndexBase indexBase) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (indexBase == null) throw BpJpaExceptionFactory.cannotBeNull("indexBase");	
		jpaGraph.getRawGraph().persist(indexBase);
	}

	@Override
	public boolean isHibernate() {
		return isHibernate;
	}

	@Override
	public void flat() {
		/* noop */		
	}

}
