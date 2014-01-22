package com.tinkerpop.blueprints.impls.jpa.performance;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.jpa.JpaGraph;
import com.tinkerpop.blueprints.impls.jpa.performance.models.SimpleEdge;
import com.tinkerpop.blueprints.impls.jpa.performance.models.SimpleVertex;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.FramedTransactionalGraph;

public class PerformanceTest  extends TestCase
{
    public void testCreatingVertices()  {
    	//int[] nums = { 1, 1, 10, 100, 1000, 10000, 100000 };
    	int[] nums = { 1, 1, 10, 100, 1000};
    	for (int num : nums) {
    		createVertices(num);
    	}
    }    
    private void createVertices(int num) {
    	FramedTransactionalGraph<TransactionalGraph> graph = createFramedGraph();
    	try {
        	stopWatch();
        	for (int i = 0; i < num; i++) {
        		SimpleVertex sv = graph.addVertex(null, SimpleVertex.class);
        		sv.setStrAttr("simpleVertex" + i);
        	}        	
        	graph.commit();
        	printTestPerformance(String.format("[testCreatingVertices] %d vertices were created : ", num), stopWatch());
        	if (num < 1000) {
        		assertEquals(num, count(graph.getVertices())); //verify
        		graph.commit();
    		}        	
    	} catch (Exception e) {
    		graph.rollback();    		
    		fail();
    	} finally {
    		graph.shutdown();
    	}    	
    }
    ////////////////////////////////////////////////////////////////////////////////////////////        
    public void testRemovingVertices()  {
    	//int[] nums = { 1, 1, 10, 100, 1000, 10000, 100000 };
    	int[] nums = { 1, 1, 10, 100, 1000};
    	for (int num : nums) {
    		removeVertices(num);
    	}
    }    
    private void removeVertices(int num) {
    	FramedTransactionalGraph<TransactionalGraph> graph = createFramedGraph();
    	try {
        	for (int i = 0; i < num; i++) {
        		SimpleVertex sv = graph.addVertex(null, SimpleVertex.class);
        		sv.setStrAttr("simpleVertex" + i);
        	}        	
        	graph.commit();
        	
        	stopWatch();
        	for ( Vertex v : graph.getVertices()) {
        		SimpleVertex sv = graph.frame(v, SimpleVertex.class);
        		graph.removeVertex(sv.asVertex());
        	}
        	graph.commit();        	
        	printTestPerformance(String.format("[testRemovingVertices] %d vertices were removed : ", num), stopWatch());
			if (num < 1000) {
        		assertEquals(0, count(graph.getVertices())); //verify
        		graph.commit();
			}        	
    	} catch (Exception e) {
    		fail();
    		graph.rollback();
    	} finally {
    		graph.shutdown();
    	}    	
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    public void testCreatingEdges() {
    	//int[] nums = { 1, 1, 10, 100, 1000, 10000, 100000 };
    	int[] nums = { 1, 1, 10, 100, 1000};
    	for (int num : nums) {
    		createEdges(num);
    	}
    }
    private void createEdges(int num) {
    	FramedTransactionalGraph<TransactionalGraph> graph = createFramedGraph();
    	try {
        	stopWatch();
    		{
 	    		SimpleVertex sv1 = graph.addVertex(null, SimpleVertex.class);
	    		SimpleVertex sv2 = graph.addVertex(null, SimpleVertex.class);

	        	for (int i = 0; i < num; i++) {
	        		SimpleEdge se = graph.addEdge(null, sv1.asVertex(), sv2.asVertex(), "edge", SimpleEdge.class);
	        		se.setStrAttr("simpleEdge" + i);
	        	}        		
        	}
        	graph.commit();
        	printTestPerformance(String.format("[testCreatingEdges] %d edges were created : ", num), stopWatch());
			if ( num < 1000 ) {
				assertEquals(num, count(graph.getEdges())); //verify
				graph.commit();
			}        	
    	} catch (Exception e) {
    		graph.rollback();    		
    		fail();
    	} finally {
    		graph.shutdown();
    	}     	
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    public void testRemovingEdges()  {
    	//int[] nums = { 1, 1, 10, 100, 1000, 10000, 100000 };
    	int[] nums = { 1, 1, 10, 100, 1000};

    	for (int num : nums) {
    		removeEdges(num);
    	}
    }
    private void removeEdges(int num) {
       	FramedTransactionalGraph<TransactionalGraph> graph = createFramedGraph();
    	try {
    		Object sv1Id;
    		{
 	    		SimpleVertex sv1 = graph.addVertex(null, SimpleVertex.class);
 	    		sv1Id = sv1.asVertex().getId();
	    		SimpleVertex sv2 = graph.addVertex(null, SimpleVertex.class);

	        	for (int i = 0; i < num; i++) {
	        		SimpleEdge se = graph.addEdge(null, sv1.asVertex(), sv2.asVertex(), "edge", SimpleEdge.class);
	        		se.setStrAttr("simpleEdge" + i);
	        	}        		
        	}
    		graph.commit();
    		
        	stopWatch();
        	{
        		SimpleVertex sv1 = graph.frame( graph.getVertex(sv1Id), SimpleVertex.class );
        		assertNotNull(sv1);
        		for ( Edge e : sv1.asVertex().getEdges(Direction.OUT, "edge") ){
        			SimpleEdge se = graph.frame(e, SimpleEdge.class);
        			graph.removeEdge(se.asEdge());
        		}
        	}
        	graph.commit();
        	printTestPerformance(String.format("[testRemovingEdges] %d edges were removed : ", num), stopWatch());
			if ( num < 1000 ) {
        		assertEquals(0, count(graph.getEdges())); //verify
        		graph.commit();
			}        	
    	} catch (Exception e) {
    		e.printStackTrace();
    		graph.rollback();    		
    		fail();
    	} finally {
    		graph.shutdown();
    	}     	
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    public void testCreateTriples() {
    	//int[] nums = { 1, 1, 10, 100, 1000, 10000, 100000 };
    	int[] nums = { 1, 1, 10, 100, 1000};
    	for (int num : nums) {
    		createTriples(num);
    	}
    }
    private void createTriples(int num) {
    	FramedTransactionalGraph<TransactionalGraph> graph = createFramedGraph();
    	try {
        	stopWatch();
        	for (int i = 0; i < num; i++) {
        		SimpleVertex sv1 = graph.addVertex(null, SimpleVertex.class);
        		sv1.setStrAttr("simpleVertex" + i);
        		SimpleVertex sv2 = graph.addVertex(null, SimpleVertex.class);
        		sv2.setStrAttr("simpleVertex" + i);
        		SimpleEdge se = graph.addEdge(null, sv1.asVertex(), sv2.asVertex(), "edge", SimpleEdge.class);
        		se.setStrAttr("simpleEdge" + i);
        	}        	
        	graph.commit();
        	printTestPerformance(String.format("[testCreateTriples] %d triples were created : ", num), stopWatch());
			if( num < 1000 ) {
        		assertEquals(num * 2, count(graph.getVertices())); //verify
	        	assertEquals(num, count(graph.getEdges())); //verify
	        	graph.commit();
			}        	
    	} catch (Exception e) {
    		graph.rollback();    		
    		fail();
    	} finally {
    		graph.shutdown();
    	} 
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    public void testFindingVertexByUsingIndex() {
    	//int[] nums = { 1, 1, 10, 100, 1000, 10000, 100000 };
    	int[] nums = { 1, 1, 10, 100, 1000};
    	for (int num : nums) {
    		findByUsingIndex(num);
    	}
    }
    private void findByUsingIndex(int num) {
    	FramedTransactionalGraph<TransactionalGraph> graph = createFramedGraph();

    	try {
        	((KeyIndexableGraph)graph.getBaseGraph()).createKeyIndex("strAttr", Vertex.class);
        	graph.commit();

        	for (int i = 0; i < num; i++) {
        		SimpleVertex sv = graph.addVertex(null, SimpleVertex.class);
        		sv.setStrAttr("simpleVertex" + i);
        		if((i % 500) == 0)
        			graph.commit();        	        	
        	}
        	graph.commit();
        	
        	stopWatch();
        	for (int i = 0; i < num; i++) {
        		Iterable<Vertex> itr = ((KeyIndexableGraph)graph.getBaseGraph()).getVertices("strAttr", "simpleVertex" + i);
        		Iterator<Vertex> it = itr.iterator();
        		assertNotNull(it.next());  // verify
        		assertFalse(it.hasNext()); // verify
        	}
        	graph.commit();
        	printTestPerformance(String.format("[testFindingVertexByUsingIndex] %d vertices were found : ", num), stopWatch());
			if ( num < 1000 ) {
        		assertEquals(num, count(graph.getVertices())); //verify
        		graph.commit();
			}        	
    	} catch (Exception e) {
    		graph.rollback();    		
    		fail();
    	} finally {
    		graph.shutdown();
    	}
    }        
    ////////////////////////////////////////////////////////////////////////////////////////////
    public PerformanceTest() {
    	super();
    	deleteDir(testDir(UNIT_NAME + "_test_perf"));		
    }
    private static FramedTransactionalGraph<TransactionalGraph> createFramedGraph() {
		FramedGraphFactory factory = new FramedGraphFactory();
		JpaGraph jpaGraph = generateGraph();
		jpaGraph.clear();
		FramedTransactionalGraph<TransactionalGraph> fgraph = factory.create((TransactionalGraph)jpaGraph);
		registerShutdownHook(jpaGraph);
    	return fgraph;
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String UNIT_NAME = System.getProperty("blueprints-jpa-graph.test-persistence-unit", "DefaultUnit");
        
    private double timer = -1.0d;    
    private double stopWatch() {
        if (this.timer == -1.0d) {
            this.timer = System.nanoTime() / 1000000.0d;
            return -1.0d;
        } else {
            double temp = (System.nanoTime() / 1000000.0d) - this.timer;
            this.timer = -1.0d;
            return temp;
        }
    }
    private static void printTestPerformance(String testName, double timeInMilliseconds) {
        System.out.println(testName + timeInMilliseconds + " (msec)");
    }
    private static void deleteDir(final File directory) {
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    deleteDir(file);
                } else {
                    file.delete();
                }
            }
            directory.delete();
        }
        if (directory.exists()) {
            throw new RuntimeException("unable to delete directory " + directory.getAbsolutePath());
        }
    }
    private static int count(final Iterator iterator) {
        int counter = 0;
        while (iterator.hasNext()) {
            iterator.next();
            counter++;
        }
        return counter;
    }
    private static int count(final Iterable iterable) {
        return count(iterable.iterator());
    }
    /////
    private static void registerShutdownHook( final JpaGraph jpaGraph ){
        Runtime.getRuntime().addShutdownHook( new Thread() {
            @Override
            public void run() {
                jpaGraph.shutdown();
            }
        });
    }
    /////
	private static JpaGraph generateGraph() {
		Map<String, Object> props = new HashMap<String, Object>();
		if(UNIT_NAME.equals("DefaultUnit")) {
			props.put("javax.persistence.jdbc.url", String.format("jdbc:derby:db/DefaultUnit_test_perf;create=true"));
		} else if(UNIT_NAME.equals("EclipseLinkUnit")) {
			props.put("javax.persistence.jdbc.url", String.format("jdbc:derby:db/EclipseLinkUnit_test_perf;create=true"));
		} else if(UNIT_NAME.equals("HibernateUnit")) {
			props.put("javax.persistence.jdbc.url", String.format("jdbc:derby:db/HibernateUnit_test_perf;create=true"));
		} else if(UNIT_NAME.equals("ObjectDbUnit")) {
			props.put("javax.persistence.jdbc.url", String.format("objectdb:db/ObjectDbUnit_test_perf/test.odb"));
		} else {
			throw new IllegalStateException("generateGraph: UNIT_NAME = " + UNIT_NAME);
		}
		final File dbDir = testDir(null);
		if(!dbDir.exists()) dbDir.mkdirs();
		return new JpaGraph(UNIT_NAME, props);
	}    
    private static File testDir(String graphName) {
    	return graphName == null || graphName.length() == 0 ? new File("db") :new File("db/" + graphName); 
    }  
}
