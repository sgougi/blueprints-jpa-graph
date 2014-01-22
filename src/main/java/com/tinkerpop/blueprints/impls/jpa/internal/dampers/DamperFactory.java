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
package com.tinkerpop.blueprints.impls.jpa.internal.dampers;

import javax.persistence.EntityManagerFactory;

public class DamperFactory {

	public static Damper create(EntityManagerFactory entityManagerFactory) {
		String className = entityManagerFactory.getClass().getName();
		if( className.startsWith("com.objectdb") ) 
			return new ObjectDBDamper(entityManagerFactory);
		else
			return new DefaultDamper(entityManagerFactory);
	}
	
}
