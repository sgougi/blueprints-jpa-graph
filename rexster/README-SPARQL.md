Using SPARQL+Rexster with JpaGraph
============

0) Install blueprints-jpa-graph to the local m2 repository as follows:

      % cd ..
      % mvn install -DskipTests=true
      % cd rexster

1) Build

      % ant

2)  Execute rexster_*.sh (unix) or rexster_*.bat (windows) as follows:

      % rexster_EclipseLink -s

3) Access to [http://localhost:8182/doghouse/main/gremlin/jpasailgraph](http://localhost:8182/doghouse/main/gremlin/jpasailgraph ) by your Internet browser, then the Gremlin console will appear on your browser.

4) To load a sample rdf data, input commands as follows:

       gremlin> g.addNamespace('tg','http://tinkerpop.com#')
       ==> null
       gremlin> g.loadRDF(new FileInputStream('sampledata/graph-example-1.ntriple'), 'http://tinkerpop.com#', 'n-triples', null)
       ==> null
       gremlin> g.commit()
       ==> null

Test as follows:

       gremlin> g.v('tg:1').out('tg:knows') 
       ==>v[http://tinkerpop.com#2]
       ==>v[http://tinkerpop.com#4]

5) Access to [http://localhost:8182/graphs/jpasailgraph/tp/sparql?query=SELECT+?y+WHERE+{+tg:1+tg:knows+?x+.+?x+tg:name+?y}](http://localhost:8182/graphs/jpasailgraph/tp/sparql?query=SELECT+?y+WHERE+{+tg:1+tg:knows+?x+.+?x+tg:name+?y} ) as SPARQL , then a JSON text below will appear on your browser:

      {
      	results: [
      		{
      			y: {
      				value: "vadas",
     				type: "http://www.w3.org/2001/XMLSchema#string",
      				kind: "literal",
      				_id: ""vadas"^^<http://www.w3.org/2001/XMLSchema#string>",
      				_type: "vertex"
      			}
      		},
      		{
      			y: {
      				value: "josh",
      				type: "http://www.w3.org/2001/XMLSchema#string",
      				kind: "literal",
      				_id: ""josh"^^<http://www.w3.org/2001/XMLSchema#string>",
      				_type: "vertex"
      			}
      		}
      	],
      	success: true,
      	version: "2.5.0-SNAPSHOT",
      	queryTime: 26.172111
      }

6) For more information, see :

* [https://github.com/tinkerpop/gremlin/wiki/SPARQL-vs.-Gremlin](https://github.com/tinkerpop/gremlin/wiki/SPARQL-vs.-Gremlin) 
* [https://github.com/tinkerpop/rexster/tree/master/rexster-kibbles/sparql-kibble](https://github.com/tinkerpop/rexster/tree/master/rexster-kibbles/sparql-kibble)

