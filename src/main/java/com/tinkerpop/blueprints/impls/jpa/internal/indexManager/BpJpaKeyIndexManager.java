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
package com.tinkerpop.blueprints.impls.jpa.internal.indexManager;

import java.util.List;
import java.util.Set;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaEdge;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaElement;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaProperty;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaVertex;

public interface BpJpaKeyIndexManager {

	void initializeAfterConnecting();
	
	<T extends Element> Set<String> getIndexedKeys(Class<T> elementClass);

	<T extends Element> void createKeyIndex(String key, Class<T> elementClass, @SuppressWarnings("rawtypes") Parameter... indexParameters);

	<T extends Element> void dropKeyIndex(String key, Class<T> elementClass);

	void createKeyIndexedPropertyIfNeeds(BpJpaProperty property);
	
	void removeKeyIndexedPropertyIfNeeds(BpJpaProperty property);

	List<BpJpaEdge> getEdgesIfExists(String key, Object value);

	List<BpJpaVertex> getVerticesIfExists(String key, Object value);

	void removeAllKeyIndexedPropertiesIfNeeds(BpJpaElement bpJpaElement);

	void updateKeyIndexedPropertyIfNeeds(BpJpaProperty property);	
	
}
