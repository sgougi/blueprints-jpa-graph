Hello, Gremlin!!
==============

0) Install blueprints-jpa-graph to the local m2 repository as follows:

      % cd ..
      % mvn install -DskipTests=true
      % cd gremlin

1) Build

      % ant

2)  Execute gremlin_*.sh (unix) or gremin_*.bat (windows) as follows:

      % gremlin_ObjectDB

3) Construct a JpaGraph with a sample data as follows:

               \,,,/
               (o o)
      -----oOOo-(_)-oOOo-----
      gremlin> g = com.tinkerpop.blueprints.impls.jpa.JpaGraphFactory.createJpaGraph()
      ==>jpagraph
      gremlin>

   Or construct a empty JpaGraph as follows:
      gremlin> g = new com.tinkerpop.blueprints.impls.jpa.JpaGraph()
      ==>jpagraph
      gremlin>


4) And see [https://github.com/tinkerpop/gremlin/wiki](https://github.com/tinkerpop/gremlin/wiki) :)

