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
package com.wingnest.blueprints.impls.jpa;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.EdgeTestSuite;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphQueryTestSuite;
import com.tinkerpop.blueprints.GraphTestSuite;
import com.tinkerpop.blueprints.IndexTestSuite;
import com.tinkerpop.blueprints.IndexableGraphTestSuite;
import com.tinkerpop.blueprints.KeyIndexableGraphTestSuite;
import com.tinkerpop.blueprints.TestSuite;
import com.tinkerpop.blueprints.TransactionalGraphTestSuite;
import com.tinkerpop.blueprints.VertexQueryTestSuite;
import com.tinkerpop.blueprints.VertexTestSuite;
import com.tinkerpop.blueprints.impls.GraphTest;
import com.tinkerpop.blueprints.util.io.gml.GMLReaderTestSuite;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReaderTestSuite;
import com.tinkerpop.blueprints.util.io.graphson.GraphSONReaderTestSuite;

public class JpaGraphTest extends GraphTest {

	private static final String UNIT_NAME = System.getProperty("blueprints-jpa-graph.test-persistence-unit", "DefaultUnit");

	public void testGraphTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphTestSuite(this));
        printTestPerformance("GraphTestSuite", this.stopWatch());
    }
    
	public void testVertexTestSuite() throws Exception {
        this.stopWatch();
       	doTestSuite(new VertexTestSuite(this));
        printTestPerformance("VertexTestSuite", this.stopWatch());
    }	

    public void testEdgeTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new EdgeTestSuite(this));
        printTestPerformance("EdgeTestSuite", this.stopWatch());
    }

    public void testVertexQueryTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new VertexQueryTestSuite(this));
        printTestPerformance("VertexQueryTestSuite", this.stopWatch());
    }

    public void testGraphQueryTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphQueryTestSuite(this));
        printTestPerformance("GraphQueryTestSuite", this.stopWatch());
    }

    public void testIndexableGraphTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new IndexableGraphTestSuite(this));
        printTestPerformance("IndexableGraphTestSuite", this.stopWatch());
    }

    public void testIndexTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new IndexTestSuite(this));
        printTestPerformance("IndexTestSuite", this.stopWatch());
    }

    public void testKeyIndexableGraphTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new KeyIndexableGraphTestSuite(this));
        printTestPerformance("KeyIndexableGraphTestSuite", this.stopWatch());
    }

    public void testTransactionalGraphTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new TransactionalGraphTestSuite(this));
        printTestPerformance("TransactionGraphTestSuite", this.stopWatch());
    }

    public void testGraphMLReaderTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphMLReaderTestSuite(this));
        printTestPerformance("GraphMLReaderTestSuite", this.stopWatch());
    }

    public void testGraphSONReaderTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GraphSONReaderTestSuite(this));
        printTestPerformance("GraphSONReaderTestSuite", this.stopWatch());
    }

    public void testGMLReaderTestSuite() throws Exception {
        this.stopWatch();
        doTestSuite(new GMLReaderTestSuite(this));
        printTestPerformance("GMLReaderTestSuite", this.stopWatch());
    }
    

    final static Set<String> notSupports = new HashSet<String>();
    
	public JpaGraphTest() {
		super();
		deleteDirectory(testDir(UNIT_NAME + "_test"));		
		deleteDirectory(testDir(UNIT_NAME + "_test_first"));
		deleteDirectory(testDir(UNIT_NAME + "_test_second"));
	}
    
	final Boolean bDetailTestReport = Boolean.parseBoolean(System.getProperty("blueprints-jpa-graph.detail-test-report", "false"));

	@Override
	public void doTestSuite(TestSuite testSuite) throws Exception {
		try {
	        for (Method method : testSuite.getClass().getDeclaredMethods()) {
	            if (method.getName().startsWith("test")) {
	            	if( notSupports.contains(method.getName()) ) {
	            		System.out.println("Skipped : " + method.getName());
	            	} else {
	            		System.out.println("Testing " + method.getName() + "...");
	            		long start = new Date().getTime();
	            		method.invoke(testSuite);
	            		long end = new Date().getTime();
	            		if(bDetailTestReport) System.out.println(String.format("time : %d : %s", end - start, method.getName()));
	        			shutdownAll();	            		
	    	   			clearDB();
	            	}
	            }
	        }
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			shutdownAll();
		}
	}

	private void shutdownAll() {
		for(JpaGraph g:JpaGraph.getCurrentJpaGraphs() ) {
			g.shutdown();
		}
	}

	@Override
	public Graph generateGraph() {
		return generateGraph(UNIT_NAME);
	}

	private void clearDB() {
		JpaGraph graph = (JpaGraph)generateGraph();
		try {
			graph.clear();
			graph.commit();
		} catch(Exception e) {
			e.printStackTrace();				
		} finally {
			graph.shutdown();
		}
	}
	
	public Graph generateGraph(String graphName) {
		Map<String, Object> props = new HashMap<String, Object>();
		if (!graphName.equals(UNIT_NAME)) {
			if(UNIT_NAME.equals("DefaultUnit")) {
				props.put("javax.persistence.jdbc.url", String.format("jdbc:derby:db/DefaultUnit_test_%s;create=true", graphName));
			} else if(UNIT_NAME.equals("EclipseLinkUnit")) {
				props.put("javax.persistence.jdbc.url", String.format("jdbc:derby:db/EclipseLinkUnit_test_%s;create=true", graphName));
			} else if(UNIT_NAME.equals("HibernateUnit")) {
				props.put("javax.persistence.jdbc.url", String.format("jdbc:derby:db/HibernateUnit_test_%s;create=true", graphName));
			} else if(UNIT_NAME.equals("HibernateOGMUnit")) {
				/* nop */
			} else if(UNIT_NAME.equals("ObjectDbUnit")) {
				props.put("javax.persistence.jdbc.url", String.format("objectdb:db/ObjectDbUnit_test_%s/test.odb", graphName));
			} else {
				throw new IllegalStateException("generateGraph: UNIT_NAME = " + UNIT_NAME);
			}
		} else {
			if(UNIT_NAME.equals("DefaultUnit")) {
				props.put("javax.persistence.jdbc.url", String.format("jdbc:derby:db/DefaultUnit_test;create=true"));
			} else if(UNIT_NAME.equals("EclipseLinkUnit")) {
				props.put("javax.persistence.jdbc.url", String.format("jdbc:derby:db/EclipseLinkUnit_test;create=true"));
			} else if(UNIT_NAME.equals("HibernateUnit")) {
				props.put("javax.persistence.jdbc.url", String.format("jdbc:derby:db/HibernateUnit_test;create=true"));
			} else if(UNIT_NAME.equals("HibernateOGMUnit")) {
				/* nop */
			} else if(UNIT_NAME.equals("ObjectDbUnit")) {
				props.put("javax.persistence.jdbc.url", String.format("objectdb:db/ObjectDbUnit_test/test.odb"));
			} else {
				throw new IllegalStateException("generateGraph: UNIT_NAME = " + UNIT_NAME);
			}			
		}
		final File dbDir = testDir(null);
		if(!dbDir.exists()) dbDir.mkdirs();
		return new JpaGraph(props);
	}
   
	@Override
	public void dropGraph(String graphName) {
	}

    private File testDir(String graphName) {
    	return graphName == null || graphName.length() == 0 ? new File("db") :new File("db/" + graphName); 
    }

}
