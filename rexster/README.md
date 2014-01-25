Hello, Rexster!!
==============

0) Install blueprints-jpa-graph to the local m2 repository as follows:

      % cd ..
      % mvn install -DskipTests=true
      % cd gremlin

1) Build

      % ant

2)  Execute rexster_*.sh (unix) or rexster_*.bat (windows) as follows:

      % rexster_ObjectDB -s

3) Access to http://localhost:8182/graphs/jpagraph by your Internet browser, then a JSON text below will appear on your browser:

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

4) And see [https://github.com/tinkerpop/rexster/wiki](https://github.com/tinkerpop/gremlin/wiki) :)

		