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

import java.util.List;

import javax.jdo.annotations.Index;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.tinkerpop.blueprints.impls.jpa.JpaGraph;
import com.tinkerpop.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaElement;

/* persistence annotations here are just for ObjectDB */
@Entity
@Index(members={"indexName","keyName","indexValue","elementId"})
public class BpJpaIndexItem extends BpJpaIndexBase {
	
	@OneToOne(cascade=CascadeType.PERSIST, fetch=FetchType.LAZY)
	private BpJpaIndex bpJpaIndex;
    
	private String indexName;
	
	private String keyName;

    private String indexValue;
    
    private Long elementId;
    
    public BpJpaIndexItem() {
	    super();
    }
  
	public BpJpaIndexItem(BpJpaElement element, String key, Object value) {
	    super();
	    if(element == null) throw BpJpaExceptionFactory.cannotBeNull("element");
	    if(key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
	    if(key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
	    if(value == null) throw BpJpaExceptionFactory.cannotBeNull("value");
		this.indexValue = value.toString();
    	setElementId(element);
    	this.keyName = key;	
	}

	public BpJpaIndex getBpJpaIndex() {
		return bpJpaIndex;
	}
    
    public String getKeyName() {
    	return this.keyName;
    }

    public String getIndexValue() {
    	return this.indexValue;
    }
    
    public void setBpJpaIndex(BpJpaIndex bpJpaIndex) {
    	if(bpJpaIndex == null) throw BpJpaExceptionFactory.cannotBeNull("bpJpaIndex");
    	this.indexName = bpJpaIndex.getIndexName();
    	this.bpJpaIndex = bpJpaIndex;
    }

	public static List<BpJpaIndexItem> find(EntityManager entityManager, String indexName, String key, Object value) {
		if (entityManager == null) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if (indexName == null) throw BpJpaExceptionFactory.cannotBeNull("indexName");
		if (indexName.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("indexName");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
    	if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");
    		
		TypedQuery<BpJpaIndexItem> q = entityManager.createNamedQuery("BpJpaIndexItem_find", BpJpaIndexItem.class)
			.setParameter("indexName", indexName)
			.setParameter("keyName", key)
			.setParameter("value", value.toString());
		List<BpJpaIndexItem> rt = q.getResultList();
		return rt;
	}

	public void setElementId(BpJpaElement bpJpaElement) {
		if (bpJpaElement == null) BpJpaExceptionFactory.cannotBeNull("bpJpaElement");
		
		this.elementId = bpJpaElement.getId();
	}
	
	public Long getElementId() {
		return this.elementId;
	}
	
	public static long count(EntityManager entityManager, String indexName, String key, Object value) {
		if (entityManager == null) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if (indexName == null) throw BpJpaExceptionFactory.cannotBeNull("indexName");
		if (indexName.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("indexName");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
    	if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");
    		
		Query q = entityManager.createNamedQuery("BpJpaIndexItem_count")
			.setParameter("indexName", indexName)
			.setParameter("keyName", key)
			.setParameter("value", value.toString());

		Object obj = q.getSingleResult();
		if (obj == null) return 0;
		long count = ((Number)obj).longValue();
		return count;
	}

	public static void remove(JpaGraph jpaGraph, String indexName, String key, Object value, Object id) {
		if (jpaGraph == null) throw BpJpaExceptionFactory.cannotBeNull("jpaGraph");
		if (indexName == null) throw BpJpaExceptionFactory.cannotBeNull("indexName");
		if (indexName.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("indexName");
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("key");
    	if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");
    		
		EntityManager entityManager = jpaGraph.getRawGraph(); 
		TypedQuery<BpJpaIndexItem> q = entityManager.createNamedQuery("BpJpaIndexItem_findByRemove", BpJpaIndexItem.class)
				.setParameter("indexName", indexName)
				.setParameter("keyName", key)
				.setParameter("value", value.toString())
				.setParameter("elementId", id instanceof Long ? id : Long.parseLong(id.toString()));
		List<BpJpaIndexItem> rt = q.getResultList();
		for ( BpJpaIndexItem item : rt ) {
			jpaGraph.getDamper().remove(jpaGraph, item);
		}
	}	

}
