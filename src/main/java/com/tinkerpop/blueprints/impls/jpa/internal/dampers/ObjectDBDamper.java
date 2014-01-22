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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.impls.jpa.JpaGraph;
import com.tinkerpop.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaEdge;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaElement;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaEntity;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaProperty;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaVertex;
import com.tinkerpop.blueprints.impls.jpa.internal.models.index.BpJpaIndex;
import com.tinkerpop.blueprints.impls.jpa.internal.models.index.BpJpaIndexBase;
import com.tinkerpop.blueprints.impls.jpa.internal.models.index.BpJpaIndexItem;
import com.tinkerpop.blueprints.impls.jpa.internal.models.index.BpJpaKeyIndex;
import com.tinkerpop.blueprints.impls.jpa.internal.models.index.BpJpaKeyIndexedProperty;

public class ObjectDBDamper implements Damper  {

	private static Logger logger = LoggerFactory.getLogger(ObjectDBDamper.class);
	
	protected final ThreadLocal<Boolean> tlDirty = new ThreadLocal<Boolean>() {
		protected Boolean initialValue() {
			return new Boolean(false);
		}
	};
	
	public ObjectDBDamper(EntityManagerFactory entityManagerFactory) {
		if (entityManagerFactory == null) throw BpJpaExceptionFactory.cannotBeNull("entityManagerFactory");
		// for ObjectDb : http://www.objectdb.com/database/forum/597
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getMetamodel().entity(BpJpaIndex.class);
		em.getMetamodel().entity(BpJpaIndexItem.class);
		em.getMetamodel().entity(BpJpaKeyIndex.class);
		em.getMetamodel().entity(BpJpaKeyIndexedProperty.class);
		em.getMetamodel().entity(BpJpaEntity.class);
		em.getMetamodel().entity(BpJpaElement.class);
		em.getMetamodel().entity(BpJpaVertex.class);
		em.getMetamodel().entity(BpJpaEdge.class);
		em.getMetamodel().entity(BpJpaProperty.class);
		em.close();
	}

	@Override
	public void remove(JpaGraph jpaGraph, BpJpaEntity entity) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (entity == null) throw BpJpaExceptionFactory.cannotBeNull("entity");
		tlDirty.set(true);
		entity.markRemoved();
		EntityManager em = jpaGraph.getRawGraph();
		em.remove(entity);
	}

	@Override
	public void remove(JpaGraph jpaGraph, BpJpaIndexBase indexBase) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (indexBase == null) throw BpJpaExceptionFactory.cannotBeNull("indexBase");	
		tlDirty.set(true);
		indexBase.markRemoved();
		EntityManager em = jpaGraph.getRawGraph();
		em.remove(indexBase);
	}
	
	@Override
	public void beforeFetch(JpaGraph jpaGraph) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if ( tlDirty.get() ) {
			//logger.info("beforeFetch", new LoggingStackTrace("beforeFetch"));
			tlDirty.set(false);
			jpaGraph.getRawGraph().flush();	
		}
	}

	@Override
	public void persist(JpaGraph jpaGraph, BpJpaEntity entity) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (entity == null) throw BpJpaExceptionFactory.cannotBeNull("entity");
		jpaGraph.getRawGraph().persist(entity);
		tlDirty.set(true);	
	}

	@Override
	public void persist(JpaGraph jpaGraph, BpJpaIndexBase indexBase) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (indexBase == null) throw BpJpaExceptionFactory.cannotBeNull("indexBase");
		jpaGraph.getRawGraph().persist(indexBase);
		tlDirty.set(true);
	}

	@Override
	public boolean isHibernate() {
		return false;
	}

	@Override
	public void flat() {
		tlDirty.set(false);		
	}
}
