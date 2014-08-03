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
* Java 5, 6 or 7

Support JPAs
==================

Which one you need depends on what you want to use with it:

* [EclipseLink 2.5.2-](http://www.eclipse.org/eclipselink/)
* [ObjectDB 2.5.6_02-](http://www.objectdb.com/)
* [Hibernate 4.3.6.Final-](http://hibernate.org/)

Weak points
========
* IndexableGraph and KeyIndexableGraph have poorer performance than native GraphDBs.
* In case of using EclipseLink and Hibernate, adding massive data at one commit is very slow, then we should divide massive data into a plurality of sub-blocks.

Micro benchmark only as a guide
===============================


|                            |   [OrientDB](http://www.orientechnologies.com/orientdb/)   | JPA:ObjectDB(*1)| JPA:EclipseLink+Derby|  JPA:Hibernate+Derby |   [DEX](http://www.sparsity-technologies.com/dex)        |
|:---------------------------|--------------|-----------------|----------------------|----------------------|-------------:|
|                            |  2.5.0-SNAPSHOT(*2)   |  2.5.0-SNAPSHOT(*3) |     2.5.0-SNAPSHOT(*4)   |    2.5.0-SNAPSHOT(*5)    |   2.4.0(*6)  |
| VertexTestSuite            |  8212.51     |     3662.94     |       4001.32        |      7233.31         |   5051.43    |
| VertexQueryTestSuite       |  3808.78     |      804.28     |        762.79        |      1943.51         |   1482.64    |
| EdgeTestSuite              |  9863.45     |     2282.13     |       2386.27        |      5482.16         |   4416.40    |
| GraphTestSuite             | 11141.28     |    10893.83     |       8591.78        |     15962.46         |   5631.75    |
| GraphQueryTestSuite        |  1456.90     |      336.30     |        357.44        |      1585.12         |    633.13    |
| GraphMLReaderTestSuite     |  3096.25     |     1526.83     |       1581.42        |      3710.91         |   2771.08    |
| IndexableGraphTestSuite    |  5669.64     |     1307.18     |       1176.68        |      3099.10         |     -        |
| IndexTestSuite             |  2255.25     |      541.50     |        526.15        |      1345.00         |     -        |
| KeyIndexableGraphTestSuite |  3027.29     |     1780.79     |       2209.91        |      5404.79         |     -        |
| TransactionGraphTestSuite  | 17533.41     |    14936.44     |      22549.49        |     36736.34         |     -        |
(msec)

     *1: ObjectDB 2.5.4
     *2: mvn test -Dtest=OrientGraphLightweightEdgesTest
     *3: mvn -f pom-objectdb.xml test -Dtest=JpaGraphTest
     *4: mvn -f pom-eclipselink.xml test -Dtest=JpaGraphTest
     *5: mvn -f pom-hibernate.xml test -Dtest=JpaGraphTest
     *6: mvn test -Dtest=DexGraphTest


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

#### Using [SPARQL](http://en.wikipedia.org/wiki/SPARQL)+[Rexster](https://github.com/tinkerpop/rexster/wiki) with JpaGraph

* see [rexster/README-SPARQL.md](rexster/README-SPARQL.md)

Integration
===========

#### Constracting a JpaGraph in Java code

* see [wiki/Constracting-a-JpaGraph-in-Java-code](https://github.com/sgougi/blueprints-jpa-graph/wiki/Constracting-a-JpaGraph-in-Java-code)

#### Sample Maven Dependencies

* see [wiki/Sample-Maven-Dependencies](https://github.com/sgougi/blueprints-jpa-graph/wiki/Sample-Maven-Dependencies)

#### Integrating with Vert.x Tinkerpop Persistor Module and JpaGraph

* see [wiki/Integrating-with-Vert.x-Tinkerpop-Persistor-Module-and-JpaGraph](https://github.com/sgougi/blueprints-jpa-graph/wiki/Integrating-with-Vert.x-Tinkerpop-Persistor-Module-and-JpaGraph)

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
 
