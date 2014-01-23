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

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.wingnest.blueprints.impls.jpa.JpaGraph;
import com.wingnest.blueprints.impls.jpa.JpaGraph.BeginTransactionHook;
import com.wingnest.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaEdge;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaElement;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaProperty;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaVertex;
import com.wingnest.blueprints.impls.jpa.internal.models.ElementType;
import com.wingnest.blueprints.impls.jpa.internal.models.index.BpJpaKeyIndex;
import com.wingnest.blueprints.impls.jpa.internal.models.index.BpJpaKeyIndexedProperty;

public class DefaultKeyIndexManager implements BpJpaKeyIndexManager {

	private static Logger logger = LoggerFactory.getLogger(DefaultKeyIndexManager.class);
	
	final private JpaGraph jpaGraph;

	public DefaultKeyIndexManager(JpaGraph jpaGraph) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		this.jpaGraph = jpaGraph;
	}

	@Override
	public void initializeAfterConnecting() {
		jpaGraph.addBeginTransactionHook(new BeginTransactionHook(){
			@Override
			public void beginTransaction() {
				tlCache.get().initialize();
			}});
	}
	
	/////

	@Override	
	public <T extends Element> Set<String> getIndexedKeys(Class<T> elementClass) {
		if (elementClass == null) throw BpJpaExceptionFactory.indexedKeysCannotAcceptNullArgumentForClass();
		try {
			return tlCache.get().getIndexedKeys(elementClass);
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}
	}
	
	@Override
	public <T extends Element> void createKeyIndex(String key, Class<T> elementClass, @SuppressWarnings("rawtypes") Parameter... indexParameters) {
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		if (elementClass == null) throw BpJpaExceptionFactory.indexedKeysCannotAcceptNullArgumentForClass();		
		if (hasKeyIndex_( key, elementClass )) throw ExceptionFactory.indexAlreadyExists("elementClass = " + elementClass.getName() +  ", key = " + key);
		try {
			BpJpaKeyIndex index = new BpJpaKeyIndex(key, elementClass);
			jpaGraph.getDamper().persist(jpaGraph, index);
			ElementType elementType = ElementType.toElementType(elementClass);
			if (elementType.equals(ElementType.VERTEX)){
				List<BpJpaVertex> vs = BpJpaVertex.getVertices(getEntityManager(), key);
				for (BpJpaVertex v: vs)
					createKeyIndexProperty_(index, key, v);
			} else {
				List<BpJpaEdge> es = BpJpaEdge.getEdges(getEntityManager(), key);
				for (BpJpaEdge e: es)
					createKeyIndexProperty_(index, key, e);
			}
			tlCache.get().initialize();
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}	
	}

	@Override	
	public <T extends Element> void dropKeyIndex(String key, Class<T> elementClass) {
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		if (elementClass == null) throw BpJpaExceptionFactory.indexedKeysCannotAcceptNullArgumentForClass();		
		try {
			BpJpaKeyIndex index = getKeyIndex_(key, ElementType.toElementType(elementClass));
			if ( index != null ) {
				jpaGraph.getDamper().remove(jpaGraph, index);
				tlCache.get().initialize();
			}
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}
	}

	@Override
	public void createKeyIndexedPropertyIfNeeds(BpJpaProperty property) {
		if (property == null) throw new IllegalArgumentException("property must not be null");
		if (property.getElement() == null) throw new IllegalArgumentException("property.getElement() must not be null");		
		try {
			BpJpaKeyIndex index = getKeyIndex_(property.getKey(), property.getElement().getElementType());
			if (index != null) {
				BpJpaKeyIndexedProperty idxProp = new BpJpaKeyIndexedProperty(property);
				jpaGraph.getDamper().persist(jpaGraph, idxProp);
				idxProp.setKeyIndex(index);
			}
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}		
	}

	@Override
	public List<BpJpaVertex> getVerticesIfExists(String key, Object value) {
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");		
		try {
			BpJpaKeyIndex index = getKeyIndex_(key, ElementType.VERTEX);
			if ( index != null ) {
				List<BpJpaVertex> rt = BpJpaKeyIndex.getVerticesByUsingKeyIndex(getEntityManager(), key, value);
				return rt;
			}
			return null;
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}
	}	
	
	@Override
	public List<BpJpaEdge> getEdgesIfExists(String key, Object value) {
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");		
		try {
			BpJpaKeyIndex index = getKeyIndex_(key, ElementType.EDGE);
			if ( index != null ) {
				List<BpJpaEdge> rt = BpJpaKeyIndex.getEdgesByUsingKeyIndex(getEntityManager(), key, value);
				return rt;
			}
			return null;
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}
	}

	@Override
	public void updateKeyIndexedPropertyIfNeeds(BpJpaProperty property) {
		if (property == null) throw BpJpaExceptionFactory.cannotBeNull("property");		
		try {
			BpJpaKeyIndexedProperty keyIndexedProperty = getKeyIndexedProperty_(property);
			if (keyIndexedProperty != null) {
				keyIndexedProperty.setIndexValue(property.getValue());
			}
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}
	}	
	
	@Override
	public void removeAllKeyIndexedPropertiesIfNeeds(BpJpaElement bpJpaElement) {
		if (bpJpaElement == null) throw BpJpaExceptionFactory.cannotBeNull("bpJpaElement");		
		try {
			jpaGraph.getDamper().beforeFetch(jpaGraph);		
			BpJpaKeyIndexedProperty.removeByElementId(jpaGraph.getDamper(), getEntityManager(), bpJpaElement.getId());
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}		
	}	

	@Override
	public void removeKeyIndexedPropertyIfNeeds(BpJpaProperty property) {
		if (property == null) throw BpJpaExceptionFactory.cannotBeNull("property");
		if(!tlCache.get().hasKeyIndex(property.getKey(), property.getElement().getElementType())) return;
		try {
			BpJpaKeyIndexedProperty keyIndexedProperty = getKeyIndexedProperty_(property);
			if (keyIndexedProperty != null) {
				jpaGraph.getDamper().remove(jpaGraph, keyIndexedProperty);
			}			
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}		
	}	
		
	/////

	private <T extends Element> boolean hasKeyIndex_( String key, Class<T> elementClass ) {
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
		if (elementClass == null) throw BpJpaExceptionFactory.cannotBeNull("elementType");		
		try {
			return tlCache.get().hasKeyIndex(key, ElementType.toElementType(elementClass));
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}
	}
	
	private <T extends Element> Set<String> getIndexedKeys_(Class<T> elementClass) {
		if (elementClass == null) throw BpJpaExceptionFactory.indexedKeysCannotAcceptNullArgumentForClass();
		try {
			jpaGraph.getDamper().beforeFetch(jpaGraph);
			EntityManager entityManager = getEntityManager();
			Set<String> indexedKeys = BpJpaKeyIndex.getIndexedKeys(entityManager, (Class<? extends Element>)elementClass);
			return indexedKeys;
		} catch (Exception e) { 
			throw BpJpaExceptionFactory.reformExcepitonIfNeeds(e);
		}
	}
	
	private BpJpaKeyIndexedProperty getKeyIndexedProperty_(BpJpaProperty property) {
		if (property == null) throw BpJpaExceptionFactory.cannotBeNull("property");
		if(!tlCache.get().hasKeyIndex( property.getKey(), property.getElement().getElementType())) return null;
		jpaGraph.getDamper().beforeFetch(jpaGraph);
		return BpJpaKeyIndexedProperty.getKeyIndexedPropertyByPropertyId(getEntityManager(), property.getId());
	}	

	private <T extends Element> BpJpaKeyIndex getKeyIndex_(String key, ElementType elementType) {
		if(!tlCache.get().hasKeyIndex(key, elementType)) return null;
		jpaGraph.getDamper().beforeFetch(jpaGraph);
		BpJpaKeyIndex index = BpJpaKeyIndex.getKeyIndex(getEntityManager(), key, elementType);
		return index;
	}

	private void createKeyIndexProperty_(BpJpaKeyIndex index, String key, BpJpaElement e) {
		BpJpaKeyIndexedProperty ip = new BpJpaKeyIndexedProperty(e.getProperty(key));
		ip.setKeyIndex(index);
		jpaGraph.getDamper().persist(jpaGraph, ip);
		index.addKeyIndexedItems(ip);		
	}
	
	private EntityManager getEntityManager() {
		return jpaGraph.getRawGraph();
	}
	
	protected final ThreadLocal<Cache> tlCache = new ThreadLocal<Cache>() {
		protected Cache initialValue() {
			return new Cache();
		}
	};

	private class Cache {
		private Set<String> vertexIndexedKeys;
		private Set<String> edgeIndexedKeys;
		public void initialize() {
			vertexIndexedKeys = getIndexedKeys_(Vertex.class); 
			edgeIndexedKeys = getIndexedKeys_(Edge.class); 
		}
		public <T extends Element> Set<String> getIndexedKeys(Class<T> elementClass) {
			if ( Vertex.class.isAssignableFrom(elementClass) ) {
				return vertexIndexedKeys;
			} else if ( Edge.class.isAssignableFrom(elementClass) ) {
				return edgeIndexedKeys;
			} else {
				throw BpJpaExceptionFactory.bug(elementClass.getName());
			}
		}
		public <T extends Element> boolean hasKeyIndex(String key, ElementType elementType) {
			if ( ElementType.VERTEX.equals(elementType) ) {
				return vertexIndexedKeys.contains(key);
			} else if ( ElementType.EDGE.equals(elementType) ) {				
				return edgeIndexedKeys.contains(key);
			} else {
				throw BpJpaExceptionFactory.bug(elementType.toString());
			}			
		}
	}	
}
