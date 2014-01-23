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
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.wingnest.blueprints.impls.jpa.internal.EdgeFacade;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaEdge;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaElement;

public class JpaEdge extends JpaElement implements Edge {

	final private BpJpaEdge bpJpaEdge;
	
	public JpaEdge(JpaGraph jpaGraph, BpJpaEdge bpJpaEdge) {
		super(jpaGraph, bpJpaEdge.getId());
		this.bpJpaEdge = bpJpaEdge;
	}

	public JpaEdge(JpaGraph jpaGraph, Vertex outVertex, Vertex inVertex, String label) {
		super(jpaGraph);
		bpJpaEdge = EdgeFacade.create(jpaGraph, outVertex, inVertex, label);
		this.id = bpJpaEdge.getId();
		if ( this.id == null) throw new IllegalStateException("bug");
	}

	public void remove() {
		jpaGraph.autoStartTransaction();
		EdgeFacade.removeEdge(jpaGraph, this);
	}
	
	public Vertex getVertex(Direction direction) {
		if (Direction.BOTH.equals(direction)) throw ExceptionFactory.bothIsNotSupported();
		jpaGraph.autoStartTransaction();
		return EdgeFacade.getVertex(jpaGraph, this, direction);
	}

	public String getLabel() {
		jpaGraph.autoStartTransaction();	
		return bpJpaEdge.getLabel();
	}

	@Override
	public BpJpaElement getAsBpJpaElement() {
		return bpJpaEdge;
	}

	public BpJpaEdge getAsBpJpaEdge() {
		return bpJpaEdge;
	}	
}
