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
package com.wingnest.blueprints.impls.jpa.internal.indexManager;

import com.tinkerpop.blueprints.CloseableIterable;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.Parameter;
import com.wingnest.blueprints.impls.jpa.JpaIndex;

public interface BpJpaIndexManager {

	<T extends Element> Iterable<Index<? extends Element>> getIndices();

	<T extends Element> Index<T> createIndex(String indexName, Class<T> indexClass, @SuppressWarnings("rawtypes") Parameter... indexParameters);

	<T extends Element> void dropIndex(String indexName);

	void initializeAfterConnecting();
	
	<T extends Element> Index<T> getIndex(String indexName,	Class<T> indexClass);
	
	<T extends Element>  void put(JpaIndex<T> jpaIndex, String key, Object value, T element);
	
	<T extends Element> CloseableIterable<T> get(JpaIndex<T> jpaIndex, String key, Object value);
	
	<T extends Element> void remove(JpaIndex<T> jpaIndex, String key, Object value, T element);

	<T extends Element> long count(JpaIndex<T> jpaIndex, String key, Object value);
	
}
