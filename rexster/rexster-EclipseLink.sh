#!/bin/bash

case `uname` in
  MINGW*|CYGWIN*)
    CP=$( echo `dirname $0`/lib/EclipseLink/*.jar . | sed 's/ /;/g')
    ;;
  *)
    CP=$( echo `dirname $0`/lib/EclipseLink/*.jar . | sed 's/ /:/g')
esac

case `uname` in
  MINGW*|CYGWIN*)  
    CP=$CP;$(echo `dirname $0`/ext/*.jar . | sed 's/ /;/g')
    ;;
  *)
    CP=$CP:$(echo `dirname $0`/ext/*.jar . | sed 's/ /:/g')
esac

PUBLIC=public/
EXTRA=

if [ $1 = "-s" ] ; then
    EXTRA="-wr $PUBLIC"
fi

# Find Java
if [ "$JAVA_HOME" = "" ] ; then
    JAVA="java -server"
else
    JAVA="$JAVA_HOME/bin/java -server"
fi

# Set Java options
if [ "$JAVA_OPTIONS" = "" ] ; then
    JAVA_OPTIONS="-Xms32m -Xmx512m"
fi

# Launch the application
$JAVA $JAVA_OPTIONS -cp $CP com.tinkerpop.rexster.Application $@ $EXTRA -c config/rexster-eclipselink.xml

# Return the program's exit code
exit $?
