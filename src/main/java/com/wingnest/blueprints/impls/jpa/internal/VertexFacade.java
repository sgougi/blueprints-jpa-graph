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

import java.util.ArrayList;
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
import com.wingnest.blueprints.impls.jpa.JpaVertexIterable;
import com.wingnest.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaEdge;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaVertex;

final public class VertexFacade {

	@SuppressWarnings("unused")	private static Logger logger = LoggerFactory.getLogger(VertexFacade.class);

	public static BpJpaVertex create(JpaGraph jpaGraph) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		try {
			BpJpaVertex bpJpaVertex = new BpJpaVertex();
			jpaGraph.getDamper().persist(jpaGraph, bpJpaVertex);
			//System.out.println(("after persist : bpJpaVertex = " + (bpJpaVertex.getVersion() == null ? "null" : bpJpaVertex.getVersion().toString())) ); 
			return bpJpaVertex;
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}			
	}
	
	public static Vertex getVertex(JpaGraph jpaGraph, Object id) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (id == null) throw ExceptionFactory.vertexIdCanNotBeNull();
		try {
			jpaGraph.getDamper().beforeFetch(jpaGraph);
			BpJpaVertex bpJpaVertex = BpJpaVertex.getVertexById(jpaGraph.getRawGraph(), id);
			return bpJpaVertex == null ? null : new JpaVertex(jpaGraph, bpJpaVertex);
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}			
	}

	public static Iterable<Vertex> getVertices(JpaGraph jpaGraph) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		try {
			jpaGraph.getDamper().beforeFetch(jpaGraph);
			return new JpaVertexIterable<Vertex>(jpaGraph, BpJpaVertex.getVertices(jpaGraph.getRawGraph()));
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}			
	}

	public static Iterable<Vertex> getVertices(JpaGraph jpaGraph, String key, Object value) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");
		try {
			jpaGraph.getDamper().beforeFetch(jpaGraph);
			Iterable<BpJpaVertex> rt= jpaGraph.getBpJpaKeyIndexManager().getVerticesIfExists(key, value);
			if ( rt == null ) {
				rt = BpJpaVertex.getVertices(jpaGraph.getRawGraph(), key, value);
			}
			return new JpaVertexIterable<Vertex>(jpaGraph, rt);
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}			
	}

	public static void removeVertex(JpaGraph jpaGraph, JpaVertex jpaVertex) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (jpaVertex == null) throw BpJpaExceptionFactory.cannotBeNull("jpaVertex");
		try {
			BpJpaVertex bpJpaVertex = jpaVertex.getAsBpJpaVertex();
			jpaGraph.getBpJpaKeyIndexManager().removeAllKeyIndexedPropertiesIfNeeds(bpJpaVertex);
			jpaGraph.getDamper().remove(jpaGraph, bpJpaVertex);
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}			
	}

	public static Iterable<Edge> getEdges(JpaGraph jpaGraph, JpaVertex jpaVertex, final Direction direction, String ... labels) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (jpaVertex == null) throw BpJpaExceptionFactory.cannotBeNull("jpaVertex");
		if (direction == null) throw BpJpaExceptionFactory.cannotBeNull("direction");
		try {
			jpaGraph.getDamper().beforeFetch(jpaGraph);
			BpJpaVertex bpJpaVertex = jpaVertex.getAsBpJpaVertex();
			if(jpaGraph.getDamper().isObjectDB()) {
				boolean bGraphTest = bpJpaVertex.getVersion() == null && bpJpaVertex.getVersion() > 1; // maybe ObjectDB's bug
				if(bGraphTest) {
					/* noop */
				} else {
					if ( bpJpaVertex.getVersion() != null && bpJpaVertex.getVersion() > 1 ) {
						jpaGraph.getRawGraph().refresh(bpJpaVertex);			
					}				
				}
			} else {
				// EclipseLink, Hibernate
				/* noop */			
			}
			List<BpJpaEdge> edges = bpJpaVertex.getEdges(jpaGraph.getRawGraph(), direction, labels);
			return new JpaEdgeIterable(jpaGraph, edges);
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}
	}	

	public static Iterable<Vertex> getVertices(JpaGraph jpaGraph, JpaVertex jpaVertex, Direction direction, String... labels) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (jpaVertex == null) throw BpJpaExceptionFactory.cannotBeNull("jpaVertex");
		if (direction == null) throw BpJpaExceptionFactory.cannotBeNull("direction");	
		try {
			jpaGraph.getDamper().beforeFetch(jpaGraph);
			BpJpaVertex bpJpaVertex = jpaVertex.getAsBpJpaVertex();
			List<BpJpaEdge> edges = bpJpaVertex.getEdges(jpaGraph.getRawGraph(), direction, labels);
			final List<BpJpaVertex> vertices = new ArrayList<BpJpaVertex>();
			for (BpJpaEdge edge : edges) {
				if ( edge.getIncomingVertex().equals(bpJpaVertex) )
					vertices.add(edge.getOutgoingVertex());	
				else
					vertices.add(edge.getIncomingVertex());
			}
			return new JpaVertexIterable(jpaGraph, vertices);
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}
	}

	public static Edge addEdge(JpaGraph jpaGraph, JpaVertex jpaVertex,	String label, Vertex inVertex) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (jpaVertex == null) throw BpJpaExceptionFactory.cannotBeNull("jpaVertex");
		if (label == null) throw BpJpaExceptionFactory.cannotBeNull("label");	
		if (label.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("label");
		if (inVertex == null) throw BpJpaExceptionFactory.cannotBeNull("inVertex");	
		try {
			return	new JpaEdge(jpaGraph,  jpaVertex, inVertex, label);
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}
	}		
	
}
