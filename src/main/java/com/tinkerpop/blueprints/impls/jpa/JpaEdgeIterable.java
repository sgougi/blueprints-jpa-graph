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
package com.tinkerpop.blueprints.impls.jpa;

import java.util.ArrayList;
import java.util.Iterator;

import com.tinkerpop.blueprints.CloseableIterable;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaEdge;

final public class JpaEdgeIterable<T extends Edge> implements CloseableIterable<T> {

	final private JpaGraph jpaGraph;
	final private Iterable<BpJpaEdge> inner;

	public JpaEdgeIterable() {
		this(null, new ArrayList<BpJpaEdge>());
	}

	public JpaEdgeIterable(JpaGraph jpaGraph, Iterable<BpJpaEdge> edges) {
		this.inner = edges;
		this.jpaGraph = jpaGraph;
	}

	public Iterator<T> iterator() {
		return new Iterator<T>() {
			final Iterator<BpJpaEdge> it = inner.iterator();
			public boolean hasNext() {
				return it.hasNext();
			}
			@SuppressWarnings("unchecked")
			public T next() {
				return (T)new JpaEdge(jpaGraph, it.next());
			}
			public void remove() {
				it.remove();
			}
		};
	}

	public void close() {
		/* noop */
	}
}
