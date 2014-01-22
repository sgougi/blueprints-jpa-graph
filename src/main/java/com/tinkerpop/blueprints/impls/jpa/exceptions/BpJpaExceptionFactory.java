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
package com.tinkerpop.blueprints.impls.jpa.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BpJpaExceptionFactory {

	private static Logger logger = LoggerFactory.getLogger(BpJpaExceptionFactory.class);
	
	public static IllegalArgumentException cannotBeNull(String param) {
		String mes = String.format("Argument '%s' cannot be null.", param);
		logger.debug(mes, new LoggingStackTrace("for debug"));
		return new IllegalArgumentException(mes);
	}
	
	public static IllegalArgumentException cannotBeEmpty(String param) {
		String mes = String.format("Argument '%s' cannot be empty.", param);
		logger.debug(mes, new LoggingStackTrace("for debug"));
		return new IllegalArgumentException(mes);
	}
	
	public static IllegalArgumentException bug(String message) {
		String mes = String.format("bug? : %s", message);
		logger.debug(mes, new LoggingStackTrace("for debug"));		
		return new IllegalArgumentException(mes);
	}
	
	public static JpaGraphException cannotBeSerialized(Object value) {
		String mes =  String.format("Object value '%s' cannot be serialized", value.toString());
		logger.debug(mes, new LoggingStackTrace("for debug"));		
		return new JpaGraphException(mes);
	}
	
	public static IllegalArgumentException indexedKeysCannotAcceptNullArgumentForClass () {
		String mes = "Indexed keys cannot accept null argument for class";
		logger.debug(mes, new LoggingStackTrace("for debug"));				
		return new IllegalArgumentException(mes);
	}
		
	public static RuntimeException reformExcepitonIfNeeds(Exception org) throws RuntimeException {
		try {
			throw org;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalStateException e) {
			throw e;			
		} catch (JpaGraphException e){
			throw e;
		} catch (Exception e) { 
			throw new JpaGraphException(e);
		}
	}
	
}
