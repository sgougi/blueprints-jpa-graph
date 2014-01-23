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

import java.util.List;

import javax.jdo.annotations.Index;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wingnest.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;
import com.wingnest.blueprints.impls.jpa.exceptions.LoggingStackTrace;
import com.wingnest.blueprints.impls.jpa.internal.dampers.Damper;
import com.wingnest.blueprints.impls.jpa.internal.models.BpJpaProperty;
import com.wingnest.blueprints.impls.jpa.internal.models.ElementType;

/* persistence annotations here are just for ObjectDB */
@Entity
@Index(members={"propertyId","indexValue","keyName","elementType"})
public class BpJpaKeyIndexedProperty extends BpJpaIndexBase {
	
	private static Logger logger = LoggerFactory.getLogger(BpJpaKeyIndexedProperty.class);
		
	@OneToOne(cascade={CascadeType.PERSIST}, fetch=FetchType.LAZY)
	private BpJpaKeyIndex bpJpaKeyIndex;
	
	private Long propertyId;
	
	private String indexValue;
	
	private String elementType;
	
    private String keyName;
    
	@Index private Long elementId;	

	public BpJpaKeyIndexedProperty() {
		super();
	}

	public BpJpaKeyIndexedProperty(BpJpaProperty property) {
		super();
		if (property == null) throw BpJpaExceptionFactory.cannotBeNull("property");
		if (property.getValue() == null ) throw BpJpaExceptionFactory.cannotBeNull("property.getValue()");
		setPropertyIdAndElementId(property);
		setIndexValue(property.getValue());
		setElementType(property.getElement().getElementType());
		setKeyName(property.getKey());
	}

	private void setKeyName(String key) {
		this.keyName = key;		
	}

	private void setElementType(ElementType elementType) {
		this.elementType = elementType.toString();		
	}

	public void setIndexValue(Object value) {
		if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");
		this.indexValue = value.toString();
	}
	
	public String getIndexValue() {
		return indexValue;
	}
	
	@Override
	public String toString() {
		return bpJpaKeyIndex.getKeyName() + ":" + bpJpaKeyIndex.getId() + ":" + this.getId();
	}

	public void setKeyIndex(BpJpaKeyIndex bpJpaKeyIndex) {
		if (bpJpaKeyIndex == null) throw BpJpaExceptionFactory.cannotBeNull("bpJpaKeyIndex");
		
		this.bpJpaKeyIndex = bpJpaKeyIndex; 
	}
	
	public static BpJpaKeyIndexedProperty getKeyIndexedPropertyByPropertyId(EntityManager entityManager, Long propertyId) {
		if (entityManager == null) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if (propertyId == null) throw BpJpaExceptionFactory.cannotBeNull("id");
		
		TypedQuery<BpJpaKeyIndexedProperty> q = entityManager.createNamedQuery("BpJpaKeyIndexedProperty_getKeyIndexedPropertyByPropertyId", BpJpaKeyIndexedProperty.class)
				.setParameter("propertyId", propertyId);
		List<BpJpaKeyIndexedProperty> rt = q.getResultList();
		return rt.size() == 0 ? null : rt.get(0); 
	}	

	public static void removeByElementId(Damper damper, EntityManager entityManager, Long elementId) {
		if (entityManager == null) throw BpJpaExceptionFactory.cannotBeNull("entityManager");
		if (elementId == null) throw BpJpaExceptionFactory.cannotBeNull("elementId");
		
		if( damper.isHibernate() )
			entityManager.createQuery("DELETE FROM BpJpaKeyIndexedProperty p WHERE p.elementId = :elementId").setParameter("elementId", elementId).executeUpdate();
		else
			entityManager.createNamedQuery("BpJpaKeyIndexedProperty_removeByElementId").setParameter("elementId", elementId).executeUpdate();
			
		// Note:  http://stackoverflow.com/questions/20752789/hibernate-4-3-0-final-and-illegal-attempt-to-set-lock-mode-on-a-non-select-query
	}	
		
	////////////
	
	private void setPropertyIdAndElementId(BpJpaProperty property) {
		this.propertyId = property.getId();
		this.elementId = property.getElement().getId();
		if (this.propertyId == null) {
			logger.error("bug :this.propertyId == null", new LoggingStackTrace("BpJpaKeyIndexedProperty#setProperty()"));
			throw BpJpaExceptionFactory.cannotBeNull("propertyId");
		}
	}

}
