Blueprints JPA Graph
====================

 An implementation of the Blueprints API over [JPA](http://en.wikipedia.org/wiki/Java_Persistence_API)(1).
 
The reason I implemented blueprints-jpa-graph is that I was attracted to the ObjectDB(2)'s performance(3).


* 1:JPA: [http://en.wikipedia.org/wiki/Java_Persistence_API](http://en.wikipedia.org/wiki/Java_Persistence_API)
* 2:ObjectDB : [http://www.objectdb.com/](http://www.objectdb.com/)
* 3:JPA Performance Benchmark : [http://www.jpab.org/All/All/All.html](http://www.jpab.org/All/All/All.html)

Support Features
==================

* All Blueprints features support, except [ThreadedTransactions](https://groups.google.com/d/msg/gremlin-users/6ys0OnNPk2s/wxF_TjoZ_S0J)
* [Sail](https://github.com/tinkerpop/blueprints/wiki/Sail-Implementation) support 
* [Rexster](https://github.com/tinkerpop/rexster/wiki) support
* [GraphFactory](https://github.com/tinkerpop/blueprints/wiki/Implementations#graphfactory-support) support

Support JPAs
==================
* [ObjectDB](http://www.objectdb.com/)
* [EclipseLink](http://www.eclipse.org/eclipselink/)  (At this point, I recommend EclipseLink)
* [Hibernate](http://hibernate.org/)

Weak points
========
* IndexableGraph and KeyIndexableGraph have poorer performance than native GraphDBs.
* In case of using EclipseLink and Hibernate, adding massive data at one commit is very slow, then we should divide massive data into a plurality of sub-blocks.
* I am poor in English.

Micro benchmark only as a guide
===============================


|                            |   [OrientDB](http://www.orientechnologies.com/orientdb/)   | JPA:ObjectDB(*1)| JPA:EclipseLink+Derby|  JPA:Hibernate+Derby |   DEX        |
|:---------------------------|--------------|-----------------|----------------------|----------------------|-------------:|
|                            |  2.4.0(*2)   |  2.5.0-SNAPSHOT |     2.5.0-SNAPSHOT   |    2.5.0-SNAPSHOT    |   2.4.0(*3)  |
| VertexTestSuite            |  5731.31     |     3184.87     |       3650.64        |      7594.04         |   5051.43    |
| VertexQueryTestSuite       |  2023.91     |      850.28     |        749.70        |      1979.19         |   1482.64    |
| EdgeTestSuite              |  7829.16     |     2201.05     |       2176.02        |      5690.72         |   4416.40    |
| GraphTestSuite             |  7763.33     |     4392.80     |       7969.95        |     18427.35         |   5631.75    |
| GraphQueryTestSuite        |   938.26     |      419.23     |        338.75        |      1585.12         |    633.13    |
| GraphMLReaderTestSuite     |  2193.59     |     2269.13     |       1435.05        |      3588.97         |   2771.08    |
| IndexableGraphTestSuite    |  2526.43     |      472.26     |        438.25        |      3092.67         |     -        |
| IndexTestSuite             |  1299.97     |     2528.38     |       1435.05        |      1109.06         |     -        |
| KeyIndexableGraphTestSuite |  1566.96     |     1486.62     |        979.66        |      4930.77         |     -        |
| TransactionGraphTestSuite  |  6050.46     |    15300.70(*4) |      22021.37(*4)    |     34268.88(*4)     |     -        |
(msec)

     *1: ObjectDB 2.5.4
     *2: mvn test -Dtest=OrientGraphLightweightEdgesTest
     *3: mvn test -Dtest=DexGraphTest
     *4: JpaGraph.getVertices()/getEdges() are slightly slow.

Testing and Installing
======================

#### Installing into the Maven local repository manually

     % mvn install -DskipTests=true

#### Running [Property Graph Model Test Suite](https://github.com/tinkerpop/blueprints/wiki/Property-Graph-Model-Test-Suite)


     % ant test-all

Samples
========
These will probably be good references for that you integrate into your system with JpaGraph.

#### Running [Gremlin](https://github.com/tinkerpop/gremlin/wiki) with JpaGraph

* see [gremlin/README.md](gremlin/README.md)

#### Running [Rexster](https://github.com/tinkerpop/rexster/wiki) with JpaGraph

* see [rexster/README.md](rexster/README.md)

Integration
===========

#### Constracting a JpaGraph in Java code

* see [wiki/Constracting-a-JpaGraph-in-Java-code](https://github.com/sgougi/blueprints-jpa-graph/wiki/Constracting-a-JpaGraph-in-Java-code)

#### Sample Maven Dependencies

* see [wiki/Sample-Maven-Dependencies](https://github.com/sgougi/blueprints-jpa-graph/wiki/Sample-Maven-Dependencies)


#### Using GraphFactory

If using [GraphFactory](https://github.com/tinkerpop/blueprints/wiki/Code-Examples#use-graphfactory) to instantiate a JpaGraph, the following properties will apply:

|            Key                                                                         |   Description                         |
|:---------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------:|
|           blueprints.jpagraph.unit-name    |     set  a persistance unit name:  'EclipseLinkUnit', 'HibernateUnit', or  'ObjectDbUnit'          |
|   blueprints.jpagraph.persistence-unit-properties | set persistence unit properties if it is necessary   |


Maven Repository
==================

     <dependency>
         <groupId>com.wingnest.blueprints</groupId>
         <artifactId>blueprints-jpa-graph</artifactId>
         <version>2.5.0-SNAPSHOT</version>
     </dependency>

     Repository: http://www.wingnest.com/mvn-repo/ 

Licence
========
Blueprints JPA Graph is distributed under the [Apache 2 licence](http://www.apache.org/licenses/LICENSE-2.0.html).
 
