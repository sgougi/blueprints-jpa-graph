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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

/* persistence annotations here are just for ObjectDB */
@Entity
@SequenceGenerator(name="seq")
public abstract class BpJpaEntity {

	private boolean removed = false; // ObjectDB don't need to index
		
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="seq")
	private Long id;
	
	@Version
	protected Long version = null;

	public Long getId() {		
		return id;
	}

	public void markRemoved() {
		removed = true;
	}

	public boolean isRemoved() {
		return this.removed;
	}
	
	public Long getVersion() {
		return this.version;
	}	

}
