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
package com.wingnest.blueprints.impls.jpa.internal.models;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wingnest.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;

final public class BpJpaEdge extends BpJpaElement {

	private static Logger logger = LoggerFactory.getLogger(BpJpaEdge.class);
	
	private String label;
	
	private BpJpaVertex outgoingVertex;
	
	private BpJpaVertex incomingVertex;
	
	public BpJpaEdge() {
		super();
	}
	
	public BpJpaEdge(String label) {
		super();
		if (label == null) throw BpJpaExceptionFactory.cannotBeNull("label");
		if (label.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("label");
		this.label = label;		
	}
	
	public String getLabel() {
		return label;
	}

	public ElementType getElementType() {
		return ElementType.EDGE;
	}

	public BpJpaVertex getOutgoingVertex() {
		return outgoingVertex;
	}

	public BpJpaVertex getIncomingVertex() {
		return incomingVertex;
	}

	public void setOutgoingVertex(BpJpaVertex bpJpaVertex) {
		if (bpJpaVertex == null) throw BpJpaExceptionFactory.cannotBeNull("bpJpaVertex");
		this.outgoingVertex = bpJpaVertex;		
	}

	public void setIncomingVertex(BpJpaVertex bpJpaVertex) {
		if (bpJpaVertex == null) throw BpJpaExceptionFactory.cannotBeNull("bpJpaVertex");
		this.incomingVertex = bpJpaVertex; 
	}
		
	public static  List<BpJpaEdge> getEdges(EntityManager entityManager) {
		if (entityManager == null) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		
		TypedQuery<BpJpaEdge> q = entityManager.createNamedQuery("BpJpaEdge_getEdges", BpJpaEdge.class);
		List<BpJpaEdge> es = q.getResultList();
		return es; 
	}
	
	public static List<BpJpaEdge> getEdges(EntityManager entityManager, String key, Object value) {
		if (entityManager == null) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");
		TypedQuery<BpJpaEdge> q = entityManager.createNamedQuery("BpJpaEdge_getEdgesByKey", BpJpaEdge.class).setParameter("keyName", key);
		List<BpJpaEdge> es = q.getResultList();
		Iterator<BpJpaEdge> it = es.iterator();
		while ( it.hasNext() ) {
			BpJpaEdge e = it.next();
			if( !e.getProperty(key).getValue().equals(value) ) {
				it.remove();	
			}
		}
		return es;
	}

	public static List<BpJpaEdge> getEdges(EntityManager entityManager, String key) {
		if (entityManager == null) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		TypedQuery<BpJpaEdge> q = entityManager.createNamedQuery("BpJpaEdge_getEdgesByKey", BpJpaEdge.class).setParameter("keyName", key);
		List<BpJpaEdge> es = q.getResultList();
		return es;	
	}	

	public static BpJpaEdge getEdgeById(EntityManager entityManager, Object id) {
		if (entityManager == null) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if (id == null) throw BpJpaExceptionFactory.cannotBeNull("id");
		try {
			TypedQuery<BpJpaEdge> q = entityManager.createNamedQuery("BpJpaEdge_getEdgeById", BpJpaEdge.class).setParameter("id", (id instanceof Long ? id : Long.parseLong(id.toString()) ));
			List<BpJpaEdge> es = q.getResultList();
			return es.size() == 0 ? null : es.get(0);
		} catch (NumberFormatException e) {
			logger.debug("getEdgeById", e);			
			return null;
		}
	}
		
}
