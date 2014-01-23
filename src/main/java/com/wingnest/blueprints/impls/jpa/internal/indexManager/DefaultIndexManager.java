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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.CloseableIterable;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.wingnest.blueprints.impls.jpa.JpaEdgeIterable;
import com.wingnest.blueprints.impls.jpa.JpaGraph;
import com.wingnest.blueprints.impls.jpa.JpaIndex;
import com.wingnest.blueprints.impls.jpa.JpaVertexIterable;
import com.wingnest.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaEdge;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaElement;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaVertex;
import com.wingnest.blueprints.impls.jpa.internal.models.index.BpJpaIndex;
import com.wingnest.blueprints.impls.jpa.internal.models.index.BpJpaIndexItem;

public class DefaultIndexManager implements BpJpaIndexManager {
	
	private static Logger logger = LoggerFactory.getLogger(DefaultIndexManager.class);
	
	final private JpaGraph jpaGraph;

	public DefaultIndexManager(JpaGraph jpaGraph) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		this.jpaGraph = jpaGraph;
	}
	
	@Override
	public void initializeAfterConnecting() {
		/* noop */
	}
		
	/////
	
	@Override
	public Iterable<Index<? extends Element>> getIndices() {
		try {
			jpaGraph.getDamper().beforeFetch(jpaGraph);
			List<BpJpaIndex> indices = BpJpaIndex.getIndices(jpaGraph.getRawGraph());
			List<Index<? extends Element>> rt = new ArrayList<Index<? extends Element>>();
			for ( BpJpaIndex idx : indices ) {
				rt.add(new JpaIndex(jpaGraph, idx.getIndexName(), idx.getIndexClass()));
			}
			return rt;
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}		
	}

	@Override
	public <T extends Element> Index<T> createIndex(String indexName, Class<T> indexClass, @SuppressWarnings("rawtypes") Parameter... indexParameters) {
		if (indexName == null) throw BpJpaExceptionFactory.cannotBeNull("indexName");
		if (indexName.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("indexName");
		if (indexClass == null) throw BpJpaExceptionFactory.cannotBeNull("indexClass");
		try {
			BpJpaIndex idx = BpJpaIndex.getIndex(getEntityManager(), indexName);
			if ( idx != null ) {
				throw ExceptionFactory.indexAlreadyExists(indexName);
			}
			idx = new BpJpaIndex(indexName, indexClass);
			jpaGraph.getDamper().persist(jpaGraph,idx);
			return new JpaIndex<T>(jpaGraph, idx.getIndexName(), (Class<T>)idx.getIndexClass());
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}
	}

	@Override
	public <T extends Element> void dropIndex(String indexName) {
		if (indexName == null) throw BpJpaExceptionFactory.cannotBeNull("indexName");
		if (indexName.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("indexName");	
		try {
			BpJpaIndex idx = BpJpaIndex.getIndex(getEntityManager(), indexName);
			if ( idx == null ) 	return;
			jpaGraph.getDamper().remove(jpaGraph, idx);
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}		
	}
	
	@Override
	public <T extends Element> Index<T> getIndex(String indexName,	Class<T> indexClass) {
		if (indexName == null) throw BpJpaExceptionFactory.cannotBeNull("indexName");
		if (indexName.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("indexName");
		if (indexClass == null) throw BpJpaExceptionFactory.cannotBeNull("indexClass");	
		try {
			jpaGraph.getDamper().beforeFetch(jpaGraph);
			BpJpaIndex idx = BpJpaIndex.getIndex(getEntityManager(), indexName);
			if ( idx != null ) {				
				if ( indexClass.equals(idx.getIndexClass()) )  {
					JpaIndex<T> index = new JpaIndex(jpaGraph, idx.getIndexName(), idx.getIndexClass());
					return index;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}
	}	
	
	@Override
	public <T extends Element> void put(JpaIndex<T> jpaIndex, String key, Object value, T element) {
		if (jpaIndex == null) throw BpJpaExceptionFactory.cannotBeNull("jpaIndex");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");	
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");	
		if (element == null) throw BpJpaExceptionFactory.cannotBeNull("element");	
		try {
			EntityManager entityManager = getEntityManager();
			jpaGraph.getDamper().beforeFetch(jpaGraph);
			BpJpaElement  bpJpaElement = BpJpaElement.getElementById(entityManager, element.getId());
			if (bpJpaElement != null) {
				BpJpaIndex idx = BpJpaIndex.getIndex(entityManager, jpaIndex.getIndexName());
				if ( idx != null ) { 
					BpJpaIndexItem indexItem = new BpJpaIndexItem(bpJpaElement, key, value);
					jpaGraph.getDamper().persist(jpaGraph, indexItem);					
					idx.addIndexItems(indexItem);
				}
			}
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}	
	}

	@Override
	public <T extends Element> CloseableIterable<T> get(JpaIndex<T> jpaIndex, String key, Object value) {
		if (jpaIndex == null) throw BpJpaExceptionFactory.cannotBeNull("jpaIndex");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");	
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");	
		try {
			jpaGraph.getDamper().beforeFetch(jpaGraph); 
			EntityManager entityManager = getEntityManager();
			List<BpJpaIndexItem> indexItems = BpJpaIndexItem.find(entityManager, jpaIndex.getIndexName(), key, value); 
			if ( Vertex.class.isAssignableFrom(jpaIndex.getIndexClass()) ) {
				List<BpJpaVertex> vertices = new ArrayList<BpJpaVertex>();
				for (BpJpaIndexItem item : indexItems) {
					Long elementId = item.getElementId();
					BpJpaElement e = BpJpaElement.getElementById(entityManager, elementId); 
					if ( e instanceof BpJpaVertex ) {
						vertices.add((BpJpaVertex)e);
					}
				}
				return new JpaVertexIterable(jpaGraph, vertices); 
			} else if ( Edge.class.isAssignableFrom(jpaIndex.getIndexClass()) ) {
				List<BpJpaEdge> edges = new ArrayList<BpJpaEdge>();
				for (BpJpaIndexItem item : indexItems) {
					Long elementId = item.getElementId();
					BpJpaElement e = BpJpaElement.getElementById(entityManager, elementId); 
					if ( e instanceof BpJpaEdge ) {
						edges.add((BpJpaEdge)e);
					}
				}
				return new JpaEdgeIterable(jpaGraph, edges);
			} else {
				throw new IllegalStateException("bug?"); 
			}
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}	
	}

	@Override
	public <T extends Element> void remove(JpaIndex<T> jpaIndex, String key, Object value, T element) {
		if (jpaIndex == null) throw BpJpaExceptionFactory.cannotBeNull("jpaIndex");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");	
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");	
		if (element == null) throw BpJpaExceptionFactory.cannotBeNull("element");	
		try {
			BpJpaIndexItem.remove(jpaGraph, jpaIndex.getIndexName(), key, value, element.getId());
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}
	}

	@Override
	public <T extends Element> long count(JpaIndex<T> jpaIndex, String key, Object value) {
		if (jpaIndex == null) throw BpJpaExceptionFactory.cannotBeNull("jpaIndex");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");	
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");	
		try {
			jpaGraph.getDamper().beforeFetch(jpaGraph);
			long count = BpJpaIndexItem.count(getEntityManager(), jpaIndex.getIndexName(), key, value);
			return count;
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}
	}	

	////
	
	private EntityManager getEntityManager() {
		return jpaGraph.getRawGraph();
	}		
}
