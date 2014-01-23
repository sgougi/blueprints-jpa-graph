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

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.wingnest.blueprints.impls.jpa.exceptions.BpJpaExceptionFactory;

public enum ElementType {

	VERTEX, EDGE;

	public static ElementType toElementType(Class<?> elementClass) {
		if (elementClass == null) throw BpJpaExceptionFactory.cannotBeNull("elementClass");
		 
		if ( Vertex.class.isAssignableFrom(elementClass) ) {
			return ElementType.VERTEX;
		} else if ( Edge.class.isAssignableFrom(elementClass) ) {
			return ElementType.EDGE;
		} else {
			throw BpJpaExceptionFactory.bug(elementClass.getName());
		}
	}

	public static ElementType parseElementType(String elementType) {
		if (elementType == null) throw BpJpaExceptionFactory.cannotBeNull("elementType");
		
		if (ElementType.VERTEX.name().equals(elementType)) {
			return ElementType.VERTEX;
		} else if (ElementType.EDGE.name().equals(elementType)) {
			return ElementType.VERTEX;
		} else {
			throw BpJpaExceptionFactory.bug(elementType);
		}
	}
		
}
