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
package com.tinkerpop.blueprints.impls.jpa.internal.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;

final public class BpJpaVertex extends BpJpaElement {

	private static Logger logger = LoggerFactory.getLogger(BpJpaVertex.class);
	
	private Set<BpJpaEdge> outgoingEdges = new HashSet<BpJpaEdge>(); 
	private Set<BpJpaEdge> incomingEdges = new HashSet<BpJpaEdge>(); 
	
	public BpJpaVertex() {
		super();
	}
	 
	public ElementType getElementType() {
		return ElementType.VERTEX;
	}
	
	public Set<BpJpaEdge> getOutgoingEdges() {
		return this.outgoingEdges;
	}

	public Set<BpJpaEdge> getIncomingEdges() {
		return this.incomingEdges;
	}

	public void addOutgoingEdge(BpJpaEdge edge) {
		if ( edge == null ) throw BpJpaExceptionFactory.cannotBeNull("edge");
		
		this.outgoingEdges.add(edge);
		edge.setOutgoingVertex(this);
	}

	public void addIncomingEdge(BpJpaEdge edge) {
		if ( edge == null ) throw BpJpaExceptionFactory.cannotBeNull("edge");
		
		this.incomingEdges.add(edge);
		edge.setIncomingVertex(this);
	}
	
	public void removeOutgoingEdge(BpJpaEdge edge) {
		if ( edge == null ) throw BpJpaExceptionFactory.cannotBeNull("edge");
		
		this.outgoingEdges.remove(edge);		
	}

	public void removeIncomingEdge(BpJpaEdge edge) {
		if ( edge == null ) throw BpJpaExceptionFactory.cannotBeNull("edge");
		
		this.incomingEdges.remove(edge);				
	}
	
	public List<BpJpaEdge> getEdges(EntityManager entityManager, Direction direction, String... labels) {
		if ( entityManager == null ) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if ( direction == null ) throw BpJpaExceptionFactory.cannotBeNull("direction");
		
		boolean isAllLabel = labels == null || labels.length == 0;
		if (isAllLabel) labels = new String[]{"_dummy_"};
		Set<String> labelSet = new HashSet<String>(Arrays.asList(labels)); 
		boolean getIn = !Direction.OUT.equals(direction);
		boolean getOut = !Direction.IN.equals(direction);
		List<BpJpaEdge> rt = new ArrayList<BpJpaEdge>();
		if ( getIn ) {
			List<BpJpaEdge> es = new ArrayList<BpJpaEdge>();
			for ( BpJpaEdge e : incomingEdges) {
				if (isAllLabel || labelSet.contains(e.getLabel())) {
					if(checkEdges(e)) es.add(e); 
				}
			}
			if ( !getIn ) return es;
			rt.addAll(es);		
		}	
		if ( getOut ) {
			List<BpJpaEdge> es = new ArrayList<BpJpaEdge>();
			for ( BpJpaEdge e : outgoingEdges) {
				if (isAllLabel || labelSet.contains(e.getLabel())) {
					if(checkEdges(e)) es.add(e); 
				}			
			}
			if( !getOut )
				return es;		
			rt.addAll(es);				
		}	
		return rt;		
	}	

	private boolean checkEdges(BpJpaEdge edge) {
		BpJpaVertex vo = edge.getOutgoingVertex();
		BpJpaVertex vi = edge.getIncomingVertex();		
		return vo != null && vi != null && !vo.isRemoved() && !vi.isRemoved() ; 
	}

	public List<BpJpaVertex> getVertices(EntityManager entityManager, Direction direction, String... labels) {
		if ( entityManager == null ) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if ( direction == null ) throw BpJpaExceptionFactory.cannotBeNull("direction");

		boolean isAllLabel = labels == null || labels.length == 0;
		if (isAllLabel) labels = new String[]{"_dummy_"};
		boolean getIn = !Direction.OUT.equals(direction);
		boolean getOut = !Direction.IN.equals(direction);
		List<BpJpaVertex> rt = new ArrayList<BpJpaVertex>();
		if ( getIn ) {
			TypedQuery<BpJpaVertex> q = entityManager.createNamedQuery("BpJpaVertex_getIncomingVertices", BpJpaVertex.class)
					.setParameter("id", this.getId())			
					.setParameter("isAllLabel", isAllLabel)
					.setParameter("labels", Arrays.asList(labels));
			List<BpJpaVertex> vs = q.getResultList();
			rt.addAll(vs);		
		}
		if ( getOut ) {
			TypedQuery<BpJpaVertex> q = entityManager.createNamedQuery("BpJpaVertex_getOutgoingVertices", BpJpaVertex.class)
					.setParameter("id", this.getId())						
					.setParameter("isAllLabel", isAllLabel)
					.setParameter("labels", Arrays.asList(labels));
			List<BpJpaVertex> vs = q.getResultList();
			rt.addAll(vs);		
		}
		return rt;
	}
		
	//////////////////////////////////	
		
	public static List<BpJpaVertex> getVertices(EntityManager entityManager) {
		if ( entityManager == null ) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		
		TypedQuery<BpJpaVertex> q = entityManager.createNamedQuery("BpJpaVertex_getVertices", BpJpaVertex.class);
		try {
			List<BpJpaVertex> vs = q.getResultList();
			return vs;
		} catch (Exception e) {
			logger.error("ObjectDB bug?", e);
			return Collections.EMPTY_LIST;
		}		 
	}

	public static BpJpaVertex getVertexById(EntityManager entityManager, Object id) {
		if ( entityManager == null ) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if ( id == null ) throw BpJpaExceptionFactory.cannotBeNull("id");
		
		try {
			TypedQuery<BpJpaVertex> q = entityManager.createNamedQuery("BpJpaVertex_getVertexById", BpJpaVertex.class).setParameter("id", (id instanceof Long ? id : Long.parseLong(id.toString()) ));
			List<BpJpaVertex> vs = q.getResultList();
			return vs.size() == 0 ? null : vs.get(0);
		} catch (NumberFormatException e) {
			logger.debug("getEdgeById", e);	
			return null;
		}
	}

	public static List<BpJpaVertex> getVertices(EntityManager entityManager, String key, Object value) {
		if ( entityManager == null ) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if ( key == null ) throw BpJpaExceptionFactory.cannotBeNull("key");
		if ( key.length() == 0 ) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		if ( value == null ) throw BpJpaExceptionFactory.cannotBeNull("value");
		TypedQuery<BpJpaVertex> q = entityManager.createNamedQuery("BpJpaVertex_getVerticesByKey", BpJpaVertex.class).setParameter("keyName", key);
		List<BpJpaVertex> vs = q.getResultList();
		Iterator<BpJpaVertex> it = vs.iterator();
		while ( it.hasNext() ) {
			BpJpaVertex v = it.next();
			if( !v.getProperty(key).getValue().equals(value) ) {
				it.remove();	
			}
		}
		return vs;
	}

	public static List<BpJpaVertex> getVertices(EntityManager entityManager, String key) {
		if ( entityManager == null ) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if ( key == null ) throw BpJpaExceptionFactory.cannotBeNull("key");
		if ( key.length() == 0 ) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		TypedQuery<BpJpaVertex> q = entityManager.createNamedQuery("BpJpaVertex_getVerticesByKey", BpJpaVertex.class).setParameter("keyName", key);
		return q.getResultList();
	}	

	
}