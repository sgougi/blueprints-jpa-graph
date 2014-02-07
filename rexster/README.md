Hello, Rexster!!
==============

0) Install blueprints-jpa-graph to the local m2 repository as follows:

      % cd ..
      % mvn install -DskipTests=true
      % cd rexster

1) Build

      % ant

2)  Execute rexster_*.sh (unix) or rexster_*.bat (windows) as follows:

      % rexster_EclipseLink -s

3) Access to [http://localhost:8182/graphs/jpagraph](http://localhost:8182/graphs/jpagraph) by your Internet browser, then a JSON text below will appear on your browser:

		{
		　　version: "2.5.0-SNAPSHOT",
		　　name: "jpagraph",
		　　graph: "jpagraph",
		　　features: {
		　　　　ignoresSuppliedIds: true,
		　　　　supportsTransactions: true,
		　　　　supportsSelfLoops: true,
		　　　　supportsEdgeRetrieval: true,
		　　　　supportsBooleanProperty: true,
		　　　　supportsEdgeKeyIndex: true,
		　　　　supportsUniformListProperty: true,
		　　　　supportsThreadedTransactions: false,
		　　　　isPersistent: true,
		　　　　supportsVertexIndex: true,
		　　　　supportsStringProperty: true,
		　　　　supportsIntegerProperty: true,
		　　　　isWrapper: true,
		　　　　supportsMixedListProperty: true,
		　　　　supportsVertexKeyIndex: true,
		　　　　supportsLongProperty: true,
		　　　　supportsVertexIteration: true,
		　　　　supportsEdgeProperties: true,
		　　　　supportsKeyIndices: true,
		　　　　supportsEdgeIteration: true,
		　　　　supportsPrimitiveArrayProperty: true,
		　　　　supportsVertexProperties: true,
		　　　　supportsDoubleProperty: true,
		　　　　supportsSerializableObjectProperty: true,
		　　　　supportsIndices: true,
		　　　　supportsEdgeIndex: true,
		　　　　supportsMapProperty: true,
		　　　　supportsFloatProperty: true,
		　　　　supportsDuplicateEdges: true
		　　},
		　　readOnly: false,
		　　type: "com.wingnest.blueprints.impls.jpa.JpaGraph",
		　　queryTime: 2.952981,
		　　upTime: "0[d]:00[h]:00[m]:11[s]"
		}

4) Access to [http://localhost:8182/doghouse/main/gremlin/jpagraph](http://localhost:8182/doghouse/main/gremlin/jpagraph ) by your Internet browser, then the Gremlin console will appear on your browser.

5) Construct a JpaGraph with a sample data as follows:

               \,,,/
               (o o)
      -----oOOo-(_)-oOOo-----
      gremlin> g = com.wingnest.blueprints.impls.jpa.JpaGraphFactory.createJpaGraph()
      ==>jpagraph
      gremlin>

6) Access to [http://localhost:8182/graphs/jpagraph/vertices](http://localhost:8182/graphs/jpagraph/vertices):

      		{
	      		version: "2.5.0-SNAPSHOT",
      			results: [
		      		{
      					age: 27,
			       		name: "vadas",
       					_id: 2,
			      		_type: "vertex"
      				},
		      		{
	      				name: "ripple",
      					lang: "java",
			      		_id: 5,
			      		_type: "vertex"
			      	},
      				{
			      		age: 29,
			      		name: "marko",
			      		_id: 1,
			      		_type: "vertex"
  					},
  					
		      		<snip/>
      		
	      		],
	       		totalSize: 6,
	       		queryTime: 166.240235
	       	}

7) And see [https://github.com/tinkerpop/rexster/wiki](https://github.com/tinkerpop/rexster/wiki) :)
