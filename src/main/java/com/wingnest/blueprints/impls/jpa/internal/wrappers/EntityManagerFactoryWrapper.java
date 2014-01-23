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
package com.wingnest.blueprints.impls.jpa.internal.wrappers;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wingnest.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;
import com.wingnest.blueprints.impls.jpa.internal.dampers.Damper;
import com.wingnest.blueprints.impls.jpa.internal.dampers.DamperFactory;

final public class EntityManagerFactoryWrapper {

	private static Logger logger = LoggerFactory.getLogger(EntityManagerFactoryWrapper.class);
		
	private final EntityManagerFactory entityManagerFactory;

	private final Damper damper;
	 
	public EntityManagerFactoryWrapper() {
		this(null, null);
	}
		 
	public EntityManagerFactoryWrapper(String persistanceUnitName) {
		this(persistanceUnitName, null);
	}

	public EntityManagerFactoryWrapper(EntityManagerFactory entityManagerFactory) {
		super();
		if (entityManagerFactory == null) throw BpJpaExceptionFactory.cannotBeNull("entityManagerFactory");
		
		this.entityManagerFactory = entityManagerFactory;
		this.damper = DamperFactory.create(this.entityManagerFactory);
	}

	public EntityManagerFactoryWrapper(String persistanceUnitName, Map<String, Object> props) {
		String pPersistanceUnitName = persistanceUnitName != null ? persistanceUnitName : System.getProperty("jpa.graph.default.unit", "DefaultUnit");
		if (pPersistanceUnitName == null) throw BpJpaExceptionFactory.cannotBeNull("pPersistanceUnitName");
		if (pPersistanceUnitName.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("pPersistanceUnitName");
		
		logger.debug(String.format("persistanceUnitName = %s", pPersistanceUnitName));
		try {
			this.entityManagerFactory = Persistence.createEntityManagerFactory(pPersistanceUnitName, props);
			this.damper = DamperFactory.create(this.entityManagerFactory);
		} catch (RuntimeException e) {
			logger.error(String.format("Called Persistence.createEntityManagerFactory: persistanceUnitName = '%s'", pPersistanceUnitName), e);
			throw e;
		}
	}
	
	public EntityManager createEntityManager(Map<String, Object> map) {
		EntityManager entityManager = entityManagerFactory.createEntityManager(map);
		return entityManager;		
	}

	public EntityManager createEntityManager() {
		return createEntityManager(null);
	}
	
	public EntityManagerFactory getEntityManagerFactory() {
		return this.entityManagerFactory;
	}
	
	public void close() {
		this.entityManagerFactory.close();
	}

	public Damper getDamper() {
		return this.damper;
	}

}
