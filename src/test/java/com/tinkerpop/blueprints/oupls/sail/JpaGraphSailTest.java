package com.tinkerpop.blueprints.oupls.sail;

import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.impls.jpa.JpaGraph;

public class JpaGraphSailTest extends GraphSailTest {

	private JpaGraph jpaGraph = null;
	
	@Override
	protected KeyIndexableGraph createGraph() throws Exception {
		if ( jpaGraph == null )
			jpaGraph = new JpaGraph();
		else
			jpaGraph.clear();
		return jpaGraph;
	}

}
