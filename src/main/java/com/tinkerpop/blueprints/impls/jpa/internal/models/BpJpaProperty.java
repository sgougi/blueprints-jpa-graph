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

import javax.jdo.annotations.Index;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.tinkerpop.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;
import com.tinkerpop.blueprints.impls.jpa.internal.utils.EncodeUtil;

/* persistence annotations here are just for ObjectDB */
@Entity
final public class BpJpaProperty extends BpJpaEntity {

	private byte[] valueData;
	
	@Index private String keyName;

	@OneToOne(cascade=CascadeType.PERSIST)	
	private BpJpaElement element;
	
	public BpJpaProperty() {
		super();
	}
	
	public BpJpaProperty(String key, Object value, BpJpaElement element) {
		super();
		if (key == null) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (key.length() == 0) throw BpJpaExceptionFactory.cannotBeNull("key");
		if (value == null) throw BpJpaExceptionFactory.cannotBeNull("value");
		this.keyName = key;
		setElement(element);
		setValue(value);
	}

	public Object getValue() {
		return EncodeUtil.decodeValue(valueData);
	}
	
	public BpJpaElement getElement() {
		return this.element;
	}	
	
	public void setElement(BpJpaElement element) {
		if ( element == null ) throw BpJpaExceptionFactory.cannotBeNull("element");
		this.element = element;
	}

	public String getKey() {
		return keyName;
	}

	@Override
	public String toString() {
		return this.keyName;
	}

	public void setValue(Object value) {
		if ( value == null ) throw BpJpaExceptionFactory.cannotBeNull("value");
		this.valueData = EncodeUtil.encodeValue(value);
	}

}
