package com.wingnest.rexster.config;


import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.impls.sail.SailGraph;
import com.tinkerpop.blueprints.oupls.sail.GraphSail;
import com.tinkerpop.rexster.config.GraphConfigurationContext;
import com.tinkerpop.rexster.config.GraphConfigurationException;
import com.wingnest.blueprints.impls.jpa.JpaGraph;

public class JpaSailGraphConfiguration extends JpaGraphConfiguration {

	@Override
	public Graph configureGraphInstance(GraphConfigurationContext confContext) throws GraphConfigurationException {
		final JpaGraph jpaGraph = (JpaGraph)super.configureGraphInstance(confContext);
		GraphSail<KeyIndexableGraph> graphSail = new GraphSail<KeyIndexableGraph>(jpaGraph){};
		graphSail.enforceUniqueStatements(true);
		return new SailGraph(graphSail) {
			@Override
			public void commit() {
				super.commit();			
				jpaGraph.commit();							
			}
			@Override
			public synchronized void shutdown() {
				super.shutdown();
			}
			@Override
			public void rollback() {
				super.rollback();			
				jpaGraph.rollback();
			}
			@Override
			public String toString() {
				return "jpasailgraph"; 
			}
		};
	}

}
