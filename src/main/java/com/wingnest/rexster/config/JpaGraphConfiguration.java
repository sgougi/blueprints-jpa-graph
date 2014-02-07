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
package com.wingnest.rexster.config;

import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.rexster.config.GraphConfiguration;
import com.tinkerpop.rexster.config.GraphConfigurationException;
import com.wingnest.blueprints.impls.jpa.JpaGraph;

public class JpaGraphConfiguration implements GraphConfiguration {

	private static final String TOKEN_PROPERTIES = "properties";
	private static final String TOKEN_JPA_GRAPH_UNIT_NAME = "jpagraph-unit-name";

	@Override
	public Graph configureGraphInstance(Configuration properties) throws GraphConfigurationException {
		String unitName  = properties.getString(TOKEN_JPA_GRAPH_UNIT_NAME);
		Iterator<String> it = properties.getKeys(TOKEN_PROPERTIES);
		Properties props = new Properties();
		while (it.hasNext()) {
			String key = it.next();
			String newkey = key.substring(TOKEN_PROPERTIES.length() + 1).replaceAll("\\.\\.", ".");
			props.put(newkey, properties.getString(key));
		}
		return new JpaGraph(unitName, props); 
	}

}
