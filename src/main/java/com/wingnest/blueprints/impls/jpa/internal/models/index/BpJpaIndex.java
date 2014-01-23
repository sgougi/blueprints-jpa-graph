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
package com.wingnest.blueprints.impls.jpa.internal.models.index;

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

import com.tinkerpop.blueprints.Element;
import com.wingnest.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;
import com.wingnest.blueprints.impls.jpa.exceptions.JpaGraphException;

/* persistence annotations here are just for ObjectDB */
@Entity
public class BpJpaIndex extends BpJpaIndexBase {
		
    @Index private String indexName;
    
    private String indexClassName;

	@OneToMany(mappedBy = "bpJpaIndex", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch=FetchType.LAZY, orphanRemoval = true)	
	private Set<BpJpaIndexItem> indexItems = new HashSet<BpJpaIndexItem>(); 
	
    public BpJpaIndex() {
    	super();
    }
    
    public BpJpaIndex(String indexName, Class<? extends Element> indexClass) {
    	super();
    	this.indexName = indexName;
		this.indexClassName = indexClass.getName();  
    }

	public Class<? extends Element> getIndexClass() {
		try {
			return (Class<? extends Element>)Class.forName(indexClassName);
		} catch (ClassNotFoundException e) {
			throw new JpaGraphException(e);
		}
	}

	public String getIndexName() {
		return this.indexName;
	}

	public void addIndexItems(BpJpaIndexItem indexItem) {
		if (indexItem == null) throw BpJpaExceptionFactory.cannotBeNull("indexItem");
		
		indexItems.add(indexItem);
		indexItem.setBpJpaIndex(this);
	}

	////

	public static List<BpJpaIndex> getIndices(EntityManager entityManager) {
		if (entityManager == null) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		
		TypedQuery<BpJpaIndex> q = entityManager.createNamedQuery("BpJpaIndex_getIndices", BpJpaIndex.class);
		List<BpJpaIndex> rt = q.getResultList();
		return rt;
	}

	public static BpJpaIndex getIndex(EntityManager entityManager, String indexName) {
		if (entityManager == null) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if (indexName == null) throw BpJpaExceptionFactory.cannotBeNull("indexName");
		if (indexName.length() == 0) throw BpJpaExceptionFactory.cannotBeEmpty("indexName");
		
		TypedQuery<BpJpaIndex> q = entityManager.createNamedQuery("BpJpaIndex_getIndex", BpJpaIndex.class).setParameter("indexName", indexName);
		List<BpJpaIndex> rt = q.getResultList();
		return rt.size() == 0 ? null : rt.get(0);
	}

}
