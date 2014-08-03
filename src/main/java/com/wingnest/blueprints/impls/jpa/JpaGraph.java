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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Features;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.IndexableGraph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.MetaGraph;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.DefaultGraphQuery;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.wingnest.blueprints.impls.jpa.exceptions.JpaGraphException;
import com.wingnest.blueprints.impls.jpa.internal.EdgeFacade;
import com.wingnest.blueprints.impls.jpa.internal.VertexFacade;
import com.wingnest.blueprints.impls.jpa.internal.dampers.Damper;
import com.wingnest.blueprints.impls.jpa.internal.indexManager.BpJpaIndexManager;
import com.wingnest.blueprints.impls.jpa.internal.indexManager.BpJpaKeyIndexManager;
import com.wingnest.blueprints.impls.jpa.internal.indexManager.DefaultIndexManager;
import com.wingnest.blueprints.impls.jpa.internal.indexManager.DefaultKeyIndexManager;
import com.wingnest.blueprints.impls.jpa.internal.wrappers.EntityManagerFactoryWrapper;

public class JpaGraph implements 
	MetaGraph<EntityManager>, TransactionalGraph , IndexableGraph, KeyIndexableGraph {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static final Set<JpaGraph> weakJpaGraphs = Collections.newSetFromMap(new WeakHashMap());
	
	private static final Features FEATURES = new Features();
	static {
		FEATURES.supportsSerializableObjectProperty = true;
		FEATURES.supportsBooleanProperty = true;
		FEATURES.supportsDoubleProperty = true;
		FEATURES.supportsFloatProperty = true;
		FEATURES.supportsIntegerProperty = true;
		FEATURES.supportsPrimitiveArrayProperty = true;
		FEATURES.supportsUniformListProperty = true;
		FEATURES.supportsMixedListProperty = true;
		FEATURES.supportsLongProperty = true;
		FEATURES.supportsMapProperty = true;
		FEATURES.supportsStringProperty = true;
		FEATURES.supportsDuplicateEdges = true;
		FEATURES.supportsSelfLoops = true;
		FEATURES.isPersistent = true;
		FEATURES.isWrapper = true;
		FEATURES.supportsVertexIteration = true;
		FEATURES.supportsEdgeIteration = true;
		FEATURES.supportsVertexIndex = true;
		FEATURES.supportsEdgeIndex = true;
		FEATURES.ignoresSuppliedIds = true;
		FEATURES.supportsTransactions = true;
		FEATURES.supportsIndices = true;
		FEATURES.supportsKeyIndices = true;
		FEATURES.supportsVertexKeyIndex = true;
		FEATURES.supportsEdgeKeyIndex = true;
		FEATURES.supportsEdgeRetrieval = true;
		FEATURES.supportsVertexProperties = true;
		FEATURES.supportsEdgeProperties = true;
		FEATURES.supportsThreadedTransactions = false; // https://groups.google.com/d/msg/gremlin-users/6ys0OnNPk2s/wxF_TjoZ_S0J
	}

	private static Logger logger = LoggerFactory.getLogger(JpaGraph.class);
	
	private boolean isShutdowned = false;
	private boolean bAutoStartTransaction = true;
		
	////
	
	final private EntityManagerFactoryWrapper entityManagerFactoryWrapper;
	final private BpJpaIndexManager indexManager;
	final private BpJpaKeyIndexManager keyIndexManager;
	final private Set<BeginTransactionHook> beginTransactionHooks;
	final private PersistenceManager pm = new PersistenceManager();
	
	////　constructors
	
    public JpaGraph(final Configuration configuration) {
    	this(new EntityManagerFactoryWrapper(configuration));
	}
	
	public JpaGraph() {
		this(new EntityManagerFactoryWrapper());
	}
		
	public JpaGraph(String persistanceUnitName) {
		this(new EntityManagerFactoryWrapper(persistanceUnitName));
	}
		
	public JpaGraph(String persistanceUnitName, @SuppressWarnings("rawtypes") Map propertiesForEntityManager) {
		this(new EntityManagerFactoryWrapper(persistanceUnitName, propertiesForEntityManager));
	}
	
	public JpaGraph(@SuppressWarnings("rawtypes") Map propertiesForEntityManager) {
		this(new EntityManagerFactoryWrapper(null, propertiesForEntityManager));
	}	
	
	public JpaGraph(EntityManagerFactory entityManagerFactory, @SuppressWarnings("rawtypes") Map propertiesForEntityManager) {
		this(new EntityManagerFactoryWrapper(entityManagerFactory));
	}
	
	public JpaGraph(EntityManagerFactory entityManagerFactory) {
		this(entityManagerFactory, null);
	}
	
	protected JpaGraph(
			EntityManagerFactoryWrapper entityManagerFactoryWrapper
		) {
		this.entityManagerFactoryWrapper = entityManagerFactoryWrapper;		
		this.beginTransactionHooks = new HashSet<BeginTransactionHook>();
		this.indexManager = new DefaultIndexManager(this);
		this.keyIndexManager = new DefaultKeyIndexManager(this);
		try {
			this.indexManager.initializeAfterConnecting();
			this.keyIndexManager.initializeAfterConnecting();
			weakJpaGraphs.add(this);
		} catch (Exception e) {
			throw new JpaGraphException(e);
		}	
	}
				
	public static List<JpaGraph> getCurrentJpaGraphs() {
		return new ArrayList<JpaGraph>(weakJpaGraphs);
	}		
	
	/////////////////////////////

	public static interface BeginTransactionHook {
		void beginTransaction();
	}
	
	public void addBeginTransactionHook(BeginTransactionHook beginTransactionHook) {
		beginTransactionHooks.add(beginTransactionHook);
	}
	
	/////////////////////////////
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName().toLowerCase();
	}
	
	public BpJpaIndexManager getBpJpaIndexManager() {
		return indexManager;
	}
	
	public BpJpaKeyIndexManager getBpJpaKeyIndexManager() {
		return keyIndexManager;
	}
	
	public Damper getDamper() {
		return this.entityManagerFactoryWrapper.getDamper();
	}
	
	// MetaGraph
	
	public EntityManager getRawGraph() {
		return pm.em();
	}	
	
	// TransactionalGraph

	public Edge addEdge(Object id, Vertex outVertex, Vertex inVertex, String label) {
		if (label == null) throw ExceptionFactory.edgeLabelCanNotBeNull();
		autoStartTransaction();
		return new JpaEdge(this, outVertex, inVertex, label);
	}

	public Vertex addVertex(Object id) {
		autoStartTransaction();
		return new JpaVertex(this);
	}

	public Edge getEdge(Object id) {
        if (null == id) throw ExceptionFactory.edgeIdCanNotBeNull();		
		return EdgeFacade.getEdge(this, id);
	}

	public Iterable<Edge> getEdges() {
		autoStartTransaction();
		return EdgeFacade.getEdges(this);		
	}

	public Iterable<Edge> getEdges(String key, Object value) {
		autoStartTransaction();
		return EdgeFacade.getEdges(this, key, value);		
	}

	public Features getFeatures() {
		return FEATURES;
	}

	public Vertex getVertex(Object id) {
        if (null == id) throw ExceptionFactory.vertexIdCanNotBeNull();		
		autoStartTransaction();
		return VertexFacade.getVertex(this, id);
	}

	public Iterable<Vertex> getVertices() {
		autoStartTransaction();
		return VertexFacade.getVertices(this);
	}

	public Iterable<Vertex> getVertices(String key, Object value) {
		autoStartTransaction();
		return VertexFacade.getVertices(this, key, value);		
	}

	public GraphQuery query() {
		return new DefaultGraphQuery(this);
	}

	public void removeEdge(Edge edge) {
		autoStartTransaction();		
		EdgeFacade.removeEdge(this, (JpaEdge)edge);
	}

	public void removeVertex(Vertex vertex) {
		autoStartTransaction();
		VertexFacade.removeVertex(this, (JpaVertex)vertex);
	}
	
	public void commit() {
		pm.commit();
	}

	public void rollback() {
		pm.rollback();
	}

	public void shutdown() {
		pm.shutdown();
	}

	public void clear() { /* for testing */
		pm.clear();		
	}

	public void autoStartTransaction() {
		pm.autoStartTransaction();
	}	

	public void begin() {
		if (!this.bAutoStartTransaction) throw new IllegalStateException("bAutoStartTransaction is false");
		pm.beginTransaction();
	}
	
	public void setAutoStartTransaction(boolean b) {
		this.bAutoStartTransaction = b;
	}
	
	@Deprecated
	public void stopTransaction(Conclusion conclusion) {
		if (Conclusion.SUCCESS.equals(conclusion)) {
			commit();
		} else {
			rollback();
		}
	}

	// IndexableGraph
	
	public <T extends Element> Index<T> createIndex(String indexName, Class<T> indexClass, @SuppressWarnings("rawtypes") Parameter... indexParameters) {
		logger.debug("create Index:" + indexName);
		autoStartTransaction();
		return indexManager.createIndex(indexName, indexClass, indexParameters);
	}

	public <T extends Element> Index<T> getIndex(String indexName,	Class<T> indexClass) {
		logger.debug("get Index:" + indexName);
		autoStartTransaction();		
		return indexManager.getIndex(indexName, indexClass);
	}

	public Iterable<Index<? extends Element>> getIndices() {
		autoStartTransaction();
		return indexManager.getIndices();
	}

	public void dropIndex(String indexName) {
		autoStartTransaction();
		indexManager.dropIndex(indexName);
	}

	// KeyIndexableGraph
	
	public <T extends Element> void dropKeyIndex(String key, Class<T> elementClass) {
		autoStartTransaction();
		keyIndexManager.dropKeyIndex(key, elementClass);
	}

	public <T extends Element> void createKeyIndex(String key, Class<T> elementClass, @SuppressWarnings("rawtypes") Parameter... indexParameters) {
		logger.debug("create KeyIndex:" + key);
		autoStartTransaction();
		keyIndexManager.createKeyIndex(key, elementClass, indexParameters);
	}

	public <T extends Element> Set<String> getIndexedKeys(Class<T> elementClass) {
		autoStartTransaction();								
		return keyIndexManager.getIndexedKeys(elementClass);
	}

	////////////////////////////////////////////////////////////////////////////////////////

	private class PersistenceManager {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected final Set<EntityManager> weakEntityManagers = Collections.newSetFromMap(new WeakHashMap());
		protected final ThreadLocal<EntityManager> tlEntityManager = new ThreadLocal<EntityManager>() {
			protected EntityManager initialValue() {
				return null;
			}
		};

		public EntityManager createEntityManager() {
			logger.debug("createEntityManager");
			EntityManager em = entityManagerFactoryWrapper.createEntityManager();
			weakEntityManagers.add(em);
			return em;
		}

		public void clear() { /* for testing */
			logger.debug("clear db");
			EntityManager entityManager = createEntityManager();
			EntityTransaction tx = entityManager.getTransaction();
			tx.begin();		
			try {
				entityManager.createQuery("DELETE FROM BpJpaIndexBase").executeUpdate();
				entityManager.createQuery("DELETE FROM BpJpaEntity").executeUpdate();										
				tx.commit();			
			} catch (RuntimeException e) {
				logger.error("clear db", e);
				e.printStackTrace();
				tx.rollback();
			}  finally {
				entityManager.close();
			}
		}

		public void autoStartTransaction() {
			if ( bAutoStartTransaction && !tx().isActive() ) {
				logger.debug("autoStartTransaction");							
				beginTransaction();
			}			
		}
		
		public void beginTransaction() {
			getDamper().flat();
			tx().begin();
			for (BeginTransactionHook hook : beginTransactionHooks) {
				hook.beginTransaction();
			}		
		}
		
		public void shutdown() {
			if ( isShutdowned )	return;
			logger.debug("shutdown");
			try {
				for ( EntityManager em : weakEntityManagers ) {
					try {
						if ( em.isOpen() ) {
							EntityTransaction et = em.getTransaction();
							if ( et.isActive() ) {
								if (et.getRollbackOnly())
									et.rollback();
								else
									et.commit();
							}
						}				
					} catch ( Exception e ) {
						logger.error(String.format("shutdown EntitityManager : %s", em.toString()), e);
					}
				}
			} finally {
				entityManagerFactoryWrapper.close();
				weakEntityManagers.clear();
				isShutdowned = true;
			}			
		}

		public void rollback() {
			if (!tx().isActive())
				return;
			logger.debug("rollback");
			try {			
				tx().rollback();
			} catch (Exception e) {
				throw new JpaGraphException(e);			
			} finally {
				closeEntityManager();
			}			
		}

		public void commit() {
			if (!tx().isActive())	
				return;
			logger.debug("commit");
			try {
				tx().commit();
			} catch (Exception e) {
				throw new JpaGraphException(e);
			} finally {
				closeEntityManager();
			}			
		}

		private EntityManager em() {
			EntityManager entityManager = tlEntityManager.get();
			if ( entityManager == null || !entityManager.isOpen() ) {
				entityManager = createEntityManager();
				tlEntityManager.set(entityManager);
			}
			return entityManager;
		}

		private EntityTransaction tx() {
			return em().getTransaction();
		}

		private void closeEntityManager() {
			if ( tlEntityManager.get() != null ) {
				try {
					EntityManager em = em();
					if ( em != null ) {
						em.close();
						weakEntityManagers.remove(em);
					}
				} catch ( Exception e ) {
					logger.error(e.getMessage(), e);
				}
				tlEntityManager.set(null);
			}
		}
	}	
}
