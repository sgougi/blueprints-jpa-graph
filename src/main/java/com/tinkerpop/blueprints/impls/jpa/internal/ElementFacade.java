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
package com.tinkerpop.blueprints.impls.jpa.internal;

import java.util.Set;

import com.tinkerpop.blueprints.impls.jpa.JpaElement;
import com.tinkerpop.blueprints.impls.jpa.JpaGraph;
import com.tinkerpop.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaElement;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaProperty;

final public class ElementFacade {

	public static <T> T getProperty(JpaGraph jpaGraph, JpaElement jpaElement, String key) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (jpaElement == null) throw BpJpaExceptionFactory.cannotBeNull("jpaElement");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		try {
			BpJpaProperty property = jpaElement.getAsBpJpaElement().getProperty(key);
			return (T)(property == null ? null : property.getValue());
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}			
	}

	public static <T> T removeProperty(JpaGraph jpaGraph, JpaElement jpaElement, String key) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (jpaElement == null) throw BpJpaExceptionFactory.cannotBeNull("jpaElement");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");	
		try {
			BpJpaProperty property = jpaElement.getAsBpJpaElement().getProperty(key);
			if (property == null) return null;
			@SuppressWarnings("unchecked")
			T value = (T)property.getValue();
			jpaGraph.getBpJpaKeyIndexManager().removeKeyIndexedPropertyIfNeeds(property);
			jpaElement.getAsBpJpaElement().removeProperty(key);
			jpaGraph.getDamper().remove(jpaGraph, property);
			return value;
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}			
	}

	public static Set<String> getPropertyKeys(JpaGraph jpaGraph, JpaElement jpaElement) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (jpaElement == null) throw BpJpaExceptionFactory.cannotBeNull("jpaElement");
		try {
			return jpaElement.getAsBpJpaElement().getKeys();
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}			
	}

	public static void setProperty(JpaGraph jpaGraph, JpaElement jpaElement, String key, Object value) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (jpaElement == null) throw BpJpaExceptionFactory.cannotBeNull("jpaElement");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");
		try {
			ElementFacade.updateOrCreateProperty(jpaGraph, jpaElement, key, value);
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}			
	}
	
	/////

	private static void updateOrCreateProperty(JpaGraph jpaGraph, JpaElement jpaElement, String key, Object value) {
		BpJpaProperty property = jpaElement.getAsBpJpaElement().getProperty(key);
		if (property == null) {
			BpJpaElement bpJpaElement = jpaElement.getAsBpJpaElement(); 
			property = new BpJpaProperty(key, value, bpJpaElement);
			//jpaGraph.getDamper().persist(jpaGraph, property);
			bpJpaElement.putProperty(property);
			jpaGraph.getBpJpaKeyIndexManager().createKeyIndexedPropertyIfNeeds(property);
		} else {
			property.setValue(value);
			jpaGraph.getBpJpaKeyIndexManager().updateKeyIndexedPropertyIfNeeds(property);
		}
	}	
	
}
