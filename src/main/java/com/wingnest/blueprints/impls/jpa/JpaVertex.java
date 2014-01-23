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

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.blueprints.util.DefaultVertexQuery;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.wingnest.blueprints.impls.jpa.internal.VertexFacade;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaElement;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaVertex;

public class JpaVertex extends JpaElement implements Vertex {

	final private BpJpaVertex bpJpaVertex;
		
	public JpaVertex(JpaGraph jpaGraph, BpJpaVertex bpJpaVertex) {
		super(jpaGraph, bpJpaVertex.getId());
		this.bpJpaVertex = bpJpaVertex;
	}

	public JpaVertex(JpaGraph jpaGraph) {
		super(jpaGraph);
		bpJpaVertex = VertexFacade.create(jpaGraph);
		this.id = bpJpaVertex.getId();
		if ( this.id == null ) throw new IllegalStateException("bug");
	}

	public void remove() {
		jpaGraph.autoStartTransaction();
		VertexFacade.removeVertex(jpaGraph, this);
	}

	public Iterable<Edge> getEdges(Direction direction, String... labels) {
		jpaGraph.autoStartTransaction();
		return VertexFacade.getEdges(jpaGraph, this, direction, labels);
	}

	public Iterable<Vertex> getVertices(Direction direction, String... labels) {
		jpaGraph.autoStartTransaction();
		return VertexFacade.getVertices(jpaGraph, this, direction, labels);		
	}

	public VertexQuery query() {
		return new DefaultVertexQuery(this);
	}

	public Edge addEdge(String label, Vertex inVertex) {
		if (label == null) throw ExceptionFactory.edgeLabelCanNotBeNull();
		jpaGraph.autoStartTransaction();
		return VertexFacade.addEdge(jpaGraph, this, label, inVertex);
	}
	
	@Override
	public BpJpaElement getAsBpJpaElement() {
		return bpJpaVertex;
	}	

	public BpJpaVertex getAsBpJpaVertex() {
		return bpJpaVertex;
	}	
}
