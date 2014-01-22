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
package com.tinkerpop.blueprints.impls.jpa;

import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.impls.jpa.internal.ElementFacade;
import com.tinkerpop.blueprints.impls.jpa.internal.models.BpJpaElement;
import com.tinkerpop.blueprints.util.ExceptionFactory;

public abstract class JpaElement implements Element {
	
	protected JpaGraph jpaGraph;
	protected Long id;

	public JpaElement(JpaGraph jpaGraph, Long id) {
		this.jpaGraph = jpaGraph;
		this.id = id;
	}
	
	public JpaElement(JpaGraph jpaGraph) {
		this(jpaGraph, null);
	}	
	
	@Override
	public int hashCode(){
	    return new HashCodeBuilder()
	        .append(this.getId())
	        .append(this.getClass().getSimpleName())
	        .toHashCode();
	}

	@Override
	public boolean equals(final Object obj){
	    if (obj instanceof JpaElement){
	        final JpaElement other = (JpaElement) obj;
	        return new EqualsBuilder()
	            .append(getId(), other.getId())
	            .isEquals();
	    } else{
	        return false;
	    }
	}	

	final public void setProperty(String key, Object value) {
		if (key == null) throw ExceptionFactory.propertyKeyCanNotBeNull();
		if (key.length() == 0) throw ExceptionFactory.propertyKeyCanNotBeEmpty();
		if (key.equals("id")) throw ExceptionFactory.propertyKeyIdIsReserved();
		if (value == null) throw ExceptionFactory.propertyValueCanNotBeNull();
		if (this instanceof JpaEdge && key.equals("label")) throw ExceptionFactory.propertyKeyLabelIsReservedForEdges();
		jpaGraph.autoStartTransaction();
		ElementFacade.setProperty(jpaGraph, this, key, value);
	}
	
	final public <T> T getProperty(String key) {
		if (key == null) throw ExceptionFactory.propertyKeyCanNotBeNull();
		if (key.length() == 0) throw ExceptionFactory.propertyKeyCanNotBeEmpty();
		if (key.equals("id")) throw ExceptionFactory.propertyKeyIdIsReserved();
		if (this instanceof JpaEdge && key.equals("label")) throw ExceptionFactory.propertyKeyLabelIsReservedForEdges();
		jpaGraph.autoStartTransaction();	
		return ElementFacade.getProperty(jpaGraph, this, key);
	}

	final public <T> T removeProperty(String key) {
		if (key == null) throw ExceptionFactory.propertyKeyCanNotBeNull();
		if (key.length() == 0) throw ExceptionFactory.propertyKeyCanNotBeEmpty();
		if (key.equals("id")) throw ExceptionFactory.propertyKeyIdIsReserved();		
		jpaGraph.autoStartTransaction();	
		return ElementFacade.removeProperty(jpaGraph, this, key);
	}	
	
	final public Set<String> getPropertyKeys() {
		jpaGraph.autoStartTransaction();
		return ElementFacade.getPropertyKeys(jpaGraph, this);		
	}
	
	final public Object getId() {
		if ( this.id == null) throw new IllegalStateException("bug");
		return this.id;
	}
	
	@Override
	final public String toString() {
		return this.getClass().getName() + " : id = " + this.getId();
	}	
	
	abstract public BpJpaElement getAsBpJpaElement();
}
