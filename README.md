Blueprints JPA Graph
====================

 An implementation of the Blueprints API over JPA(1).
 
The reason I implemented blueprints-jpa-graph is that I was attracted to the ObjectDB(2)'s performance(3).


* 1:JPA: [http://en.wikipedia.org/wiki/Java_Persistence_API](http://en.wikipedia.org/wiki/Java_Persistence_API)
* 2:ObjectDB : [http://www.objectdb.com/](http://www.objectdb.com/)
* 3:JPA Performance Benchmark : [http://www.jpab.org/All/All/All.html](http://www.jpab.org/All/All/All.html)

Blueprints support
==================

* Support all features, except supportsThreadedTransactions.
* Support [SailGraph](https://github.com/tinkerpop/blueprints/wiki/Sail-Implementation).

JPA support
==================

* ObjectDB
* EclipseLink
* Hibernate

Weak points
========
* IndexableGraph and KeyIndexableGraph have poorer performance than native GraphDBs.
* Adding massive data at one commit is very slow, then we should divide massive data into a plurality of sub-blocks.
* I am poor in English.

Micro benchmark only as a guide
===============================

|                            | JPA:ObjectDB(*1)| JPA:EclipseLink+Derby|  JPA:Hibernate+Derby |   OrientDB    |   DEX      |
|:---------------------------|----------------|----------------------|----------------------|--------------|-------------:|
|                            | 2.5.0-SNAPSHOT |     2.5.0-SNAPSHOT   |    2.5.0-SNAPSHOT    |  2.4.0(*2)   |   2.4.0(*3)  |
| VertexTestSuite            |    3184.87     |       3650.64        |      7594.04         |  5731.31     |   5051.43    |
| VertexQueryTestSuite       |     850.28     |        749.70        |      1979.19         |  2023.91     |   1482.64    |
| EdgeTestSuite              |    2201.05     |       2176.02        |      5690.72         |  7829.16     |   4416.40    |
| GraphTestSuite             |    4392.80     |       7969.95        |     18427.35         |  7763.33     |   5631.75    |
| GraphQueryTestSuite        |     419.23     |        338.75        |      1585.12         |   938.26     |    633.13    |
| GraphMLReaderTestSuite     |    2269.13     |       1435.05        |      3588.97         |  2193.59     |   2771.08    |
| IndexableGraphTestSuite    |    2528.38     |        438.25        |      3092.67         |  2526.43     |     -        |
| IndexTestSuite             |     472.26     |       1435.05        |      1109.06         |  1299.97     |     -        |
| KeyIndexableGraphTestSuite |    1486.62     |        979.66        |      4930.77         |  1566.96     |     -        |
| TransactionGraphTestSuite  |   15300.70(*4) |      22021.37(*4)    |     34268.88(*4)     |  6050.46     |     -        |

     *1: ObjectDB 2.5.4
     *2: mvn test -Dtest=OrientGraphLightweightEdgesTest
     *3: mvn test -Dtest=DexGraphTest
     *4: JpaGraph.getVertices()/getEdges() are slightly slow.

Installing into the Maven local repository manually
================================================

     % ant install -DskipTests=true

Run GraphTest
===============

     % ant test-all

Executing Gremlin
================
 - see [gremlin/README.md](gremlin/README.md)

Maven Repository
==================

     <dependency>
         <groupId>com.tinkerpop.blueprints</groupId>
         <artifactId>blueprints-jpa-graph</artifactId>
         <version>2.5.0-SNAPSHOT</version>
     </dependency>

     Repository: http://www.wingnest.com/mvn-repo/ 

Licence
========
Blueprints JPA Graph is distributed under the [Apache 2 licence](http://www.apache.org/licenses/LICENSE-2.0.html).
 