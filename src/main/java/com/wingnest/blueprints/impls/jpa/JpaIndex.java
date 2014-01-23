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
package com.wingnest.blueprints.impls.jpa;

import com.tinkerpop.blueprints.CloseableIterable;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Index;

public class JpaIndex<T extends Element> implements Index<T> {

    protected final String indexName;
    protected final Class<T> indexClass;
    protected final JpaGraph jpaGraph;
    
    public JpaIndex(JpaGraph jpaGraph, String indexName, Class<T> indexClass) {
    	this.indexName = indexName;
    	this.indexClass = indexClass;
    	this.jpaGraph = jpaGraph;
    }

	public String getIndexName() {
		return indexName;
	}

	public Class<T> getIndexClass() {
		return indexClass;
	}

	public void put(String key, Object value, T element) {
		jpaGraph.getBpJpaIndexManager().put(this, key, value, element);
	}

	public CloseableIterable<T> get(String key, Object value) {
		return jpaGraph.getBpJpaIndexManager().get(this, key, value);
	}

	public long count(String key, Object value) {
		return jpaGraph.getBpJpaIndexManager().count(this, key, value);
	}

	public void remove(String key, Object value, T element) {
		jpaGraph.getBpJpaIndexManager().remove(this, key, value, element);
	}

	public CloseableIterable<T> query(String key, Object query) {
		throw new UnsupportedOperationException();
	}	
}
