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
package com.wingnest.blueprints.impls.jpa.internal;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.wingnest.blueprints.impls.jpa.JpaEdge;
import com.wingnest.blueprints.impls.jpa.JpaEdgeIterable;
import com.wingnest.blueprints.impls.jpa.JpaGraph;
import com.wingnest.blueprints.impls.jpa.JpaVertex;
import com.wingnest.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaEdge;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaVertex;

final public class EdgeFacade {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(EdgeFacade.class);
	
	public static Edge getEdge(JpaGraph jpaGraph, Object id) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (id == null) throw ExceptionFactory.edgeIdCanNotBeNull();
		try {
			jpaGraph.getDamper().beforeFetch(jpaGraph);
			BpJpaEdge bpJpaEdge = BpJpaEdge.getEdgeById(jpaGraph.getRawGraph(), id);
			return bpJpaEdge == null ? null : new JpaEdge(jpaGraph, bpJpaEdge);
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}		
	}
	
	public static Iterable<Edge> getEdges(JpaGraph jpaGraph) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		try {
			jpaGraph.getDamper().beforeFetch(jpaGraph);
			return new JpaEdgeIterable<Edge>(jpaGraph, BpJpaEdge.getEdges(jpaGraph.getRawGraph()));
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}			
	}

	public static BpJpaEdge create(JpaGraph jpaGraph, Vertex outVertex, Vertex inVertex, String label) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (outVertex == null) throw BpJpaExceptionFactory.cannotBeNull("outVertex");
		if (inVertex == null) throw BpJpaExceptionFactory.cannotBeNull("inVertex");
		if (label == null) throw BpJpaExceptionFactory.cannotBeNull("label");
		if (label.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("label");
		try {
			BpJpaEdge bpJpaEdge = new BpJpaEdge(label);
			jpaGraph.getDamper().persist(jpaGraph, bpJpaEdge);
			((JpaVertex)outVertex).getAsBpJpaVertex().addOutgoingEdge(bpJpaEdge);
			((JpaVertex)inVertex).getAsBpJpaVertex().addIncomingEdge(bpJpaEdge);
			return bpJpaEdge;
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}			
	}

	public static Iterable<Edge> getEdges(JpaGraph jpaGraph, String key, Object value) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");	
		try {
			jpaGraph.getDamper().beforeFetch(jpaGraph);		
			List<BpJpaEdge> rt = null;
			if (key.equals("label")) {
				rt = BpJpaEdge.getEdgesByLabel(jpaGraph.getRawGraph(), value);
			} else {
				rt = jpaGraph.getBpJpaKeyIndexManager().getEdgesIfExists(key, value);
				if (rt == null) {
					rt = BpJpaEdge.getEdges(jpaGraph.getRawGraph(), key, value);
				}
			}
			return new JpaEdgeIterable<Edge>(jpaGraph, rt);
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}			
	}

	public static Vertex getVertex(JpaGraph jpaGraph, JpaEdge jpaEdge, Direction direction) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (jpaEdge == null) throw BpJpaExceptionFactory.cannotBeNull("jpaEdge");
		if (direction == null) throw BpJpaExceptionFactory.cannotBeNull("direction");	
		try {
			BpJpaVertex bpJpaVertex = null;
			switch ( direction ) {
			case IN:
				bpJpaVertex  = jpaEdge.getAsBpJpaEdge().getIncomingVertex();
				break;
			case OUT:
				bpJpaVertex  = jpaEdge.getAsBpJpaEdge().getOutgoingVertex();
				break;
			case BOTH:
				throw ExceptionFactory.bothIsNotSupported();
			}
			return new JpaVertex(jpaGraph, bpJpaVertex);
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}			
	}

	public static void removeEdge(JpaGraph jpaGraph, JpaEdge jpaEdge) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (jpaEdge == null) throw BpJpaExceptionFactory.cannotBeNull("jpaEdge");
		try {
			BpJpaEdge edge = jpaEdge.getAsBpJpaEdge();
			jpaGraph.getBpJpaKeyIndexManager().removeAllKeyIndexedPropertiesIfNeeds(edge);			
			edge.getOutgoingVertex().removeOutgoingEdge(edge);
			edge.getIncomingVertex().removeIncomingEdge(edge);
			jpaGraph.getDamper().remove(jpaGraph, edge);
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}			
	}
	
}
