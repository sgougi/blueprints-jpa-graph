Blueprints JPA Graph
====================

 An implementation of the Blueprints API over JPA

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
* IndexableGraph and KeyIndexableGraph have poor performance.
* Adding massive data at one commit is very slow, then we should divide massive data into a plurality of sub-blocks.
* I am poor in English.

Micro benchmark only as a guide
===============================

|                            | JPA:ObjectDB(*1)| JPA:EclipseLink+Derby|  JPA:Hibernate+Derby |   OrientDB    |   DEX      |
|:---------------------------|----------------|----------------------|----------------------|--------------|-------------:|
|                            | 2.5.0-SNAPSHOT |     2.5.0-SNAPSHOT   |    2.5.0-SNAPSHOT    |  2.4.0(*2)   |   2.4.0(*3)  |
| VertexTestSuite            |    2890.85     |       3650.64        |      7594.04         |  5731.31     |   5051.43    |
| VertexQueryTestSuite       |     825.25     |        749.70        |      1979.19         |  2023.91     |   1482.64    |
| EdgeTestSuite              |    2535.48     |       2176.02        |      5690.72         |  7829.16     |   4416.40    |
| GraphTestSuite             |    6041.01     |       7969.95        |     18427.35         |  7763.33     |   5631.75    |
| GraphQueryTestSuite        |     396.37     |        338.75        |      1585.12         |   938.26     |    633.13    |
| GraphMLReaderTestSuite     |    1854.74     |       1435.05        |      3588.97         |  2193.59     |   2771.08    |
| IndexableGraphTestSuite    |    1735.38     |        438.25        |      3092.67         |  2526.43     |     -        |
| IndexTestSuite             |     490.88     |       1435.05        |      1109.06         |  1299.97     |     -        |
| KeyIndexableGraphTestSuite |    1493.31     |        979.66        |      4930.77         |  1566.96     |     -        |
| TransactionGraphTestSuite  |   16092.23(*4) |      22021.37(*4)    |     34268.88(*4)     |  6050.46     |     -        |

     *1: ObjectDB 2.5.4
     *2: mvn test -Dtest=OrientGraphLightweightEdgesTest
     *3: mvn test -Dtest=DexGraphTest
     *4: JpaGraph.getVertices()/getEdges() are slightly slow.

Installing into the Maven local repository manually
================================================

     % ant install -DskipTests=true

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
 