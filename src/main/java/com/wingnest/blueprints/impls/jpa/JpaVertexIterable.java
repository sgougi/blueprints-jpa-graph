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

import java.util.ArrayList;
import java.util.Iterator;

import com.tinkerpop.blueprints.CloseableIterable;
import com.tinkerpop.blueprints.Vertex;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaVertex;

final public class JpaVertexIterable<T extends Vertex> implements CloseableIterable<T> {

	final private JpaGraph jpaGraph;
	final private Iterable<BpJpaVertex> inner;
	
	public JpaVertexIterable() {
		this(null, new ArrayList<BpJpaVertex>());
	}

	public JpaVertexIterable(JpaGraph jpaGraph, Iterable<BpJpaVertex> vertices) {
		this.inner = vertices;
		this.jpaGraph = jpaGraph;
	}

	public Iterator<T> iterator() {
		return new Iterator<T>() {
			final Iterator<BpJpaVertex> it = inner.iterator();
			public boolean hasNext() {
				return it.hasNext();
			}
			@SuppressWarnings("unchecked")
			public T next() {
				return (T)new JpaVertex(jpaGraph, it.next());
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
