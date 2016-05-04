#!/bin/sh

export ESCLIENT_HOME=..
CONFIG_DIR=$ESCLIENT_HOME/config

RESOLVED_CONFIG_DIR=`cd "$CONFIG_DIR"; pwd`
export CLASSPATH=$RESOLVED_CONFIG_DIR

for i in `ls $ESCLIENT_HOME/lib/*.jar`; do
	CLASSPATH=$i:$CLASSPATH
done
java -classpath $CLASSPATH  edu.song.linuxmonit.monit.NetMonit $*
