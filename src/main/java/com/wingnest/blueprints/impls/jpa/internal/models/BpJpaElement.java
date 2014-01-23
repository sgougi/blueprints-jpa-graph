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
package com.wingnest.blueprints.impls.jpa.internal.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wingnest.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;

public abstract class BpJpaElement extends BpJpaEntity {

	private static Logger logger = LoggerFactory.getLogger(BpJpaElement.class);

	private Map<String, BpJpaProperty> propMap = new HashMap<String, BpJpaProperty>();
	
	public abstract ElementType getElementType();
	
	public void putProperty(BpJpaProperty property) {
		if (property == null) throw BpJpaExceptionFactory.cannotBeNull("property");
		
		propMap.put(property.getKey(), property);	
		property.setElement(this);
	}
		
	public BpJpaProperty getProperty(String key) {
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");

		return propMap.get(key);
	}	
	
	public boolean hasProperty(String key) {
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");

		return propMap.containsKey(key);
	}		

	public Set<String> getKeys() {
		return new CopyOnWriteArraySet<String>(propMap.keySet());
	}

	public void removeProperty(String key) {
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		
		propMap.remove(key);
	}

	public static BpJpaElement getElementById(EntityManager entityManager, Object id) {
		if (entityManager == null) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if (id == null) throw BpJpaExceptionFactory.cannotBeNull("id");
		
		try {
			TypedQuery<BpJpaElement> q = entityManager.createNamedQuery("BpJpaElement_getElementById", BpJpaElement.class).setParameter("id", (id instanceof Long ? id : Long.parseLong(id.toString()) ));
			List<BpJpaElement> vs = q.getResultList();
			return vs.size() == 0 ? null : vs.get(0);
		} catch (NumberFormatException e) {
			logger.debug("getEdgeById", e);	
			return null;
		}
	}

}
