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
package com.tinkerpop.blueprints.impls.jpa.internal.models.index;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.Index;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaEdge;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaElement;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaVertex;
import com.tinkerpop.blueprints.impls.jpa.internal.models.ElementType;

/* persistence annotations here are just for ObjectDB */
@Entity
@Index(members={"elementType","keyName"})
public class BpJpaKeyIndex extends BpJpaIndexBase {

	private static Logger logger = LoggerFactory.getLogger(BpJpaKeyIndex.class);
	
	private String elementType;
	
    private String keyName;    
    
	@OneToMany(mappedBy = "bpJpaKeyIndex", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true, fetch=FetchType.LAZY)	
	private Set<BpJpaKeyIndexedProperty> keyIndexedItems = new HashSet<BpJpaKeyIndexedProperty>();    
    
    public BpJpaKeyIndex(){
    	super();
    }
    
    public BpJpaKeyIndex(String keyName, Class<? extends Element> elementClass) {
    	super();
    	setKeyName(keyName);
    	setElementType(ElementType.toElementType(elementClass));
    }
	
	public void addKeyIndexedItems(BpJpaKeyIndexedProperty bpJpaKeyIndexedProperty) {
		keyIndexedItems.add(bpJpaKeyIndexedProperty);
	}
	
	public ElementType getElementType() {
		return ElementType.parseElementType(this.elementType);
	}
	
	public void setElementType(ElementType elementType) {
		if (elementType == null) throw BpJpaExceptionFactory.cannotBeNull("elementType");
		this.elementType = elementType.name();
	}
	
    public String getKeyName() {
    	return keyName;
    }
    
    public void setKeyName(String keyName) {
    	if (keyName == null) throw BpJpaExceptionFactory.cannotBeNull("keyName");
    	if (keyName.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("keyName");    
    	this.keyName = keyName;
    }
    
	@Override
	public String toString() {
		return this.elementType + ": " + this.keyName;
	}	
   
	/////
	
	public static Set<String> getIndexedKeys(EntityManager entityManager, Class<? extends Element> elementClass) {
		if (entityManager == null) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if (elementClass == null) throw BpJpaExceptionFactory.cannotBeNull("elementClass");
		
		ElementType elementType = ElementType.toElementType(elementClass);
		TypedQuery<String> q = entityManager.createNamedQuery("BpJpaKeyIndex_getIndexedKeys", String.class).setParameter("elementType", elementType.toString());
		return new HashSet<String>(q.getResultList());
	}

	public static BpJpaKeyIndex getKeyIndex(EntityManager entityManager, String key, ElementType elementType) {
		if (entityManager == null) BpJpaExceptionFactory.cannotBeNull("entityManager");
    	if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
    	if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
    	if (elementType == null) throw BpJpaExceptionFactory.cannotBeNull("elementType");
    	    			
		TypedQuery<BpJpaKeyIndex> q = entityManager.createNamedQuery("BpJpaKeyIndex_getKeyIndex", BpJpaKeyIndex.class)
			.setParameter("keyName", key)
			.setParameter("elementType", elementType.toString());
		List<BpJpaKeyIndex> rt = q.getResultList();
		return rt.size() == 0? null : rt.get(0);
	}

	public static List<BpJpaVertex> getVerticesByUsingKeyIndex(EntityManager entityManager, String key, Object value) {
		if (entityManager == null) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");
		TypedQuery<BpJpaElement> q = entityManager.createNamedQuery("BpJpaKeyIndex_getElementsByUsingKeyIndex", BpJpaElement.class)
				.setParameter("elementType", ElementType.VERTEX.toString())
				.setParameter("keyName", key)
				.setParameter("indexValue", value.toString());

		@SuppressWarnings("unchecked")
		List<BpJpaVertex> rt = (List<BpJpaVertex>)(List<? extends BpJpaElement>)q.getResultList();
		return rt;
	}

	public static List<BpJpaEdge> getEdgesByUsingKeyIndex(EntityManager entityManager, String key, Object value) {
		if (entityManager == null) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");
		TypedQuery<BpJpaElement> q = entityManager.createNamedQuery("BpJpaKeyIndex_getElementsByUsingKeyIndex", BpJpaElement.class)
				.setParameter("elementType", ElementType.EDGE.toString())
				.setParameter("keyName", key)
				.setParameter("indexValue", value.toString());

		@SuppressWarnings("unchecked")
		List<BpJpaEdge> rt = (List<BpJpaEdge>)(List<? extends BpJpaElement>)q.getResultList();
		return rt;
	}	
}
